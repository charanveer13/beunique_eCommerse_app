package com.smartit.beunique.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartit.beunique.R;
import com.smartit.beunique.application.ApplicationHelper;
import com.smartit.beunique.components.BUniqueTextView;
import com.smartit.beunique.components.SessionSecuredPreferences;
import com.smartit.beunique.entity.pointsSystem.EOPointsSystem;
import com.smartit.beunique.util.ObjectUtil;
import com.smartit.beunique.util.StringUtil;

import java.util.ArrayList;

import static com.smartit.beunique.util.Constants.LANGUAGE_CURRENCY_PREFERENCE;
import static com.smartit.beunique.util.Constants.SELECTED_LANG_ID;

/**
 * Created by android on 18/3/19.
 */

public class PointsSystemAdapter extends RecyclerView.Adapter <PointsSystemAdapter.ViewHolder> {

    private Context context;
    private ArrayList <EOPointsSystem> pointsSystemArrayList;
    private SessionSecuredPreferences securedPreferences;
    private int selectedLangId;

    public PointsSystemAdapter(Context context, ArrayList <EOPointsSystem> pointsSystemArrayList) {
        this.context = context;
        this.pointsSystemArrayList = pointsSystemArrayList;

        this.securedPreferences = ApplicationHelper.application ( ).sharedPreferences ( LANGUAGE_CURRENCY_PREFERENCE );
        this.selectedLangId = this.securedPreferences.getInt ( SELECTED_LANG_ID, 0 );
    }

    @NonNull
    @Override
    public PointsSystemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_points_system, parent, false );
        return new PointsSystemAdapter.ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull PointsSystemAdapter.ViewHolder holder, int position) {
        EOPointsSystem eoPointsSystem = this.pointsSystemArrayList.get ( position );

        holder.tv_order_hash.setText ( selectedLangId == 1 ? StringUtil.getStringForID ( R.string.order_hash ) : "طلب#" );
        holder.tv_order_number.setText ( eoPointsSystem.getIdOrder ( ) );
        holder.tv_order_status.setText ( eoPointsSystem.getPointsStatus ( ) );
        holder.tv_order_date.setText ( eoPointsSystem.getDate ( ) );
        holder.tv_order_points.setText ( eoPointsSystem.getPoints ( ) );
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( this.pointsSystemArrayList.size ( ) ) ? 0 : this.pointsSystemArrayList.size ( );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private BUniqueTextView tv_order_hash, tv_order_number, tv_order_status, tv_order_date, tv_order_points;

        public ViewHolder(View view) {
            super ( view );
            tv_order_hash = view.findViewById ( R.id.tv_order_hash );
            tv_order_number = view.findViewById ( R.id.tv_order_number );
            tv_order_status = view.findViewById ( R.id.tv_order_status );
            tv_order_date = view.findViewById ( R.id.tv_order_date );
            tv_order_points = view.findViewById ( R.id.tv_order_points );
        }
    }

}
