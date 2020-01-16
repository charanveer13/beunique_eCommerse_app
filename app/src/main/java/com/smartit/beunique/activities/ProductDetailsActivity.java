package com.smartit.beunique.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.adapters.ProductDetailDescriptionAdapter;
import com.smartit.beunique.adapters.ProductDetailViewPagerAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueEditText;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.FontAwesomeIcon;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.allproducts.EOAllProductData;
import com.smartit.beunique.entity.allproducts.EOLikeProductCount;
import com.smartit.beunique.entity.allproducts.EOProductAddWishlist;
import com.smartit.beunique.entity.allproducts.EOProductComment;
import com.smartit.beunique.entity.allproducts.EOProductDetailList;
import com.smartit.beunique.entity.allproducts.EOProductDetailSize;
import com.smartit.beunique.entity.allproducts.EOProductFeature;
import com.smartit.beunique.entity.allproducts.EOProductRemoveWishlist;
import com.smartit.beunique.entity.allproducts.EOProductReview;
import com.smartit.beunique.entity.cart.EOAddToCartObject;
import com.smartit.beunique.entity.cart.EOShowCartList;
import com.smartit.beunique.entity.cart.EOShowCartResult;
import com.smartit.beunique.entity.dashboardCategory.EOCategoryProducts;
import com.smartit.beunique.networking.JsonParser;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.Constants;
import com.smartit.beunique.util.FontTypeface;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;
import com.smartit.beunique.util.StringUtil;
import com.smartit.beunique.util.UIUtil;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import am.appwise.components.ni.NoInternetDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_CONVERSION_RATE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_ID;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_SIGN;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

