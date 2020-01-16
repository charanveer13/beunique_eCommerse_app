package com.smartit.beunique.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.adapters.SearchAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.search.EOSearchData;
import com.smartit.beunique.entity.search.EOSearchList;
import com.smartit.beunique.networking.JsonParser;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.ObjectUtil;

import java.io.IOException;
import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private View view;
    private NoInternetDialog noInternetDialog;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private SearchView searchView;
    private RecyclerView searchRecyclerView;
    private LinearLayout layout_results_found;
    private BUniqueTextView tv_no_results_found, tv_results_found;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate ( R.layout.fragment_search, container, false );

        this.initView ( );
        this.dataToView ( );

        return this.view;
    }

    private void initView() {
        this.noInternetDialog = new NoInternetDialog.Builder ( getActivity ( ) ).build ( );
        this.apiInterface = RestClient.getClient ( );
        this.progress = new GlobalProgressDialog ( getContext ( ) );
        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );

        this.searchView = this.view.findViewById ( R.id.searchView );
        this.searchRecyclerView = this.view.findViewById ( R.id.searchRecyclerView );
        this.tv_no_results_found = this.view.findViewById ( R.id.tv_no_results_found );
        this.tv_results_found = this.view.findViewById ( R.id.tv_results_found );
        this.layout_results_found = this.view.findViewById ( R.id.layout_results_found );
    }

    private void dataToView() {

        this.searchView.setOnQueryTextListener ( new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String minCharacter = newText.toLowerCase ( );
                if(ObjectUtil.isEmpty ( minCharacter )) {
                    //TODO if user clear searchView then show no result found text and hide list
                    tv_no_results_found.setVisibility ( View.VISIBLE );
                    layout_results_found.setVisibility ( View.GONE );
                    searchRecyclerView.setVisibility ( View.GONE );
                    new Handler ( ).postDelayed ( new Runnable ( ) {
                        @Override
                        public void run() {
                            hideKeyboard ( getActivity ( ) );
                            searchView.clearFocus ( );
                        }
                    }, 100 );
                }

                if(minCharacter.length ( ) >= 3) {
                    getAllSearchResults ( minCharacter );
                }
                return true;
            }
        } );
    }

    private void getAllSearchResults(String minCharacter) {
        progress.showProgressBar ( );
        apiInterface.getAllSearchResults ( "webservice/search.php?query=" + minCharacter + "&id_lang=" + selectedLangId ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );
                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "searchApi" );
                        EOSearchList eoSearchList = JsonParser.getInstance ( ).getObject ( EOSearchList.class, tempArray[ 1 ] );

                        if(!ObjectUtil.isEmpty ( eoSearchList )) {
                            if(eoSearchList.getStatus ( ).equalsIgnoreCase ( "success" )) {

                                if(eoSearchList.getMessage ( ).equalsIgnoreCase ( "uploaded success" )) {
                                    hideKeyboard ( getActivity ( ) );
                                    layout_results_found.setVisibility ( View.VISIBLE );
                                    tv_no_results_found.setVisibility ( View.GONE );
                                    tv_results_found.setText ( String.valueOf ( eoSearchList.getPayload ( ).getTotalResultsFound ( ) ) );

                                    //TODO send array from here to SearchAdapter to show results
                                    searchRecyclerView.setVisibility ( View.VISIBLE );
                                    searchRecyclerView.setHasFixedSize ( true );
                                    searchRecyclerView.setLayoutManager ( new GridLayoutManager ( getActivity ( ), 2 ) );
                                    searchRecyclerView.setAdapter ( new SearchAdapter ( getContext ( ), (ArrayList <EOSearchData>) eoSearchList.getPayload ( ).getData ( ) ) );
                                }
                                if(eoSearchList.getMessage ( ).equalsIgnoreCase ( "No result found" )) {
                                    progress.hideProgressBar ( );
                                    tv_no_results_found.setVisibility ( View.VISIBLE );
                                    layout_results_found.setVisibility ( View.GONE );
                                    searchRecyclerView.setVisibility ( View.GONE );
                                }
                            } else {
                                progress.hideProgressBar ( );
                                tv_no_results_found.setVisibility ( View.VISIBLE );
                                layout_results_found.setVisibility ( View.GONE );
                                searchRecyclerView.setVisibility ( View.GONE );
                                Toast.makeText ( getActivity ( ), "" + eoSearchList.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
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
                    //Toast.makeText ( getActivity ( ), "Server is Under Maintenance.", Toast.LENGTH_SHORT ).show ( );
                    new GlobalAlertDialog ( getActivity (), false, true ) {
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
    public void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }

    /**
     * Helper to hide the keyboard
     *
     * @param act
     */
    public void hideKeyboard(Activity act) {
        if(act != null && act.getCurrentFocus ( ) != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService ( Activity.INPUT_METHOD_SERVICE );
            inputMethodManager.hideSoftInputFromWindow ( act.getCurrentFocus ( ).getWindowToken ( ), 0 );
        }
    }

}
