package com.smartit.beunique.expandable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android on 11/2/19.
 */

public class DrawerSubMenu implements Parcelable {

    public final Object subMenuName;

    public DrawerSubMenu(Object name) {
        this.subMenuName = name;
    }

    protected DrawerSubMenu(Parcel parcel) {
        subMenuName = parcel.readString ( );
    }


    public static final Creator <DrawerSubMenu> CREATOR = new Creator <DrawerSubMenu> ( ) {
        @Override
        public DrawerSubMenu createFromParcel(Parcel in) {
            return new DrawerSubMenu ( in );
        }

        @Override
        public DrawerSubMenu[] newArray(int size) {
            return new DrawerSubMenu[ size ];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue (subMenuName  );
    }
}
