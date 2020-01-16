package com.smartit.beunique.adapters;

import android.app.Activity;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.MagazinesActivity;
import com.smartit.beunique.entity.magazine.EOMagazineCategory;
import com.smartit.beunique.util.ObjectUtil;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MagazineCategoryAdapter extends RecyclerView.Adapter <MagazineCategoryAdapter.ProductViewHolder> {

    private List <EOMagazineCategory> magazineCategoryList;
    private Context context;

    public MagazineCategoryAdapter(Context context, List <EOMagazineCategory> magazineCategoryList) {
        this.context = context;
        this.magazineCategoryList = magazineCategoryList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_magazine_category, viewGroup, false );
        return new ProductViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int position) {
        final EOMagazineCategory magazineCategory = this.magazineCategoryList.get ( position );
        String[] colorsArray = {"#5B457B", "#D64B29", "#1A2E4D", "#B32F59", "#344E3F"};
        productViewHolder.tv_blog_category.setText ( magazineCategory.getName ( ) );
        productViewHolder.tv_blog_category.setBackgroundResource ( R.drawable.blog_category_drawable );
        GradientDrawable drawable = (GradientDrawable) productViewHolder.tv_blog_category.getBackground ( );
        drawable.setColor ( Color.parseColor ( colorsArray[ position % 5 ] ) );

        productViewHolder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                //TODO reload magazine page from here
                ((Activity) context).finish ( );
                Intent magazinePageIntent = new Intent ( context, MagazinesActivity.class );
                magazinePageIntent.putExtra ( "magazineBlogCategoryId", magazineCategory.getId ( ) );
                magazinePageIntent.putExtra ( "isShowCategoryId", true );
                context.startActivity ( magazinePageIntent );
            }
        } );
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( this.magazineCategoryList ) ? 0 : this.magazineCategoryList.size ( );
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_blog_category;

        public ProductViewHolder(@NonNull View itemView) {
            super ( itemView );
            tv_blog_category = itemView.findViewById ( R.id.tv_blog_category );
        }
    }

}
