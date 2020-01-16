package com.smartit.beunique.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.currency.EOCurrency;
import com.smartit.beunique.entity.currency.EOCurrencyList;
import com.smartit.beunique.entity.language.EOLanguage;
import com.smartit.beunique.entity.language.EOLanguageList;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.Constants;
import com.smartit.beunique.util.LocalizationHelper;
import com.smartit.beunique.util.ObjectUtil;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_CONVERSION_RATE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_ID;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_ISO_CODE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_SIGN;

public class LanguageCurrencyActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialSpinner spinnerLanguage, spinnerCurrency;
    private TextView textViewStart;

    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private NoInternetDialog noInternetDialog;

    private ArrayList <String> languageLists = new ArrayList <> ( );
    private ArrayList <String> currencyLists = new ArrayList <> ( );
    private ArrayList <EOLanguage> eoLanguageArrayList = new ArrayList <> ( );
    private ArrayList <EOCurrency> eoCurrencyArrayList = new ArrayList <> ( );
    private SessionSecuredPreferences securedPreferences;
    private String selectedLanguageName = null;
    private String selectedCurrencyCode = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext ( LocalizationHelper.onAttach ( newBase ) );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        setContentView ( R.layout.activity_language_currency );

        this.initView ( );
        this.setOnClickListener ( );

        this.getLanguageApi ( );
        this.getCurrencyApi ( );

        this.getSelectedDataFromSpinner ( );
    }

    private void initView() {
        this.apiInterface = RestClient.getClient ( );
        this.progress = new GlobalProgressDialog ( this );
        this.noInternetDialog = new NoInternetDialog.Builder ( this ).build ( );
        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );

        this.spinnerLanguage = findViewById ( R.id.spinnerLanguage );
        this.spinnerCurrency = findViewById ( R.id.spinnerCurrency );
        this.textViewStart = findViewById ( R.id.textViewStart );

        ArrayAdapter <String> languageAdapter = new ArrayAdapter <> ( this, android.R.layout.simple_spinner_item, languageLists );
        languageAdapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
        this.spinnerLanguage.setAdapter ( languageAdapter );

        ArrayAdapter <String> currencyAdapter = new ArrayAdapter <> ( this, android.R.layout.simple_spinner_item, currencyLists );
        currencyAdapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
        this.spinnerCurrency.setAdapter ( currencyAdapter );
    }

    private void setOnClickListener() {
        this.textViewStart.setOnClickListener ( this );
    }

    private void getLanguageApi() {

        progress.showProgressBar ( );
        apiInterface.getLanguageApi ( ).enqueue ( new Callback <EOLanguageList> ( ) {
            @Override
            public void onResponse(Call <EOLanguageList> call, Response <EOLanguageList> response) {
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(!ObjectUtil.isEmpty ( response.body ( ).getLanguages ( ) )) {
                        eoLanguageArrayList.addAll ( response.body ( ).getLanguages ( ) );
                        for (EOLanguage eoLanguage : response.body ( ).getLanguages ( )) {
                            if(!ObjectUtil.isEmpty ( eoLanguage ))
                                languageLists.add ( eoLanguage.getName ( ) );
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call <EOLanguageList> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    //Toast.makeText ( LanguageCurrencyActivity.this, "Server is Under Maintenance " , Toast.LENGTH_SHORT ).show ( );
                }
            }
        } );

    }

    private void getCurrencyApi() {
        apiInterface.getCurrencyApi ( ).enqueue ( new Callback <EOCurrencyList> ( ) {
            @Override
            public void onResponse(Call <EOCurrencyList> call, Response <EOCurrencyList> response) {
                progress.hideProgressBar ( );
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    if(!ObjectUtil.isEmpty ( response.body ( ).getCurrencies ( ) )) {
                        eoCurrencyArrayList.addAll ( response.body ( ).getCurrencies ( ) );
                        for (EOCurrency eoCurrency : response.body ( ).getCurrencies ( )) {
                            if(!ObjectUtil.isEmpty ( eoCurrency ))
                                currencyLists.add ( eoCurrency.getIsoCode ( ) );
                        }
                    }
                    findViewById ( R.id.dataLayout ).setVisibility ( View.VISIBLE );
                }
            }

            @Override
            public void onFailure(Call <EOCurrencyList> call, Throwable t) {
                if(t.getMessage ( ) != null) {
                    progress.hideProgressBar ( );
                    //Toast.makeText ( LanguageCurrencyActivity.this, "Server is Under Maintenance.", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( LanguageCurrencyActivity.this, false, true ) {
                        @Override
                        public void onDefault() {
                            super.onDefault ( );
                        }
                    }.show ( R.string.server_is_under_maintenance );
                }
            }
        } );

    }

    private void getSelectedDataFromSpinner() {
        this.spinnerLanguage.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ( ) {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                selectedLanguageName = (String) parent.getItemAtPosition ( position );
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        } );

        this.spinnerCurrency.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ( ) {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                selectedCurrencyCode = (String) parent.getItemAtPosition ( position );
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        } );
    }

    private void saveDataInSharedPreferences() {
        //TODO for language dropdown
        for (EOLanguage eoLanguage : eoLanguageArrayList) {
            if(eoLanguage.getName ( ).equalsIgnoreCase ( selectedLanguageName )) {
                securedPreferences.edit ( ).putInt ( Constants.SELECTED_LANG_ID, eoLanguage.getId ( ) ).apply ( );
                LocalizationHelper.setLocale ( this, eoLanguage.getIsoCode ( ) );
                //securedPreferences.edit ( ).putString ( "langIsRtl", eoLanguage.getIsRtl ( ) ).apply ( );
            }
        }

        //TODO for currency dropdown
        for (EOCurrency eoCurrency : eoCurrencyArrayList) {
            if(eoCurrency.getIsoCode ( ).equalsIgnoreCase ( selectedCurrencyCode )) {
                securedPreferences.edit ( ).putInt ( SELECTED_CURRENCY_ID, eoCurrency.getId ( ) ).apply ( );
                securedPreferences.edit ( ).putString ( SELECTED_CURRENCY_ISO_CODE, eoCurrency.getIsoCode ( ) ).apply ( );
                securedPreferences.edit ( ).putString ( SELECTED_CURRENCY_SIGN, eoCurrency.getSign ( ) ).apply ( );
                securedPreferences.edit ( ).putString ( SELECTED_CURRENCY_CONVERSION_RATE, eoCurrency.getConversionRate ( ) ).apply ( );
            }
        }

    }

    @Override
    public void onClick(View view) {
        if(view.getId ( ) == R.id.textViewStart) {
            if(ObjectUtil.isEmpty ( spinnerLanguage.getSelectedItem ( ) )) {
                Toast.makeText ( this, "Please select language.", Toast.LENGTH_SHORT ).show ( );
            } else if(ObjectUtil.isEmpty ( spinnerCurrency.getSelectedItem ( ) )) {
                Toast.makeText ( this, "Please select currency.", Toast.LENGTH_SHORT ).show ( );
            } else {
                this.saveDataInSharedPreferences ( );
                Intent intentExploreActivity = new Intent ( this, ActivityExplore.class );
                startActivity ( intentExploreActivity );
                this.finish ( );
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }
}
