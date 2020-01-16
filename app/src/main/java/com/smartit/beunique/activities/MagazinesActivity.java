package com.smartit.beunique.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.adapters.MagazineCategoryAdapter;
import com.smartit.beunique.adapters.MagazinesAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueEditText;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.FontAwesomeIcon;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.magazine.EOMagazineArticle;
import com.smartit.beunique.entity.magazine.EOMagazineCategory;
import com.smartit.beunique.entity.magazine.EOMagazineList;
import com.smartit.beunique.networking.JsonParser;
import com.smartit.beunique.networking.RestClient;
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
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class MagazinesActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private RecyclerView articlesRecyclerView, category_magazine_recyclerView;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private MagazinesAdapter magazinesAdapter;
    private FontAwesomeIcon icon_grid, icon_list, icon_instagram, icon_twitter, icon_youtube;
    private BUniqueEditText et_joinus_email_id;
    private BUniqueTextView tv_send_email;
    private int layout_grid_row = R.layout.magazines_row_grid;
    private ArrayList <EOMagazineArticle> eoMagazineArrayList = new ArrayList <> ( );
    private ArrayList <EOMagazineCategory> magazineCategoryArrayList = new ArrayList <> ( );

    private String blogCategoryId;
    private boolean isShowCategoryId;

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

        setContentView ( R.layout.activity_magazines );

        if(!ObjectUtil.isEmpty ( this.getIntent ( ).getIntExtra ( "magazineBlogCategoryId", 0 ) ) || !ObjectUtil.isEmpty ( this.getIntent ( ).getBooleanExtra ( "isShowCategoryId", false ) )) {
            this.blogCategoryId = (String) this.getIntent ( ).getStringExtra ( "magazineBlogCategoryId" );
            this.isShowCategoryId = this.getIntent ( ).getBooleanExtra ( "isShowCategoryId", false );
        }

        this.initView ( );
        this.setOnClickListener ( );
        this.getMagazineData ( );
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
        this.articlesRecyclerView = this.findViewById ( R.id.articlesRecyclerView );
        this.category_magazine_recyclerView = this.findViewById ( R.id.category_magazine_recyclerView );
        this.icon_grid = this.findViewById ( R.id.icon_grid );
        this.icon_list = this.findViewById ( R.id.icon_list );
        this.et_joinus_email_id = this.findViewById ( R.id.et_joinus_email_id );
        this.tv_send_email = this.findViewById ( R.id.tv_send_email );
        this.icon_instagram = this.findViewById ( R.id.icon_instagram );
        this.icon_twitter = this.findViewById ( R.id.icon_twitter );
        this.icon_youtube = this.findViewById ( R.id.icon_youtube );

    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
        this.icon_grid.setOnClickListener ( this );
        this.icon_list.setOnClickListener ( this );
        this.icon_instagram.setOnClickListener ( this );
        this.icon_twitter.setOnClickListener ( this );
        this.icon_youtube.setOnClickListener ( this );
    }

    private void getMagazineData() {
        progress.showProgressBar ( );
        this.apiInterface.getMagazinesApi ( magazinesCategoryUrl ( ) ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "BlogApi" );
                        EOMagazineList eoMagazineList = JsonParser.getInstance ( ).getObject ( EOMagazineList.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoMagazineList )) {
                            if(eoMagazineList.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                if(!ObjectUtil.isEmpty ( eoMagazineList.getPayload ( ) )) {
                                    magazineCategoryArrayList.addAll ( eoMagazineList.getPayload ( ).getCategory ( ) );
                                    category_magazine_recyclerView.setAdapter ( new MagazineCategoryAdapter ( MagazinesActivity.this, magazineCategoryArrayList ) );

                                    eoMagazineArrayList.addAll ( eoMagazineList.getPayload ( ).getArticle ( ) );
                                    articlesRecyclerView.setHasFixedSize ( true );
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager ( MagazinesActivity.this, 2 );
                                    articlesRecyclerView.setLayoutManager ( gridLayoutManager );
                                    icon_grid.setTextColor ( getResources ( ).getColor ( android.R.color.holo_green_dark ) );
                                    magazinesAdapter = new MagazinesAdapter ( MagazinesActivity.this, eoMagazineArrayList, layout_grid_row );
                                    articlesRecyclerView.setAdapter ( magazinesAdapter );

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
                    //Toast.makeText ( MagazinesActivity.this, "Server is Under Maintenance .", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( MagazinesActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }

    private String magazinesCategoryUrl() {
        if(this.isShowCategoryId) {
            return "webservice/blog/blogs.php?id_lang=" + selectedLangId + "&id_category=" + blogCategoryId;
        } else {
            return "webservice/blog/blogs.php?id_lang=" + selectedLangId;
        }
    }

    private void dataToView() {
        this.iv_back_arrow.setImageResource ( R.drawable.back_arrow_icon );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );
        this.iv_cart_icon.setVisibility ( View.GONE );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId ( )) {
            case R.id.iv_back_arrow:
                this.finish ( );
                break;
            case R.id.icon_grid:
                this.icon_grid.setTextColor ( getResources ( ).getColor ( android.R.color.holo_green_dark ) );
                this.icon_list.setTextColor ( getResources ( ).getColor ( android.R.color.white ) );
                GridLayoutManager gridLayoutManager = new GridLayoutManager ( MagazinesActivity.this, 2 );
                articlesRecyclerView.setLayoutManager ( gridLayoutManager );
                magazinesAdapter = new MagazinesAdapter ( MagazinesActivity.this, eoMagazineArrayList, layout_grid_row );
                articlesRecyclerView.setAdapter ( magazinesAdapter );
                break;
            case R.id.icon_list:
                this.icon_list.setTextColor ( getResources ( ).getColor ( android.R.color.holo_green_dark ) );
                this.icon_grid.setTextColor ( getResources ( ).getColor ( android.R.color.white ) );
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager ( MagazinesActivity.this, LinearLayoutManager.VERTICAL, false );
                articlesRecyclerView.setLayoutManager ( linearLayoutManager );
                magazinesAdapter = new MagazinesAdapter ( MagazinesActivity.this, eoMagazineArrayList, R.layout.magazine_row_list );
                articlesRecyclerView.setAdapter ( magazinesAdapter );
                break;
            case R.id.tv_send_email:

                break;
            case R.id.icon_instagram:
                this.goToInstagramPage ( );
                break;
            case R.id.icon_twitter:
                this.goToTwitterPage ( );
                break;
            case R.id.icon_youtube:
                this.goToYoutubePage ( );
                break;
        }
    }

    public void goToInstagramPage() {
        Uri uri = Uri.parse ( "https://www.instagram.com/bu_beunique/" );
        Intent intent = new Intent ( Intent.ACTION_VIEW, uri );
        intent.setPackage ( "com.instagram.android" );
        try {
            startActivity ( intent );
        } catch (ActivityNotFoundException e) {
            e.printStackTrace ( );
        }
    }

    public void goToTwitterPage() {
        Uri uri = Uri.parse ( "https://twitter.com/Bu_BeUniquee" );
        Intent intent = new Intent ( Intent.ACTION_VIEW, uri );
        intent.setPackage ( "com.twitter.android" );
        try {
            startActivity ( intent );
        } catch (ActivityNotFoundException e) {
            e.printStackTrace ( );
        }
    }

    public void goToYoutubePage() {
        Uri uri = Uri.parse ( "https://www.youtube.com/+beuniquesa" );
        Intent intent = new Intent ( Intent.ACTION_VIEW, uri );
        intent.setPackage ( "com.google.android.youtube" );
        try {
            startActivity ( intent );
        } catch (ActivityNotFoundException e) {
            e.printStackTrace ( );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }

}
