package com.smartit.beunique.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartit.beunique.R;

import am.appwise.components.ni.NoInternetDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class WhatsappFragment extends Fragment {

    private View view;
    private NoInternetDialog noInternetDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate ( R.layout.fragment_whatsapp, container, false );

        this.initView ( );

        return this.view;
    }

    private void initView() {
        this.noInternetDialog = new NoInternetDialog.Builder ( getActivity ( ) ).build ( );
    }

    @Override
    public void onDestroy() {
        super.onDestroy ( );
        this.noInternetDialog.onDestroy ( );
    }

}
