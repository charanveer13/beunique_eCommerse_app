package com.smartit.beunique.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.ProductDetailsActivity;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.allproducts.EOAllProductData;
import com.smartit.beunique.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.smartit.beunique.util.Constants.BASE_URL;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_CONVERSION_RATE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_SIGN;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

/**
 * Created by android on 15/2/19.
 */

public class ShowAllProductsAdapter extends RecyclerView.Adapter <ShowAllProductsAdapter.ProductHolder> {

    private Activity context;
    private ArrayList <EOAllProductData> eoProductsDataList;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;
    private String selectedCurrencySign, selectedConversionRate;
    private String searchText = "";

    public ShowAllProductsAdapter(Activity context, ArrayList <EOAllProductData> eoProductsDataList) {
        this.context = context;
        this.eoProductsDataList = eoProductsDataList;

        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.selectedCurrencySign = this.securedPreferences.getString ( SELECTED_CURRENCY_SIGN, "" );
        this.selectedConversionRate = this.securedPreferences.getString ( SELECTED_CURRENCY_CONVERSION_RATE, "" );
    }

    @NonNull
    @Override
    public ShowAllProductsAdapter.ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_product_page, parent, false );
        return new ShowAllProductsAdapter.ProductHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ShowAllProductsAdapter.ProductHolder holder, int position) {
        if(!ObjectUtil.isEmpty ( eoProductsDataList.get ( position ) )) {
            final EOAllProductData product = this.eoProductsDataList.get ( position );

            if(!ObjectUtil.isEmpty ( product.getName ( ) )) {
                holder.tvTitle.setText ( product.getName ( ) );
            }

            if(!ObjectUtil.isEmpty ( product.getManufacturerName ( ) )) {
                holder.tv_manufacturer_name.setText ( product.getManufacturerName ( ) );
            }

            //TODO load images form here
            if(!ObjectUtil.isEmpty ( product.getAssociations ( ) )) {
                if(!ObjectUtil.isEmpty ( product.getAssociations ( ).getImages ( ) )) {
                    Integer PRODUCT_ID = null;
                    String IMAGE_ID = null;
                    if(product.getId ( ) != null)
                        PRODUCT_ID = product.getId ( );
                    if(product.getAssociations ( ).getImages ( ).get ( 0 ).getId ( ) != null)
                        IMAGE_ID = product.getAssociations ( ).getImages ( ).get ( 0 ).getId ( );
                    loadImages ( BASE_URL + "api/images/products/" + PRODUCT_ID + "/" + IMAGE_ID + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV", holder.productImage );
                }
            }


            if(!ObjectUtil.isEmpty ( product.getPrice ( ) )) {
                double mainPrice = Double.parseDouble ( product.getPrice ( ) ) * Double.parseDouble ( selectedConversionRate );
                holder.tv_main_price.setText ( String.format ( Locale.US, "%.2f", mainPrice ).concat ( " " ).concat ( selectedCurrencySign ) );
            }
//            if(!ObjectUtil.isEmpty ( product.getWholesalePrice ( ) )) {
//                holder.tv_wholesale_price.setText ( dollar.concat ( String.format ( Locale.US, "%.2f", Double.parseDouble ( product.getWholesalePrice ( ) ) ) ) );
//            }

            holder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Intent detailPageIntent = new Intent ( context, ProductDetailsActivity.class );
                    detailPageIntent.putExtra ( "productDetailId", String.valueOf ( product.getId ( ) ) );
                    context.startActivity ( detailPageIntent );
                }
            } );

        }
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get ( )
                .load ( imagePath )
                .error ( R.drawable.icon_no_image )
                .fit ( )
                .centerCrop ( )
                .into ( imageView );
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( eoProductsDataList ) ? 0 : eoProductsDataList.size ( );
    }

    protected static class ProductHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tv_manufacturer_name, tv_main_price, tv_wholesale_price;
        private ImageView productImage;

        public ProductHolder(@NonNull View itemView) {
            super ( itemView );
            tvTitle = itemView.findViewById ( R.id.tvTitle );
            tv_manufacturer_name = itemView.findViewById ( R.id.tv_manufacturer_name );
            tv_main_price = itemView.findViewById ( R.id.tv_main_price );
            tv_main_price = itemView.findViewById ( R.id.tv_main_price );
            tv_wholesale_price = itemView.findViewById ( R.id.tv_wholesale_price );
            productImage = itemView.findViewById ( R.id.productImage );
        }
    }

    public void updateList(List <EOAllProductData> list, String searchText) {
        this.eoProductsDataList = new ArrayList <> ( );
        this.eoProductsDataList.addAll ( list );
        this.searchText = searchText;
        notifyDataSetChanged ( );
    }

}
