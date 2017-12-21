package com.olahackerearth.pratik.olaplaystudios.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.olahackerearth.pratik.olaplaystudios.R;
import com.olahackerearth.pratik.olaplaystudios.database.DBHelper;
import com.olahackerearth.pratik.olaplaystudios.model.History;
import com.olahackerearth.pratik.olaplaystudios.model.History;
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.service.PlayBackService;
import com.olahackerearth.pratik.olaplaystudios.service.SongDownloader;
import com.olahackerearth.pratik.olaplaystudios.singleton.Player;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by pratik on 20/12/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    public List<History> historyList;
    private Context mContext;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_single_history, parent, false);
        return new MyViewHolder(itemView);
    }

    public HistoryAdapter(List<History> historyList, Context mContext) {
        this.historyList = historyList;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final History history = historyList.get(position);
        holder.time.setVisibility(View.GONE);


        SongDBModel songDBModel = DBHelper.getDBHelper(mContext).fetchSongNameById((int) history.getId());
//        holder.time.setText(history.getTimeStamp()+"");
        switch (history.getType()){
            case Constant.DATABASE_CONSTANT_HISTORY_DOWNLOAD:
                holder.typeImage.setImageResource(R.drawable.ic_download);
                holder.containt.setText("Download Song "+ songDBModel.getSong());
                break;
            case Constant.DATABASE_CONSTANT_HISTORY_PLAY:
                holder.typeImage.setImageResource(R.drawable.ic_play_song);
                holder.containt.setText("Play "+ songDBModel.getSong());
                break;
            case Constant.DATABASE_CONSTANT_HISTORY_TYPE_ADD_TO_FAVORITE:
                holder.typeImage.setImageResource(R.drawable.ic_favorite_hover);
                holder.containt.setText("Add song "+ songDBModel.getSong()+" to favorite list");
                break;
            case Constant.DATABASE_CONSTANT_HISTORY_TYPE_REMOVE_FROM_FAVORITE:
                holder.typeImage.setImageResource(R.drawable.ic_favorite);
                holder.containt.setText("Remove song "+ songDBModel.getSong()+" from favorite list");
                break;
            default:
                holder.typeImage.setVisibility(View.GONE);
                break;
        }
    }



    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView containt, time;
        private ImageView typeImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            containt = (TextView) itemView.findViewById(R.id.item_history_content);
            time = (TextView) itemView.findViewById(R.id.item_history_time);
            typeImage = (ImageView) itemView.findViewById(R.id.item_history_type);
        }
    }
}
