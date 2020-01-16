package com.smartit.beunique.activities;

import com.smartit.beunique.R;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.util.Constants;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.components.SessionSecuredPreferences;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext ( LocalizationHelper.onAttach ( newBase ) );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_splash_screen );

        SessionSecuredPreferences securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );

        //TODO first time to check langId or currencyId contains in shared preferences or not
        if(securedPreferences.contains ( Constants.SELECTED_LANG_ID )) {
            new Handler ( ).postDelayed ( new Runnable ( ) {
                @Override
                public void run() {
                    startActivity ( new Intent ( SplashScreenActivity.this, ActivityExplore.class ) );
                    finish ( );
                }
            }, SPLASH_TIME_OUT );

        } else {
            new Handler ( ).postDelayed ( new Runnable ( ) {
                @Override
                public void run() {
                    startActivity ( new Intent ( SplashScreenActivity.this, LanguageCurrencyActivity.class ) );
                    finish ( );
                }
            }, SPLASH_TIME_OUT );
        }
    }

}
