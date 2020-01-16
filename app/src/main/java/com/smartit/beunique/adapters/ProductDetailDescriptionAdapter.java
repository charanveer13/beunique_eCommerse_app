package com.smartit.beunique.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartit.beunique.R;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.entity.allproducts.EOProductFeature;
import com.smartit.beunique.util.ObjectUtil;

import java.util.ArrayList;

/**
 * Created by android on 9/3/19.
 */

public class ProductDetailDescriptionAdapter extends RecyclerView.Adapter <ProductDetailDescriptionAdapter.DetailViewHolder> {

    private Context context;
    private ArrayList <EOProductFeature> productFeatureArrayList;
    private ArrayList <EOProductFeature> productFeatureValuesArrayList;

    public ProductDetailDescriptionAdapter(Context context, ArrayList <EOProductFeature> productFeature, ArrayList <EOProductFeature> productFeatureValues) {
        setHasStableIds ( true );
        this.context = context;
        this.productFeatureArrayList = productFeature;
        this.productFeatureValuesArrayList = productFeatureValues;
    }

    @NonNull
    @Override
    public ProductDetailDescriptionAdapter.DetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.product_detail_row_layout, viewGroup, false );
        return new ProductDetailDescriptionAdapter.DetailViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDetailDescriptionAdapter.DetailViewHolder detailViewHolder, int position) {
        EOProductFeature productFeature = this.productFeatureArrayList.get ( position );
        EOProductFeature productFeatureValue = this.productFeatureValuesArrayList.get ( position );

        detailViewHolder.tv_product_feature.setText (productFeature.getName ( ).concat ( " :" )  );

        detailViewHolder.tv_product_feature_value.setText ( productFeatureValue.getName ( ) );
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( this.productFeatureArrayList ) ? 0 : this.productFeatureArrayList.size ( );
    }


    public class DetailViewHolder extends RecyclerView.ViewHolder {

        private BUniqueTextView tv_product_feature, tv_product_feature_value;

        public DetailViewHolder(@NonNull View itemView) {
            super ( itemView );
            tv_product_feature = itemView.findViewById ( R.id.tv_product_feature );
            tv_product_feature_value = itemView.findViewById ( R.id.tv_product_feature_value );
        }
    }


}
