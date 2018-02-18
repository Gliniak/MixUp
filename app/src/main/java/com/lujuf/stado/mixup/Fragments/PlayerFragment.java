package com.lujuf.stado.mixup.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;
import com.lujuf.stado.mixup.R;

/**
 * Created by Gliniak on 18.02.2018.
 */

public class PlayerFragment extends Fragment {

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

        super.onViewCreated(view, savedInstanceState);
    }

}
