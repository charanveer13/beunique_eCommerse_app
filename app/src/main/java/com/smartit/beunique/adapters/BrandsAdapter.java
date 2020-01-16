package com.smartit.beunique.adapters;

import com.smartit.beunique.R;
import com.smartit.beunique.activities.ShowAllProductsActivity;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.manufacturers.EOManufacturers;
import com.smartit.beunique.util.ObjectUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

/**
 * Created by android on 16/02/2019.
 */
public class BrandsAdapter extends RecyclerView.Adapter <BrandsAdapter.ViewHolder> {

    private Context context;
    private ArrayList <EOManufacturers> eoManufacturersList;
    private String searchText = "";
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;

    public BrandsAdapter(Context context, ArrayList <EOManufacturers> eoManufacturers) {
        this.context = context;
        this.eoManufacturersList = eoManufacturers;

        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.brands_activity_recycler_row, parent, false );
        return new ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final EOManufacturers eoManufacturer = this.eoManufacturersList.get ( position );

        //holder.tv_brandsName.setText ( eoManufacturer.getName ( ) );

        if(!ObjectUtil.isEmpty ( eoManufacturer.getName ( ) ) || !ObjectUtil.isEmpty ( eoManufacturer.getNameLang ( ) )) {
            holder.tv_brandsName.setText ( selectedLangId == 1 ? eoManufacturer.getName ( ) : eoManufacturer.getNameLang ( ) );
        }


//        if(!ObjectUtil.isEmpty ( eoManufacturer.getName ( ) )) {
//            String title1 = eoManufacturer.getName ( ).toLowerCase ( Locale.getDefault ( ) );
//            holder.tv_brandsName.setText ( eoManufacturer.getNameLang ( ) );
//
//            if(title1.contains ( searchText )) {
//                int startPos = title1.indexOf ( searchText );
//                int endPos = startPos + searchText.length ( );
//                Spannable spanString = Spannable.Factory.getInstance ( ).newSpannable ( holder.tv_brandsName.getText ( ) );
//                spanString.setSpan ( new ForegroundColorSpan ( Color.RED ), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
//                holder.tv_brandsName.setText ( spanString );
//            }
//        }

        holder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Intent productCategoryIntent = new Intent ( view.getContext ( ), ShowAllProductsActivity.class );
                productCategoryIntent.putExtra ( "manufacturerId", eoManufacturer.getId ( ) );
                view.getContext ( ).startActivity ( productCategoryIntent );
            }
        } );
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( this.eoManufacturersList.size ( ) ) ? 0 : this.eoManufacturersList.size ( );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_brandsName;

        public ViewHolder(View view) {
            super ( view );
            tv_brandsName = view.findViewById ( R.id.tv_brandsName );
        }
    }

    public void updateList(List <EOManufacturers> list, String searchText) {
        this.eoManufacturersList = new ArrayList <> ( );
        this.eoManufacturersList.addAll ( list );
        this.searchText = searchText;
        notifyDataSetChanged ( );
    }

}
