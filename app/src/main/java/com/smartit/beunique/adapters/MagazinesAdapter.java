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
import com.smartit.beunique.activities.MagazinesDetailActivity;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.FontAwesomeIcon;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.magazine.EOMagazineArticle;
import com.smartit.beunique.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.smartit.beunique.util.Constants.BASE_URL;
import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

/**
 * Created by android on 26/2/19.
 */

public class MagazinesAdapter extends RecyclerView.Adapter <MagazinesAdapter.ViewHolder> {

    private Context context;
    private List <EOMagazineArticle> magazinesArrayList;
    private int magazine_row_layout;

    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;

    public MagazinesAdapter(Context context, List <EOMagazineArticle> magazinesArrayList, int magazine_row_layout) {
        this.context = context;
        this.magazinesArrayList = magazinesArrayList;
        this.magazine_row_layout = magazine_row_layout;

        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( magazine_row_layout, parent, false );
        return new MagazinesAdapter.ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final EOMagazineArticle eoMagazine = this.magazinesArrayList.get ( position );

        holder.tv_title_article.setText ( eoMagazine.getName ( ) );
        holder.tv_title_description.setText ( eoMagazine.getContentShort ( ) );
        loadImages ( eoMagazine.getImageUrl ( ), holder.iv_magazine );

        //TODO set here right arrow in read more string basis of language
        holder.icon_angle_arrow.setText ( selectedLangId == 1 ? R.string.icon_angle_next : R.string.icon_angle_prev );

        holder.iv_share.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent ( android.content.Intent.ACTION_SEND );
                shareIntent.setType ( "text/plain" );
                shareIntent.putExtra ( Intent.EXTRA_SUBJECT, "Insert Subject here" );
                String app_url = "https://play.google.com/store/apps/details?id=" + ApplicationHelper.application ( ).packageName ( );
                shareIntent.putExtra ( android.content.Intent.EXTRA_TEXT, app_url );
                context.startActivity ( Intent.createChooser ( shareIntent, "Share via" ) );
            }
        } );

        holder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Intent newsDetailPageIntent = new Intent ( context, MagazinesDetailActivity.class );
                newsDetailPageIntent.putExtra ( "magazineBlogObjectId", eoMagazine.getId ( ) );
                context.startActivity ( newsDetailPageIntent );
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
        return ObjectUtil.isEmpty ( this.magazinesArrayList ) ? 0 : this.magazinesArrayList.size ( );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_magazine, iv_share;
        private BUniqueTextView tv_title_article, tv_title_description, tv_read_more;
        private FontAwesomeIcon icon_angle_arrow;

        public ViewHolder(View itemView) {
            super ( itemView );
            iv_magazine = itemView.findViewById ( R.id.iv_magazine );
            iv_share = itemView.findViewById ( R.id.iv_share );
            tv_title_article = itemView.findViewById ( R.id.tv_title_article );
            tv_title_description = itemView.findViewById ( R.id.tv_title_description );
            tv_read_more = itemView.findViewById ( R.id.tv_read_more );
            icon_angle_arrow = itemView.findViewById ( R.id.icon_angle_arrow );
        }

    }


}
