package com.smartit.beunique.adapters;

import com.smartit.beunique.R;
import com.smartit.beunique.entity.allproducts.EOAllProductData;
import com.smartit.beunique.util.ObjectUtil;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by android on 16/2/19.
 */

public class CategoryWiseProductViewPagerAdapter extends PagerAdapter {

    private Context context;
    private EOAllProductData productsData;

    public CategoryWiseProductViewPagerAdapter(Context context, EOAllProductData eoAllProductsData) {
        this.context = context;
        this.productsData = eoAllProductsData;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView ( (View) object );
    }

    @Override
    public int getCount() {
        return this.productsData.getAssociations ( ).getImages ( ) == null ? 0 : this.productsData.getAssociations ( ).getImages ( ).size ( );
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View imageLayout = LayoutInflater.from ( context ).inflate ( R.layout.product_detail_view_pager_adapter, view, false );
        assert imageLayout != null;
        ImageView productImageView = imageLayout.findViewById ( R.id.image );

        //loading images from here
        if(!ObjectUtil.isEmpty ( productsData.getAssociations ( ) )) {
            if(!ObjectUtil.isEmpty ( productsData.getAssociations ( ).getImages ( ) )) {
                Picasso.get ( ).load ( "https://www.beunique.com.sa/api/images/products/" + productsData.getId ( ) + "/" + this.productsData.getAssociations ( ).getImages ( ).get ( position ).getId ( ) + "/?ws_key=RIQADSXL8ARE7C76LWQLEKLTIDFH4RPV" )
                        .error ( R.drawable.icon_no_image )
                        .into ( productImageView );
            }
        }

        view.addView ( imageLayout );
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals ( object );
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
