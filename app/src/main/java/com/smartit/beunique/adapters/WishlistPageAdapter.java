package com.smartit.beunique.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.ProductDetailsActivity;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.FontAwesomeIcon;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.allproducts.EOProductRemoveWishlist;
import com.smartit.beunique.entity.allproducts.EOProductWishlist;
import com.smartit.beunique.fragments.WhishlistFragment;
import com.smartit.beunique.networking.JsonParser;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.BASE_URL;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_CONVERSION_RATE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_SIGN;

/**
 * Created by android on 15/3/19.
 */

public class WishlistPageAdapter extends RecyclerView.Adapter <WishlistPageAdapter.ViewHolder> {

    private Context context;
    private List <EOProductWishlist> eoProductWishlists;
    private SessionSecuredPreferences securedPreferences;
    private String selectedCurrencySign, selectedConversionRate;
    private String searchText = "";
    private GlobalProgressDialog progress;
    private RestClient.APIInterface apiInterface;

    public WishlistPageAdapter(Context context, ArrayList <EOProductWishlist> productWishlistList) {
        this.context = context;
        this.eoProductWishlists = productWishlistList;

        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedCurrencySign = this.securedPreferences.getString ( SELECTED_CURRENCY_SIGN, "" );
        this.selectedConversionRate = this.securedPreferences.getString ( SELECTED_CURRENCY_CONVERSION_RATE, "" );

        setHasStableIds ( true );
        this.progress = new GlobalProgressDialog ( context );
        this.apiInterface = RestClient.getClient ( );
    }

    @NonNull
    @Override
    public WishlistPageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_wishlist_product, parent, false );
        return new WishlistPageAdapter.ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistPageAdapter.ViewHolder holder, final int position) {
        final EOProductWishlist eoProductWishlist = eoProductWishlists.get ( position );

        if(!ObjectUtil.isEmpty ( eoProductWishlist )) {

            if(!ObjectUtil.isEmpty ( eoProductWishlist.getProductName ( ) )) {
                holder.tvTitle.setText ( eoProductWishlist.getProductName ( ) );
            }

            if(!ObjectUtil.isEmpty ( eoProductWishlist.getManufacturerName ( ) ))
                holder.tv_manufacturer_name.setText ( eoProductWishlist.getManufacturerName ( ) );

            double mainPrice = Double.parseDouble ( eoProductWishlist.getPrice ( ) ) * Double.parseDouble ( selectedConversionRate );
            holder.tv_main_price.setText ( String.format ( Locale.US, "%.2f", mainPrice ).concat ( " " ).concat ( selectedCurrencySign ) );

            //TODO load images form here
            if(!ObjectUtil.isEmpty ( eoProductWishlist.getIdImage ( ) ) && !ObjectUtil.isEmpty ( eoProductWishlist.getIdProduct ( ) )) {
                loadImages ( BASE_URL + "api/images/products/" + eoProductWishlist.getIdProduct ( ) + "/" + eoProductWishlist.getIdImage ( ) + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV", holder.productImage );
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailPageIntent = new Intent (context, ProductDetailsActivity.class);
                    detailPageIntent.putExtra("productDetailId", eoProductWishlist.getIdProduct ());
                    context.startActivity(detailPageIntent);
                }
            });

            holder.font_wishlist_fill.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    new GlobalAlertDialog ( context, true, false ) {
                        @Override
                        public void onConfirmation() {
                            super.onConfirmation ( );
                            removeProductToWishlist ( eoProductWishlist.getId ( ), position );
                        }
                    }.show ( R.string.do_you_want_to_remove_product_from_wishlist );
                }
            } );
        }
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get ( )
                .load ( imagePath )
                .error ( R.drawable.icon_no_image )
                .into ( imageView );
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( eoProductWishlists ) ? 0 : eoProductWishlists.size ( );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView tvTitle, tv_main_price, tv_manufacturer_name;
        private FontAwesomeIcon font_wishlist_fill;

        public ViewHolder(@NonNull View itemView) {
            super ( itemView );
            tvTitle = itemView.findViewById ( R.id.tvTitle );
            tv_main_price = itemView.findViewById ( R.id.tv_main_price );
            tv_manufacturer_name = itemView.findViewById ( R.id.tv_manufacturer_name );
            productImage = itemView.findViewById ( R.id.productImage );
            font_wishlist_fill = itemView.findViewById ( R.id.font_wishlist_fill );
        }
    }

    private void removeProductToWishlist(String wishlistId, final int position) {
        // progress.showProgressBar ( );
        this.apiInterface.removeProductToWishlist ( wishlistId ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                //progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "RemoveWishlistApi" );
                        EOProductRemoveWishlist eoProductRemoveWishlist = JsonParser.getInstance ( ).getObject ( EOProductRemoveWishlist.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoProductRemoveWishlist )) {
                            if(eoProductRemoveWishlist.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                //TODO refresh adapter
                                eoProductWishlists.remove ( position );
                                Toast.makeText ( context, "" + eoProductRemoveWishlist.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                                notifyDataSetChanged ( );
                                if(eoProductWishlists.size ( ) == 0) {
                                    WhishlistFragment.noItemsInWishlist ( );
                                }
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
                    new GlobalAlertDialog ( context, false, true ) {
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void updateList(List <EOProductWishlist> list, String searchText) {
        this.eoProductWishlists = new ArrayList <> ( );
        this.eoProductWishlists.addAll ( list );
        this.searchText = searchText;
        notifyDataSetChanged ( );
    }

}
