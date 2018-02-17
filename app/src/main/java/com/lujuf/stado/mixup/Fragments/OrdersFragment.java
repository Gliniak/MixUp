package com.lujuf.stado.mixup.Fragments;

/**
 * Created by Gliniak on 17.02.2018.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lujuf.stado.mixup.Adapters.OrderHistoryAdapter;
import com.lujuf.stado.mixup.Database.FirebaseQueries;
import com.lujuf.stado.mixup.Listeners.OrderTouchListener;
import com.lujuf.stado.mixup.Objects.FirebaseDatabaseObject;
import com.lujuf.stado.mixup.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gliniak on 08.02.2018.
 */

public class OrdersFragment extends Fragment {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView order_view;

    private List<Pair<String, FirebaseDatabaseObject.UserOrderHistoryElement>> orderList;
    private OrderHistoryAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        orderList = new ArrayList<Pair<String, FirebaseDatabaseObject.UserOrderHistoryElement> >();

        LoadOrdersData();
        return inflater.inflate(R.layout.fragment_user_orders_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        order_view = getView().findViewById(R.id.order_history_elements_view);

        mAdapter = new OrderHistoryAdapter(orderList, new OrderHistoryAdapter.ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        order_view.setLayoutManager(mLayoutManager);
        order_view.setItemAnimator(new DefaultItemAnimator());
        order_view.setAdapter(mAdapter);

        order_view.addOnItemTouchListener(new OrderTouchListener(this.getContext(), order_view, new OrderTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FirebaseDatabaseObject.UserOrderHistoryElement order = orderList.get(position).second;
                String orderId = orderList.get(position).first;

                //Button history_payment_test_button = view.findViewById(R.id.history_payment_test_button);

                //if(history_payment_test_button != null)
                //{
                 //   if(history_payment_test_button.getVisibility() == View.GONE)
                //        history_payment_test_button.setVisibility(View.VISIBLE);
                //    else history_payment_test_button.setVisibility(View.GONE);
                //}
                Log.d("onClick - order_view", "Pressed OrderId:" + orderId);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        order_view.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {

        // Important lesson!
        // If we want to remove listener we have to be in the same "place" as we creating listener... #PoorEngrish
        //FirebaseQueries.GetUserCart(mDatabase, mAuth.getUid()).removeEventListener(CartListener);
        super.onDestroy();
    }

    @Nullable
    public void LoadOrdersData() {

        Query orderQuery = FirebaseQueries.GetUserOrders(mDatabase, mAuth.getUid());

        orderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("DB", "Reading User Orders...");

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    Pair<String, FirebaseDatabaseObject.UserOrderHistoryElement> order = new Pair<>(singleSnapshot.getKey(), FirebaseDatabaseObject.UserOrderHistoryElement.ConvertFromSnapshot(singleSnapshot));

                    orderList.add(order);

                    mAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}