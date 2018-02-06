package com.lujuf.stado.mixup;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

/**
 * Created by Gliniak on 06.02.2018.
 */

public class DatabaseSongsAdapter extends RecyclerView.Adapter<DatabaseSongsAdapter.MyViewHolder>
{
    private List<FirebaseDatabaseObject.DatabaseSongs> songsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button buySong;

        public MyViewHolder(View view) {
            super(view);
            buySong = (Button) view.findViewById(R.id.buy_song);
        }
    }


    public DatabaseSongsAdapter(List<FirebaseDatabaseObject.DatabaseSongs> songsList) {
        this.songsList = songsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FirebaseDatabaseObject.DatabaseSongs song = songsList.get(position);
        holder.buySong.setText(song.songData.GetSongTitle());
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }
}
