package com.smartit.beunique.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.CartActivity;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.FontAwesomeIcon;
import com.smartit.beunique.components.GlobalAlertDialog;
import com.smartit.beunique.components.GlobalProgressDialog;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.cart.EORemoveCart;
import com.smartit.beunique.entity.cart.EOShowCartResult;
import com.smartit.beunique.entity.cart.EOUpdateCart;
import com.smartit.beunique.networking.JsonParser;
import com.smartit.beunique.networking.RestClient;
import com.smartit.beunique.util.GlobalUtil;
import com.smartit.beunique.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.beunique.util.Constants.BASE_URL;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_CONVERSION_RATE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_SIGN;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

/**
 * Created by android on 26/3/19.
 */

public class CartAdapter extends RecyclerView.Adapter <CartAdapter.ViewHolder> {

    private Context context;
    private ArrayList <EOShowCartResult> addToCartResultArrayList;

    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private String selectedCurrencySign, selectedConversionRate;
    private GlobalProgressDialog progress;
    private RestClient.APIInterface apiInterface;
    private Integer maxLength;

    public CartAdapter(Context context, ArrayList <EOShowCartResult> addToCartResultArrayList) {
        this.context = context;
        this.addToCartResultArrayList = addToCartResultArrayList;

        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.selectedCurrencySign = this.securedPreferences.getString ( SELECTED_CURRENCY_SIGN, "" );
        this.selectedConversionRate = this.securedPreferences.getString ( SELECTED_CURRENCY_CONVERSION_RATE, "" );

        setHasStableIds ( true );
        this.progress = new GlobalProgressDialog ( context );
        this.apiInterface = RestClient.getClient ( );
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_cart, parent, false );
        return new CartAdapter.ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final CartAdapter.ViewHolder holder, final int position) {
        final EOShowCartResult eoAddToCartResult = this.addToCartResultArrayList.get ( position );

        holder.tv_product_name.setText ( eoAddToCartResult.getProductName ( ) );
        holder.tv_brand_name.setText ( eoAddToCartResult.getManufacturerName ( ) );

        if(!ObjectUtil.isEmpty ( eoAddToCartResult.getCartQuantity ( ) ))
            holder.value.setText ( eoAddToCartResult.getCartQuantity ( ) );

        String getTotalQuantity = holder.value.getText ( ).toString ( );

        double mainPrice = Double.parseDouble ( eoAddToCartResult.getPrice ( ) ) * Double.parseDouble ( selectedConversionRate );
        double calculatedPrice = mainPrice * Double.parseDouble ( getTotalQuantity );

        holder.tv_product_price.setText ( String.format ( Locale.US, "%.2f", calculatedPrice ).concat ( " " ).concat ( selectedCurrencySign ) );

        //TODO load images form here
        if(!ObjectUtil.isEmpty ( eoAddToCartResult.getIdImage ( ) ) && !ObjectUtil.isEmpty ( eoAddToCartResult.getIdProduct ( ) )) {
            loadImages ( BASE_URL + "api/images/products/" + eoAddToCartResult.getIdProduct ( ) + "/" + eoAddToCartResult.getIdImage ( ) + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV", holder.productImage );
        }


        holder.icon_remove_product.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                new GlobalAlertDialog ( context, true, false ) {
                    @Override
                    public void onConfirmation() {
                        super.onConfirmation ( );
                        removeItemFromCart ( Integer.parseInt ( eoAddToCartResult.getIdCart ( ) ), position );
                    }
                }.show ( R.string.do_you_want_to_remove_this_item );

            }
        } );


        holder.plus.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                double amount = GlobalUtil.stringToDouble ( getValue ( holder.value ) );
                if(amount != getMaxValue ( holder.value )) {
                    amount++;
                }
                setValue ( holder.value, String.valueOf ( amount ) );

                updateItemInCart ( Integer.parseInt ( eoAddToCartResult.getIdCart ( ) ), Integer.parseInt ( getValue ( holder.value ) ), holder.tv_product_price, holder.value );

            }
        } );


        holder.minus.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                double amount = GlobalUtil.stringToDouble ( getValue ( holder.value ) );
                if(amount == 1) {
                    return;
                } else if(amount - 1 >= 0) {
                    amount--;
                } else {
                    amount = 0;
                }
                setValue ( holder.value, String.valueOf ( amount ) );

                updateItemInCart ( Integer.parseInt ( eoAddToCartResult.getIdCart ( ) ), Integer.parseInt ( getValue ( holder.value ) ), holder.tv_product_price, holder.value );

            }
        } );

    }

    private void updateItemInCart(int cartId, int quantity, final BUniqueTextView priceView, final TextView quantityField) {
        progress.showProgressBar ( );
        this.apiInterface.updateProductInCart ( "webservice/cart/update_cart.php?id_lang=" + selectedLangId, cartId, quantity ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "UpdateCartApi" );
                        EOUpdateCart eoUpdateCart = JsonParser.getInstance ( ).getObject ( EOUpdateCart.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoUpdateCart )) {
                            if(eoUpdateCart.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                Toast.makeText ( context, "" + eoUpdateCart.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                                if(!ObjectUtil.isEmpty ( eoUpdateCart.getPayload ( ) )) {
                                    quantityField.setText ( eoUpdateCart.getPayload ( ).getCartQuantity ( ) );
                                    String getTotalQuantity = quantityField.getText ( ).toString ( );
                                    double mainPrice = Double.parseDouble ( eoUpdateCart.getPayload ( ).getPrice ( ) ) * Double.parseDouble ( selectedConversionRate );
                                    double calculatedPrice = mainPrice * Double.parseDouble ( getTotalQuantity );
                                    priceView.setText ( String.format ( Locale.US, "%.2f", calculatedPrice ).concat ( " " ).concat ( selectedCurrencySign ) );

                                }
                            } else {
                                Toast.makeText ( context, "" + eoUpdateCart.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
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


    private void setValue(TextView value, String number) {
        value.setText ( number.split ( "\\." )[ 0 ] );
    }

    private String getValue(TextView value) {
        return !ObjectUtil.isEmptyView ( value ) ? ObjectUtil.getTextFromView ( value ) : "0";
    }

    private double getMaxValue(TextView value) {
        return maxLength == null ? Long.MAX_VALUE : Math.pow ( 10, maxLength ) - 1;
    }

    private void removeItemFromCart(int cartId, final int position) {
        progress.showProgressBar ( );
        this.apiInterface.removeProductFromCart ( cartId ).enqueue ( new Callback <ResponseBody> ( ) {
            @Override
            public void onResponse(Call <ResponseBody> call, Response <ResponseBody> response) {
                progress.hideProgressBar ( );

                if(!ObjectUtil.isEmpty ( response.body ( ) )) {
                    try {
                        String mainString = response.body ( ).string ( );
                        String tempArray[] = mainString.split ( "RemoveCartApi" );
                        EORemoveCart eoRemoveCart = JsonParser.getInstance ( ).getObject ( EORemoveCart.class, tempArray[ 1 ] );
                        if(!ObjectUtil.isEmpty ( eoRemoveCart )) {
                            if(eoRemoveCart.getStatus ( ).equalsIgnoreCase ( "success" )) {
                                //TODO refresh adapter from here
                                addToCartResultArrayList.remove ( position );
                                Toast.makeText ( context, "" + eoRemoveCart.getMessage ( ), Toast.LENGTH_SHORT ).show ( );
                                notifyDataSetChanged ( );
                                if(addToCartResultArrayList.size ( ) == 0) {
                                    CartActivity.noItemsInCart ( );
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


    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get ( )
                .load ( imagePath )
                .error ( R.drawable.icon_no_image )
                .into ( imageView );
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( this.addToCartResultArrayList.size ( ) ) ? 0 : this.addToCartResultArrayList.size ( );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private BUniqueTextView tv_product_name, tv_brand_name, tv_product_price;
        private FontAwesomeIcon icon_remove_product, minus, plus;
        private TextView value;

        public ViewHolder(View view) {
            super ( view );
            productImage = view.findViewById ( R.id.productImage );
            tv_product_name = view.findViewById ( R.id.tv_product_name );
            icon_remove_product = view.findViewById ( R.id.icon_remove_product );
            tv_brand_name = view.findViewById ( R.id.tv_brand_name );
            tv_product_price = view.findViewById ( R.id.tv_product_price );
            minus = view.findViewById ( R.id.minus );
            plus = view.findViewById ( R.id.plus );
            value = view.findViewById ( R.id.value );
        }
    }

}
