package com.smartit.beunique.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.smartit.beunique.R;
import com.smartit.beunique.adapters.ProductCommentsAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.allproducts.EOProductComment;
import com.smartit.beunique.util.DividerItemDecoration;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class ProductCommentsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences languageCurrencyPreferences;
    private int selectedLangId;
    private RecyclerView productCommentsRecyclerView;
    private ProductCommentsAdapter productCommentsAdapter;
    private ArrayList <EOProductComment> eoProductCommentList;
    private BUniqueTextView tv_no_data;

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

        setContentView ( R.layout.activity_product_comments );

        if(!ObjectUtil.isEmpty ( this.getIntent ( ).getSerializableExtra ( "commentsList" ) )) {
            this.eoProductCommentList = (ArrayList <EOProductComment>) this.getIntent ( ).getSerializableExtra ( "commentsList" );
        }

        this.initView ( );
        this.setOnClickListener ( );
        this.dataToView ( );
    }

    private void initView() {
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );
        this.languageCurrencyPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.languageCurrencyPreferences.getInt ( SELECTED_LANG_ID, 0 );

        //here toolbar layout view
        this.toolbarLayout = this.findViewById ( R.id.toolbarLayout );
        this.setSupportActionBar ( this.toolbarLayout );
        this.iv_back_arrow = this.toolbarLayout.findViewById ( R.id.iv_back_arrow );
        this.iv_title_logo = this.toolbarLayout.findViewById ( R.id.iv_title_logo );
        this.iv_cart_icon = this.toolbarLayout.findViewById ( R.id.iv_cart_icon );

        this.productCommentsRecyclerView = this.findViewById ( R.id.productCommentsRecyclerView );
        this.tv_no_data = this.findViewById ( R.id.tv_no_data );
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
    }

    private void dataToView() {
        this.iv_back_arrow.setImageResource ( R.drawable.back_arrow_icon );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );

        if(ObjectUtil.isEmpty ( this.eoProductCommentList )) {
            this.tv_no_data.setVisibility ( View.VISIBLE );
            this.productCommentsRecyclerView.setVisibility ( View.GONE );
        } else {
            this.tv_no_data.setVisibility ( View.GONE );
            this.productCommentsAdapter = new ProductCommentsAdapter ( this, this.eoProductCommentList );
            this.productCommentsRecyclerView.setHasFixedSize ( true );
            this.productCommentsRecyclerView.addItemDecoration ( new DividerItemDecoration ( this ) );
            this.productCommentsRecyclerView.setLayoutManager ( new LinearLayoutManager ( this, LinearLayoutManager.VERTICAL, false ) );
            this.productCommentsRecyclerView.setAdapter ( this.productCommentsAdapter );
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId ( )) {
            case R.id.iv_back_arrow:
                this.finish ( );
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }


}
