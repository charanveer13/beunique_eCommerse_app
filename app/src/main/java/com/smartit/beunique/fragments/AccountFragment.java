package com.smartit.beunique.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.LoginActivity;
import com.smartit.beunique.activities.MainActivity;
import com.smartit.beunique.activities.PointsSystemActivity;
import com.smartit.beunique.activities.ProfileActivity;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.util.Constants;

import am.appwise.components.ni.NoInternetDialog;

import static com.smartit.beunique.util.Constants.LOGIN_SIGNUP_PREFERENCE;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private View view;
    private NoInternetDialog noInternetDialog;
    private LinearLayout layout_login_signup, layout_after_login, layout_my_account, layout_my_order, layout_address, layout_track_order,
            layout_credit_card, layout_general, layout_affliate_system, layout_points_system, layout_whishlist, layout_recently_viewed,
            layout_help_center, layout_contact_us, layout_logout;

    private BUniqueTextView tv_login_signup, tv_logout;
    private SessionSecuredPreferences loginSignupPreference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate ( R.layout.fragment_account, container, false );

        this.initView ( );
        this.setOnClickListener ( );

        return this.view;
    }

    private void initView() {
        this.loginSignupPreference = ApplicationHelper.application ( ).loginPreferences ( LOGIN_SIGNUP_PREFERENCE );
        this.noInternetDialog = new NoInternetDialog.Builder ( getActivity ( ) ).build ( );
        this.layout_login_signup = this.view.findViewById ( R.id.layout_login_signup );
        this.tv_login_signup = this.view.findViewById ( R.id.tv_login_signup );
        this.layout_after_login = this.view.findViewById ( R.id.layout_after_login );
        this.layout_my_account = this.view.findViewById ( R.id.layout_my_account );
        this.layout_my_order = this.view.findViewById ( R.id.layout_my_order );
        this.layout_address = this.view.findViewById ( R.id.layout_address );
        this.layout_track_order = this.view.findViewById ( R.id.layout_track_order );
        this.layout_credit_card = this.view.findViewById ( R.id.layout_credit_card );

        this.layout_general = this.view.findViewById ( R.id.layout_general );
        this.layout_affliate_system = this.view.findViewById ( R.id.layout_affliate_system );
        this.layout_points_system = this.view.findViewById ( R.id.layout_points_system );
        this.layout_whishlist = this.view.findViewById ( R.id.layout_whishlist );
        this.layout_recently_viewed = this.view.findViewById ( R.id.layout_recently_viewed );

        this.layout_help_center = this.view.findViewById ( R.id.layout_help_center );
        this.layout_contact_us = this.view.findViewById ( R.id.layout_contact_us );
        this.layout_logout = this.view.findViewById ( R.id.layout_logout );
        this.tv_logout = this.view.findViewById ( R.id.tv_logout );
    }

    private void setOnClickListener() {
        this.tv_login_signup.setOnClickListener ( this );
        this.layout_my_account.setOnClickListener ( this );
        this.layout_my_order.setOnClickListener ( this );
        this.layout_address.setOnClickListener ( this );
        this.layout_track_order.setOnClickListener ( this );
        this.layout_credit_card.setOnClickListener ( this );
        this.layout_affliate_system.setOnClickListener ( this );
        this.layout_points_system.setOnClickListener ( this );
        this.layout_whishlist.setOnClickListener ( this );
        this.layout_recently_viewed.setOnClickListener ( this );
        this.layout_help_center.setOnClickListener ( this );
        this.layout_contact_us.setOnClickListener ( this );
        this.tv_logout.setOnClickListener ( this );
    }

    private void dataToView() {
        //TODO check firstName, lastName, emailId in exists in shared preference or not
        if(loginSignupPreference.contains ( Constants.SELECTED_FIRST_NAME ) && loginSignupPreference.contains ( Constants.SELECTED_LAST_NAME )
                && loginSignupPreference.contains ( Constants.SELECTED_EMAIL_ID ) && loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {

            this.layout_login_signup.setVisibility ( View.GONE );
            this.layout_after_login.setVisibility ( View.VISIBLE );
            this.layout_logout.setVisibility ( View.VISIBLE );
        }
    }

    @Override
    public void onResume() {
        super.onResume ( );
        this.dataToView ( );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId ( )) {
            case R.id.tv_login_signup:
                this.startActivity ( new Intent ( getActivity ( ), LoginActivity.class ) );
                break;
            case R.id.tv_logout:
                this.logoutFromApplication ( );
                break;
            case R.id.layout_my_account:
                this.startActivity ( new Intent ( getActivity ( ), ProfileActivity.class ) );
                break;
            case R.id.layout_whishlist:
                // TODO from here open wishlist fragment and wishlist tab is selected
                ((MainActivity) getActivity ( )).wishlistTabIsSelected ( );
                break;
            case R.id.layout_points_system:
                this.startActivity ( new Intent ( getActivity ( ), PointsSystemActivity.class ) );
                break;
        }
    }

    private void logoutFromApplication() {
        new GlobalAlertDialog ( getActivity ( ), true, false ) {
            @Override
            public void onConfirmation() {
                super.onConfirmation ( );

                //TODO when user is logout out then clear the login shared preferences
                if(loginSignupPreference.contains ( Constants.SELECTED_FIRST_NAME ) && loginSignupPreference.contains ( Constants.SELECTED_LAST_NAME )
                        && loginSignupPreference.contains ( Constants.SELECTED_EMAIL_ID ) && loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {

                    loginSignupPreference.edit ( ).clear ( ).apply ( );

                    layout_login_signup.setVisibility ( View.VISIBLE );
                    layout_after_login.setVisibility ( View.GONE );
                    layout_logout.setVisibility ( View.GONE );
                }
            }
        }.show ( R.string.are_you_sure_you_want_to_logout );
    }

    @Override
    public void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }

}
