package com.lujuf.stado.mixup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gliniak on 08.02.2018.
 */

public class UserCartFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private float cartPrice;
    private EditText cart_price;

    private RecyclerView cart_view;
    private SwipeRefreshLayout cart_view_refresh;

    private List<FirebaseDatabaseObject.DatabaseSongs> cartList = new ArrayList<>();
    private CartItemAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_usercart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        cartPrice = 0.0f;

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cart_price = view.findViewById(R.id.cart_price);

        cart_view = getView().findViewById(R.id.cart_elements_view);
        cart_view_refresh = getView().findViewById(R.id.swipeRefreshLayout);

        mAdapter = new CartItemAdapter(cartList, new CartItemAdapter.ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        cart_view.setLayoutManager(mLayoutManager);
        cart_view.setItemAnimator(new DefaultItemAnimator());
        cart_view.setAdapter(mAdapter);


        cart_view_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cartPrice = 0.0f;
                LoadCartData();
            }
        });

        LoadCartData();

        cart_price.setText(String.valueOf(cartPrice) + " zł");

        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    public void LoadCartData()
    {
        cartList.clear();

        Query cartQuery = mDatabase.getReference().child("Users").child(mAuth.getUid()).child("Cart");

        cartQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cartPrice = 0.0f;
                cartList.clear();
                Log.d("DB", "Reloading User Cart Songs List");

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    Query songQuery = mDatabase.getReference().child("Songs").child(singleSnapshot.getKey());

                    songQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FirebaseDatabaseObject.DatabaseSongs song = FirebaseDatabaseObject.DatabaseSongs.ConvertFromSnapshot(dataSnapshot);

                            cartPrice += song.GetSongData().price;
                            cartList.add(song);
                            cart_price.setText(String.valueOf(cartPrice) + " zł");
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                cart_view_refresh.setRefreshing(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DBFIRE", "onCancelled", databaseError.toException());
            }
        });
    }

}