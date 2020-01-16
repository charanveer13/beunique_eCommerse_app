package com.smartit.beunique.expandable;

import android.view.View;
import android.widget.TextView;

import com.smartit.beunique.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

/**
 * Created by android on 11/2/19.
 */

public class SubMenuViewHolder extends ChildViewHolder {

    public TextView tv_sub_menu_row_item;

    public SubMenuViewHolder(View itemView) {
        super ( itemView );
        tv_sub_menu_row_item = itemView.findViewById ( R.id.tv_sub_menu_row_item );
    }

}
