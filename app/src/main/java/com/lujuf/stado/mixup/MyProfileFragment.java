package com.lujuf.stado.mixup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gliniak on 09.01.2018.
 */

public class MyProfileFragment extends Fragment {

    private FirebaseDatabase mDatabase;

    private Button dummy_song_button;
    private Button show_songs;
    private TextView songs_list;
    private RecyclerView songs_view;

    private List<FirebaseDatabaseObject.DatabaseSongs> songsList = new ArrayList<>();
    private DatabaseSongsAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        Log.d("GUI", "Avatar onAttach!");
        // TODO Auto-generated method stub
        super.onAttach(context);
        //context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    //@Override
    //public void onAttach(Context context) {
    //    super.onAttach(context);
   // }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance();

        dummy_song_button = (Button) getView().findViewById(R.id.generate_dummy_songs);
        show_songs = (Button) getView().findViewById(R.id.show_songs);
        songs_list = (TextView) getView().findViewById(R.id.song_list);
        songs_view = (RecyclerView) getView().findViewById(R.id.songs_view);

        TextView userMail = (TextView) getView().findViewById(R.id.user_email);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        userMail.setText(auth.getCurrentUser().getEmail());

        mAdapter = new DatabaseSongsAdapter(songsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        songs_view.setLayoutManager(mLayoutManager);
        songs_view.setItemAnimator(new DefaultItemAnimator());
        songs_view.setAdapter(mAdapter);

        dummy_song_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GUI", "dummy_song_button");
                String newSongId = mDatabase.getReference().child("Songs").push().getKey();

                FirebaseDatabaseObject.DatabaseSongs defaultSong;
                defaultSong = new FirebaseDatabaseObject.DatabaseSongs(newSongId, "Ma Ballz", "Yo Ass", "So Big", "WTF BRO", 0, 1);

                mDatabase.getReference().child("Songs").child(newSongId).setValue(defaultSong.GetSongData());
                mDatabase.getReference().push();
            }
        });

        show_songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d("GUI", "show_songs");
                Query songsQuery = mDatabase.getReference().child("Songs");

                songsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                        {
                            songsList.add(FirebaseDatabaseObject.DatabaseSongs.ConvertFromSnapshot(singleSnapshot));
                            mAdapter.notifyDataSetChanged();

                            songs_list.append("SONG ID: " + singleSnapshot.getKey() + "\n");
                            songs_list.append(singleSnapshot.getValue().toString() + "\n");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("DBFIRE", "onCancelled", databaseError.toException());
                    }
                });
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

}