/**
 * Created by android on 25/3/19.
 */

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon, noImageAvailable;
    private ViewPager productViewPager;
    private CirclePageIndicator circleIndicator;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private FontAwesomeIcon font_wishlist, font_wishlist_fill;
    private TextView tv_bag_count;

    private BUniqueTextView tvProductTitle, tv_manufacturer_name, tv_main_price,
            tv_percentage_view, tv_save_price, tv_product_likes, tv_product_points, tv_item_description, tv_view_all_comments,
            textViewAddToCart, textViewGoToCart, tv_write_review;

    private LinearLayout layout_description;
    private Spinner spinner_select_size;
    private ImageView product_video_view;
    private RecyclerView recyclerProductDescription;

    private SessionSecuredPreferences securedPreferences;
    private FontTypeface fontTypeface;
    private int selectedLangId, selectedCurrencyId;
    private String selectedCurrencySign, selectedConversionRate, selectedCustomerId;

    private RestClient.APIInterface apiInterface;
    private String productDetailId;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private SessionSecuredPreferences loginSignupPreference;
    private ArrayList <EOProductComment> eoProductCommentList;
    private EOLikeProductCount eoLikeProductCount;
    private ArrayList <String> sizeArrayList = new ArrayList <> ( );
    private String videoUrl, video_title;
    private String wishlistId;
    private EOAllProductData eoAllProductData;
    private ArrayList <EOShowCartResult> addToCartResultArrayList = new ArrayList <> ( );

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

        setContentView ( R.layout.activity_details_product );

        if(this.getIntent ( ).getSerializableExtra ( "productDetailId" ) != null && getIntent ( ).hasExtra ( "productDetailId" ))
            this.productDetailId = (String) this.getIntent ( ).getSerializableExtra ( "productDetailId" );

        this.initView ( );
        this.setOnClickListener ( );
        this.getDetailsOfProduct ( );
        this.getProductDetailVideoWishlistData ( );

    }

    private void initView() {
        this.progress = new GlobalProgressDialog ( this );
        this.apiInterface = RestClient.getClient ( );
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );
        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.selectedCurrencyId = this.securedPreferences.getInt ( SELECTED_CURRENCY_ID, 0 );

        this.selectedCurrencySign = this.securedPreferences.getString ( SELECTED_CURRENCY_SIGN, "" );
        this.selectedConversionRate = this.securedPreferences.getString ( SELECTED_CURRENCY_CONVERSION_RATE, "" );
        this.fontTypeface = FontTypeface.getInstance ( this );
        this.loginSignupPreference = ApplicationHelper.application ( ).loginPreferences ( LOGIN_SIGNUP_PREFERENCE );
        this.selectedCustomerId = loginSignupPreference.getString ( Constants.SELECTED_CUSTOMER_ID, "" );

        //here toolbar layout view
        this.toolbarLayout = this.findViewById ( R.id.toolbarLayout );
        this.setSupportActionBar ( this.toolbarLayout );
        this.iv_back_arrow = this.toolbarLayout.findViewById ( R.id.iv_back_arrow );
        this.iv_title_logo = this.toolbarLayout.findViewById ( R.id.iv_title_logo );
        this.iv_cart_icon = this.toolbarLayout.findViewById ( R.id.iv_cart_icon );
        this.tv_bag_count = this.toolbarLayout.findViewById ( R.id.tv_bag_count );

        this.tvProductTitle = this.findViewById ( R.id.tvProductTitle );
        this.tv_manufacturer_name = this.findViewById ( R.id.tv_manufacturer_name );
        this.tv_main_price = this.findViewById ( R.id.tv_main_price );
        this.tv_item_description = this.findViewById ( R.id.tv_item_description );
        this.textViewGoToCart = this.findViewById ( R.id.textViewGoToCart );

        this.tv_view_all_comments = this.findViewById ( R.id.tv_view_all_comments );
        this.textViewAddToCart = this.findViewById ( R.id.textViewAddToCart );
        this.tv_write_review = this.findViewById ( R.id.tv_write_review );
        this.productViewPager = this.findViewById ( R.id.productViewPager );
        this.circleIndicator = this.findViewById ( R.id.circleIndicator );
        this.noImageAvailable = this.findViewById ( R.id.noImageAvailable );

        this.font_wishlist = this.findViewById ( R.id.font_wishlist );
        this.font_wishlist_fill = this.findViewById ( R.id.font_wishlist_fill );
        this.layout_description = this.findViewById ( R.id.layout_description );
        this.recyclerProductDescription = this.findViewById ( R.id.recyclerProductDescription );

        this.tv_percentage_view = this.findViewById ( R.id.tv_percentage_view );
        this.tv_save_price = this.findViewById ( R.id.tv_save_price );
        this.spinner_select_size = this.findViewById ( R.id.spinner_select_size );
        this.tv_product_likes = this.findViewById ( R.id.tv_product_likes );
        this.tv_product_points = this.findViewById ( R.id.tv_product_points );
        this.product_video_view = this.findViewById ( R.id.product_video_view );

        this.iv_back_arrow.setImageResource ( R.drawable.back_arrow_icon );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
        this.iv_cart_icon.setOnClickListener ( this );
        this.tv_view_all_comments.setOnClickListener ( this );
        this.textViewAddToCart.setOnClickListener ( this );
        this.font_wishlist.setOnClickListener ( this );
        this.font_wishlist_fill.setOnClickListener ( this );
        this.product_video_view.setOnClickListener ( this );
        this.tv_write_review.setOnClickListener ( this );
        this.textViewGoToCart.setOnClickListener ( this );
    }

    @Override
    protected void onResume() {
        super.onResume ( );
        //TODO first check here if items available in cart or not
        if(selectedCustomerId != null)
            this.showDataInCart ( selectedCustomerId );
    }

    private void getDetailsOfProduct() {
        if(!ObjectUtil.isEmpty ( productDetailId )) {
            progress.showProgressBar ( );
            apiInterface.getWishlistProduct ( "api/products/" + productDetailId + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&language=" + selectedLangId ).enqueue ( new Callback <EOCategoryProducts> ( ) {
                @Override
                public void onResponse(Call <EOCategoryProducts> call, Response <EOCategoryProducts> response) {
                    if(!ObjectUtil.isEmpty ( response.body ( ) )) {

                        eoAllProductData = response.body ( ).getProduct ( );

                        if(!ObjectUtil.isEmpty ( eoAllProductData )) {

                            if(!ObjectUtil.isEmpty ( eoAllProductData.getName ( ) ))
                                tvProductTitle.setText ( eoAllProductData.getName ( ) );

                            if(!ObjectUtil.isEmpty ( eoAllProductData.getManufacturerName ( ) )) {
                                tv_manufacturer_name.setVisibility ( View.VISIBLE );
                                tv_manufacturer_name.setText ( eoAllProductData.getManufacturerName ( ) );
                            }

                            double mainPrice = Double.parseDouble ( eoAllProductData.getPrice ( ) ) * Double.parseDouble ( selectedConversionRate );
                            tv_main_price.setText ( String.format ( Locale.US, "%.2f", mainPrice ).concat ( " " ).concat ( selectedCurrencySign ) );

                            if(!ObjectUtil.isEmpty ( eoAllProductData.getDescriptionShort ( ) )) {
                                layout_description.setVisibility ( View.VISIBLE );
                                tv_item_description.setText ( UIUtil.fromHtml ( eoAllProductData.getDescriptionShort ( ) ) );
                            }

                            //TODO Show view pager from here
                            showViewPager ( );
                        }
                    }
                }

                @Override
                public void onFailure(Call <EOCategoryProducts> call, Throwable t) {
                    if(t.getMessage ( ) != null) {
                        progress.hideProgressBar ( );
                        new GlobalAlertDialog ( ProductDetailsActivity.this, false, true ) {
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

    private void getProductDetailVideoWishlistData() {
        //progress.showProgressBar ( );
        this.apiInterface.getProductDetailVideoWishlistApi ( wishlistVideoUrl ( ) ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "ProductVideoApi" );
                        EOProductDetailList eoProductDetailList = JsonParser.getInstance ( ).getObject ( EOProductDetailList.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoProductDetailList )) {
                            if(eoProductDetailList.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                if(!ObjectUtil.isEmpty ( eoProductDetailList.getPayload ( ) )) {

                                    if(!ObjectUtil.isEmpty ( eoProductDetailList.getPayload ( ) )) {
                                        if(!ObjectUtil.isEmpty ( eoProductDetailList.getPayload ( ).getWishlist ( ) )) {
                                            wishlistId = eoProductDetailList.getPayload ( ).getWishlist ( ).get ( 0 ).getId ( );
                                            font_wishlist_fill.setVisibility ( View.VISIBLE );
                                            font_wishlist.setVisibility ( View.GONE );
                                        }
                                    }

                                    eoProductCommentList = (ArrayList <EOProductComment>) eoProductDetailList.getPayload ( ).getComments ( );

                                    if(ObjectUtil.isEmpty ( eoProductDetailList.getPayload ( ).getLikeProductCount ( ) )) {
                                        tv_product_likes.setText ( StringUtil.getStringForID ( R.string.likes ).concat ( " " ).concat ( "0" ) );
                                    } else {
                                        eoLikeProductCount = eoProductDetailList.getPayload ( ).getLikeProductCount ( ).get ( 0 );
                                        tv_product_likes.setText ( StringUtil.getStringForID ( R.string.likes ).concat ( " " ).concat ( eoLikeProductCount.getCount ( ) ) );
                                    }

                                    //TODO set here video thumbnail in webview
                                    if(!ObjectUtil.isEmpty ( eoProductDetailList.getPayload ( ).getVideos ( ) )) {
                                        product_video_view.setVisibility ( View.VISIBLE );
                                        Picasso.get ( ).load ( eoProductDetailList.getPayload ( ).getVideos ( ).get ( 0 ).getThumbnail ( ) ).fit ( ).centerCrop ( ).
                                                into ( product_video_view );
                                        videoUrl = eoProductDetailList.getPayload ( ).getVideos ( ).get ( 0 ).getUrl ( );
                                        video_title = eoProductDetailList.getPayload ( ).getVideos ( ).get ( 0 ).getTitle ( );
                                    }

                                    //TODO set here product detail descriptions
                                    if(!ObjectUtil.isEmpty ( eoProductDetailList.getPayload ( ).getFeatures ( ) ) || !ObjectUtil.isEmpty ( eoProductDetailList.getPayload ( ).getFeatureValues ( ) )) {
                                        recyclerProductDescription.setVisibility ( View.VISIBLE );
                                        ProductDetailDescriptionAdapter descriptionAdapter = new ProductDetailDescriptionAdapter ( ProductDetailsActivity.this, (ArrayList <EOProductFeature>) eoProductDetailList.getPayload ( ).getFeatures ( ), (ArrayList <EOProductFeature>) eoProductDetailList.getPayload ( ).getFeatureValues ( ) );
                                        recyclerProductDescription.setHasFixedSize ( true );
                                        recyclerProductDescription.setLayoutManager ( new LinearLayoutManager ( ProductDetailsActivity.this, LinearLayoutManager.VERTICAL, false ) );
                                        recyclerProductDescription.setAdapter ( descriptionAdapter );
                                    }

                                    //TODO set here bottle size spinner
                                    if(!ObjectUtil.isEmpty ( eoProductDetailList.getPayload ( ).getProduct_option_values ( ) )) {
                                        for (EOProductDetailSize eoProductDetailSize : eoProductDetailList.getPayload ( ).getProduct_option_values ( )) {
                                            sizeArrayList.add ( eoProductDetailSize.getName ( ) );
                                        }
                                        ArrayAdapter <String> arrayAdapter = new ArrayAdapter <> ( ProductDetailsActivity.this, android.R.layout.simple_spinner_item, sizeArrayList );
                                        arrayAdapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
                                        findViewById ( R.id.view_divider_size ).setVisibility ( View.VISIBLE );
                                        findViewById ( R.id.tv_bottle_size ).setVisibility ( View.VISIBLE );
                                        spinner_select_size.setVisibility ( View.VISIBLE );
                                        spinner_select_size.setAdapter ( arrayAdapter );
                                        spinner_select_size.setOnItemSelectedListener ( onItemSelectedListener );
                                    }

                                }
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
//                    new GlobalAlertDialog ( ProductDetailsForWishlistActivity.this, false, true ) {
//                        @Override
//                        public void onDefault() {
//                            super.onDefault ( );
//                        }
//                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }

    private void showDataInCart(String customerId) {
        if(!ObjectUtil.isEmpty ( customerId ) && !ObjectUtil.isEmpty ( selectedLangId )) {
            //progress.showProgressBar ( );
            this.apiInterface.showDataInCart ( "webservice/cart/count.php/?id_customer=" + customerId + "&id_lang=" + selectedLangId ).enqueue ( new Callback <ResponseBody> ( ) {
                @Override
                public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                    progress.hideProgressBar ( );

                    if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                        try {
                            String mainString = response.body ( ).string ( );
                            String tempArray[] = mainString.split ( "cartApi" );
                            EOShowCartList eoAddToCartList = JsonParser.getInstance ( ).getObject ( EOShowCartList.class, tempArray[ 1 ] );
                            if(!ObjectUtil.isEmpty ( eoAddToCartList )) {
                                if(eoAddToCartList.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                    tv_bag_count.setVisibility ( View.VISIBLE );
                                    tv_bag_count.setText ( String.valueOf ( eoAddToCartList.getPayload ( ).getTotalCart ( ) ) );
                                    if(!ObjectUtil.isEmpty ( addToCartResultArrayList )) {
                                        addToCartResultArrayList.clear ( );
                                    }
                                    addToCartResultArrayList.addAll ( eoAddToCartList.getPayload ( ).getResult ( ) );
                                } else {
                                    tv_bag_count.setVisibility ( View.GONE );
                                    //Toast.makeText ( ProductDetailsActivity.this, "" + eoAddToCartList.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
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
                        new GlobalAlertDialog ( ProductDetailsActivity.this, false, true ) {
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

    //TODO clicked bottle size spinner
    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener ( ) {
        @Override
        public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView <?> parent) {

        }
    };

    private String wishlistVideoUrl() {
        if(loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {
            String customer_Id = loginSignupPreference.getString ( Constants.SELECTED_CUSTOMER_ID, "" );
            return "webservice/product/product_details.php/?id_lang=" + selectedLangId + "&id_customer=" + customer_Id + "&id_product=" + productDetailId;
        } else {
            return "webservice/product/product_details.php/?id_lang=" + selectedLangId + "&id_product=" + productDetailId;
        }
    }

    private void showViewPager() {
        this.productViewPager.setAdapter ( new ProductDetailViewPagerAdapter ( this, this.eoAllProductData ) );
        this.circleIndicator.setViewPager ( productViewPager );
        final float density = getResources ( ).getDisplayMetrics ( ).density;
        this.circleIndicator.setRadius ( 5 * density );
        NUM_PAGES = eoAllProductData.getAssociations ( ).getImages ( ) == null ? 0 : eoAllProductData.getAssociations ( ).getImages ( ).size ( );

        if(NUM_PAGES == 0) {
            this.noImageAvailable.setVisibility ( View.VISIBLE );
            this.productViewPager.setVisibility ( View.GONE );
        }

        //TODO Auto start of viewpager
        final Handler handler = new Handler ( );
        final Runnable Update = new Runnable ( ) {
            public void run() {
                if(currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                productViewPager.setCurrentItem ( currentPage++, true );
            }
        };

        Timer swipeTimer = new Timer ( );
        swipeTimer.schedule ( new TimerTask ( ) {
            @Override
            public void run() {
                handler.post ( Update );
            }
        }, 2000, 2000 );

        // TODO Pager listener over indicator
        this.circleIndicator.setOnPageChangeListener ( new ViewPager.OnPageChangeListener ( ) {
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId ( )) {
            case R.id.iv_back_arrow:
                this.finish ( );
                break;
            case R.id.iv_cart_icon:
                Intent addToCartIntent = new Intent ( ProductDetailsActivity.this, CartActivity.class );
                if(!ObjectUtil.isEmpty ( addToCartResultArrayList )) {
                    Bundle bundle = new Bundle ( );
                    bundle.putSerializable ( "addToCartList", addToCartResultArrayList );
                    addToCartIntent.putExtra ( "bundleList", bundle );
                }
                startActivity ( addToCartIntent );
                break;
            case R.id.tv_view_all_comments:
                Intent commentIntent = new Intent ( ProductDetailsActivity.this, ProductCommentsActivity.class );
                commentIntent.putExtra ( "commentsList", eoProductCommentList );
                startActivity ( commentIntent );
                break;
            case R.id.textViewAddToCart:
                if(!loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {
                    Toast.makeText ( ProductDetailsActivity.this, "Please login first to add product in cart", Toast.LENGTH_SHORT ).show ( );
                } else {
                    //TODO add to cart api from here
                    this.addToCartApi ( selectedCurrencyId, selectedLangId, Integer.parseInt ( productDetailId ), Integer.parseInt ( loginSignupPreference.getString ( Constants.SELECTED_CUSTOMER_ID, "" ) ) );
                }
                break;
            case R.id.font_wishlist:
                if(!loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {
                    Toast.makeText ( ProductDetailsActivity.this, "Please login first to add your wishlist", Toast.LENGTH_SHORT ).show ( );
                } else {
                    this.addProductToWishlist ( loginSignupPreference.getString ( Constants.SELECTED_CUSTOMER_ID, "" ),
                            eoAllProductData.getName ( ), eoAllProductData.getId ( ), 1, 0 );
                }
                break;
            case R.id.product_video_view:
                this.playVideoForProduct ( );
                break;
            case R.id.tv_write_review:
                this.writeCommentForProduct ( );
                break;
            case R.id.font_wishlist_fill:
                if(!ObjectUtil.isEmpty ( wishlistId )) {
                    this.removeProductToWishlist ( wishlistId );
                }
                break;
            case R.id.textViewGoToCart:
                Intent goToCartIntent = new Intent ( ProductDetailsActivity.this, CartActivity.class );
                if(!ObjectUtil.isEmpty ( addToCartResultArrayList )) {
                    Bundle bundle = new Bundle ( );
                    bundle.putSerializable ( "addToCartList", addToCartResultArrayList );
                    goToCartIntent.putExtra ( "bundleList", bundle );
                }
                startActivity ( goToCartIntent );
                break;
        }
    }

    private void addToCartApi(int currencyId, int langId, int productId, int customerId) {
        progress.showProgressBar ( );
        this.apiInterface.addProductInCart ( currencyId, langId, productId, 1, customerId, 1 ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                //progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "CartInsertApi" );
                        EOAddToCartObject eoAddToCartObject = JsonParser.getInstance ( ).getObject ( EOAddToCartObject.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoAddToCartObject )) {
                            if(eoAddToCartObject.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                Toast.makeText ( ProductDetailsActivity.this, "" + eoAddToCartObject.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                                textViewAddToCart.setVisibility ( View.GONE );
                                textViewGoToCart.setVisibility ( View.VISIBLE );
                                if(selectedCustomerId != null) {
                                    showDataInCart ( selectedCustomerId );
                                }
                            } else {
                                textViewAddToCart.setVisibility ( View.VISIBLE );
                                textViewGoToCart.setVisibility ( View.GONE );
                                Toast.makeText ( ProductDetailsActivity.this, "" + eoAddToCartObject.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
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
                    new GlobalAlertDialog ( ProductDetailsActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }

    private void addProductToWishlist(String customerId, String productName, int productId, int quantity, int product_attribute) {
        progress.showProgressBar ( );
        this.apiInterface.addProductToWishlist ( customerId, productName, productId, quantity, product_attribute ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "AddWishlistApi" );
                        EOProductAddWishlist productAddWishlist = JsonParser.getInstance ( ).getObject ( EOProductAddWishlist.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( productAddWishlist )) {
                            if(productAddWishlist.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                font_wishlist.setVisibility ( View.GONE );
                                font_wishlist_fill.setVisibility ( View.VISIBLE );
                                //TODO refresh this page to remove wishlist
                                getProductDetailVideoWishlistData ( );
                                Toast.makeText ( ProductDetailsActivity.this, "" + productAddWishlist.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
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
                    new GlobalAlertDialog ( ProductDetailsActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }

    private void removeProductToWishlist(String wishlistId) {
        progress.showProgressBar ( );
        this.apiInterface.removeProductToWishlist ( wishlistId ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "RemoveWishlistApi" );
                        EOProductRemoveWishlist eoProductRemoveWishlist = JsonParser.getInstance ( ).getObject ( EOProductRemoveWishlist.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoProductRemoveWishlist )) {
                            if(eoProductRemoveWishlist.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                font_wishlist_fill.setVisibility ( View.GONE );
                                font_wishlist.setVisibility ( View.VISIBLE );
                                //TODO refresh this page to remove wishlist
                                getProductDetailVideoWishlistData ( );
                                Toast.makeText ( ProductDetailsActivity.this, "" + eoProductRemoveWishlist.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
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
                    new GlobalAlertDialog ( ProductDetailsActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }

    private void playVideoForProduct() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );
        final AlertDialog alertDialog = alertDialogBuilder.create ( );
        View dialogView = LayoutInflater.from ( this ).inflate ( R.layout.dialog_product_video, null );
        alertDialogBuilder.setView ( dialogView );
        alertDialog.show ( );
        //TODO set layout direction on language change
        if(LANGUAGE_ARABIC.equals ( LocalizationHelper.getLanguage ( this ) )) {
            getWindow ( ).getDecorView ( ).setLayoutDirection ( View.LAYOUT_DIRECTION_RTL );
        } else {
            getWindow ( ).getDecorView ( ).setLayoutDirection ( View.LAYOUT_DIRECTION_LTR );
        }
        alertDialog.getWindow ( ).setContentView ( dialogView );

        BUniqueTextView product_video_title = dialogView.findViewById ( R.id.product_video_title );
        FontAwesomeIcon font_cross = dialogView.findViewById ( R.id.font_cross );
        WebView dialog_product_video = dialogView.findViewById ( R.id.dialog_product_video );

        product_video_title.setText ( video_title );
        dialog_product_video.setWebChromeClient ( new WebChromeClient ( ) );
        WebSettings webSettings = dialog_product_video.getSettings ( );
        webSettings.setJavaScriptEnabled ( true );
        webSettings.setSupportZoom ( true );
        webSettings.setBuiltInZoomControls ( true );
        webSettings.setDisplayZoomControls ( false );
        dialog_product_video.setWebViewClient ( new WebViewClient ( ) );
        webSettings.setLoadWithOverviewMode ( true );
        webSettings.setUseWideViewPort ( true );
        dialog_product_video.loadUrl ( videoUrl );
        dialog_product_video.getSettings ( ).setMediaPlaybackRequiresUserGesture ( false );

        font_cross.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss ( );
            }
        } );
    }

    private void writeCommentForProduct() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( this );

        View dialogView = LayoutInflater.from ( this ).inflate ( R.layout.dialog_product_comment, null );

        //TODO set layout direction on language change
        if(LANGUAGE_ARABIC.equals ( LocalizationHelper.getLanguage ( this ) )) {
            getWindow ( ).getDecorView ( ).setLayoutDirection ( View.LAYOUT_DIRECTION_RTL );
        } else {
            getWindow ( ).getDecorView ( ).setLayoutDirection ( View.LAYOUT_DIRECTION_LTR );
        }

        alertDialogBuilder.setView ( dialogView );
        final AlertDialog alertDialog = alertDialogBuilder.create ( );

        alertDialog.getWindow ( ).setContentView ( dialogView );
        alertDialog.show ( );

        FontAwesomeIcon font_cross = dialogView.findViewById ( R.id.font_cross );
        final BUniqueEditText et_review_title = dialogView.findViewById ( R.id.et_review_title );
        final BUniqueEditText et_customer_name = dialogView.findViewById ( R.id.et_customer_name );
        final BUniqueEditText et_comment = dialogView.findViewById ( R.id.et_comment );
        BUniqueTextView tv_cancel = dialogView.findViewById ( R.id.tv_cancel );
        BUniqueTextView tv_submit = dialogView.findViewById ( R.id.tv_submit );
        RatingBar reviewRatingBar = dialogView.findViewById ( R.id.reviewRatingBar );

        final int[] ratingValue = new int[ 1 ];
        RatingBar.OnRatingBarChangeListener onRatingBarChangeListener = new RatingBar.OnRatingBarChangeListener ( ) {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue[ 0 ] = (int) ratingBar.getRating ( );
            }
        };
        reviewRatingBar.setOnRatingBarChangeListener ( onRatingBarChangeListener );

        tv_submit.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                if(ObjectUtil.isEmpty ( et_review_title.getText ( ).toString ( ) )) {
                    Toast.makeText ( ProductDetailsActivity.this, "Please enter product title", Toast.LENGTH_SHORT ).show ( );
                } else if(ObjectUtil.isEmpty ( et_customer_name.getText ( ).toString ( ) )) {
                    Toast.makeText ( ProductDetailsActivity.this, "Please enter your name", Toast.LENGTH_SHORT ).show ( );
                } else if(ObjectUtil.isEmpty ( et_comment.getText ( ).toString ( ) )) {
                    Toast.makeText ( ProductDetailsActivity.this, "Please enter product review", Toast.LENGTH_SHORT ).show ( );
                } else if(!loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {
                    Toast.makeText ( ProductDetailsActivity.this, "Please login first to write review", Toast.LENGTH_SHORT ).show ( );
                } else {
                    sendReviewForProduct ( loginSignupPreference.getString ( Constants.SELECTED_CUSTOMER_ID, "" ), et_customer_name.getText ( ).toString ( ),
                            eoAllProductData.getId ( ), ratingValue[ 0 ], et_review_title.getText ( ).toString ( ), et_comment.getText ( ).toString ( ) );
                    alertDialog.dismiss ( );
                }
            }
        } );

        tv_cancel.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss ( );
            }
        } );

        font_cross.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss ( );
            }
        } );
    }

    private void sendReviewForProduct(String customerId, String customerName, int productId, int grade, String title, String description) {
        progress.showProgressBar ( );
        this.apiInterface.sendReviewForProduct ( customerId, customerName, productId, grade, title, description ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "AddCommentApi" );
                        EOProductReview eoProductReview = JsonParser.getInstance ( ).getObject ( EOProductReview.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoProductReview )) {
                            if(eoProductReview.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                new GlobalAlertDialog ( ProductDetailsActivity.this, false, false ) {
                                    @Override
                                    public void onConfirmation() {
                                        super.onConfirmation ( );
                                    }
                                }.show ( R.string.your_comment_has_been_added_and_will_be_avaialable );
                            } else {
                                Toast.makeText ( ProductDetailsActivity.this, "Wrong format : " + eoProductReview.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
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
                    new GlobalAlertDialog ( ProductDetailsActivity.this, false, true ) {
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
    protected void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }

}
