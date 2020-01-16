package com.smartit.beunique.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.adapters.PointsSystemAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.pointsSystem.EOPointsSystem;
import com.smartit.beunique.entity.pointsSystem.EOPointsSystemList;
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

public class PointsSystemActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private RecyclerView pointsSystemRecyclerView;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private SessionSecuredPreferences loginSignupPreference;
    private BUniqueTextView tv_no_points, tv_total_points;
    private RelativeLayout layout_total_points;
    private ArrayList <EOPointsSystem> pointsSystemArrayList = new ArrayList <> ( );
    private int totalPoints;

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

        setContentView ( R.layout.activity_points_system );

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
        this.loginSignupPreference = ApplicationHelper.application ( ).loginPreferences ( LOGIN_SIGNUP_PREFERENCE );

        //here toolbar layout view
        this.toolbarLayout = this.findViewById ( R.id.toolbarLayout );
        this.setSupportActionBar ( this.toolbarLayout );
        this.iv_back_arrow = this.toolbarLayout.findViewById ( R.id.iv_back_arrow );
        this.iv_title_logo = this.toolbarLayout.findViewById ( R.id.iv_title_logo );
        this.iv_cart_icon = this.toolbarLayout.findViewById ( R.id.iv_cart_icon );
        this.pointsSystemRecyclerView = this.findViewById ( R.id.pointsSystemRecyclerView );
        this.tv_no_points = this.findViewById ( R.id.tv_no_points );
        this.layout_total_points = this.findViewById ( R.id.layout_total_points );
        this.tv_total_points = this.findViewById ( R.id.tv_total_points );

    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
    }

    private void dataToView() {
        this.iv_back_arrow.setImageResource ( R.drawable.back_arrow_icon );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );

        //TODO check user is log in or not
        this.checkCustomerId ( );
    }

    private void checkCustomerId() {
        if(!loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {
            new GlobalAlertDialog ( PointsSystemActivity.this, false, false ) {
                @Override
                public void onConfirmation() {
                    super.onConfirmation ( );
                }
            }.show ( R.string.please_login_first_to_see_your_points );
        } else {
            getAllPointsOfOrders ( loginSignupPreference.getString ( Constants.SELECTED_CUSTOMER_ID, "" ) );
        }
    }


    private void getAllPointsOfOrders(String customerId) {
        progress.showProgressBar ( );
        apiInterface.getAllPointsOfOrders ( "webservice/my-account/points.php?id_customer=" + customerId + "&id_lang=" + selectedLangId ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "PointsApi" );
                        EOPointsSystemList pointsSystemList = JsonParser.getInstance ( ).getObject ( EOPointsSystemList.class, tempArray[ 1 ] );

                        if(!ObjectUtil.isEmpty ( pointsSystemList )) {
                            if(pointsSystemList.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                tv_no_points.setVisibility ( View.GONE );
                                pointsSystemRecyclerView.setVisibility ( View.VISIBLE );
                                layout_total_points.setVisibility ( View.VISIBLE );
                                pointsSystemArrayList.addAll ( pointsSystemList.getPayload ( ) );
                                for (EOPointsSystem eoPointsSystem : pointsSystemArrayList) {
                                    totalPoints += Integer.parseInt ( eoPointsSystem.getPoints ( ) );
                                }
                                tv_total_points.setText ( String.valueOf ( totalPoints ) );

                                pointsSystemRecyclerView.setHasFixedSize ( true );
                                pointsSystemRecyclerView.setLayoutManager ( new LinearLayoutManager ( PointsSystemActivity.this, LinearLayoutManager.VERTICAL, false ) );
                                pointsSystemRecyclerView.setAdapter ( new PointsSystemAdapter ( PointsSystemActivity.this, pointsSystemArrayList ) );
                            } else {
                                progress.hideProgressBar ( );
                                tv_no_points.setVisibility ( View.VISIBLE );
                                pointsSystemRecyclerView.setVisibility ( View.GONE );
                                layout_total_points.setVisibility ( View.GONE );
                                Toast.makeText ( PointsSystemActivity.this, "" + pointsSystemList.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
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
                    //Toast.makeText ( PointsSystemActivity.this, "Server is Under Maintenance.", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( PointsSystemActivity.this, false, true ) {
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
