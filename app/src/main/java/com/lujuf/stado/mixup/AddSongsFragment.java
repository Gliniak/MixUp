package com.lujuf.stado.mixup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dnl on 13.02.2018.
 */

public class AddSongsFragment extends Fragment {

    private FirebaseDatabase mDatabase;

    private Button add_song;
    private List<FirebaseDatabaseObject.DatabaseSongs> songsList = new ArrayList<>();

    private DatabaseSongsAdapter mAdapter;
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;

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

        View rootView = inflater.inflate(R.layout.fragment_add_songs, container, false);
        ExpandableListView listView = (ExpandableListView)rootView.findViewById(R.id.elv);
        initData();
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this.getContext(),listDataHeader,listHash);

        return rootView;
    }

    private void initData(){
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("D");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

       super.onCreate(savedInstanceState);



        mDatabase = FirebaseDatabase.getInstance();

        add_song = (Button) getView().findViewById(R.id.add_song_button);

        TextView userMail = (TextView) getView().findViewById(R.id.user_email);

        FirebaseAuth auth = FirebaseAuth.getInstance();
      //  userMail.setText(auth.getCurrentUser().getEmail());

        mAdapter = new DatabaseSongsAdapter(songsList, new DatabaseSongsAdapter.ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        add_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GUI", "Add_song_button");
                String newSongId = mDatabase.getReference().child("Songs").push().getKey();

                EditText etArtist = (EditText)getView().findViewById(R.id.etArtist);
                String author = etArtist.getText().toString();
                EditText etAlbum = (EditText)getView().findViewById(R.id.etAlbum);
                String album = etAlbum.getText().toString();
                EditText etName = (EditText)getView().findViewById(R.id.etName);
                String name = etName.getText().toString();
                EditText etLink = (EditText)getView().findViewById(R.id.etLink);
                String link = etLink.getText().toString();
                EditText etGenre = (EditText)getView().findViewById(R.id.etGenre);
                int genre =Integer.parseInt(etGenre.getText().toString());
                EditText etPrice = (EditText)getView().findViewById(R.id.etPrice);
                float price = Float.parseFloat(etPrice.getText().toString());

                FirebaseDatabaseObject.DatabaseSongs defaultSong;
                defaultSong = new FirebaseDatabaseObject.DatabaseSongs(newSongId, author, album, name, link, genre, 1, price);

                mDatabase.getReference().child("Songs").child(newSongId).setValue(defaultSong.GetSongData());
                mDatabase.getReference().push();
            }
        });

            super.onViewCreated(view, savedInstanceState);
        }

    }

