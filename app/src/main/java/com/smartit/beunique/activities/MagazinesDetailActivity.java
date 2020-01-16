package com.smartit.beunique.activities;

import com.smartit.beunique.R;
import com.smartit.beunique.adapters.MagazineRelatedArticleAdapter;
import com.smartit.beunique.adapters.MagazineRelatedCommentsAdapter;
import com.smartit.beunique.adapters.MagazineRelatedProductAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueEditText;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.allproducts.EOAllProductData;
import com.smartit.beunique.entity.dashboardCategory.EOCategoryProducts;
import com.smartit.beunique.entity.magazine.EOMagazineAddComment;
import com.smartit.beunique.entity.magazine.EOMagazineDetailPayload;
import com.smartit.beunique.entity.magazine.EOMagazineDetails;
import com.smartit.beunique.networking.JsonParser;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.Constants;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;
import com.smartit.beunique.util.UIUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.BASE_URL;
import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class MagazinesDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon, iv_article, iv_share;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private RecyclerView recyclerView_related_products, recyclerView_related_articles, comments_recycler_view;
    private BUniqueTextView tv_article_title, tv_time_minutes, tv_total_comments, tv_total_counter, tv_magazine_content, tv_ok_button;
    private WebView videoView;
    private BUniqueEditText et_name, et_email, et_comment;
    private LinearLayout layout_video_view, layout_related_products, layout_related_articles, layout_comments;
    private EOMagazineDetailPayload eoMagazineDetailPayload;

    private ArrayList <EOAllProductData> relatedProductsArray = new ArrayList <> ( );
    private MagazineRelatedProductAdapter relatedProductAdapter;
    private MagazineRelatedArticleAdapter relatedArticleAdapter;
    private ArrayList <EOMagazineDetailPayload> relatedArticlesArray = new ArrayList <> ( );

    private String magazineBlogObjectId;
    private SessionSecuredPreferences loginSignupPreference;
    String firstName, lastName, emailId, customerId;

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

        setContentView ( R.layout.activity_magazines_detail );

        if(!ObjectUtil.isEmpty ( this.getIntent ( ).getSerializableExtra ( "magazineBlogObjectId" ) )) {
            this.magazineBlogObjectId = (String) this.getIntent ( ).getSerializableExtra ( "magazineBlogObjectId" );
        }

        this.initView ( );
        this.setOnClickListener ( );
        this.getMagazineDetails ( );
        this.dataToView ( );
    }

    private void initView() {
        this.progress = new GlobalProgressDialog ( this );
        this.apiInterface = RestClient.getClient ( );
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );
        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.loginSignupPreference = ApplicationHelper.application ( ).loginPreferences ( LOGIN_SIGNUP_PREFERENCE );

        //here toolbar layout view
        this.toolbarLayout = this.findViewById ( R.id.toolbarLayout );
        this.setSupportActionBar ( this.toolbarLayout );
        this.iv_back_arrow = this.toolbarLayout.findViewById ( R.id.iv_back_arrow );
        this.iv_title_logo = this.toolbarLayout.findViewById ( R.id.iv_title_logo );
        this.iv_cart_icon = this.toolbarLayout.findViewById ( R.id.iv_cart_icon );

        this.recyclerView_related_products = this.findViewById ( R.id.recyclerView_related_products );
        this.recyclerView_related_articles = this.findViewById ( R.id.recyclerView_related_articles );
        this.comments_recycler_view = this.findViewById ( R.id.comments_recycler_view );

        this.iv_article = this.findViewById ( R.id.iv_article );
        this.iv_share = this.findViewById ( R.id.iv_share );
        this.tv_article_title = this.findViewById ( R.id.tv_article_title );
        this.tv_time_minutes = this.findViewById ( R.id.tv_time_minutes );
        this.tv_total_comments = this.findViewById ( R.id.tv_total_comments );
        this.tv_total_counter = this.findViewById ( R.id.tv_total_counter );
        this.tv_magazine_content = this.findViewById ( R.id.tv_magazine_content );
        this.videoView = this.findViewById ( R.id.videoView );
        this.et_name = this.findViewById ( R.id.et_name );
        this.et_email = this.findViewById ( R.id.et_email );
        this.et_comment = this.findViewById ( R.id.et_comment );
        this.tv_ok_button = this.findViewById ( R.id.tv_ok_button );
        this.layout_video_view = this.findViewById ( R.id.layout_video_view );
        this.layout_related_products = this.findViewById ( R.id.layout_related_products );
        this.layout_related_articles = this.findViewById ( R.id.layout_related_articles );
        this.layout_comments = this.findViewById ( R.id.layout_comments );

        //TODO load here related products list
        relatedProductAdapter = new MagazineRelatedProductAdapter ( MagazinesDetailActivity.this, relatedProductsArray );
        recyclerView_related_products.setHasFixedSize ( true );
        recyclerView_related_products.setAdapter ( relatedProductAdapter );

        //TODO load here related article list
        relatedArticleAdapter = new MagazineRelatedArticleAdapter ( MagazinesDetailActivity.this, this.relatedArticlesArray );
        recyclerView_related_articles.setHasFixedSize ( true );
        recyclerView_related_articles.setAdapter ( relatedArticleAdapter );

    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
        this.tv_ok_button.setOnClickListener ( this );
        this.iv_share.setOnClickListener ( this );
    }

    private void getMagazineDetails() {
        progress.showProgressBar ( );
        this.apiInterface.getMagazineDetailsApi ( "webservice/blog/blog.php?id=" + magazineBlogObjectId + "&id_lang=" + selectedLangId ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "BlogApi" );
                        EOMagazineDetails eoMagazineDetails = JsonParser.getInstance ( ).getObject ( EOMagazineDetails.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoMagazineDetails )) {
                            if(eoMagazineDetails.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                if(!ObjectUtil.isEmpty ( eoMagazineDetails.getPayload ( ) )) {
                                    eoMagazineDetailPayload = eoMagazineDetails.getPayload ( );

                                    loadImages ( eoMagazineDetailPayload.getImageUrl ( ), iv_article );
                                    tv_article_title.setText ( eoMagazineDetailPayload.getName ( ) );
                                    tv_magazine_content.setText ( UIUtil.fromHtml ( eoMagazineDetailPayload.getContent ( ) ) );
                                    tv_total_comments.setText ( String.valueOf ( eoMagazineDetailPayload.getTotalComments ( ) ).concat ( " " ) );
                                    tv_total_counter.setText ( eoMagazineDetailPayload.getCounter ( ) );


                                    //TODO check firstName, lastName, emailId in exists in shared preference or not
                                    if(loginSignupPreference.contains ( Constants.SELECTED_FIRST_NAME ) && loginSignupPreference.contains ( Constants.SELECTED_LAST_NAME )
                                            && loginSignupPreference.contains ( Constants.SELECTED_EMAIL_ID ) && loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {

                                        firstName = loginSignupPreference.getString ( Constants.SELECTED_FIRST_NAME, "" );
                                        lastName = loginSignupPreference.getString ( Constants.SELECTED_LAST_NAME, "" );
                                        emailId = loginSignupPreference.getString ( Constants.SELECTED_EMAIL_ID, "" );
                                        customerId = loginSignupPreference.getString ( Constants.SELECTED_CUSTOMER_ID, "" );
                                        et_name.setText ( firstName.concat ( " " ).concat ( lastName ) );
                                        et_email.setText ( emailId );
                                    }

                                    if(!ObjectUtil.isEmpty ( eoMagazineDetailPayload.getDate ( ) )) {
                                        String[] timeStr = eoMagazineDetailPayload.getDate ( ).split ( "\\s+" );
                                        tv_time_minutes.setText ( (ObjectUtil.isEmpty ( timeStr[ 0 ] ) ? "yyyy-mm-dd" : timeStr[ 0 ]).concat ( " " ) );
                                    }

                                    if(!ObjectUtil.isEmpty ( eoMagazineDetailPayload.getVideo ( ) )) {
                                        layout_video_view.setVisibility ( View.VISIBLE );
                                        videoView.setWebChromeClient ( new WebChromeClient ( ) );
                                        WebSettings webSettings = videoView.getSettings ( );
                                        webSettings.setJavaScriptEnabled ( true );
                                        webSettings.setSupportZoom ( true );
                                        webSettings.setBuiltInZoomControls ( true );
                                        webSettings.setDisplayZoomControls ( false );
                                        videoView.loadUrl ( eoMagazineDetailPayload.getVideo ( ) );
                                    }

                                    //TODO load here related products list
                                    if(!ObjectUtil.isEmpty ( eoMagazineDetailPayload.getRelatedProduct ( ) )) {
                                        layout_related_products.setVisibility ( View.VISIBLE );
                                        for (String productId : eoMagazineDetailPayload.getRelatedProduct ( )) {
                                            getRelatedProducts ( productId );
                                        }
                                    }

                                    //TODO load here related articles list
                                    if(!ObjectUtil.isEmpty ( eoMagazineDetailPayload.getRelatedArticle ( ) )) {
                                        layout_related_articles.setVisibility ( View.VISIBLE );
                                        for (String articleId : eoMagazineDetailPayload.getRelatedArticle ( )) {
                                            getRelatedArticles ( articleId );
                                        }
                                    }

                                    //TODO load here comment layout
                                    if(!ObjectUtil.isEmpty ( eoMagazineDetailPayload.getAcceptComment ( ) ) || !ObjectUtil.isEmpty ( eoMagazineDetailPayload.getComments ( ) )) {
                                        if(eoMagazineDetailPayload.getAcceptComment ( ).equalsIgnoreCase ( "1" )) {
                                            layout_comments.setVisibility ( View.VISIBLE );

                                            MagazineRelatedCommentsAdapter magazineRelatedCommentsAdapter = new MagazineRelatedCommentsAdapter ( MagazinesDetailActivity.this, eoMagazineDetailPayload.getComments ( ) );
                                            comments_recycler_view.setHasFixedSize ( true );
                                            comments_recycler_view.setAdapter ( magazineRelatedCommentsAdapter );

                                        }
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
                    //Toast.makeText ( MagazinesDetailActivity.this, "Server is Under Maintenance ", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( MagazinesDetailActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }

    private void getRelatedProducts(String productId) {
        apiInterface.getNewArrivalProduct ( "api/products/" + productId + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV&output_format=JSON&language=" + selectedLangId ).enqueue ( new Callback <EOCategoryProducts> ( ) {
            @Override
            public void onResponse(Call <EOCategoryProducts> call, Response <EOCategoryProducts> response) {
                progress.hideProgressBar ( );
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(!ObjectUtil.isEmpty ( response.body ( ).getProduct ( ) )) {
                        relatedProductsArray.add ( response.body ( ).getProduct ( ) );
                        relatedProductAdapter.notifyDataSetChanged ( );
                    }
                }
            }

            @Override
            public void onFailure(Call <EOCategoryProducts> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    //Toast.makeText ( MagazinesDetailActivity.this, "Server is Under Maintenance ", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( MagazinesDetailActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }

    private void getRelatedArticles(String articleId) {
        this.apiInterface.getMagazineDetailsApi ( "webservice/blog/blog.php?id=" + articleId + "&id_lang=" + selectedLangId ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "BlogApi" );
                        EOMagazineDetails eoMagazineDetails = JsonParser.getInstance ( ).getObject ( EOMagazineDetails.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoMagazineDetails )) {
                            if(eoMagazineDetails.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                if(!ObjectUtil.isEmpty ( eoMagazineDetails.getPayload ( ) )) {
                                    relatedArticlesArray.add ( eoMagazineDetails.getPayload ( ) );
                                    relatedArticleAdapter.notifyDataSetChanged ( );
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
                    //Toast.makeText ( MagazinesDetailActivity.this, "Server is Under Maintenance ", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( MagazinesDetailActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }

        } );
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get ( )
                .load ( BASE_URL + imagePath )
                .error ( R.drawable.icon_no_image )
                .fit ( )
                .centerCrop ( )
                .into ( imageView );
    }

    private void dataToView() {
        this.iv_back_arrow.setImageResource ( R.drawable.back_arrow_icon );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );
        this.iv_cart_icon.setVisibility ( View.GONE );

        this.et_name.setEnabled ( false );
        this.et_email.setEnabled ( false );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId ( )) {
            case R.id.iv_back_arrow:
                this.finish ( );
                break;
            case R.id.tv_ok_button:
                this.sendCommentsToServer ( );
                break;
            case R.id.iv_share:
                Intent shareIntent = new Intent ( android.content.Intent.ACTION_SEND );
                shareIntent.setType ( "text/plain" );
                shareIntent.putExtra ( Intent.EXTRA_SUBJECT, "Insert Subject here" );
                String app_url = "https://play.google.com/store/apps/details?id=" + ApplicationHelper.application ( ).packageName ( );
                shareIntent.putExtra ( android.content.Intent.EXTRA_TEXT, app_url );
                startActivity ( Intent.createChooser ( shareIntent, "Share via" ) );
                break;

        }
    }

    private void sendCommentsToServer() {
        if(ObjectUtil.isEmpty ( et_comment.getText ( ) )) {
            Toast.makeText ( this, "Please write comment.", Toast.LENGTH_SHORT ).show ( );
        } else if(ObjectUtil.isEmpty ( customerId )) {
            Toast.makeText ( this, "Please login first to comment.", Toast.LENGTH_SHORT ).show ( );
        } else {
            sendComment ( );
        }
    }

    private void sendComment() {
        progress.showProgressBar ( );
        apiInterface.sendComment ( magazineBlogObjectId, customerId, firstName.concat ( lastName ), emailId, et_comment.getText ( ).toString ( ) ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, retrofit2.Response <ResponseBody> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "AddCommentApi" );
                        EOMagazineAddComment magazineAddComment = JsonParser.getInstance ( ).getObject ( EOMagazineAddComment.class, tempArray[ 1 ] );
                        if(magazineAddComment.getStatus ( ).equalsIgnoreCase ( "success" )) {
                            new GlobalAlertDialog ( MagazinesDetailActivity.this, false, false ) {
                                @Override
                                public void onDefault() {
                                    super.onConfirmation ( );
                                    et_comment.getText ( ).clear ( );
                                    MagazinesDetailActivity.this.finish ( );
                                }
                            }.show ( magazineAddComment.getMessage ( ) );
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
                    //Toast.makeText ( MagazinesDetailActivity.this, "Server is Under Maintenance ", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( MagazinesDetailActivity.this, false, true ) {
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
