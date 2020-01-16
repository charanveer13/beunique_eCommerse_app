package com.smartit.beunique.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.smartit.beunique.R;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueEditText;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.FontAwesomeIcon;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.account.EOAccountRegister;
import com.smartit.beunique.networking.JsonParser;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.Constants;
import com.smartit.beunique.util.GlobalUtil;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;

import java.io.IOException;

import am.appwise.components.ni.NoInternetDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private BUniqueTextView tvLogInBtn, tvRegisterNoewBtn, tvForgetYourPassword;
    private BUniqueEditText et_user_name, et_user_password;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private SessionSecuredPreferences loginSignupPreference;
    private boolean isEyeIconClicked;
    private FontAwesomeIcon font_see_pwd, icon_facebook, icon_google;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO set layout direction on language change
        if (LANGUAGE_ARABIC.equals(LocalizationHelper.getLanguage(this))) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setContentView(R.layout.activity_login);

        this.initView();
        this.setOnClickListener();
        this.dataToView();
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(this);
        this.noInternetDialog = new NoInternetDialog.Builder(this).build();

        this.securedPreferences = ApplicationHelper.application().sharedPreferences(LANGUAGE_CURRENCY_PREFERENCE);
        this.selectedLangId = this.securedPreferences.getInt(SELECTED_LANG_ID, 0);
        this.loginSignupPreference = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);

        //here toolbar layout view
        this.toolbarLayout = this.findViewById(R.id.toolbarLayout);
        this.setSupportActionBar(this.toolbarLayout);
        this.iv_back_arrow = this.toolbarLayout.findViewById(R.id.iv_back_arrow);
        this.iv_title_logo = this.toolbarLayout.findViewById(R.id.iv_title_logo);
        this.iv_cart_icon = this.toolbarLayout.findViewById(R.id.iv_cart_icon);

        this.tvLogInBtn = this.findViewById(R.id.tvLogInBtn);
        this.tvRegisterNoewBtn = this.findViewById(R.id.tvRegisterNoewBtn);
        this.tvForgetYourPassword = this.findViewById(R.id.tvForgetYourPassword);
        this.et_user_name = this.findViewById(R.id.et_user_name);
        this.et_user_password = this.findViewById(R.id.et_user_password);
        this.font_see_pwd = this.findViewById(R.id.font_see_pwd);
        this.icon_facebook = this.findViewById(R.id.icon_facebook);
        this.icon_google = this.findViewById(R.id.icon_google);
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
        this.tvLogInBtn.setOnClickListener(this);
        this.tvRegisterNoewBtn.setOnClickListener(this);
        this.tvForgetYourPassword.setOnClickListener(this);
        this.font_see_pwd.setOnClickListener(this);
        this.icon_facebook.setOnClickListener(this);
        this.icon_google.setOnClickListener(this);
    }

    private void dataToView() {
        this.iv_back_arrow.setImageResource(R.drawable.back_arrow_icon);
        this.iv_title_logo.setImageResource(selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic);
        this.iv_cart_icon.setVisibility(View.GONE);
    }

    private void loginUser() {
        progress.showProgressBar();
        RestClient.APIInterface apiInterface = RestClient.getClient();
        apiInterface.loginUser(et_user_name.getText().toString().trim(), et_user_password.getText().toString().trim()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    try {
                        String mainString = response.body().string();
                        String tempArray[] = mainString.split("LoginApi");
                        EOAccountRegister eoLogin = JsonParser.getInstance().getObject(EOAccountRegister.class, tempArray[1]);
                        if (eoLogin.getStatus().equalsIgnoreCase("success")) {
                            Toast.makeText(LoginActivity.this, "" + eoLogin.getMessage(), Toast.LENGTH_SHORT).show();
                            //TODO put data into shared preference for global label application
                            loginSignupPreference.edit().putString(Constants.SELECTED_CUSTOMER_ID, eoLogin.getPayload().getCustomer().getId()).apply();


                            loginSignupPreference.edit().putString(Constants.SELECTED_FIRST_NAME, eoLogin.getPayload().getCustomer().getFirstname()).apply();
                            loginSignupPreference.edit().putString(Constants.SELECTED_LAST_NAME, eoLogin.getPayload().getCustomer().getLastname()).apply();
                            loginSignupPreference.edit().putString(Constants.SELECTED_EMAIL_ID, eoLogin.getPayload().getCustomer().getEmail()).apply();

                            //TODO on successful login open account page
                            LoginActivity.this.finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "" + eoLogin.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    new GlobalAlertDialog(LoginActivity.this, false, true) {
                        @Override
                        public void onDefault() {
                            super.onDefault();
                        }
                    }.show(R.string.server_is_under_maintenance);
                }
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRegisterNoewBtn:
                this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
                break;
            case R.id.tvLogInBtn:
                if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_user_name))) {
                    Toast.makeText(this, "Please enter emailId", Toast.LENGTH_SHORT).show();
                } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_user_password))) {
                    Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else if (!GlobalUtil.isValidEmail(ObjectUtil.getTextFromView(et_user_name))) {
                    Toast.makeText(this, "Email id is not valid ", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser();
                }
                break;
            case R.id.iv_back_arrow:
                this.finish();
                break;
            case R.id.font_see_pwd:
                this.showPassword();
                break;
            case R.id.tvForgetYourPassword:
                this.startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.icon_facebook:

                break;

            case R.id.icon_google:

                break;
        }
    }

    private void showPassword() {
        if (isEyeIconClicked) {
            isEyeIconClicked = false;
            this.font_see_pwd.setText(R.string.icon_eye_slash);
            this.et_user_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            isEyeIconClicked = true;
            this.font_see_pwd.setText(R.string.icon_eye);
            this.et_user_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.noInternetDialog.onDestroy();
    }


}
