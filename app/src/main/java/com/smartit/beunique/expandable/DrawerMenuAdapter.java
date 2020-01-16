package com.smartit.beunique.expandable;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.ActivityExplore;
import com.smartit.beunique.activities.PerfumesMenuCategoryActivity;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.currency.EOCurrency;
import com.smartit.beunique.entity.drawerMenu.SubMenuCategory;
import com.smartit.beunique.entity.language.EOLanguage;
import com.smartit.beunique.util.Constants;
import com.smartit.beunique.util.LocalizationHelper;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;


/**
 * Created by android on 11/2/19.
 */

public class DrawerMenuAdapter extends ExpandableRecyclerViewAdapter <MenuViewHolder, SubMenuViewHolder> {

    public DrawerMenuAdapter(List <? extends ExpandableGroup> groups) {
        super ( groups );
    }

    @Override
    public MenuViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext ( ) ).inflate ( R.layout.drawer_menu_row, parent, false );
        return new MenuViewHolder ( view );
    }

    @Override
    public SubMenuViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( parent.getContext ( ) ).inflate ( R.layout.drawer_submenu_row, parent, false );
        return new SubMenuViewHolder ( view );
    }

    @Override
    public void onBindChildViewHolder(SubMenuViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final DrawerSubMenu subMenu = (DrawerSubMenu) group.getItems ( ).get ( childIndex );

        if(subMenu.subMenuName instanceof EOLanguage) {
            EOLanguage eoLanguage = (EOLanguage) subMenu.subMenuName;
            holder.tv_sub_menu_row_item.setText ( eoLanguage.getName ( ) );
        } else if(subMenu.subMenuName instanceof EOCurrency) {
            EOCurrency eoCurrency = (EOCurrency) subMenu.subMenuName;
            holder.tv_sub_menu_row_item.setText ( eoCurrency.getIsoCode ( ) );
        } else if(subMenu.subMenuName instanceof SubMenuCategory) {
            SubMenuCategory subMenuCategory = (SubMenuCategory) subMenu.subMenuName;
            holder.tv_sub_menu_row_item.setText ( subMenuCategory.getName ( ) );
        }

        holder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                if(subMenu.subMenuName instanceof EOLanguage) {
                    EOLanguage eoLanguage = (EOLanguage) subMenu.subMenuName;
                    SessionSecuredPreferences securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
                    securedPreferences.edit ( ).putInt ( Constants.SELECTED_LANG_ID, eoLanguage.getId ( ) ).apply ( );
                    LocalizationHelper.setLocale ( view.getContext ( ), eoLanguage.getIsoCode ( ) );
                    view.getContext ( ).startActivity ( new Intent ( view.getContext ( ), ActivityExplore.class ) );
                } else if(subMenu.subMenuName instanceof SubMenuCategory) {
                    SubMenuCategory subMenuCategory = (SubMenuCategory) subMenu.subMenuName;
                    //from here open new page for category
                    Intent menuCategoryIntent = new Intent ( view.getContext ( ), PerfumesMenuCategoryActivity.class );
                    menuCategoryIntent.putExtra ( "menuCategoryId", subMenuCategory.getId ( ) );
                    view.getContext ( ).startActivity ( menuCategoryIntent );
                } else if(subMenu.subMenuName instanceof EOCurrency) {
                    EOCurrency eoCurrency = (EOCurrency) subMenu.subMenuName;
                    Toast.makeText ( view.getContext ( ), "You Clicked on : " + eoCurrency.getIsoCode ( ), Toast.LENGTH_SHORT ).show ( );

                }
            }
        } );

    }

    @Override
    public void onBindGroupViewHolder(MenuViewHolder holder, final int flatPosition, ExpandableGroup group) {
        final DrawerMenu menu = (DrawerMenu) group;

        holder.tv_menu_row_item.setText ( menu.getTitle ( ) );

//        if(this.isGroupExpanded ( group )) {
//            Toast.makeText ( ApplicationHelper.application (), "true", Toast.LENGTH_SHORT ).show ( );
//        }  else {
//            Toast.makeText ( ApplicationHelper.application (), "false " , Toast.LENGTH_SHORT ).show ( );
//        }





//        holder.layout_menu_row.setOnClickListener ( new View.OnClickListener ( ) {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText ( ApplicationHelper.application ( ), "Title : " + menu.getTitle ( ) + "Position : " + flatPosition, Toast.LENGTH_SHORT ).show ( );
//
//                //Toast.makeText ( view.getContext ( ), "Clicked on : " + menu.getTitle ( ), Toast.LENGTH_SHORT ).show ( );
//            }
//        } );


    }


//        if(menu.getTitle ( ).equals ( StringUtil.getStringForID ( R.string.brands ) )) {
//            brandsPosition = flatPosition;
//        } else if(menu.getTitle ( ).equals ( StringUtil.getStringForID ( R.string.magazine ) )) {
//            magazinePosition = flatPosition;
//        } else if(menu.getTitle ( ).equals ( StringUtil.getStringForID ( R.string.q_and_a ) )) {
//            qandAPosition = flatPosition;
//        }


}

