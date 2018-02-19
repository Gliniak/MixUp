package com.lujuf.stado.mixup.Fragments;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lujuf.stado.mixup.Adapters.MyLibraryAdapter;
import com.lujuf.stado.mixup.Database.FirebaseQueries;
import com.lujuf.stado.mixup.Objects.FirebaseDatabaseObject;
import com.lujuf.stado.mixup.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dnl on 15.02.2018.
 */

public class MyLibFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView songs_view;
    private SwipeRefreshLayout songs_view_refresh;

    private List<FirebaseDatabaseObject.DatabaseSongs> songsList = new ArrayList<>();
    private MyLibraryAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_my_lib, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        songs_view = getView().findViewById(R.id.mylib_elements_view);
        songs_view_refresh = getView().findViewById(R.id.swipeRefreshLayout);

        mAdapter = new MyLibraryAdapter(songsList, new MyLibraryAdapter.ClickListener() {
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

        super.onViewCreated(view, savedInstanceState);
    }


    @Nullable
    public void LoadSongsData()
    {
        songsList.clear();

        Query myLibSongsQuery = FirebaseQueries.GetUserSongs(mDatabase, mAuth.getUid());

        myLibSongsQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DB", "Load User Songs List");
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    Query songQuery = FirebaseQueries.GetSong(mDatabase, singleSnapshot.getKey());

                    songQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            FirebaseDatabaseObject.DatabaseSongs song = FirebaseDatabaseObject.DatabaseSongs.ConvertFromSnapshot(dataSnapshot);
                            songsList.add(song);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

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
