package com.smartit.beunique.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.account.EOAccountRegister;
import com.smartit.beunique.entity.currency.EOCurrency;
import com.smartit.beunique.entity.currency.EOCurrencyList;
import com.smartit.beunique.entity.drawerMenu.EOPerfumesSubMenu;
import com.smartit.beunique.entity.drawerMenu.SubMenuCategory;
import com.smartit.beunique.entity.language.EOLanguage;
import com.smartit.beunique.entity.language.EOLanguageList;
import com.smartit.beunique.networking.JsonParser;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.Constants;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;

import java.io.IOException;
import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class ActivityExplore extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private TextView tvLetsExplore;
    private ImageView iv_app_name;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private SessionSecuredPreferences loginSignupPreference;
    private String selectedCustomerId;

    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;

    private ArrayList <EOLanguage> eoLanguageArrayList = new ArrayList <> ( );
    private ArrayList <EOCurrency> eoCurrencyArrayList = new ArrayList <> ( );
    private ArrayList <SubMenuCategory> menuPerfumesArrayList = new ArrayList <> ( );
    private EOAccountRegister userInfoObject;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext ( LocalizationHelper.onAttach ( newBase ) );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        //TODO set layout direction on language change
        if(LANGUAGE_ARABIC.equals ( LocalizationHelper.getLanguage ( this ) )) {
            getWindow ( ).getDecorView ( ).setLayoutDirection ( View.LAYOUT_DIRECTION_RTL );
        } else {
            getWindow ( ).getDecorView ( ).setLayoutDirection ( View.LAYOUT_DIRECTION_LTR );
        }

        setContentView ( R.layout.activity_explore );

        this.initView ( );
        this.setOnClickListener ( );
        this.dataToView ( );
    }

    private void initView() {
        this.apiInterface = RestClient.getClient ( );
        this.progress = new GlobalProgressDialog ( this );
        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );
        this.loginSignupPreference = ApplicationHelper.application ( ).loginPreferences ( LOGIN_SIGNUP_PREFERENCE );
        this.selectedCustomerId = loginSignupPreference.getString ( Constants.SELECTED_CUSTOMER_ID, "" );

        this.progressBar = findViewById ( R.id.progressBar );
        this.tvLetsExplore = findViewById ( R.id.tvLetsExplore );
        this.iv_app_name = this.findViewById ( R.id.iv_app_name );

        this.getLanguageApi ( );
        this.getCurrencyApi ( );
        this.getPerfumesCategoryMenuApi ( );
        if(!ObjectUtil.isEmpty ( selectedCustomerId )) {
            this.getProfileInformation ( );
        }
    }

    private void setOnClickListener() {
        this.tvLetsExplore.setOnClickListener ( this );
    }

    private void dataToView() {
        this.iv_app_name.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );
    }

    @Override
    public void onClick(View view) {
        if(view.getId ( ) == R.id.tvLetsExplore) {

            this.openMainActivity ( );
        }
    }

    private void openMainActivity() {
        Intent intentMainActivity = new Intent ( ActivityExplore.this, MainActivity.class );
        Bundle bundle = new Bundle ( );
        bundle.putSerializable ( "languageList", eoLanguageArrayList );
        bundle.putSerializable ( "currencyList", eoCurrencyArrayList );
        bundle.putSerializable ( "perfumesList", menuPerfumesArrayList );
        intentMainActivity.putExtra ( "bundleList", bundle );
        if(!ObjectUtil.isEmpty ( userInfoObject ))
            intentMainActivity.putExtra ( "userInfoObject", userInfoObject );
        startActivity ( intentMainActivity );
        this.finish ( );
    }


    private void getLanguageApi() {
        progress.showProgressBar ( );
        apiInterface.getLanguageApi ( ).enqueue ( new Callback <EOLanguageList> ( ) {
            @Override
            public void onResponse(Call <EOLanguageList> call, Response <EOLanguageList> response) {
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(!ObjectUtil.isEmpty ( response.body ( ).getLanguages ( ) )) {
                        eoLanguageArrayList.addAll ( response.body ( ).getLanguages ( ) );
                    }
                }
            }

            @Override
            public void onFailure(Call <EOLanguageList> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    //Toast.makeText ( ActivityExplore.this, "Server is Under Maintenance. ", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( ActivityExplore.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );

    }

    private void getCurrencyApi() {
        apiInterface.getCurrencyApi ( ).enqueue ( new Callback <EOCurrencyList> ( ) {
            @Override
            public void onResponse(Call <EOCurrencyList> call, Response <EOCurrencyList> response) {
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(!ObjectUtil.isEmpty ( response.body ( ).getCurrencies ( ) )) {
                        eoCurrencyArrayList.addAll ( response.body ( ).getCurrencies ( ) );
                    }
                }
            }

            @Override
            public void onFailure(Call <EOCurrencyList> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    //Toast.makeText ( ActivityExplore.this, "" + t.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );
    }

    private void getPerfumesCategoryMenuApi() {
        apiInterface.getPerfumesSubMenus ( "api/categories/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&display=[id,id_parent,name]&filter[id_parent]=334&filter[active]=1&language=" + selectedLangId ).enqueue ( new Callback <EOPerfumesSubMenu> ( ) {
            @Override
            public void onResponse(Call <EOPerfumesSubMenu> call, Response <EOPerfumesSubMenu> response) {
                progress.hideProgressBar ( );
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(!ObjectUtil.isEmpty ( response.body ( ).getCategories ( ) )) {
                        menuPerfumesArrayList.addAll ( response.body ( ).getCategories ( ) );
                    }
                }
            }

            @Override
            public void onFailure(Call <EOPerfumesSubMenu> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    //Toast.makeText ( ActivityExplore.this, "Server is Under Maintenance ", Toast.LENGTH_SHORT ).show ( );

                }
            }
        } );
    }

    private void getProfileInformation() {
        // progress.showProgressBar ( );
        apiInterface.getUserInfo ( selectedCustomerId ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, retrofit2.Response <ResponseBody> response) {
                progress.hideProgressBar ( );
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "profileApi" );
                        EOAccountRegister eoProfileInfo = JsonParser.getInstance ( ).getObject ( EOAccountRegister.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoProfileInfo )) {
                            if(eoProfileInfo.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                userInfoObject = eoProfileInfo;

                            } else {
                                Toast.makeText ( ActivityExplore.this, "" + eoProfileInfo.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace ( );
                    }
                }
            }

            @Override
            public void onFailure(Call <ResponseBody> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
//                    new GlobalAlertDialog ( ActivityExplore.this, false, true ) {
//                        @Override
//                        public void onDefault() {
//                            super.onDefault ( );
//                        }
//                    }.show ( R.string.server_is_under_maintenance );
                }
            }

        } );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }


}
