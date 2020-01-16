package com.smartit.beunique.expandable;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartit.beunique.R;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * Created by android on 11/2/19.
 */

public class MenuViewHolder extends GroupViewHolder {


    public TextView tv_menu_row_item;
    public ImageView iv_menu_arrow;
    public RelativeLayout layout_menu_row;

    public MenuViewHolder(View itemView) {
        super ( itemView );
        tv_menu_row_item = itemView.findViewById ( R.id.tv_menu_row_item );
        iv_menu_arrow = itemView.findViewById ( R.id.iv_menu_arrow );
        layout_menu_row = itemView.findViewById ( R.id.layout_menu_row );
    }

//    @Override
//    public void expand() {
//        animateExpand ( );
//    }
//
//    @Override
//    public void collapse() {
//        animateCollapse ( );
//    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation ( 360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f );
        rotate.setDuration ( 300 );
        rotate.setFillAfter ( true );
        iv_menu_arrow.setAnimation ( rotate );
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation ( 180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f );
        rotate.setDuration ( 300 );
        rotate.setFillAfter ( true );
        iv_menu_arrow.setAnimation ( rotate );
    }



}





