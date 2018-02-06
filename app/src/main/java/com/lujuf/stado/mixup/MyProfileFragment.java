package com.lujuf.stado.mixup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Gliniak on 09.01.2018.
 */

public class MyProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {


        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        TextView userMail = (TextView) getView().findViewById(R.id.user_email);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        userMail.setText(auth.getCurrentUser().getEmail());

        super.onViewCreated(view, savedInstanceState);
    }
}
