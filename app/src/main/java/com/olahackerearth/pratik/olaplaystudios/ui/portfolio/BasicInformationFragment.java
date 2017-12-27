package com.olahackerearth.pratik.olaplaystudios.ui.portfolio;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.olahackerearth.pratik.olaplaystudios.R;

public class BasicInformationFragment extends Fragment {

    public BasicInformationFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static BasicInformationFragment newInstance() {
        BasicInformationFragment fragment = new BasicInformationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic_information, container, false);
    }

    public void printLog(String label, String message ){
        Log.e(label, message);
    }

    public void printToast(String message ){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
    }
}
