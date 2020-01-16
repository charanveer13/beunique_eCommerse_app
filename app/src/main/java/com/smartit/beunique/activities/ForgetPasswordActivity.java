package com.smartit.beunique.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueEditText;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.util.GlobalUtil;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;

import am.appwise.components.ni.NoInternetDialog;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private int selectedLangId;
    private SessionSecuredPreferences languageCurrencyPreferences;
    private BUniqueEditText et_email_address;
    private BUniqueTextView tv_reset_password;

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

        setContentView ( R.layout.activity_forget_password );

        this.initView ( );
        this.setOnClickListener ( );
        this.dataToView ( );

    }

    private void initView() {
        this.progress = new GlobalProgressDialog ( this );
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );

        this.languageCurrencyPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.languageCurrencyPreferences.getInt ( SELECTED_LANG_ID, 0 );

        //here toolbar layout view
        this.toolbarLayout = this.findViewById ( R.id.toolbarLayout );
        this.setSupportActionBar ( this.toolbarLayout );
        this.iv_back_arrow = this.toolbarLayout.findViewById ( R.id.iv_back_arrow );
        this.iv_title_logo = this.toolbarLayout.findViewById ( R.id.iv_title_logo );
        this.iv_cart_icon = this.toolbarLayout.findViewById ( R.id.iv_cart_icon );

        this.et_email_address = this.findViewById ( R.id.et_email_address );
        this.tv_reset_password = this.findViewById ( R.id.tv_reset_password );
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
    }

    private void dataToView() {
        this.iv_back_arrow.setImageResource ( R.drawable.back_arrow_icon );
        this.iv_title_logo.setImageResource ( selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic );
        this.tv_reset_password.setOnClickListener ( this );
        this.iv_cart_icon.setVisibility ( View.GONE );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId ( )) {
            case R.id.iv_back_arrow:
                this.finish ( );
                break;
            case R.id.tv_reset_password:
                if(ObjectUtil.isEmpty ( ObjectUtil.getTextFromView ( et_email_address ) )) {
                    Toast.makeText ( this, "Please enter emailId", Toast.LENGTH_SHORT ).show ( );
                } else if(!GlobalUtil.isValidEmail ( ObjectUtil.getTextFromView ( et_email_address ) )) {
                    Toast.makeText ( this, "Email id is not valid ", Toast.LENGTH_SHORT ).show ( );
                } else {
                    resetPassword ( );
                }
                break;
        }
    }


    private void resetPassword() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }
}
