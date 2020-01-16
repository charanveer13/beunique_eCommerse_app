package com.smartit.beunique.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartit.beunique.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardImagesRecyclerAdapter extends RecyclerView.Adapter <DashboardImagesRecyclerAdapter.ViewHolder> {

    private Context context;
    private View view;
    private ViewHolder viewHolder;
    private List <Integer> stringsList;

    public DashboardImagesRecyclerAdapter(FragmentActivity activity, ArrayList <Integer> imageArrayList) {
        this.context = activity;
        this.stringsList = imageArrayList;
    }

    @NonNull
    @Override
    public DashboardImagesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from ( viewGroup.getContext ( ) ).inflate ( R.layout.dashboard_images_recycler_adapter_row, viewGroup, false );
        viewHolder = new ViewHolder ( view );
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull DashboardImagesRecyclerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.imageAds.setImageResource ( stringsList.get ( i ) );
    }


    @Override
    public int getItemCount() {
        return stringsList.size ( );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageAds;

        public ViewHolder(@NonNull View itemView) {
            super ( itemView );
            imageAds = itemView.findViewById ( R.id.imageAds );
        }
    }


}
