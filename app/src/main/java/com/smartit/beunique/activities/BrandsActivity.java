package com.smartit.beunique.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.smartit.beunique.R;
import com.smartit.beunique.adapters.BrandsAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.manufacturers.EOManufacturers;
import com.smartit.beunique.entity.manufacturers.EOManufacturersList;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.DividerItemDecoration;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;
import com.smartit.beunique.util.RecyclerSectionItemDecoration;
import com.smartit.beunique.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class BrandsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private RecyclerView brandsNameRecyclerView;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private BrandsAdapter brandsAdapter;
    private LinearLayout searchBarLayout;
    private SearchView searchView;
    private ArrayList <EOManufacturers> manufacturersArrayList;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext ( LocalizationHelper.onAttach ( newBase ) );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        //TODO set layout direction on language change
        if(LANGUAGE_ARABIC.equals ( LocalizationHelper.getLanguage ( this ) )) {
            getWindow ( ).getDecorView ( ).setLayoutDirection ( View.LAYOUT_DIRECTION_RTL );
        } else {
            getWindow ( ).getDecorView ( ).setLayoutDirection ( View.LAYOUT_DIRECTION_LTR );
        }

        setContentView ( R.layout.activity_brands );

        this.initView ( );
        this.setOnClickListener ( );
        this.getAllBrandsApi ( );
        this.dataToView ( );
    }

    private void initView() {
        this.progress = new GlobalProgressDialog ( this );
        this.apiInterface = RestClient.getClient ( );
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );
        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );

        //here toolbar layout view
        this.toolbarLayout = this.findViewById ( R.id.toolbarLayout );
        this.setSupportActionBar ( this.toolbarLayout );
        this.iv_back_arrow = this.toolbarLayout.findViewById ( R.id.iv_back_arrow );
        this.iv_title_logo = this.toolbarLayout.findViewById ( R.id.iv_title_logo );
        this.iv_cart_icon = this.toolbarLayout.findViewById ( R.id.iv_cart_icon );
        this.brandsNameRecyclerView = this.findViewById ( R.id.brandsNameRecyclerView );
        this.searchBarLayout = this.findViewById ( R.id.searchBarLayout );
        this.searchView = this.findViewById ( R.id.searchView );


        this.manufacturersArrayList = new ArrayList <> ( );
        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration ( getResources ( ).getDimensionPixelSize ( R.dimen._35sdp ), true,
                        getSectionCallback ( this.manufacturersArrayList ) );
        LinearLayoutManager layoutManager = new LinearLayoutManager ( BrandsActivity.this, LinearLayoutManager.VERTICAL, false );
        brandsNameRecyclerView.setHasFixedSize ( true );
        brandsNameRecyclerView.setLayoutManager ( layoutManager );
        brandsNameRecyclerView.addItemDecoration ( sectionItemDecoration );
        brandsNameRecyclerView.addItemDecoration ( new DividerItemDecoration ( BrandsActivity.this ) );
        brandsAdapter = new BrandsAdapter ( BrandsActivity.this, this.manufacturersArrayList );
        brandsNameRecyclerView.setAdapter ( brandsAdapter );
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
    }

    private void getAllBrandsApi() {
        progress.showProgressBar ( );
        apiInterface.getAllBrandsApi ( "api/manufacturers/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&display=full&filter[active]=1&sort=[name_ASC]&language=" + selectedLangId ).enqueue ( new Callback <EOManufacturersList> ( ) {
            @Override
            public void onResponse(Call <EOManufacturersList> call, Response <EOManufacturersList> response) {
                progress.hideProgressBar ( );
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(!ObjectUtil.isEmpty ( response.body ( ).getManufacturers ( ) )) {
                        manufacturersArrayList.clear ( );
                        manufacturersArrayList.addAll ( response.body ( ).getManufacturers ( ) );
                        Collections.sort ( manufacturersArrayList );
                        //TODO refresh here recycler view
                        brandsAdapter.notifyDataSetChanged ( );
                    }
                }
            }

            @Override
            public void onFailure(Call <EOManufacturersList> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    new GlobalAlertDialog ( BrandsActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }


    private void dataToView() {
        this.iv_back_arrow.setImageResource ( R.drawable.back_arrow_icon );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );
        this.iv_cart_icon.setVisibility ( View.GONE );
        this.searchBarLayout.setVisibility ( View.GONE );


        this.searchView.setOnQueryTextListener ( new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase ( );
                if(ObjectUtil.isEmpty ( newText )) {
                    new Handler ( ).postDelayed ( new Runnable ( ) {
                        @Override
                        public void run() {
                            hideKeyboard ( BrandsActivity.this );
                            searchView.clearFocus ( );
                        }
                    }, 100 );
                }

                ArrayList <EOManufacturers> newList = new ArrayList <> ( );
                for (EOManufacturers eoManufacturer : manufacturersArrayList) {
                    String type = eoManufacturer.getName ( ).toLowerCase ( );
                    if(type.contains ( newText )) {
                        newList.add ( eoManufacturer );
                    }
                }
                brandsAdapter.updateList ( newList, newText );
                return true;
            }
        } );

    }

    @Override
    public void onClick(View view) {
        if(view.getId ( ) == R.id.iv_back_arrow) {
            this.finish ( );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }

    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List <EOManufacturers> eoManufacturersList) {

        return new RecyclerSectionItemDecoration.SectionCallback ( ) {
            @Override
            public boolean isSection(int position) {
                return position == 0 || eoManufacturersList.get ( position ).getName ( ).charAt ( 0 ) != eoManufacturersList.get ( position - 1 ).getName ( ).charAt ( 0 );
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.a ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.a ) : "ا";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.b ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.b ) : "ب";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.c ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.c ) : "ج";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.d ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.d ) : "د";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.e ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.e ) : "ث";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.f ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.f ) : "ف";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.g ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.g ) : "ز";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.h ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.h ) : "ح";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.i ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.i ) : "أنا";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.j ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.j ) : "ي";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.k ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.k ) : "ك";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.l ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.l ) : "ل";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.m ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.m ) : "م";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.n ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.n ) : "ن";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.o ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.o ) : "س";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.p ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.p ) : "ص";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.q ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.q ) : "ف";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.r ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.r ) : "ص";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.s ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.s ) : "س";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.t ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.t ) : "تي";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.u ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.u ) : "ش";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.v ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.v ) : "ر";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.w ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.w ) : "ث";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.x ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.x ) : "س";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.y ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.y ) : "ذ";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.z ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.z ) : "ض";
                else if(eoManufacturersList.get ( position ).getName ( ).subSequence ( 0, 1 ).equals ( StringUtil.getStringForID ( R.string.four_7_1_1 ) ))
                    return selectedLangId == 1 ? StringUtil.getStringForID ( R.string.four_7_1_1 ) : "٤";
                return "";
            }
        };
    }

    /**
     * Helper to hide the keyboard
     *
     * @param act
     */
    public void hideKeyboard(Activity act) {
        if(act != null && act.getCurrentFocus ( ) != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService ( Activity.INPUT_METHOD_SERVICE );
            inputMethodManager.hideSoftInputFromWindow ( act.getCurrentFocus ( ).getWindowToken ( ), 0 );
        }
    }

}
