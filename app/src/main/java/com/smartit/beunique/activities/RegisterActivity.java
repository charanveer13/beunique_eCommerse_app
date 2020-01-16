package com.smartit.beunique.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.hbb20.CountryCodePicker;
import com.smartit.beunique.R;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueEditText;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private SessionSecuredPreferences loginSignupPreference;
    private BUniqueEditText et_first_name, et_last_name, et_email, et_password, et_mobile_number;
    private TextView tvRegisterBtn, tvHaveAnAccount, tvLoginNowBtn;
    private LinearLayout layout_have_an_account;
    private FontAwesomeIcon font_see_pwd;
    private CountryCodePicker countryCodePicker;
    private RadioGroup radio_btn_group;
    private RadioButton radio_mr_btn, radio_mrs_btn, radio_miss_btn;
    private boolean isEyeIconClicked;
    private String gender, countryCode, mobileNumber;

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

        setContentView(R.layout.activity_register);

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

        this.et_first_name = this.findViewById(R.id.et_first_name);
        this.et_last_name = this.findViewById(R.id.et_last_name);
        this.et_email = this.findViewById(R.id.et_email);
        this.et_password = this.findViewById(R.id.et_password);
        this.tvRegisterBtn = this.findViewById(R.id.tvRegisterBtn);
        this.tvHaveAnAccount = this.findViewById(R.id.tvHaveAnAccount);
        this.tvLoginNowBtn = this.findViewById(R.id.tvLoginNowBtn);
        this.layout_have_an_account = this.findViewById(R.id.layout_have_an_account);
        this.font_see_pwd = this.findViewById(R.id.font_see_pwd);
        this.countryCodePicker = this.findViewById(R.id.countryCodePicker);
        this.et_mobile_number = this.findViewById(R.id.et_mobile_number);
        this.radio_btn_group = this.findViewById(R.id.radio_btn_group);
        this.radio_mr_btn = this.findViewById(R.id.radio_mr_btn);
        this.radio_mrs_btn = this.findViewById(R.id.radio_mrs_btn);
        this.radio_miss_btn = this.findViewById(R.id.radio_miss_btn);
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
        this.layout_have_an_account.setOnClickListener(this);
        this.tvRegisterBtn.setOnClickListener(this);
        this.font_see_pwd.setOnClickListener(this);
        this.radio_btn_group.setOnCheckedChangeListener(this.onCheckedChangeListener);
        this.countryCodePicker.setOnCountryChangeListener(this.onCountryChangeListener);
    }

    private void dataToView() {
        this.iv_back_arrow.setImageResource(R.drawable.back_arrow_icon);
        this.iv_title_logo.setImageResource(selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic);
        this.iv_cart_icon.setVisibility(View.GONE);
    }

    private CountryCodePicker.OnCountryChangeListener onCountryChangeListener = new CountryCodePicker.OnCountryChangeListener() {
        @Override
        public void onCountrySelected() {
            countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
        }
    };

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            RadioButton checkedRadioButton = group.findViewById(checkedId);
            switch (checkedRadioButton.getId()) {
                case R.id.radio_mr_btn:
                    gender = "1";
                    return;
                case R.id.radio_mrs_btn:
                    gender = "2";
                    return;
                case R.id.radio_miss_btn:
                    gender = "3";
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRegisterBtn:
                if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_first_name))) {
                    Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
                } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_last_name))) {
                    Toast.makeText(this, "Please enter last name", Toast.LENGTH_SHORT).show();
                } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_email))) {
                    Toast.makeText(this, "Please enter emailId", Toast.LENGTH_SHORT).show();
                } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_password))) {
                    Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else if (!GlobalUtil.isValidEmail(ObjectUtil.getTextFromView(et_email))) {
                    Toast.makeText(this, "Email id is not valid", Toast.LENGTH_SHORT).show();
                } else if (ObjectUtil.isEmpty(gender)) {
                    Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
                } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_mobile_number))) {
                    Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    mobileNumber = et_mobile_number.getText().toString().trim();
                    if (ObjectUtil.isEmpty(countryCode)) {
                        mobileNumber = mobileNumber.concat(" ").concat(countryCodePicker.getDefaultCountryCodeWithPlus());
                    } else {
                        mobileNumber = mobileNumber.concat(" ").concat(countryCode);
                    }
                    registerNewUser();
                }
                break;
            case R.id.layout_have_an_account:
                this.finish();
                break;
            case R.id.iv_back_arrow:
                this.mainPage();
                break;
            case R.id.font_see_pwd:
                this.showPassword();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.mainPage();
    }

    private void registerNewUser() {
        progress.showProgressBar();
        RestClient.APIInterface apiInterface = RestClient.getClient();
        apiInterface.registerUser(et_first_name.getText().toString().trim(), et_last_name.getText().toString().trim(), et_email.getText().toString().trim(),
                et_password.getText().toString().trim(), gender, this.mobileNumber).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progress.hideProgressBar();

                if (!ObjectUtil.isEmpty(response.body())) {
                    try {
                        String mainString = response.body().string();
                        String tempArray[] = mainString.split("RegistrationApi");
                        EOAccountRegister eoRegister = JsonParser.getInstance().getObject(EOAccountRegister.class, tempArray[1]);
                        if (eoRegister.getStatus().equalsIgnoreCase("success")) {
                            Toast.makeText(RegisterActivity.this, "" + eoRegister.getMessage(), Toast.LENGTH_SHORT).show();
                            //TODO put data into shared preference for global label application
                            loginSignupPreference.edit().putString(Constants.SELECTED_CUSTOMER_ID, eoRegister.getPayload().getCustomer().getId()).apply();


                            loginSignupPreference.edit().putString(Constants.SELECTED_FIRST_NAME, eoRegister.getPayload().getCustomer().getFirstname()).apply();
                            loginSignupPreference.edit().putString(Constants.SELECTED_LAST_NAME, eoRegister.getPayload().getCustomer().getLastname()).apply();
                            loginSignupPreference.edit().putString(Constants.SELECTED_EMAIL_ID, eoRegister.getPayload().getCustomer().getEmail()).apply();
                            //TODO on successful register navigate to account page
                            RegisterActivity.this.finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "" + eoRegister.getMessage(), Toast.LENGTH_SHORT).show();
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
                    new GlobalAlertDialog(RegisterActivity.this, false, true) {
                        @Override
                        public void onDefault() {
                            super.onDefault();
                        }
                    }.show(R.string.server_is_under_maintenance);
                }
            }

        });
    }

    private void showPassword() {
        if (isEyeIconClicked) {
            isEyeIconClicked = false;
            this.font_see_pwd.setText(R.string.icon_eye_slash);
            this.et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            isEyeIconClicked = true;
            this.font_see_pwd.setText(R.string.icon_eye);
            this.et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.noInternetDialog.onDestroy();
    }

    private void mainPage() {
        this.startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        RegisterActivity.this.finish();
        Animatoo.animateSlideRight(this);
    }

}
