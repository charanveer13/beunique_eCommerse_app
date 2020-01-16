package com.smartit.beunique.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueEditText;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.FontAwesomeIcon;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.account.EOChangePassword;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.Constants;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private RestClient.APIInterface apiInterface;
    private int selectedLangId;
    private SessionSecuredPreferences languageCurrencyPreferences;
    private BUniqueEditText et_old_password, et_new_password, et_confirm_password;
    private FontAwesomeIcon font_see_pwd_old, font_see_pwd_new, font_see_pwd_confirm;
    private BUniqueTextView tv_save;
    private boolean isOldPwdClicked, isNewPwdClicked, isConfirmPwdClicked;
    private SessionSecuredPreferences loginSignupPreference;
    private String selectedCustomerId;

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

        setContentView ( R.layout.activity_change_password );

        this.initView ( );
        this.setOnClickListener ( );
        this.dataToView ( );
    }

    private void initView() {
        this.progress = new GlobalProgressDialog ( this );
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );
        this.apiInterface = RestClient.getClient ( );

        this.languageCurrencyPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.languageCurrencyPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.loginSignupPreference = ApplicationHelper.application ( ).loginPreferences ( LOGIN_SIGNUP_PREFERENCE );
        this.selectedCustomerId = loginSignupPreference.getString ( Constants.SELECTED_CUSTOMER_ID, "" );

        //here toolbar layout view
        this.toolbarLayout = this.findViewById ( R.id.toolbarLayout );
        this.setSupportActionBar ( this.toolbarLayout );
        this.iv_back_arrow = this.toolbarLayout.findViewById ( R.id.iv_back_arrow );
        this.iv_title_logo = this.toolbarLayout.findViewById ( R.id.iv_title_logo );
        this.iv_cart_icon = this.toolbarLayout.findViewById ( R.id.iv_cart_icon );

        this.et_old_password = this.findViewById ( R.id.et_old_password );
        this.font_see_pwd_old = this.findViewById ( R.id.font_see_pwd_old );
        this.et_new_password = this.findViewById ( R.id.et_new_password );
        this.font_see_pwd_new = this.findViewById ( R.id.font_see_pwd_new );
        this.et_confirm_password = this.findViewById ( R.id.et_confirm_password );
        this.font_see_pwd_confirm = this.findViewById ( R.id.font_see_pwd_confirm );
        this.tv_save = this.findViewById ( R.id.tv_save );
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener ( this );
        this.font_see_pwd_old.setOnClickListener ( this );
        this.font_see_pwd_new.setOnClickListener ( this );
        this.font_see_pwd_confirm.setOnClickListener ( this );
        this.tv_save.setOnClickListener ( this );
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
                this.finish();
                break;
            case R.id.font_see_pwd_old:
                showPasswordOld ( );
                break;
            case R.id.font_see_pwd_new:
                showPasswordNew ( );
                break;
            case R.id.font_see_pwd_confirm:
                showPasswordConfirm ( );
                break;
            case R.id.tv_save:
                if(ObjectUtil.isEmpty ( ObjectUtil.getTextFromView ( et_old_password ) )) {
                    Toast.makeText ( this, "Please enter old password", Toast.LENGTH_SHORT ).show ( );
                } else if(ObjectUtil.isEmpty ( ObjectUtil.getTextFromView ( et_new_password ) )) {
                    Toast.makeText ( this, "Please enter new password", Toast.LENGTH_SHORT ).show ( );
                } else if(ObjectUtil.isEmpty ( ObjectUtil.getTextFromView ( et_confirm_password ) )) {
                    Toast.makeText ( this, "Please enter confirm password ", Toast.LENGTH_SHORT ).show ( );
                } else if(!ObjectUtil.getTextFromView ( et_new_password ).equals ( ObjectUtil.getTextFromView ( et_confirm_password ) )) {
                    Toast.makeText ( this, "Password not matched ", Toast.LENGTH_SHORT ).show ( );
                } else {
                    if(ObjectUtil.isEmpty ( selectedCustomerId )) {
                        Toast.makeText ( this, "Please login first to change your password.", Toast.LENGTH_SHORT ).show ( );
                    } else {
                        changePasswordApi ( selectedCustomerId, ObjectUtil.getTextFromView ( et_old_password ), ObjectUtil.getTextFromView ( et_new_password ) );
                    }
                }
                break;
        }
    }

    private void changePasswordApi(String customerId, String oldPwd, String newPwd) {
        progress.showProgressBar ( );
        this.apiInterface.changePasswordApi ( customerId, oldPwd, newPwd ).enqueue ( new Callback <EOChangePassword> ( ) {
            @Override
            public void onResponse(Call <EOChangePassword> call, Response <EOChangePassword> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    EOChangePassword eoChangePassword = response.body ( );
                    if(!ObjectUtil.isEmpty ( eoChangePassword )) {
                        if(eoChangePassword.getStatus ( ).equalsIgnoreCase ( "success" )) {
                            Toast.makeText ( ChangePasswordActivity.this, "" + eoChangePassword.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                            ChangePasswordActivity.this.finish ( );
                            //TODO when user is logout out then clear the login shared preferences
                            if(loginSignupPreference.contains ( Constants.SELECTED_FIRST_NAME ) && loginSignupPreference.contains ( Constants.SELECTED_LAST_NAME )
                                    && loginSignupPreference.contains ( Constants.SELECTED_EMAIL_ID ) && loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {
                                loginSignupPreference.edit ( ).clear ( ).apply ( );
                            }
                            //startActivity ( new Intent ( ChangePasswordActivity.this, LoginActivity.class ) );
                        } else {
                            Toast.makeText ( ChangePasswordActivity.this, "" + eoChangePassword.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call <EOChangePassword> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    new GlobalAlertDialog ( ChangePasswordActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );
    }


    private void showPasswordOld() {
        if(isOldPwdClicked) {
            isOldPwdClicked = false;
            this.font_see_pwd_old.setText ( R.string.icon_eye_slash );
            this.et_old_password.setInputType ( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        } else {
            isOldPwdClicked = true;
            this.font_see_pwd_old.setText ( R.string.icon_eye );
            this.et_old_password.setInputType ( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
        }
    }

    private void showPasswordNew() {
        if(isNewPwdClicked) {
            isNewPwdClicked = false;
            this.font_see_pwd_new.setText ( R.string.icon_eye_slash );
            this.et_new_password.setInputType ( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        } else {
            isNewPwdClicked = true;
            this.font_see_pwd_new.setText ( R.string.icon_eye );
            this.et_new_password.setInputType ( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
        }
    }

    private void showPasswordConfirm() {
        if(isConfirmPwdClicked) {
            isConfirmPwdClicked = false;
            this.font_see_pwd_confirm.setText ( R.string.icon_eye_slash );
            this.et_confirm_password.setInputType ( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        } else {
            isConfirmPwdClicked = true;
            this.font_see_pwd_confirm.setText ( R.string.icon_eye );
            this.et_confirm_password.setInputType ( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }

}
