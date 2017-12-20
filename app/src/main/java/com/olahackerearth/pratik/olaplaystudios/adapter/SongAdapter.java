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
import com.olahackerearth.pratik.olaplaystudios.model.SongDBModel;
import com.olahackerearth.pratik.olaplaystudios.service.PlayBackService;
import com.olahackerearth.pratik.olaplaystudios.service.SongDownloader;
import com.olahackerearth.pratik.olaplaystudios.singleton.Player;
import com.olahackerearth.pratik.olaplaystudios.utility.Constant;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by pratik on 17/12/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    public List<SongDBModel> songList;
    private Context mContext;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_single_song, parent, false);
        return new MyViewHolder(itemView);
    }

    public SongAdapter(List<SongDBModel> songList, Context mContext) {
        this.songList = songList;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SongDBModel song = songList.get(position);
        holder.songName.setText(song.getSong());
        holder.artist.setText(song.getArtists());

        Picasso.with(mContext)
                .load(Uri.parse(song.getCoverImageFullUlr()).buildUpon().build())
                .placeholder(R.drawable.placeholder_song)
                .error(R.drawable.placeholder_song)
                .into(holder.coverImage);

        holder.songName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSong(song);
            }
        });
        holder.artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSong(song);
            }
        });
        holder.coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSong(song);
            }
        });
        if(song.getDownloadStatus().equals(Constant.CONSTANT_SONG_DOWNLOAD_STATUS_DOWNLOADED)){
            holder.downloadSongDBModel.setVisibility(View.GONE);
        } else{
            holder.downloadSongDBModel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int permissionCheck = ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                        holder.downloadSongDBModel.setVisibility(View.GONE);
                        Toast.makeText(mContext, "Downloading...", Toast.LENGTH_SHORT).show();
                        final SongDownloader songDownloader = new SongDownloader(mContext);
                        songDownloader.execute(song);
                    }
                    else {
                        Toast.makeText(mContext, "Permission Required", Toast.LENGTH_SHORT).show();
                        Intent playIntent = new Intent(Constant.ACTION_PERMISSION_REQUEST);
                        playIntent.setData(Uri.parse(song.getUrl()).buildUpon().build());
                        playIntent.putExtra(Constant.INTENT_PASSING_SINGLE_SONG, song);
                        mContext.sendBroadcast(playIntent);
                    }
                }
            });
        }

        if(song.getFavorite().equals(Constant.CONSTANT_SONG_FAVORITE_STATUS_YES)){
            holder.favoriteImage.setImageResource(R.drawable.ic_favorite_hover);
        } else{
            holder.favoriteImage.setImageResource(R.drawable.ic_favorite);
        }
        holder.favoriteImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(song.getFavorite().equals(Constant.CONSTANT_SONG_FAVORITE_STATUS_YES)){
                    song.setFavorite(Constant.CONSTANT_SONG_FAVORITE_STATUS_NOT);
                    holder.favoriteImage.setImageResource(R.drawable.ic_favorite);

                    DBHelper.getDBHelper(mContext).updateSong(song);
                    History history = new History(
                            Constant.DATABASE_CONSTANT_HISTORY_TYPE_REMOVE_FROM_FAVORITE,
                            song.getId(),
                            new Date().getTime()
                    );
                    DBHelper.getDBHelper(mContext).addHistory(history);
                } else{
                    song.setFavorite(Constant.CONSTANT_SONG_FAVORITE_STATUS_YES);
                    holder.favoriteImage.setImageResource(R.drawable.ic_favorite_hover);

                    DBHelper.getDBHelper(mContext).updateSong(song);
                    History history = new History(
                            Constant.DATABASE_CONSTANT_HISTORY_TYPE_ADD_TO_FAVORITE,
                            song.getId(),
                            new Date().getTime()
                    );
                    DBHelper.getDBHelper(mContext).addHistory(history);
                }
            }
        });
    }

    private void playSong(SongDBModel songDBModel) {
        Player.play(songList, songDBModel);
        Intent playIntent = new Intent(mContext, PlayBackService.class);
        playIntent.putExtra(Constant.INTENT_PASSING_SINGLE_SONG, songDBModel);
        mContext.startService(playIntent);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView songName, artist;
        private ImageView coverImage, downloadSongDBModel, favoriteImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.item_song_name);
            artist = (TextView) itemView.findViewById(R.id.item_song_artist);
            coverImage = (ImageView) itemView.findViewById(R.id.item_song_image);
            downloadSongDBModel = (ImageView) itemView.findViewById(R.id.item_history_type);
            favoriteImage = (ImageView) itemView.findViewById(R.id.item_song_image_favorite);
        }
    }
}
