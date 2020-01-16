package com.smartit.beunique.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;

import com.smartit.beunique.R;
import com.smartit.beunique.adapters.ShowAllProductsAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.allproducts.EOAllProductData;
import com.smartit.beunique.entity.allproducts.EOBrandProducts;
import com.smartit.beunique.entity.allproducts.EOProductsCount;
import com.smartit.beunique.networking.JsonParser;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class ShowAllProductsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private RecyclerView productRecyclerView;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private int manufacturerId;
    private Integer TOTAL_ITEMS;
    private GridLayoutManager linearLayoutManager;
    private static final int ITEM_START = 0;
    private int currentPage = ITEM_START;
    private ShowAllProductsAdapter brandsProductsAdapter;
    private ArrayList <EOAllProductData> productArrayList = new ArrayList <> ( );
    private SearchView searchView;
    private boolean isShowAllProducts;

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

        if(!ObjectUtil.isEmpty ( this.getIntent ( ).getIntExtra ( "manufacturerId", 0 ) ) || !ObjectUtil.isEmpty ( this.getIntent ( ).getBooleanExtra ( "isShowAllProducts", false ) )) {
            this.manufacturerId = this.getIntent ( ).getIntExtra ( "manufacturerId", 0 );
            this.isShowAllProducts = this.getIntent ( ).getBooleanExtra ( "isShowAllProducts", false );
        }

        this.initView ( );
        this.setOnClickListener ( );
        this.dataToView ( );
    }

    private void initView() {
        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.progress = new GlobalProgressDialog ( this );
        this.apiInterface = RestClient.getClient ( );
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );
        //here toolbar layout view
        this.toolbarLayout = this.findViewById ( R.id.toolbarLayout );
        this.setSupportActionBar ( this.toolbarLayout );
        this.iv_back_arrow = this.toolbarLayout.findViewById ( R.id.iv_back_arrow );
        this.iv_title_logo = this.toolbarLayout.findViewById ( R.id.iv_title_logo );
        this.iv_cart_icon = this.toolbarLayout.findViewById ( R.id.iv_cart_icon );
        this.productRecyclerView = this.findViewById ( R.id.brandsNameRecyclerView );
        this.searchView = this.findViewById ( R.id.searchView );

        this.iv_back_arrow.setImageResource ( R.drawable.back_arrow_icon );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );
        this.iv_cart_icon.setVisibility ( View.GONE );
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
    }

    private void dataToView() {

        progress.showProgressBar ( );
        totalProductsCountApi ( ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "countProductApi" );
                        EOProductsCount eoProductsCount = JsonParser.getInstance ( ).getObject ( EOProductsCount.class, tempArray[ 1 ] );
                        if(eoProductsCount.getStatus ( ).equalsIgnoreCase ( "success" )) {
                            TOTAL_ITEMS = eoProductsCount.getPayload ( );

                            //TODO At first time load by default page
                            loadFirstPage ( );

                            //TODO when user scroll then this api will call again & again
                            productRecyclerView.addOnScrollListener ( new RecyclerView.OnScrollListener ( ) {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged ( recyclerView, newState );
                                }

                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled ( recyclerView, dx, dy );
                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager ( );
                                    if(linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition ( ) == productArrayList.size ( ) - 1) {
                                        currentPage += 10;
                                        loadNextPage ( );
                                    }
                                }
                            } );


                        } else {
                            progress.hideProgressBar ( );
                            new GlobalAlertDialog ( ShowAllProductsActivity.this, false, true ) {
                                @Override
                                public void onDefault() {
                                    super.onDefault ( );
                                }
                            }.show ( eoProductsCount.getMessage ( ) );
                        }
                    } catch (Exception e) {
                        e.printStackTrace ( );
                    }
                }
            }

            @Override
            public void onFailure(Call <ResponseBody> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    new GlobalAlertDialog ( ShowAllProductsActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );


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
                            hideKeyboard ( ShowAllProductsActivity.this );
                            searchView.clearFocus ( );
                        }
                    }, 100 );
                }

                ArrayList <EOAllProductData> newList = new ArrayList <> ( );
                for (EOAllProductData product : productArrayList) {
                    String type = product.getName ( ).toLowerCase ( );
                    if(type.contains ( newText )) {
                        newList.add ( product );
                    }
                }
                brandsProductsAdapter.updateList ( newList, newText );
                return true;
            }
        } );

    }

    private void loadFirstPage() {
        showAllProductsApi ( ).enqueue ( new Callback <EOBrandProducts> ( ) {
            @Override
            public void onResponse(Call <EOBrandProducts> call, Response <EOBrandProducts> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(!ObjectUtil.isEmpty ( response.body ( ).getProducts ( ) )) {
                        productArrayList.addAll ( response.body ( ).getProducts ( ) );
                        brandsProductsAdapter = new ShowAllProductsAdapter ( ShowAllProductsActivity.this, productArrayList );
                        linearLayoutManager = new GridLayoutManager ( ShowAllProductsActivity.this, 2 );
                        productRecyclerView.setLayoutManager ( linearLayoutManager );
                        productRecyclerView.setItemAnimator ( new DefaultItemAnimator ( ) );
                        productRecyclerView.setAdapter ( brandsProductsAdapter );
                    }
                }
            }

            @Override
            public void onFailure(Call <EOBrandProducts> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    //Toast.makeText ( ShowAllProductsActivity.this, "Server is Under Maintenance. ", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( ShowAllProductsActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }

    private void loadNextPage() {
        if(TOTAL_ITEMS != productArrayList.size ( )) {
            progress.showProgressBar ( );
            showAllProductsApi ( ).enqueue ( new Callback <EOBrandProducts> ( ) {
                @Override
                public void onResponse(Call <EOBrandProducts> call, Response <EOBrandProducts> response) {
                    progress.hideProgressBar ( );

                    if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                        if(!ObjectUtil.isEmpty ( response.body ( ).getProducts ( ) )) {
                            productArrayList.addAll ( response.body ( ).getProducts ( ) );
                        }
                        brandsProductsAdapter.updateList ( productArrayList, "" );
                    }
                }

                @Override
                public void onFailure(Call <EOBrandProducts> call, Throwable t) {
                    if(t.getMessage ( ) != null) {
                        progress.hideProgressBar ( );
                        //Toast.makeText ( ShowAllProductsActivity.this, "Server is Under Maintenance. ", Toast.LENGTH_SHORT ).show ( );
                        new GlobalAlertDialog ( ShowAllProductsActivity.this, false, true ) {
                            @Override
                            public void onDefault() {
                                super.onDefault ( );
                            }
                        }.show ( R.string.server_is_under_maintenance );
                    }
                }
            } );
        }

    }

    private Call <ResponseBody> totalProductsCountApi() {
        String totalCountUrl;
        if(isShowAllProducts) {
            totalCountUrl = "webservice/get/products.php/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON";
        } else {
            totalCountUrl = "webservice/get/products.php/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&id_manufacturer=" + this.manufacturerId;
        }
        return apiInterface.getTotalProductsCount ( totalCountUrl );
    }

    private Call <EOBrandProducts> showAllProductsApi() {
        String productsUrl;
        if(isShowAllProducts) {
            productsUrl = "api/products/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&limit=" + currentPage + "," + "10" + "&display=full" + "&language=" + selectedLangId;
        } else {
            productsUrl = "api/products/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&display=full&filter[id_manufacturer]=" + manufacturerId + "&language=" + selectedLangId + "&limit=" + currentPage + "," + "10" + "&filter[active]=1";
        }
        return apiInterface.getBrandsWiseProducts ( productsUrl );
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
