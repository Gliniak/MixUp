package com.lujuf.stado.mixup.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.lujuf.stado.mixup.Objects.FirebaseDatabaseObject;
import com.lujuf.stado.mixup.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Gliniak on 15.02.2018.
 */

public class MyLibraryAdapter extends RecyclerView.Adapter<MyLibraryAdapter.MyViewHolder>
{
    public interface ClickListener {

        void onPositionClicked(int position);

        void onLongClicked(int position);
    }

    private final MyLibraryAdapter.ClickListener listener;
    private List<FirebaseDatabaseObject.DatabaseSongs> songsList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title_author;
        public TextView song_genre;

        public ImageButton play_song;
        public ImageButton download_song;

        private WeakReference<MyLibraryAdapter.ClickListener> listenerRef;

        public MyViewHolder(View view, MyLibraryAdapter.ClickListener listener) {
            super(view);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();

            title_author = view.findViewById(R.id.song_list_title_author);
            song_genre = view.findViewById(R.id.song_list_genre);

            play_song = view.findViewById(R.id.mylib_play_song);
            download_song = view.findViewById(R.id.download_song);

            listenerRef = new WeakReference<>(listener);
            download_song.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if (v.getId() == download_song.getId())
            {
                FirebaseDatabaseObject.DatabaseSongs song = songsList.get(getAdapterPosition());
                Toast.makeText(v.getContext(), "You Downloading Song: " + song.songData.Name, Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }


    public MyLibraryAdapter(List<FirebaseDatabaseObject.DatabaseSongs> songsList, MyLibraryAdapter.ClickListener listener) {
        this.songsList = songsList;
        this.listener = listener;
    }

    @Override
    public MyLibraryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mylib_song_row, parent, false);

        return new MyLibraryAdapter.MyViewHolder(itemView, new MyLibraryAdapter.ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
    }

    @Override
    public void onBindViewHolder(MyLibraryAdapter.MyViewHolder holder, int position) {

        FirebaseDatabaseObject.DatabaseSongs song = songsList.get(position);
        holder.title_author.setText(song.GetSongData().GetSongTitle());
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }
}
