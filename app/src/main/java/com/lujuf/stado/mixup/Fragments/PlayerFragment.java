package com.lujuf.stado.mixup.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.database.FirebaseDatabase;
import com.lujuf.stado.mixup.R;

/**
 * Created by Gliniak on 18.02.2018.
 */

public class PlayerFragment extends Fragment {

    ImageButton prevSong;
    ImageButton nextSong;
    ImageButton playSong;
    ImageButton pauseSong;

    @Override
    public void onAttach(Context context) {
        Log.d("GUI", "Avatar onAttach!");
        // TODO Auto-generated method stub
        super.onAttach(context);
        //context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        return rootView;
    }

    //@Override
    //public void onAttach(Context context) {
    //    super.onAttach(context);
    // }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        //FirebaseAuth mAuth = FirebaseAuth.getInstance();

        prevSong = view.findViewById(R.id.player_prev_song);
        nextSong = view.findViewById(R.id.player_next_song);
        playSong = view.findViewById(R.id.player_play_button);
        pauseSong = view.findViewById(R.id.player_stop_button);

        prevSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MIXUP - MP3PLAYER:", "Playing Previous Song");
            }
        });

        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MIXUP - MP3PLAYER:", "Playing Next Song");
            }
        });

        playSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSong.setVisibility(View.INVISIBLE);
                pauseSong.setVisibility(View.VISIBLE);
            }
        });

        pauseSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseSong.setVisibility(View.INVISIBLE);
                playSong.setVisibility(View.VISIBLE);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

}
