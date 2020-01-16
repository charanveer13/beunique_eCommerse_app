package com.smartit.beunique.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import am.appwise.components.ni.NoInternetDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_ARABIC;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_FIRST_NAME;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;
import static com.smartit.beunique.util.Constants.SELECTED_LAST_NAME;
import static com.smartit.beunique.util.Constants.SELECTED_PROFILE_PIC;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_title_logo, iv_cart_icon;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;
    private RestClient.APIInterface apiInterface;
    private SessionSecuredPreferences languageCurrencyPreferences;
    private int selectedLangId;
    private SessionSecuredPreferences loginSignupPreference;
    private CircleImageView profileImage;
    private FontAwesomeIcon icon_camera;
    private BUniqueEditText et_first_name, et_last_name, et_email, et_mobile_number;
    private RadioGroup radio_btn_group;
    private RadioButton radio_mr_btn, radio_mrs_btn, radio_miss_btn;
    private BUniqueTextView tv_change_password, tv_save;
    private static BUniqueEditText et_dob;
    private String gender;
    private int mYear, mMonth, mDay;
    private String selectedCustomerId;

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

        setContentView(R.layout.activity_profile);

        this.initView();
        this.setOnClickListener();
        this.dataToView();
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(this);
        this.noInternetDialog = new NoInternetDialog.Builder(this).build();
        this.apiInterface = RestClient.getClient();

        this.languageCurrencyPreferences = ApplicationHelper.application().sharedPreferences(LANGUAGE_CURRENCY_PREFERENCE);
        this.selectedLangId = this.languageCurrencyPreferences.getInt(SELECTED_LANG_ID, 0);
        this.loginSignupPreference = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.selectedCustomerId = loginSignupPreference.getString(Constants.SELECTED_CUSTOMER_ID, "");

        //here toolbar layout view
        this.toolbarLayout = this.findViewById(R.id.toolbarLayout);
        this.setSupportActionBar(this.toolbarLayout);
        this.iv_back_arrow = this.toolbarLayout.findViewById(R.id.iv_back_arrow);
        this.iv_title_logo = this.toolbarLayout.findViewById(R.id.iv_title_logo);
        this.iv_cart_icon = this.toolbarLayout.findViewById(R.id.iv_cart_icon);

        this.profileImage = this.findViewById(R.id.profileImage);
        this.icon_camera = this.findViewById(R.id.icon_camera);
        this.et_first_name = this.findViewById(R.id.et_first_name);
        this.et_last_name = this.findViewById(R.id.et_last_name);
        this.et_email = this.findViewById(R.id.et_email);
        this.et_mobile_number = this.findViewById(R.id.et_mobile_number);
        et_dob = this.findViewById(R.id.et_dob);
        this.radio_btn_group = this.findViewById(R.id.radio_btn_group);
        this.radio_mr_btn = this.findViewById(R.id.radio_mr_btn);
        this.radio_mrs_btn = this.findViewById(R.id.radio_mrs_btn);
        this.radio_miss_btn = this.findViewById(R.id.radio_miss_btn);
        this.tv_change_password = this.findViewById(R.id.tv_change_password);
        this.tv_save = this.findViewById(R.id.tv_save);

    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
        this.icon_camera.setOnClickListener(this);
        et_dob.setOnClickListener(this);
        this.tv_change_password.setOnClickListener(this);
        this.tv_save.setOnClickListener(this);
        this.radio_btn_group.setOnCheckedChangeListener(this.onCheckedChangeListener);
    }

    private void dataToView() {
        this.iv_back_arrow.setImageResource(R.drawable.back_arrow_icon);
        this.iv_title_logo.setImageResource(selectedLangId == 1 ? R.drawable.app_name_english : R.drawable.app_name_arabic);
        this.iv_cart_icon.setVisibility(View.GONE);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (ObjectUtil.isEmpty(selectedCustomerId)) {
            Toast.makeText(this, "Please login first to update your profile.", Toast.LENGTH_SHORT).show();
        } else {
            getProfileInformation();
        }
    }

    private void getProfileInformation() {
        progress.showProgressBar();
        apiInterface.getUserInfo(selectedCustomerId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    try {
                        String mainString = response.body().string();
                        String tempArray[] = mainString.split("profileApi");
                        EOAccountRegister eoProfileInfo = JsonParser.getInstance().getObject(EOAccountRegister.class, tempArray[1]);
                        if (!ObjectUtil.isEmpty(eoProfileInfo)) {
                            if (eoProfileInfo.getStatus().equalsIgnoreCase("success")) {

                                loadProfileImage((String) eoProfileInfo.getPayload().getCustomer().getProfilePic(), profileImage);
                                et_first_name.setText(eoProfileInfo.getPayload().getCustomer().getFirstname());
                                et_last_name.setText(eoProfileInfo.getPayload().getCustomer().getLastname());
                                et_email.setText(eoProfileInfo.getPayload().getCustomer().getEmail());
                                et_mobile_number.setText(eoProfileInfo.getPayload().getCustomer().getMobile());
                                et_dob.setText((String) eoProfileInfo.getPayload().getCustomer().getDob());

                                if (eoProfileInfo.getPayload().getCustomer().getIdGender().equalsIgnoreCase("1")) {
                                    radio_mr_btn.setChecked(true);
                                } else if (eoProfileInfo.getPayload().getCustomer().getIdGender().equalsIgnoreCase("2")) {
                                    radio_mrs_btn.setChecked(true);
                                } else if (eoProfileInfo.getPayload().getCustomer().getIdGender().equalsIgnoreCase("3")) {
                                    radio_miss_btn.setChecked(true);
                                }

                            } else {
                                Toast.makeText(ProfileActivity.this, "" + eoProfileInfo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
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
                    new GlobalAlertDialog(ProfileActivity.this, false, true) {
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
            case R.id.iv_back_arrow:
                this.finish();
                break;
            case R.id.icon_camera:
                this.openGalleryAndCamera();
                break;
            case R.id.et_dob:
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.tv_change_password:
                this.startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.tv_save:
                if (ObjectUtil.isEmpty(selectedCustomerId)) {
                    Toast.makeText(this, "Please login first to update your profile.", Toast.LENGTH_SHORT).show();
                } else {
                    if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_mobile_number))) {
                        Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                    } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_dob))) {
                        Toast.makeText(this, "Please select DOB", Toast.LENGTH_SHORT).show();
                    } else if (ObjectUtil.isEmpty(gender)) {
                        Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
                    } else {
                        updateProfile(selectedCustomerId, et_first_name.getText().toString().trim(), et_last_name.getText().toString().trim(), et_email.getText().toString().trim(),
                                gender, selectedLangId, et_mobile_number.getText().toString().trim(), et_dob.getText().toString().trim());

                    }
                }
                break;
        }
    }

    private void updateProfile(String customerId, String fName, String lName, String email, String idGender, int langId, String mobile, String dob) {
        progress.showProgressBar();
        this.apiInterface.updateProfileApi("webservice/update_profile.php/?id=" + customerId, fName, lName, email, idGender, langId, mobile, dob).enqueue(new Callback<EOAccountRegister>() {
            @Override
            public void onResponse(Call<EOAccountRegister> call, Response<EOAccountRegister> response) {
                progress.hideProgressBar();

                if (!ObjectUtil.isEmpty(response.body())) {
                    EOAccountRegister updateProfile = response.body();
                    if (!ObjectUtil.isEmpty(updateProfile)) {
                        if (updateProfile.getStatus().equalsIgnoreCase("success")) {
                            Toast.makeText(ProfileActivity.this, "" + updateProfile.getMessage(), Toast.LENGTH_SHORT).show();

                            loginSignupPreference.edit().putString(SELECTED_FIRST_NAME, updateProfile.getPayload().getCustomer().getFirstname()).apply();
                            loginSignupPreference.edit().putString(SELECTED_LAST_NAME, updateProfile.getPayload().getCustomer().getLastname()).apply();
                            loginSignupPreference.edit().putString(SELECTED_PROFILE_PIC,(String) updateProfile.getPayload().getCustomer().getProfilePic()).apply();

                            //TODO back one page
                            ProfileActivity.this.finish();
                        } else {
                            Toast.makeText(ProfileActivity.this, "" + updateProfile.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOAccountRegister> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    new GlobalAlertDialog(ProfileActivity.this, false, true) {
                        @Override
                        public void onDefault() {
                            super.onDefault();
                        }
                    }.show(R.string.server_is_under_maintenance);
                }
            }
        });
    }

    private void openGalleryAndCamera() {
        PickSetup setup = new PickSetup()
                .setTitle("Choose from")
                .setTitleColor(Color.BLACK)
                .setBackgroundColor(Color.BLACK)
                .setCancelText("Close")
                .setCancelTextColor(Color.BLACK)
                .setButtonTextColor(Color.BLACK)
                .setMaxSize(500)
                .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
                .setButtonOrientation(LinearLayout.VERTICAL)
                .setSystemDialog(false)
                .setGalleryIcon(R.drawable.gallery)
                .setCameraIcon(R.drawable.camera)
                .setSystemDialog(false).setBackgroundColor(Color.parseColor("#FFFFFF"));

        PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
            @Override
            public void onPickResult(PickResult pickResult) {

                //TODO here image response come from gallery and camera and send on server
                uploadImageOnServer(pickResult.getPath());

            }
        }).setOnPickCancel(new IPickCancel() {
            @Override
            public void onCancelClick() {

            }

        }).show(this);
    }

    private void uploadImageOnServer(String path) {
        if (ObjectUtil.isEmpty(selectedCustomerId)) {
            Toast.makeText(this, "Please login first to update your profile.", Toast.LENGTH_SHORT).show();
        } else {
            File file = new File(path);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile); // NOTE: upload here is the name which is the file name

            progress.showProgressBar();
            this.apiInterface.uploadProfileImage("webservice/my-account/update_image.php/?id=" + selectedCustomerId, body).enqueue(new Callback<EOAccountRegister>() {
                @Override
                public void onResponse(Call<EOAccountRegister> call, Response<EOAccountRegister> response) {
                    progress.hideProgressBar();

                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOAccountRegister updateProfile = response.body();
                        if (!ObjectUtil.isEmpty(updateProfile)) {
                            if (updateProfile.getStatus().equalsIgnoreCase("success")) {
                                loadProfileImage((String) updateProfile.getPayload().getCustomer().getProfilePic(), profileImage);
                                Toast.makeText(ProfileActivity.this, "" + updateProfile.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "" + updateProfile.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOAccountRegister> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(ProfileActivity.this, false, true) {
                            @Override
                            public void onDefault() {
                                super.onDefault();
                            }
                        }.show(R.string.server_is_under_maintenance);
                    }
                }
            });

        }

    }

    private void loadProfileImage(String imagePath, ImageView imageView) {
        Picasso.get()
                .load("https://bu-beunique.com/images/" + imagePath)
                .error(R.drawable.ic_profile)
                .into(imageView);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker() /*.setMaxDate ( c.getTimeInMillis ( ) )*/;
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            et_dob.setClickable(true);
            et_dob.setText(String.valueOf(year).concat("-").concat(String.valueOf(month + 1)).concat("-").concat(String.valueOf(dayOfMonth)));
        }
    }

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
    public void onDestroy() {
        super.onDestroy();
        this.noInternetDialog.onDestroy();
    }

}
