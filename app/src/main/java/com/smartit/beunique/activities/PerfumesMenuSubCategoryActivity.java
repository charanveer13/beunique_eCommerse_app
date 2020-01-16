package com.smartit.beunique.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.smartit.beunique.entity.dashboardCategory.EOCategoryCounts;
import com.smartit.beunique.entity.dashboardCategory.EOCategoryProducts;
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

public class PerfumesMenuSubCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private RecyclerView subCategoryRecyclerView;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private SearchView searchView;
    private int menuCategoryId;
    private Integer TOTAL_ITEMS;
    private int pageNumber = 1;
    private int totalPage = 0;
    boolean isScrolled = false;
    private ArrayList <EOAllProductData> allProductDataArrayList = new ArrayList <> ( );
    private ShowAllProductsAdapter showAllProductsAdapter;
    private boolean isShowAllHairPerfumes;
    private static final int HAIR_PERFUMES_CATEGORY_ID = 421;
    private boolean isShowAllKidsPerfumes;
    private ImageView iv_sub_category_banner;


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

        setContentView ( R.layout.activity_perfumes_sub_category );

        if(!ObjectUtil.isEmpty ( this.getIntent ( ).getIntExtra ( "menuCategoryId", 0 ) ) || !ObjectUtil.isEmpty ( this.getIntent ( ).getBooleanExtra ( "isShowAllHairPerfumes", false ) )
                || !ObjectUtil.isEmpty ( this.getIntent ( ).getBooleanExtra ( "isShowAllKidsPerfumes", false ) )) {

            this.menuCategoryId = this.getIntent ( ).getIntExtra ( "menuCategoryId", 0 );
            this.isShowAllHairPerfumes = this.getIntent ( ).getBooleanExtra ( "isShowAllHairPerfumes", false );
            this.isShowAllKidsPerfumes = this.getIntent ( ).getBooleanExtra ( "isShowAllKidsPerfumes", false );
        }

        this.initView ( );
        this.setOnClickListener ( );
        this.dataToView ( );
        this.getTotalCountsWithProducts ( );
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
        this.subCategoryRecyclerView = this.findViewById ( R.id.subCategoryRecyclerView );
        this.searchView = this.findViewById ( R.id.searchView );
        this.iv_sub_category_banner = this.findViewById ( R.id.iv_sub_category_banner );


        this.showAllProductsAdapter = new ShowAllProductsAdapter ( PerfumesMenuSubCategoryActivity.this, allProductDataArrayList );
        this.subCategoryRecyclerView.setHasFixedSize ( true );
        this.subCategoryRecyclerView.setItemViewCacheSize ( 20 );
        this.subCategoryRecyclerView.setDrawingCacheEnabled ( true );
        GridLayoutManager linearLayoutManager = new GridLayoutManager ( PerfumesMenuSubCategoryActivity.this, 2 );
        this.subCategoryRecyclerView.setLayoutManager ( linearLayoutManager );
        this.subCategoryRecyclerView.setAdapter ( showAllProductsAdapter );
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
        this.subCategoryRecyclerView.addOnScrollListener ( this.onScrollListener );
    }

    private void dataToView() {
        this.iv_back_arrow.setImageResource ( R.drawable.back_arrow_icon );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );
        this.iv_cart_icon.setVisibility ( View.GONE );

        //TODO from here hide banner for when user click women,men,by notes, moods etc.
        switch (menuCategoryId) {
            case 335:
                iv_sub_category_banner.setVisibility ( View.GONE );
                break;
            case 339:
                iv_sub_category_banner.setVisibility ( View.GONE );
                break;
            case 343:
                iv_sub_category_banner.setVisibility ( View.GONE );
                break;
            case 365:
                iv_sub_category_banner.setVisibility ( View.GONE );
                break;
        }


        if(isShowAllHairPerfumes) { //TODO for hair perfumes from direct navigation menu
            iv_sub_category_banner.setImageResource ( R.drawable.ic_hair_perfumes );
        }

        //TODO set here all sub_categories banner
        switch (menuCategoryId) {
            case 336:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_eau_de_toilette_women );
                break;
            case 337:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_eau_de_perfum_women );
                break;
            case 338:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_eau_de_oud_women );
                break;
            case 421:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_hair_perfumes );
                break;
            case 340:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_eau_de_toilette_men );
                break;
            case 341:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_eau_de_perfum_men );
                break;
            case 342:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_eau_de_oud_women );
                break;
            case 344:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_by_notes_floral );
                break;
            case 345:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_by_notes_fruity );
                break;
            case 346:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_by_notes_aromatic );
                break;
            case 347:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_by_notes_oriental );
                break;
            case 348:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_by_notes_woody );
                break;
            case 349:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_by_notes_leather );
                break;
            case 418:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_by_notes_citrus );
                break;
            case 419:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_by_notes_aqua );
                break;
            case 366:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_moods_royalty );
                break;
            case 367:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_moods_class );
                break;
            case 368:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_moods_executive );
                break;
            case 369:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_moods_casual );
                break;
            case 370:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_moods_sexy );
                break;
            case 377:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_moods_bride );
                break;
            case 416:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_moods_groom );
                break;
            case 417:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_moods_gym );
                break;
            case 459:
                iv_sub_category_banner.setImageResource ( R.drawable.ic_kids_perfume );
                break;
            default:
                iv_sub_category_banner.setImageResource ( R.drawable.icon_no_image );
                break;
        }


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
                            hideKeyboard ( PerfumesMenuSubCategoryActivity.this );
                            searchView.clearFocus ( );
                        }
                    }, 100 );
                }

                ArrayList <EOAllProductData> newList = new ArrayList <> ( );
                for (EOAllProductData eoAllProductData : allProductDataArrayList) {
                    String type = eoAllProductData.getName ( ).toLowerCase ( );
                    if(type.contains ( newText )) {
                        newList.add ( eoAllProductData );
                    }
                }
                showAllProductsAdapter.updateList ( newList, newText );
                return true;
            }
        } );


    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener ( ) {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged ( recyclerView, newState );
            isScrolled = true;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled ( recyclerView, dx, dy );

            if(isScrolled) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager ( );
                if(linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition ( ) == allProductDataArrayList.size ( ) - 1) {
                    pageNumber += 1;
                    isScrolled = true;
                    getTotalCountsWithProducts ( );
                }
            }
        }
    };

    private void getTotalCountsWithProducts() {
        if(pageNumber <= totalPage || totalPage == 0) {
            progress.showProgressBar ( );
            totalProductsCountApi ( ).enqueue ( new Callback <ResponseBody> ( ) {
                @Override
                public void onResponse(@NonNull Call <ResponseBody> call, @NonNull Response <ResponseBody> response) {

                    if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                        try {
                            String mainString = response.body ( ).string ( );
                            String tempArray[] = mainString.split ( "countProductApi" );
                            EOCategoryCounts categoryCounts = JsonParser.getInstance ( ).getObject ( EOCategoryCounts.class, tempArray[ 1 ] );
                            if(categoryCounts.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                TOTAL_ITEMS = categoryCounts.getPayload ( ).getCount ( );
                                totalPage = categoryCounts.getPayload ( ).getTotal_no_of_pages ( );

                                if(!ObjectUtil.isEmpty ( categoryCounts.getPayload ( ).getProduct ( ) ))
                                    for (String str : categoryCounts.getPayload ( ).getProduct ( )) {
                                        loadProductPage ( str );
                                    }

                            } else {
                                progress.hideProgressBar ( );
                                new GlobalAlertDialog ( PerfumesMenuSubCategoryActivity.this, false, true ) {
                                    @Override
                                    public void onDefault() {
                                        super.onDefault ( );
                                    }
                                }.show ( categoryCounts.getMessage ( ) );
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
                        new GlobalAlertDialog ( PerfumesMenuSubCategoryActivity.this, false, true ) {
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

    private void loadProductPage(String productId) {
        if(TOTAL_ITEMS != allProductDataArrayList.size ( )) {
            progress.showProgressBar ( );
            apiInterface.getNewArrivalProduct ( "api/products/" + productId + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&language=" + selectedLangId ).enqueue ( new Callback <EOCategoryProducts> ( ) {
                @Override
                public void onResponse(Call <EOCategoryProducts> call, Response <EOCategoryProducts> response) {
                    progress.hideProgressBar ( );

                    if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                        if(!ObjectUtil.isEmpty ( response.body ( ).getProduct ( ) )) {
                            allProductDataArrayList.add ( response.body ( ).getProduct ( ) );
                            showAllProductsAdapter.notifyDataSetChanged ( );
                        }
                    }
                }

                @Override
                public void onFailure(Call <EOCategoryProducts> call, Throwable t) {
                    if(t.getMessage ( ) != null) {
                        progress.hideProgressBar ( );
                        new GlobalAlertDialog ( PerfumesMenuSubCategoryActivity.this, false, true ) {
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
        if(isShowAllHairPerfumes) {
            totalCountUrl = "webservice/get/products.php/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&id_category=" + HAIR_PERFUMES_CATEGORY_ID + "&page=" + pageNumber + "&show_per_page=10";
        } else if(isShowAllKidsPerfumes) {
            totalCountUrl = "webservice/get/products.php/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&id_category=" + this.menuCategoryId + "&page=" + pageNumber + "&show_per_page=10";
        } else {
            totalCountUrl = "webservice/get/products.php/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&id_category=" + this.menuCategoryId + "&page=" + pageNumber + "&show_per_page=10";
        }
        return apiInterface.getTotalCategoryCounts ( totalCountUrl );
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
