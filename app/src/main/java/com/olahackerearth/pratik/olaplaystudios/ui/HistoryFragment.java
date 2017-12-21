package com.olahackerearth.pratik.olaplaystudios.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.olahackerearth.pratik.olaplaystudios.R;
import com.olahackerearth.pratik.olaplaystudios.adapter.HistoryAdapter;
import com.olahackerearth.pratik.olaplaystudios.adapter.SongAdapter;
import com.olahackerearth.pratik.olaplaystudios.database.DBContract;
import com.olahackerearth.pratik.olaplaystudios.database.DBHelper;
import com.olahackerearth.pratik.olaplaystudios.model.History;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {

    /**
     * This fragment is to show the activities / history of the user
     */


    private List<History> historyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HistoryAdapter mAdapter;

    LinearLayoutManager mLayoutManager;

    DBHelper dbHelper;

    View rootView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        dbHelper = DBHelper.getDBHelper(getContext());
        fetchHistoryFromDB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_history, container, false);
        initializeScreen(rootView);
        return rootView;
    }

    public void initializeScreen(View rootView){
        recyclerView = (RecyclerView) rootView.findViewById(R.id.history_recyclerView);
        setView();
    }

    private void setView() {
        mAdapter = new HistoryAdapter(historyList, getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
    /**
     * *
     * fetch History from Database
     * */

    public void fetchHistoryFromDB(){
        String selectQuery = "SELECT  * FROM " + DBContract.History.TABLE_NAME;
        SQLiteDatabase dbw = dbHelper.getWritableDatabase();
        Cursor cursor = dbw.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        printLog("DB len ", cursor.getCount()+"");
        if (cursor.moveToFirst()) {
            do {
                History history = new History(
                        cursor.getString(DBContract.History.COLUMN_INT_TYPE),
                        cursor.getLong(DBContract.History.COLUMN_INT_SONG_ID),
                        cursor.getLong(DBContract.History.COLUMN_INT_TIME_STAMP)
                );
                history.id = cursor.getLong(DBContract.Song.COLUMN_INT_ID);
                historyList.add(history);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public void printLog(String label, String message ){
        Log.e(label, message);
    }

    public void printToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}
