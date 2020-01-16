package com.smartit.beunique.expandable;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by android on 11/2/19.
 */

public class DrawerMenu extends ExpandableGroup <DrawerSubMenu> {

    public DrawerMenu(String title, List <DrawerSubMenu> items) {
        super ( title, items );
    }

}
