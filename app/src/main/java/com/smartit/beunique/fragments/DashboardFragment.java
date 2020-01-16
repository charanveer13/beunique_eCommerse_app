package com.smartit.beunique.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.LanguageCurrencyActivity;
import com.smartit.beunique.activities.ShowAllProductsActivity;
import com.smartit.beunique.adapters.DashboardFeatureProductsCategoryAdapter;
import com.smartit.beunique.adapters.DashboardImagesRecyclerAdapter;
import com.smartit.beunique.adapters.DashboardNewArrivalCategoryAdapter;
import com.smartit.beunique.adapters.DashboardTopSellerCategoryAdapter;
import com.smartit.beunique.adapters.DashboardViewPagerBannerAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.allproducts.EOAllProductData;
import com.smartit.beunique.entity.allproducts.EOTag;
import com.smartit.beunique.entity.dashboardCategory.EOCategory;
import com.smartit.beunique.entity.dashboardCategory.EOCategoryIds;
import com.smartit.beunique.entity.dashboardCategory.EOCategoryProducts;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.FontTypeface;
import com.smartit.beunique.util.ObjectUtil;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ViewPager bannerViewPager;
    private CirclePageIndicator circlePageIndicator;
    private SearchView searchView;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private RecyclerView imagesRecyclerView, categoryNewArrivalRecyclerView, categoryFeatureProductsRecyclerView, categoryTopSellerRecyclerView;

    private DashboardImagesRecyclerAdapter imagesRecyclerAdapter;
    private DashboardNewArrivalCategoryAdapter newArrivalCategoryAdapter;
    private DashboardFeatureProductsCategoryAdapter featureProductsCategoryAdapter;
    private DashboardTopSellerCategoryAdapter topSellerCategoryAdapter;

    private TextView tv_view_all, tv_new_arrival, tv_feature_product, tv_top_seller_product;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private FontTypeface fontTypeface;
    private int selectedLangId;

    //array for all categories like : newArrival, featureProducts, topSeller list initialization
    private ArrayList <EOAllProductData> newArrivalList = new ArrayList <> ( );
    private ArrayList <EOAllProductData> featureProductsList = new ArrayList <> ( );
    private ArrayList <EOAllProductData> topSellerList = new ArrayList <> ( );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate ( R.layout.fragment_dashboard, container, false );
        this.initView ( );
        return this.view;
    }

    private void initView() {
        this.apiInterface = RestClient.getClient ( );
        this.progress = new GlobalProgressDialog ( getContext ( ) );
        this.noInternetDialog = new NoInternetDialog.Builder ( getActivity ( ) ).build ( );
        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.fontTypeface = FontTypeface.getInstance ( getActivity ( ) );


        this.tv_view_all = this.view.findViewById ( R.id.tv_view_all );
        this.tv_view_all.setOnClickListener ( this );

        this.tv_new_arrival = this.view.findViewById ( R.id.tv_new_arrival );
        this.tv_feature_product = this.view.findViewById ( R.id.tv_feature_product );
        this.tv_top_seller_product = this.view.findViewById ( R.id.tv_top_seller_product );
        this.searchView = this.view.findViewById ( R.id.searchView );
        this.searchView.clearFocus ( );

        this.imagesRecyclerView = view.findViewById ( R.id.imagesRecyclerView );
        this.categoryNewArrivalRecyclerView = this.view.findViewById ( R.id.categoryNewArrivalRecyclerView );
        this.categoryFeatureProductsRecyclerView = this.view.findViewById ( R.id.categoryFeatureProductsRecyclerView );
        this.categoryTopSellerRecyclerView = this.view.findViewById ( R.id.categoryTopSellerRecyclerView );

        //code for banner viewpager
        this.bannerViewPager = view.findViewById ( R.id.bannerViewPager );
        final ArrayList <Integer> imagesArray = new ArrayList <> ( );
        Integer[] images = {R.drawable.banner_image1, R.drawable.banner_image2, R.drawable.banner_image3};
        imagesArray.addAll ( Arrays.asList ( images ) );
        this.bannerViewPager.setAdapter ( new DashboardViewPagerBannerAdapter ( getActivity ( ), imagesArray ) );
        this.circlePageIndicator = view.findViewById ( R.id.indicator );
        this.circlePageIndicator.setViewPager ( bannerViewPager );
        final float density = getResources ( ).getDisplayMetrics ( ).density;
        this.circlePageIndicator.setRadius ( 5 * density );
        NUM_PAGES = images.length;

        // Auto start of viewpager
        final Handler handler = new Handler ( );
        final Runnable Update = new Runnable ( ) {
            public void run() {
                if(currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                bannerViewPager.setCurrentItem ( currentPage++, true );
            }
        };

        Timer swipeTimer = new Timer ( );
        swipeTimer.schedule ( new TimerTask ( ) {
            @Override
            public void run() {
                handler.post ( Update );
            }
        }, 3000, 3000 );


        // Pager listener over indicator
        this.circlePageIndicator.setOnPageChangeListener ( new ViewPager.OnPageChangeListener ( ) {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        } );

        //code for horizontal recyclerview
        ArrayList <Integer> imageArrayList = new ArrayList <> ( );
        imageArrayList.add ( R.drawable.perfume_1 );
        imageArrayList.add ( R.drawable.perfume_2 );
        imageArrayList.add ( R.drawable.perfume_3 );
        imageArrayList.add ( R.drawable.perfume_4 );
        imageArrayList.add ( R.drawable.perfume_5 );
        imageArrayList.add ( R.drawable.perfume_6 );
        this.imagesRecyclerAdapter = new DashboardImagesRecyclerAdapter ( getActivity ( ), imageArrayList );
        this.imagesRecyclerView.setHasFixedSize ( true );
        this.imagesRecyclerView.setLayoutManager ( new LinearLayoutManager ( getActivity ( ), LinearLayoutManager.HORIZONTAL, true ) );
        this.imagesRecyclerView.setAdapter ( imagesRecyclerAdapter );

        //from here all 3 categories are loaded
        this.getNewArrivalProductsIds ( );
        this.getFeaturedProductsIds ( );
        this.getTopSellerProductsIds ( );

        //adapter initialization for new arrival category
        this.newArrivalCategoryAdapter = new DashboardNewArrivalCategoryAdapter ( getContext ( ), newArrivalList );
        this.categoryNewArrivalRecyclerView.setHasFixedSize ( true );
        this.categoryNewArrivalRecyclerView.setLayoutManager ( new LinearLayoutManager ( getActivity ( ), LinearLayoutManager.HORIZONTAL, false ) );
        this.categoryNewArrivalRecyclerView.setAdapter ( newArrivalCategoryAdapter );

        //adapter initialization for featured products category
        this.featureProductsCategoryAdapter = new DashboardFeatureProductsCategoryAdapter ( getContext ( ), featureProductsList );
        this.categoryFeatureProductsRecyclerView.setHasFixedSize ( true );
        this.categoryFeatureProductsRecyclerView.setLayoutManager ( new LinearLayoutManager ( getActivity ( ), LinearLayoutManager.HORIZONTAL, false ) );
        this.categoryFeatureProductsRecyclerView.setAdapter ( featureProductsCategoryAdapter );

        //adapter initialization for top sellers category
        this.topSellerCategoryAdapter = new DashboardTopSellerCategoryAdapter ( getContext ( ), topSellerList );
        this.categoryTopSellerRecyclerView.setHasFixedSize ( true );
        this.categoryTopSellerRecyclerView.setLayoutManager ( new LinearLayoutManager ( getActivity ( ), LinearLayoutManager.HORIZONTAL, false ) );
        this.categoryTopSellerRecyclerView.setAdapter ( topSellerCategoryAdapter );
    }

    private void getNewArrivalProductsIds() {
        progress.showProgressBar ( );
        apiInterface.getNewArrivalProductIds ( "api/categories/457/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&filter[active]=1&language=" + selectedLangId ).enqueue ( new Callback <EOCategoryIds> ( ) {
            @Override
            public void onResponse(Call <EOCategoryIds> call, Response <EOCategoryIds> response) {

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(response.body ( ).getCategory ( ) != null) {
                        EOCategory category = response.body ( ).getCategory ( );

                        if(!ObjectUtil.isEmpty ( category.getName ( ) )) {
                            tv_new_arrival.setText ( category.getName ( ).toUpperCase ( ) );
                        }

                        if(!ObjectUtil.isEmpty ( category.getAssociations ( ) )) {
                            if(!ObjectUtil.isEmpty ( category.getAssociations ( ).getProducts ( ) )) {
                                for (EOTag eoTag : category.getAssociations ( ).getProducts ( )) {
                                    getNewArrivalProducts ( String.valueOf ( eoTag.getId ( ) ) );
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call <EOCategoryIds> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    //Toast.makeText ( getActivity ( ), "Server is Under Maintenance.", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( getActivity (), false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );

    }

    private void getNewArrivalProducts(String productId) {
        apiInterface.getNewArrivalProduct ( "api/products/" + productId + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&language=" + selectedLangId ).enqueue ( new Callback <EOCategoryProducts> ( ) {
            @Override
            public void onResponse(Call <EOCategoryProducts> call, Response <EOCategoryProducts> response) {
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    newArrivalList.add ( response.body ( ).getProduct ( ) );
                    newArrivalCategoryAdapter.notifyDataSetChanged ( );
                }
            }

            @Override
            public void onFailure(Call <EOCategoryProducts> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    Toast.makeText ( getActivity ( ), "" + t.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );

    }


    private void getFeaturedProductsIds() {
        apiInterface.getFeatureProductsIds ( "api/categories/245/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&filter[active]=1&language=" + selectedLangId ).enqueue ( new Callback <EOCategoryIds> ( ) {
            @Override
            public void onResponse(Call <EOCategoryIds> call, Response <EOCategoryIds> response) {
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(!ObjectUtil.isEmpty ( response.body ( ).getCategory ( ) )) {
                        EOCategory category = response.body ( ).getCategory ( );

                        if(!ObjectUtil.isEmpty ( category.getName ( ) )) {
                            tv_feature_product.setText ( category.getName ( ).toUpperCase ( ) );
                        }

                        if(!ObjectUtil.isEmpty ( category.getAssociations ( ) )) {
                            if(!ObjectUtil.isEmpty ( category.getAssociations ( ).getProducts ( ) )) {
                                for (EOTag eoTag : category.getAssociations ( ).getProducts ( )) {
                                    getFeaturedProducts ( String.valueOf ( eoTag.getId ( ) ) );
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call <EOCategoryIds> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    Toast.makeText ( getActivity ( ), "" + t.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );

    }


    private void getFeaturedProducts(String productId) {
        apiInterface.getFeatureProducts ( "api/products/" + productId + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&language=" + selectedLangId ).enqueue ( new Callback <EOCategoryProducts> ( ) {
            @Override
            public void onResponse(Call <EOCategoryProducts> call, Response <EOCategoryProducts> response) {
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    featureProductsList.add ( response.body ( ).getProduct ( ) );
                    featureProductsCategoryAdapter.notifyDataSetChanged ( );
                }
            }

            @Override
            public void onFailure(Call <EOCategoryProducts> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    Toast.makeText ( getActivity ( ), "" + t.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );

    }

    private void getTopSellerProductsIds() {
        apiInterface.getTopSellersIds ( "api/categories/384/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&filter[active]=1&language=" + selectedLangId ).enqueue ( new Callback <EOCategoryIds> ( ) {
            @Override
            public void onResponse(Call <EOCategoryIds> call, Response <EOCategoryIds> response) {
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(response.body ( ).getCategory ( ) != null) {
                        EOCategory category = response.body ( ).getCategory ( );

                        if(!ObjectUtil.isEmpty ( category.getName ( ) )) {
                            tv_top_seller_product.setText ( category.getName ( ).toUpperCase ( ) );
                        }

                        if(!ObjectUtil.isEmpty ( category.getAssociations ( ) )) {
                            if(!ObjectUtil.isEmpty ( category.getAssociations ( ).getProducts ( ) )) {
                                for (EOTag eoTag : category.getAssociations ( ).getProducts ( )) {
                                    getTopSellerProducts ( String.valueOf ( eoTag.getId ( ) ) );
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call <EOCategoryIds> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    Toast.makeText ( getActivity ( ), "" + t.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );

    }

    private void getTopSellerProducts(String productId) {
        apiInterface.getTopSellersProducts ( "api/products/" + productId + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&language=" + selectedLangId ).enqueue ( new Callback <EOCategoryProducts> ( ) {
            @Override
            public void onResponse(Call <EOCategoryProducts> call, Response <EOCategoryProducts> response) {
                progress.hideProgressBar ( );
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    topSellerList.add ( response.body ( ).getProduct ( ) );
                    topSellerCategoryAdapter.notifyDataSetChanged ( );
                }
            }

            @Override
            public void onFailure(Call <EOCategoryProducts> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    //Toast.makeText ( getActivity ( ), "Server is Under Maintenance.", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( getActivity (), false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId ( )) {
            case R.id.tv_view_all:
                Intent intent = new Intent ( getActivity ( ), ShowAllProductsActivity.class );
                intent.putExtra ( "isShowAllProducts", true );
                startActivity ( intent );
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }


}
