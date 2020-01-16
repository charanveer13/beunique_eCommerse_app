package com.smartit.beunique.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.ProductDetailsActivity;
import com.smartit.beunique.entity.allproducts.EOAllProductData;
import com.smartit.beunique.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.smartit.beunique.util.Constants.BASE_URL;

public class MagazineRelatedProductAdapter extends RecyclerView.Adapter <MagazineRelatedProductAdapter.ProductViewHolder> {

    private Context context;
    private ArrayList <EOAllProductData> allProductDataArrayList;

    public MagazineRelatedProductAdapter(Context context, ArrayList <EOAllProductData> productsDataList) {
        this.context = context;
        this.allProductDataArrayList = productsDataList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_related_products, viewGroup, false );
        return new ProductViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int position) {
        final EOAllProductData eoProductsData = this.allProductDataArrayList.get ( position );

        //TODO load images form here
        if(!ObjectUtil.isEmpty ( eoProductsData.getAssociations ( ) )) {
            if(!ObjectUtil.isEmpty ( eoProductsData.getAssociations ( ).getImages ( ) )) {
                Integer PRODUCT_ID = null;
                String IMAGE_ID = null;
                if(eoProductsData.getId ( ) != null)
                    PRODUCT_ID = eoProductsData.getId ( );
                if(eoProductsData.getAssociations ( ).getImages ( ).get ( 0 ).getId ( ) != null)
                    IMAGE_ID = eoProductsData.getAssociations ( ).getImages ( ).get ( 0 ).getId ( );
                loadImages ( BASE_URL + "api/images/products/" + PRODUCT_ID + "/" + IMAGE_ID + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV", productViewHolder.iv_related_product );
            }
        }

        productViewHolder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent detailPageIntent = new Intent ( context, ProductDetailsActivity.class );
                detailPageIntent.putExtra ( "productDetailId", String.valueOf ( eoProductsData.getId ( ) ) );
                context.startActivity ( detailPageIntent );
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
        return ObjectUtil.isEmpty ( this.allProductDataArrayList ) ? 0 : this.allProductDataArrayList.size ( );
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_related_product;

        public ProductViewHolder(@NonNull View itemView) {
            super ( itemView );
            iv_related_product = itemView.findViewById ( R.id.iv_related_product );
        }
    }

}
