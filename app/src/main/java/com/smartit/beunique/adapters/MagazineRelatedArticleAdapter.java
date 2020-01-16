package com.smartit.beunique.adapters;

import android.app.Activity;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.MagazinesDetailActivity;
import com.smartit.beunique.entity.magazine.EOMagazineDetailPayload;
import com.smartit.beunique.util.ObjectUtil;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.smartit.beunique.util.Constants.BASE_URL;

/**
 * Created by android on 28/2/19.
 */

public class MagazineRelatedArticleAdapter extends RecyclerView.Adapter <MagazineRelatedArticleAdapter.ArticleViewHolder> {

    private Context context;
    private ArrayList <EOMagazineDetailPayload> eoMagazineDetailPayloads;

    public MagazineRelatedArticleAdapter(Context context, ArrayList <EOMagazineDetailPayload> eoMagazineDetailPayloads) {
        this.context = context;
        this.eoMagazineDetailPayloads = eoMagazineDetailPayloads;
    }

    @NonNull
    @Override
    public MagazineRelatedArticleAdapter.ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_related_products, viewGroup, false );
        return new MagazineRelatedArticleAdapter.ArticleViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull MagazineRelatedArticleAdapter.ArticleViewHolder articleViewHolder, int position) {
        final EOMagazineDetailPayload eoMagazineDetailPayload = this.eoMagazineDetailPayloads.get ( position );

        loadImages ( eoMagazineDetailPayload.getImageUrl ( ), articleViewHolder.iv_related_product );

        articleViewHolder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                //TODO reload detail page from here
                ((Activity) context).finish ( );
                Intent magazineDetailPageIntent = new Intent ( context, MagazinesDetailActivity.class );
                magazineDetailPageIntent.putExtra ( "magazineBlogObjectId", eoMagazineDetailPayload.getId () );
                context.startActivity ( magazineDetailPageIntent );
            }
        } );
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get ( )
                .load ( BASE_URL + imagePath )
                .error ( R.drawable.icon_no_image )
                .fit ( )
                .centerCrop ( )
                .into ( imageView );
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( this.eoMagazineDetailPayloads ) ? 0 : this.eoMagazineDetailPayloads.size ( );
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_related_product;

        public ArticleViewHolder(@NonNull View itemView) {
            super ( itemView );
            iv_related_product = itemView.findViewById ( R.id.iv_related_product );
        }
    }

}
