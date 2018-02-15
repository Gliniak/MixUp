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

        public ImageButton play_song;
        public ImageButton buySong;

        private WeakReference<ClickListener> listenerRef;

        public MyViewHolder(View view, ClickListener listener) {
            super(view);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();

            title_author = view.findViewById(R.id.song_list_title_author);
            song_genre = view.findViewById(R.id.song_list_genre);

            play_song = view.findViewById(R.id.song_list_play_song);
            buySong = view.findViewById(R.id.buy_song);

            listenerRef = new WeakReference<>(listener);
            buySong.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == buySong.getId()) {
                FirebaseDatabaseObject.DatabaseSongs song = songsList.get(getAdapterPosition());
                Toast.makeText(v.getContext(), "You Added A new song to your Cart: " + song.songData.Name, Toast.LENGTH_SHORT).show();

                mDatabase.getReference().child("Users").child(mAuth.getUid()).child("Cart").child(song.SongID).setValue("");
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

        //mDatabase.
        //DataSnapshot snap = mDatabase.getReference().child("Users").child(mAuth.getUid()).child("Cart").child(song.SongID);

        //boolean equals = mDatabase.getReference().child("Users").child(mAuth.getUid()).child("Cart").equals(song);
        boolean equals = mDatabase.getReference().child("Users").child(mAuth.getUid()).child("Cart").child(song.SongID).getKey() == song.SongID;

        //if(equals)
        //{
           // holder.buySong.setEnabled(false);
            //holder.buySong.setAlpha(0.0f);
        //}
        /*
        mDatabase.getReference().child("Users").child(mAuth.getUid()).child("Cart").equalTo(song.SongID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot==null || dataSnapshot.getChildren()==null)
                    return;

                holder.buySong.setEnabled(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
        holder.title_author.setText(song.GetSongData().GetSongTitle());
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }
}
