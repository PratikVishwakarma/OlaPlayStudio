package com.olahackerearth.pratik.olaplaystudios.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.olahackerearth.pratik.olaplaystudios.R;
import com.olahackerearth.pratik.olaplaystudios.adapter.SongAdapter;
import com.olahackerearth.pratik.olaplaystudios.database.DBHelper;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;

import java.util.ArrayList;
import java.util.List;

public class AllSongsFragment extends Fragment {

    public static final String TYPE = "type";
    public static final String LIST = "list";

    private List<SongDBModel> songListMain;
    private RecyclerView recyclerView;
    private SearchView search;
    private SongAdapter mAdapter;

    LinearLayoutManager mLayoutManager;

    private LinearLayout mLinearScroll;
    int rowSize = 3;
    DBHelper dbHelper;

    View rootView;
    public AllSongsFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static AllSongsFragment newInstance(String type, List<SongDBModel> songList) {
        AllSongsFragment fragment = new AllSongsFragment();
        Bundle args = new Bundle();
        args.putString(TYPE,type);
        args.putParcelableArrayList(LIST, (ArrayList<? extends Parcelable>) songList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(getArguments().containsKey("type")){
                type = getArguments().getString(TYPE);
                //songListMain = getArguments().getParcelableArrayList(LIST);
            }
        }
        songListMain = ((HomeActivity)getActivity()).songListMain;
        printLog("songsfragment", "Length of list: "+songListMain.size());
    }

    String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_all_songs, container, false);
        dbHelper = DBHelper.getDBHelper(getContext());


//        fetchSongsFromDB(type);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeScreen(rootView);
//        filterList(type);
//     CreateDynamicButton();
        setView();
    }

    /**
     * Crating Dynamic Buttons on the paging View
     */
    public void CreateDynamicButton(){

        printLog("CDB()","calling ");
        int rem = songListMain.size() % rowSize;
        if (rem > 0) {
            for (int i = 0; i < rowSize - rem; i++) {
                songListMain.add(null);
            }
        }
        addItem(0);
        int size = songListMain.size() / rowSize;
        for (int j = 0; j < size; j++) {
            final int k;
            k = j;
            final Button btnPage = new Button(getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            btnPage.setTextColor(Color.BLACK);
            btnPage.setTextSize(18.0f);
            btnPage.setId(j);
            btnPage.setText(String.valueOf(j + 1));
            mLinearScroll.addView(btnPage, lp);

            btnPage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    addItem(k);
                }
            });
        }
    }
    /**
     * @param i add item to the songs temp list
     */
    private void addItem(int i) {
        List<SongDBModel> songListTemp =new ArrayList<>();
        // TODO Auto-generated method stub
        songListTemp.clear();
        i = i * rowSize;
        for (int j = 0; j < rowSize; j++) {
            songListTemp.add(j, songListMain.get(i));
            i = i + 1;
        }
//        mAdapter.notifyDataSetChanged();
        setViewPaging(songListTemp);
    }
    /**
     * set recycler view and item click
     */
    private void setViewPaging(List<SongDBModel> list) {
        mAdapter = new SongAdapter(list, getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * set recycler view and item click
     */
    private void setView() {
        mAdapter = new SongAdapter(filterList(type), getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
    /**
     * Basic Initializing function
     */
    public void initializeScreen(View rootView){
        recyclerView = (RecyclerView) rootView.findViewById(R.id.all_songs_recyclerView);
        mLinearScroll = (LinearLayout) rootView.findViewById(R.id.linear_scroll);
        search = (SearchView) rootView.findViewById(R.id.all_song_search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.toString().trim().length() == 0){
                    setView();
                } else{
                    searchForSong(s.toString());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.toString().trim().length() == 0){
                    setView();
                } else{
                    searchForSong(s.toString());
                }
                return false;
            }
        });
    }
    /**
     */
    private void searchForSong(String song){
        List<SongDBModel> songListTemp =new ArrayList<>();
        printLog("search ", song);
//        songDBList.clear();
        for(int i = 0; i< songListMain.size(); i++){
            printLog("search in if ", songListMain.get(i).getSong());
            if(songListMain.get(i).getSong().toLowerCase().contains(song)){
                printLog("search in if ", song);
                songListTemp.add(songListMain.get(i));
            }
        }
        mAdapter = new SongAdapter(songListTemp, getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public List<SongDBModel> filterList(String type){
        printLog("Type of filter ", type);
        printLog("main list ",songListMain.size()+"");
        for(SongDBModel songDBModel :songListMain){
//            printLog("data on list ", songDBModel.getSong());
        }
        List<SongDBModel> songListTemp =new ArrayList<>();
        switch (type){
            case Constant.CONSTANT_SONG_ALL_SONG:
                songListTemp.addAll(songListMain);
                break;
            case Constant.CONSTANT_SONG_DOWNLOADED_SONG:
//                printLog("temp list ",songListMain.size()+"");
                for(SongDBModel songDBModel :songListMain){
                    if(songDBModel!= null){
                        if(songDBModel.getDownloadStatus().equals(Constant.CONSTANT_SONG_DOWNLOAD_STATUS_DOWNLOADED)){
                            songListTemp.add(songDBModel);
                        }
                    } else{
//                        printLog("list chk ", "is null");
                    }
                }
                break;
            case Constant.CONSTANT_SONG_FAVORITE_SONG:
//                printLog("temp list ",songListMain.size()+"");
                for(SongDBModel songDBModel :songListMain){
                    if(songDBModel!= null)
                        if(songDBModel.getFavorite().equals(Constant.CONSTANT_SONG_FAVORITE_STATUS_YES)) {
                            songListTemp.add(songDBModel);
                    } else{
//                            printLog("list chk ", "is null");
                        }
                }
                break;
        }
//        CreateDynamicButton();
        return songListTemp;
//        setView();
    }

    public void printLog(String label, String message ){
        Log.e(label, message);
    }

    public void printToast(String message ){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
    }


    public void refresh() {
        songListMain = ((HomeActivity)getActivity()).songListMain;
        if(mAdapter!=null) {
            mAdapter.songList = filterList(type);
            mAdapter.notifyDataSetChanged();
            if(recyclerView!=null){
                if(recyclerView.getAdapter()==null)
                    recyclerView.setAdapter(mAdapter);
            }
            printLog("songsfragment", "reached the end of logic");

            //
        }
    }
}
