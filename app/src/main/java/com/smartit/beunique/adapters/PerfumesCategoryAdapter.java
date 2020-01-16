package com.smartit.beunique.adapters;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.PerfumesMenuSubCategoryActivity;
import com.smartit.beunique.entity.drawerMenu.SubMenuCategory;
import com.smartit.beunique.util.ObjectUtil;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by android on 12/2/19.
 */

public class PerfumesCategoryAdapter extends RecyclerView.Adapter <PerfumesCategoryAdapter.ViewHolder> {

    private Context context;
    private List <SubMenuCategory> categoryList;

    public PerfumesCategoryAdapter(Context context, List <SubMenuCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.perfumes_category_row, parent, false );
        return new PerfumesCategoryAdapter.ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull PerfumesCategoryAdapter.ViewHolder holder, int position) {
        if(!ObjectUtil.isEmpty ( this.categoryList.get ( position ) )) {
            final SubMenuCategory menuCategory = this.categoryList.get ( position );

            if(!ObjectUtil.isEmpty ( menuCategory.getName ( ) ))
                holder.tv_perfumes_category_name.setText ( menuCategory.getName ( ) );

            //TODO set here images for sub_category menu
            switch (menuCategory.getId ( )) {
                case 336:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_eau_de_toilette );
                    break;
                case 337:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_eau_de_oud );
                    break;
                case 338:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_eau_de_perfume );
                    break;
                case 421:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_hair_perfume );
                    break;
                case 340:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_eau_de_toilette_men );
                    break;
                case 341:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_eau_de_oud_men );
                    break;
                case 342:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_eau_de_perfume );
                    break;
                case 344:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_floral );
                    break;
                case 345:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_fruity );
                    break;
                case 346:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_aromatic );
                    break;
                case 347:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_oriental );
                    break;
                case 348:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_woody );
                    break;
                case 349:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_leather );
                    break;
                case 418:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_citrus );
                    break;
                case 419:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_aqua );
                    break;
                case 366:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_royality );
                    break;
                case 367:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_class );
                    break;
                case 368:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_excutive );
                    break;
                case 369:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_casual );
                    break;
                case 370:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_sexy );
                    break;
                case 377:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_bride );
                    break;
                case 416:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_groom );
                    break;
                case 417:
                    holder.iv_sub_category.setImageResource ( R.drawable.icon_gym );
                    break;
            }


            holder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {
                    Intent productCategoryIntent = new Intent ( view.getContext ( ), PerfumesMenuSubCategoryActivity.class );
                    productCategoryIntent.putExtra ( "menuCategoryId", menuCategory.getId ( ) );
                    view.getContext ( ).startActivity ( productCategoryIntent );
                }
            } );
        }

    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( this.categoryList ) ? 0 : this.categoryList.size ( );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_perfumes_category_name;
        private ImageView iv_sub_category;

        public ViewHolder(@NonNull View itemView) {
            super ( itemView );
            tv_perfumes_category_name = itemView.findViewById ( R.id.tv_perfumes_category_name );
            iv_sub_category = itemView.findViewById ( R.id.iv_sub_category );
        }
    }

}
