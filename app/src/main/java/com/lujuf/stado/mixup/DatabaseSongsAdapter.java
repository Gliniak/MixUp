package com.lujuf.stado.mixup;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Gliniak on 06.02.2018.
 */



public class DatabaseSongsAdapter extends RecyclerView.Adapter<DatabaseSongsAdapter.MyViewHolder>
{
    public interface ClickListener {

        void onPositionClicked(int position);

        void onLongClicked(int position);
    }

    private final ClickListener listener;
    private List<FirebaseDatabaseObject.DatabaseSongs> songsList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title_author;
        public TextView song_genre;

        public Button play_song;
        public Button buySong;


        private WeakReference<ClickListener> listenerRef;

        public MyViewHolder(View view, ClickListener listener) {
            super(view);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();

            title_author = (TextView) view.findViewById(R.id.song_list_title_author);
            song_genre = (TextView) view.findViewById(R.id.song_list_genre);


            play_song = (Button) view.findViewById(R.id.song_list_play_song);
            buySong = (Button) view.findViewById(R.id.buy_song);

            listenerRef = new WeakReference<>(listener);
            buySong.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            if (v.getId() == buySong.getId()) {
                FirebaseDatabaseObject.DatabaseSongs song = songsList.get(getAdapterPosition());
                Toast.makeText(v.getContext(), "You Added A new song to your Collection: " + song.songData.Name, Toast.LENGTH_SHORT).show();

                FirebaseDatabaseObject.UserSongs u_song = new FirebaseDatabaseObject.UserSongs(song.SongID, true, 10, false);
                mDatabase.getReference().child("Users").child(mAuth.getUid()).child("Songs").child(song.SongID).setValue(u_song);
                mDatabase.getReference().push();

            } else {
                //Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }


    public DatabaseSongsAdapter(List<FirebaseDatabaseObject.DatabaseSongs> songsList, ClickListener listener) {
        this.songsList = songsList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_list_row, parent, false);

        return new MyViewHolder(itemView, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FirebaseDatabaseObject.DatabaseSongs song = songsList.get(position);

        holder.title_author.setText(song.GetSongData().GetSongTitle());
        //holder.buySong.setText(song.songData.GetSongTitle());
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }
}
