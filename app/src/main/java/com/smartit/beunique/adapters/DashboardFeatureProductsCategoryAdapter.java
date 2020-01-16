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

import com.smartit.beunique.R;
import com.smartit.beunique.activities.ProductDetailsActivity;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.allproducts.EOAllProductData;
import com.smartit.beunique.util.FontTypeface;
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
 * Created by android on 30/1/19.
 */

public class DashboardFeatureProductsCategoryAdapter extends RecyclerView.Adapter <DashboardFeatureProductsCategoryAdapter.ViewHolder> {

    private Context context;
    private List <EOAllProductData> productsDataList;
    private SessionSecuredPreferences securedPreferences;
    private FontTypeface fontTypeface;
    private int selectedLangId;
    private String selectedCurrencySign, selectedConversionRate;

    public DashboardFeatureProductsCategoryAdapter(Context context, ArrayList <EOAllProductData> productsDataList) {
        this.context = context;
        this.productsDataList = productsDataList;

        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
        this.selectedCurrencySign = this.securedPreferences.getString ( SELECTED_CURRENCY_SIGN, "" );
        this.selectedConversionRate = this.securedPreferences.getString ( SELECTED_CURRENCY_CONVERSION_RATE, "" );
        this.fontTypeface = FontTypeface.getInstance ( context );
    }

    @NonNull
    @Override
    public DashboardFeatureProductsCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.dashboard_product_category_recycler_row, parent, false );
        return new ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardFeatureProductsCategoryAdapter.ViewHolder holder, final int position) {
        final EOAllProductData eoProductsData = productsDataList.get ( position );

        if(!ObjectUtil.isEmpty ( eoProductsData )) {

            if(!ObjectUtil.isEmpty ( eoProductsData.getName ( ) )) {
                holder.tv_product_title.setText ( eoProductsData.getName ( ) );
            }

            if (!ObjectUtil.isEmpty(eoProductsData.getManufacturerName())) {
                if (eoProductsData.getManufacturerName().equals("false")) {
                    holder.tv_manufacturer_name.setVisibility(View.GONE);
                } else {
                    holder.tv_manufacturer_name.setText(eoProductsData.getManufacturerName());
                }
            }

            // String dollar = context.getResources ( ).getString ( R.string.us );
            double mainPrice = Double.parseDouble ( eoProductsData.getPrice ( ) ) * Double.parseDouble ( selectedConversionRate );
            holder.tv_product_price.setText ( String.format ( Locale.US, "%.2f", mainPrice ).concat ( " " ).concat ( selectedCurrencySign ) );

            //load images form here
            if(!ObjectUtil.isEmpty ( eoProductsData.getAssociations ( ) )) {
                if(!ObjectUtil.isEmpty ( eoProductsData.getAssociations ( ).getImages ( ) )) {
                    Integer PRODUCT_ID = null;
                    String IMAGE_ID = null;
                    if(eoProductsData.getId ( ) != null)
                        PRODUCT_ID = eoProductsData.getId ( );
                    if(eoProductsData.getAssociations ( ).getImages ( ).get ( 0 ).getId ( ) != null)
                        IMAGE_ID = eoProductsData.getAssociations ( ).getImages ( ).get ( 0 ).getId ( );
                    loadImages ( BASE_URL + "api/images/products/" + PRODUCT_ID + "/" + IMAGE_ID + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV", holder.productImage );
                }
            }

            holder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Intent detailPageIntent = new Intent ( context, ProductDetailsActivity.class );
                    detailPageIntent.putExtra ( "productDetailId", String.valueOf ( eoProductsData.getId ( ) ) );
                    context.startActivity ( detailPageIntent );
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
        return productsDataList == null ? 0 : productsDataList.size ( );
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView tv_product_title, tv_product_price, tv_manufacturer_name;

        public ViewHolder(@NonNull View itemView) {
            super ( itemView );
            tv_product_title = itemView.findViewById ( R.id.tv_product_title );
            tv_product_price = itemView.findViewById ( R.id.tv_product_price );
            tv_manufacturer_name = itemView.findViewById ( R.id.tv_manufacturer_name );
            productImage = itemView.findViewById ( R.id.productImage );
        }
    }
}
