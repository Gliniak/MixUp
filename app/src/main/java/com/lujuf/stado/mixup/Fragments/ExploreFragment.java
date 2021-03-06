package com.lujuf.stado.mixup.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lujuf.stado.mixup.Adapters.DatabaseSongsAdapter;
import com.lujuf.stado.mixup.Database.FirebaseQueries;
import com.lujuf.stado.mixup.Objects.FirebaseDatabaseObject;
import com.lujuf.stado.mixup.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Gliniak on 09.01.2018.
 */

public class ExploreFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private Spinner sort_type_spinner;

    private RecyclerView songs_view;
    private SwipeRefreshLayout songs_view_refresh;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        return rootView;
    }

    //@Override
    //public void onAttach(Context context) {
    //    super.onAttach(context);
    // }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        sort_type_spinner = getView().findViewById(R.id.fragment_explore_spinner_sort);
        songs_view = getView().findViewById(R.id.songs_view);
        songs_view_refresh = getView().findViewById(R.id.swipeRefreshLayout);

        mAdapter = new DatabaseSongsAdapter(songsList, new DatabaseSongsAdapter.ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        songs_view.setLayoutManager(mLayoutManager);
        songs_view.setItemAnimator(new DefaultItemAnimator());
        songs_view.setAdapter(mAdapter);

        LoadSongsData();
        songs_view.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        songs_view_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadSongsData();
            }
        });

        sort_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Sort_type_spinner", "Pressed Item: i:" + String.valueOf(i) + " l:" + String.valueOf(l));

                Collections.sort(songsList, new Comparator<FirebaseDatabaseObject.DatabaseSongs>() {
                    @Override
                    public int compare(FirebaseDatabaseObject.DatabaseSongs databaseSongs, FirebaseDatabaseObject.DatabaseSongs t1) {


                        return 0;
                    }
                });
                //songsList.
                switch(i)
                {
                    //case 0:
                        //songsList.
                }
                //Toast.makeText(view.getContext(), "Pressed item: " + String.valueOf(i), Toast.LENGTH_SHORT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    public void LoadSongsData()
    {
        songsList.clear();

        Query songsQuery = FirebaseQueries.GetSongs(mDatabase);

        songsQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("GUI", "show_songs");
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    FirebaseDatabaseObject.DatabaseSongs song = FirebaseDatabaseObject.DatabaseSongs.ConvertFromSnapshot(singleSnapshot);

                    // Any Idea how to implement this in simple way?
                    Query isInUserLib = FirebaseQueries.GetUserSong(mDatabase, mAuth.getUid(), song.SongID);


                    songsList.add(FirebaseDatabaseObject.DatabaseSongs.ConvertFromSnapshot(singleSnapshot));
                    mAdapter.notifyDataSetChanged();
                }
                songs_view_refresh.setRefreshing(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DBFIRE", "onCancelled", databaseError.toException());
            }
        });
    }

}


