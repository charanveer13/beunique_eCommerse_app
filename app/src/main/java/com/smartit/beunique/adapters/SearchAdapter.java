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
import com.smartit.beunique.entity.search.EOSearchData;
import com.smartit.beunique.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import static com.smartit.beunique.util.Constants.BASE_URL;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_CONVERSION_RATE;
import static com.smartit.beunique.util.Constants.SELECTED_CURRENCY_SIGN;

/**
 * Created by android on 20/3/19.
 */

public class SearchAdapter extends RecyclerView.Adapter <SearchAdapter.ViewHolder> {

    private Context context;
    private ArrayList <EOSearchData> searchDataArrayList;
    private SessionSecuredPreferences securedPreferences;
    private String selectedCurrencySign, selectedConversionRate;

    public SearchAdapter(Context context, ArrayList <EOSearchData> searchDataArrayList) {
        this.context = context;
        this.searchDataArrayList = searchDataArrayList;

        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedCurrencySign = this.securedPreferences.getString ( SELECTED_CURRENCY_SIGN, "" );
        this.selectedConversionRate = this.securedPreferences.getString ( SELECTED_CURRENCY_CONVERSION_RATE, "" );
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_product_page, parent, false );
        return new SearchAdapter.ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        final EOSearchData eoSearchData = this.searchDataArrayList.get ( position );

        if(!ObjectUtil.isEmpty ( eoSearchData )) {
            if(!ObjectUtil.isEmpty ( eoSearchData.getProductName ( ) ))
                holder.tvTitle.setText ( eoSearchData.getProductName ( ) );

            if(!ObjectUtil.isEmpty ( eoSearchData.getManufacturerName ( ) ))
                holder.tv_manufacturer_name.setText ( eoSearchData.getManufacturerName ( ) );

            double mainPrice = Double.parseDouble ( eoSearchData.getPrice ( ) ) * Double.parseDouble ( selectedConversionRate );
            holder.tv_main_price.setText ( String.format ( Locale.US, "%.2f", mainPrice ).concat ( " " ).concat ( selectedCurrencySign ) );

            //TODO load product images form here
            loadImages ( BASE_URL + "api/images/products/" + eoSearchData.getIdProduct ( ) + "/" + eoSearchData.getImages ( ) + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV", holder.productImage );

            holder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Intent detailPageIntent = new Intent ( context, ProductDetailsActivity.class );
                    detailPageIntent.putExtra ( "productDetailId", eoSearchData.getIdProduct ( ) );
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
        return ObjectUtil.isEmpty ( searchDataArrayList ) ? 0 : searchDataArrayList.size ( );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView tvTitle, tv_main_price, tv_manufacturer_name;

        public ViewHolder(@NonNull View itemView) {
            super ( itemView );
            tvTitle = itemView.findViewById ( R.id.tvTitle );
            tv_main_price = itemView.findViewById ( R.id.tv_main_price );
            tv_manufacturer_name = itemView.findViewById ( R.id.tv_manufacturer_name );
            productImage = itemView.findViewById ( R.id.productImage );
        }
    }

}
