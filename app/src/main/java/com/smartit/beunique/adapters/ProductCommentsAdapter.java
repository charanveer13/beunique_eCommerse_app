package com.smartit.beunique.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.smartit.beunique.R;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.entity.allproducts.EOProductComment;
import com.smartit.beunique.util.ObjectUtil;
import com.smartit.beunique.util.StringUtil;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by android on 8/3/19.
 */

public class ProductCommentsAdapter extends RecyclerView.Adapter<ProductCommentsAdapter.CommentsViewHolder> {

    private Context context;
    private ArrayList<EOProductComment> eoProductCommentList;

    public ProductCommentsAdapter(Context context, ArrayList<EOProductComment> productCommentList) {
        this.context = context;
        this.eoProductCommentList = productCommentList;
    }

    @NonNull
    @Override
    public ProductCommentsAdapter.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comments_layout, viewGroup, false);
        return new ProductCommentsAdapter.CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCommentsAdapter.CommentsViewHolder commentsViewHolder, int position) {
        EOProductComment productComment = this.eoProductCommentList.get(position);

        commentsViewHolder.tv_comment_by.setText(StringUtil.getStringForID(R.string.name).concat(" : ").concat(productComment.getCustomerName()));

        if (!ObjectUtil.isEmpty(productComment.getDate())) {
            String[] timeStr = productComment.getDate().split("\\s+");
            String date = ObjectUtil.isEmpty(timeStr[0]) ? "yyyy-mm-dd" : timeStr[0];

            commentsViewHolder.tv_hours_ago.setText(StringUtil.getStringForID(R.string.date).concat(" ").concat(date));
        }
        commentsViewHolder.ratingBar.setRating(Float.parseFloat(productComment.getGrade()));
        commentsViewHolder.tv_comment_title.setText(StringUtil.getStringForID(R.string.title).concat(" ").concat(productComment.getTitle()));
        commentsViewHolder.tv_comment_content.setText(StringUtil.getStringForID(R.string.content).concat(" ").concat(productComment.getContent()));
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.eoProductCommentList) ? 0 : this.eoProductCommentList.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circleImageView;
        private BUniqueTextView tv_comment_by, tv_hours_ago, tv_comment_title, tv_comment_content;
        private RatingBar ratingBar;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            tv_comment_by = itemView.findViewById(R.id.tv_comment_by);
            tv_hours_ago = itemView.findViewById(R.id.tv_hours_ago);
            circleImageView.setVisibility(View.GONE);
            tv_comment_title = itemView.findViewById(R.id.tv_comment_title);
            tv_comment_title.setVisibility(View.VISIBLE);
            tv_comment_content = itemView.findViewById(R.id.tv_comment_content);
            tv_comment_content.setVisibility(View.VISIBLE);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingBar.setVisibility(View.VISIBLE);
        }
    }

}
