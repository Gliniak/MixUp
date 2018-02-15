package com.lujuf.stado.mixup.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.lujuf.stado.mixup.R;


/**
 * Created by Gliniak on 09.01.2018.
 */

public class MyProfileFragment extends Fragment {

    private FirebaseDatabase mDatabase;

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

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        TextView userMail = getView().findViewById(R.id.user_email);
        userMail.setText(mAuth.getCurrentUser().getEmail());

        super.onViewCreated(view, savedInstanceState);
    }
}


