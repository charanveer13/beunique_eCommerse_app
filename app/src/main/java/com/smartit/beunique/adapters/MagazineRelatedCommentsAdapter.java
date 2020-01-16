package com.smartit.beunique.adapters;

import com.smartit.beunique.R;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.entity.magazine.EOMagazineComment;
import com.smartit.beunique.util.ObjectUtil;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by android on 28/2/19.
 */

public class MagazineRelatedCommentsAdapter extends RecyclerView.Adapter <MagazineRelatedCommentsAdapter.CommentsViewHolder> {

    private Context context;
    private List <EOMagazineComment> magazineCommentArrayList;

    public MagazineRelatedCommentsAdapter(Context context, List <EOMagazineComment> eoMagazineComments) {
        this.context = context;
        this.magazineCommentArrayList = eoMagazineComments;
    }

    @NonNull
    @Override
    public MagazineRelatedCommentsAdapter.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_comments_layout, viewGroup, false );
        return new MagazineRelatedCommentsAdapter.CommentsViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull MagazineRelatedCommentsAdapter.CommentsViewHolder commentsViewHolder, int position) {
        EOMagazineComment eoMagazineComment = this.magazineCommentArrayList.get ( position );

        commentsViewHolder.tv_comment_by.setText ( eoMagazineComment.getCustomerName ( ) );

        if(!ObjectUtil.isEmpty ( eoMagazineComment.getPostedOn ( ) )) {
            String[] timeStr = eoMagazineComment.getPostedOn ( ).split ( "\\s+" );
            commentsViewHolder.tv_hours_ago.setText ( ObjectUtil.isEmpty ( timeStr[ 0 ] ) ? "yyyy-mm-dd" : timeStr[ 0 ] );
        }

        commentsViewHolder.tv_comment_detail.setText ( eoMagazineComment.getContent ( ) );
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( this.magazineCommentArrayList ) ? 0 : this.magazineCommentArrayList.size ( );
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circleImageView;
        //private RatingBar ratingBar;
        private BUniqueTextView tv_comment_by, tv_hours_ago, tv_comment_detail;

        public CommentsViewHolder(@NonNull View itemView) {
            super ( itemView );
            circleImageView = itemView.findViewById ( R.id.circleImageView );
            //ratingBar = itemView.findViewById ( R.id.ratingBar );
            tv_comment_by = itemView.findViewById ( R.id.tv_comment_by );
            tv_hours_ago = itemView.findViewById ( R.id.tv_hours_ago );
            tv_comment_detail = itemView.findViewById ( R.id.tv_comment_detail );
            tv_comment_detail.setVisibility ( View.VISIBLE );
        }
    }

}
