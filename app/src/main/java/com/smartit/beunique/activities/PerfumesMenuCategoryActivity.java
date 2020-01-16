package com.smartit.beunique.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.smartit.beunique.R;
import com.smartit.beunique.adapters.PerfumesCategoryAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.drawerMenu.EOPerfumesSubMenu;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class PerfumesMenuCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private RecyclerView recyclerViewMenuCategory;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private int menuCategoryId;
    private ImageView iv_category_banner;

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

        setContentView ( R.layout.activity_menu_category );

        if(!ObjectUtil.isEmpty ( this.getIntent ( ).getIntExtra ( "menuCategoryId", 0 ) )) {
            this.menuCategoryId = this.getIntent ( ).getIntExtra ( "menuCategoryId", 0 );
        }

        this.initView ( );
        this.setOnClickListener ( );
        this.dataToView ( );
        this.getCategoriesFromApi ( );
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
        this.recyclerViewMenuCategory = this.findViewById ( R.id.recyclerViewMenuCategory );

        this.iv_category_banner = this.findViewById ( R.id.iv_category_banner );
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
        this.iv_category_banner.setOnClickListener ( this );
    }

    private void getCategoriesFromApi() {
        progress.showProgressBar ( );
        apiInterface.getPerfumesSubMenus ( "api/categories/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&display=[id,id_parent,name]&filter[id_parent]=" + menuCategoryId + "&filter[active]=1&language=" + selectedLangId ).enqueue ( new Callback <EOPerfumesSubMenu> ( ) {
            @Override
            public void onResponse(Call <EOPerfumesSubMenu> call, Response <EOPerfumesSubMenu> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(!ObjectUtil.isEmpty ( response.body ( ).getCategories ( ) )) {
                        PerfumesCategoryAdapter perfumesCategoryAdapter = new PerfumesCategoryAdapter ( PerfumesMenuCategoryActivity.this, response.body ( ).getCategories ( ) );
                        recyclerViewMenuCategory.setHasFixedSize ( true );
                        recyclerViewMenuCategory.setLayoutManager ( new GridLayoutManager ( PerfumesMenuCategoryActivity.this, 2 ) );
                        recyclerViewMenuCategory.setItemAnimator ( new DefaultItemAnimator ( ) );
                        recyclerViewMenuCategory.setAdapter ( perfumesCategoryAdapter );
                    }
                }
            }

            @Override
            public void onFailure(Call <EOPerfumesSubMenu> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    new GlobalAlertDialog ( PerfumesMenuCategoryActivity.this, false, true ) {
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


        //TODO set here all 5 banners from here
        switch (menuCategoryId) {
            case 335:
                iv_category_banner.setImageResource ( R.drawable.ic_women_perfume );
                break;
            case 339:
                iv_category_banner.setImageResource ( R.drawable.ic_men_perfume );
                break;
            case 343:
                iv_category_banner.setImageResource ( R.drawable.ic_notes_perfume );
                break;
            case 365:
                iv_category_banner.setImageResource ( R.drawable.ic_moods_perfume );
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId ( )) {
            case R.id.iv_back_arrow:
                this.finish ( );
                break;
            case R.id.iv_category_banner:
                Intent productSubCategoryIntent = new Intent ( this, PerfumesMenuSubCategoryActivity.class );
                productSubCategoryIntent.putExtra ( "menuCategoryId", menuCategoryId );
                startActivity ( productSubCategoryIntent );
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }

}
