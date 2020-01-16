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
import com.smartit.beunique.adapters.WishlistPageAdapter;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.allproducts.EOProductWishlist;
import com.smartit.beunique.entity.allproducts.EOProductWishlistList;
import com.smartit.beunique.networking.JsonParser;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.Constants;
import com.smartit.beunique.util.ObjectUtil;

import java.io.IOException;
import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class WhishlistFragment extends Fragment {

    private View view;
    private NoInternetDialog noInternetDialog;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private SessionSecuredPreferences loginSignupPreference;
    private SearchView searchView;
    private RecyclerView wishlistRecyclerView;
    private static LinearLayout layout_search_with_list;
    private static BUniqueTextView tv_no_items_wishlist;
    private ArrayList <EOProductWishlist> wishlistArray = new ArrayList <> ( );
    private WishlistPageAdapter wishlistPageAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate ( R.layout.fragment_whishlist, container, false );

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
        this.loginSignupPreference = ApplicationHelper.application ( ).loginPreferences ( LOGIN_SIGNUP_PREFERENCE );

        this.searchView = this.view.findViewById ( R.id.searchView );
        this.wishlistRecyclerView = this.view.findViewById ( R.id.wishlistRecyclerView );
        this.layout_search_with_list = this.view.findViewById ( R.id.layout_search_with_list );
        this.tv_no_items_wishlist = this.view.findViewById ( R.id.tv_no_items_wishlist );

        //TODO adapter initialization wishliast adapter
        this.wishlistPageAdapter = new WishlistPageAdapter ( getContext ( ), wishlistArray );
        this.wishlistRecyclerView.setHasFixedSize ( true );
        this.wishlistRecyclerView.setLayoutManager ( new GridLayoutManager ( getActivity ( ), 2 ) );
        this.wishlistRecyclerView.setAdapter ( wishlistPageAdapter );

    }

    private void dataToView() {
        this.searchView.setOnQueryTextListener ( new SearchView.OnQueryTextListener ( ) {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase ( );
                if(ObjectUtil.isEmpty ( newText )) {
                    new Handler ( ).postDelayed ( new Runnable ( ) {
                        @Override
                        public void run() {
                            hideKeyboard ( getActivity ( ) );
                            searchView.clearFocus ( );
                        }
                    }, 100 );
                }

                ArrayList <EOProductWishlist> newList = new ArrayList <> ( );
                for (EOProductWishlist productWishlist : wishlistArray) {
                    String type = productWishlist.getName ( ).toLowerCase ( );
                    if(type.contains ( newText )) {
                        newList.add ( productWishlist );
                    }
                }
                wishlistPageAdapter.updateList ( newList, newText );
                return true;
            }
        } );
    }

    @Override
    public void onResume() {
        super.onResume ( );

        //TODO first check customer login or not
        if(!ObjectUtil.isEmpty ( wishlistArray )) {
            wishlistArray.clear ( );
        }
        checkCustomerId ( );
    }

    private void checkCustomerId() {
        if(!loginSignupPreference.contains ( Constants.SELECTED_CUSTOMER_ID )) {
            new GlobalAlertDialog ( getActivity ( ), false, false ) {
                @Override
                public void onConfirmation() {
                    super.onConfirmation ( );
                }
            }.show ( R.string.please_login_first_to_see_your_wishlist );
        } else {
            getAllWishlistProducts ( loginSignupPreference.getString ( Constants.SELECTED_CUSTOMER_ID, "" ) );
        }
    }

    public static void noItemsInWishlist() {
        tv_no_items_wishlist.setVisibility ( View.VISIBLE );
        layout_search_with_list.setVisibility ( View.GONE );
    }

    private void getAllWishlistProducts(String customerId) {
        progress.showProgressBar ( );
        apiInterface.getAllWishlistProducts ( "webservice/product/wishlist.php/?id_customer=" + customerId + "&id_lang=" + selectedLangId ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "WishlistApi" );
                        EOProductWishlistList productWishlistList = JsonParser.getInstance ( ).getObject ( EOProductWishlistList.class, tempArray[ 1 ] );

                        if(!ObjectUtil.isEmpty ( productWishlistList )) {
                            if(productWishlistList.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                tv_no_items_wishlist.setVisibility ( View.GONE );
                                layout_search_with_list.setVisibility ( View.VISIBLE );

                                wishlistArray.addAll ( productWishlistList.getPayload ( ) );
                                wishlistPageAdapter.notifyDataSetChanged ( );

                            } else {
                                progress.hideProgressBar ( );
                                tv_no_items_wishlist.setVisibility ( View.VISIBLE );
                                layout_search_with_list.setVisibility ( View.GONE );
                                Toast.makeText ( getActivity ( ), "" + productWishlistList.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
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
                    new GlobalAlertDialog ( getActivity ( ), false, true ) {
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
