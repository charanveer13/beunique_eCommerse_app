package com.smartit.beunique.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.smartit.beunique.R;
import com.smartit.beunique.adapters.CartAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueEditText;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.cart.EOShowCartResult;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_CONVERSION_RATE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_SIGN;
import static com.smartit.beunique.util.Constants.SELECTED_CUSTOMER_ID;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class CartActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private String selectedCurrencySign, selectedConversionRate;
    private SessionSecuredPreferences loginSignupPreference;

    private RecyclerView addCartRecyclerView;
    private BUniqueEditText et_coupon_code;
    private BUniqueTextView tv_apply_coupon, tv_product_price, tv_shipping_price, tv_vat_price, tv_total_price,
            tv_checkout;
    private static NestedScrollView layout_product_items;
    private static BUniqueTextView tv_no_items_in_bag;
    private CartAdapter addToCartAdapter;
    private String customerId;
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

        setContentView ( R.layout.activity_cart );

        Bundle bundle = (!ObjectUtil.isEmpty ( this.getIntent ( ).getBundleExtra ( "bundleList" ) )) ? this.getIntent ( ).getBundleExtra ( "bundleList" ) : new Bundle ( );
        if(!ObjectUtil.isEmpty ( bundle.getSerializable ( "addToCartList" ) )) {
            this.addToCartResultArrayList = (ArrayList <EOShowCartResult>) bundle.getSerializable ( "addToCartList" );
        }

        this.initView ( );
        this.setOnClickListener ( );
        this.dataToView ( );
    }


    private void initView() {
        this.progress = new GlobalProgressDialog ( this );
        this.apiInterface = RestClient.getClient ( );
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );
        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.selectedCurrencySign = this.securedPreferences.getString ( SELECTED_CURRENCY_SIGN, "" );
        this.selectedConversionRate = this.securedPreferences.getString ( SELECTED_CURRENCY_CONVERSION_RATE, "" );
        this.loginSignupPreference = ApplicationHelper.application ( ).loginPreferences ( LOGIN_SIGNUP_PREFERENCE );
        this.customerId = this.loginSignupPreference.getString ( SELECTED_CUSTOMER_ID, "" );

        //here toolbar layout view
        this.toolbarLayout = this.findViewById ( R.id.toolbarLayout );
        this.setSupportActionBar ( this.toolbarLayout );
        this.iv_back_arrow = this.toolbarLayout.findViewById ( R.id.iv_back_arrow );
        this.iv_title_logo = this.toolbarLayout.findViewById ( R.id.iv_title_logo );
        this.iv_cart_icon = this.toolbarLayout.findViewById ( R.id.iv_cart_icon );

        this.addCartRecyclerView = this.findViewById ( R.id.addCartRecyclerView );
        this.et_coupon_code = this.findViewById ( R.id.et_coupon_code );
        this.tv_apply_coupon = this.findViewById ( R.id.tv_apply_coupon );
        this.tv_product_price = this.findViewById ( R.id.tv_product_price );
        this.tv_shipping_price = this.findViewById ( R.id.tv_shipping_price );
        this.tv_vat_price = this.findViewById ( R.id.tv_vat_price );
        this.tv_total_price = this.findViewById ( R.id.tv_total_price );
        this.tv_checkout = this.findViewById ( R.id.tv_checkout );
        this.layout_product_items = this.findViewById ( R.id.layout_product_items );
        this.tv_no_items_in_bag = this.findViewById ( R.id.tv_no_items_in_bag );

        //TODO check here addToCartResultArrayList is empty or not
        if(!ObjectUtil.isEmpty ( addToCartResultArrayList )) {
            this.layout_product_items.setVisibility ( View.VISIBLE );
            this.tv_no_items_in_bag.setVisibility ( View.GONE );
            this.addCartRecyclerView.setHasFixedSize ( true );
            this.addCartRecyclerView.setLayoutManager ( new LinearLayoutManager ( CartActivity.this, LinearLayoutManager.VERTICAL, false ) );
            this.addToCartAdapter = new CartAdapter ( this, this.addToCartResultArrayList );
            this.addCartRecyclerView.setAdapter ( addToCartAdapter );
        }
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
    }

    private void dataToView() {
        this.iv_back_arrow.setImageResource ( R.drawable.back_arrow_icon );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );
        this.iv_cart_icon.setVisibility ( View.GONE );
    }

    public static void noItemsInCart() {
        tv_no_items_in_bag.setVisibility ( View.VISIBLE );
        layout_product_items.setVisibility ( View.GONE );
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
}
