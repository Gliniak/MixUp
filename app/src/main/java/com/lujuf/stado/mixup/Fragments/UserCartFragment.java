package com.lujuf.stado.mixup.Fragments;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lujuf.stado.mixup.Adapters.CartItemAdapter;
import com.lujuf.stado.mixup.Database.Config;
import com.lujuf.stado.mixup.Database.FirebaseQueries;
import com.lujuf.stado.mixup.Objects.FirebaseDatabaseObject;
import com.lujuf.stado.mixup.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Gliniak on 08.02.2018.
 */

public class UserCartFragment extends Fragment {

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    // For Special Usage
    private String currentPaymentID;

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private float cartPrice;
    private EditText cart_price;
    private Button buy_all;

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
        Intent intent = new Intent(this.getContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        this.getContext().startService(intent);

        cartPrice = 0.0f;

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cart_price = view.findViewById(R.id.cart_price);
        buy_all = getView().findViewById(R.id.buy_all);

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

        buy_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Add Everything to processing payment
                AddToPendings("-1");
                processPayment();
            }
        });

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

    @Override
    public void onDestroy() {

        this.getContext().stopService(new Intent(this.getContext(), PayPalService.class));
        super.onDestroy();
    }

    @Nullable
    public void LoadCartData()
    {
        cartList.clear();
        Query cartQuery = FirebaseQueries.GetUserCart(mDatabase, mAuth.getUid());

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

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                mAdapter.notifyDataSetChanged();
                cart_view_refresh.setRefreshing(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DBFIRE", "onCancelled", databaseError.toException());
            }
        });
    }

    public void processPayment()
    {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(cartPrice)), "PLN", "Payment ID: " + currentPaymentID, PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this.getContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, Config.PAYPAL_REQUEST_CODE);
    }

    public void AddToPendings(String id)
    {
        if(id == "-1")
        {
            currentPaymentID = FirebaseQueries.GetPaymentID(mDatabase, mAuth.getUid()); // .getReference().child("Users").child(mAuth.getUid()).child("PaymentsPending").push().getKey();

            FirebaseDatabaseObject.UserPendingPayments payment;
            payment = new FirebaseDatabaseObject.UserPendingPayments();

            //mDatabase.getReference().child("Users").child(mAuth.getUid()).child("PaymentsPending").child(currentPaymentID).push();

            for (FirebaseDatabaseObject.DatabaseSongs song : cartList) {
                payment.elements.add(song.GetSongID());

                FirebaseQueries.AddNewItemToPendingPayment(mDatabase, mAuth.getUid(), currentPaymentID, song.GetSongID());
            }
        }
    }

    public void RemoveItemsFromCartByPendingList(String paymentid, boolean rejected)
    {
        if(rejected == false)
        {
            Query paymentItems = FirebaseQueries.GetPaymentQuery(mDatabase, mAuth.getUid(), paymentid);

            ValueEventListener paymentValues = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("LOG", "Amount Of elements in payment: " + dataSnapshot.getChildrenCount());

                    final FirebaseDatabaseObject.UserOrderHistoryElement order = new FirebaseDatabaseObject.UserOrderHistoryElement();
                    order.timeStamp = Calendar.getInstance().getTime().toString();
                    order.price = cartPrice;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        Log.d("LOG", "Removing element from cart: " + snapshot.getKey() + " For User: " + mAuth.getUid());

                        order.buyedSongs.add(snapshot.getKey());

                        FirebaseQueries.AddSongToUser(mDatabase, mAuth.getUid(), snapshot.getKey());

                        mDatabase.getReference().child("Users").child(mAuth.getUid()).child("Cart").child(snapshot.getKey()).removeValue();
                        mDatabase.getReference().child("Users").child(mAuth.getUid()).child("PaymentsPending").child(snapshot.getRef().getParent().getKey()).child(snapshot.getKey()).removeValue();
                    }

                    // After Data Read
                    mDatabase.getReference().child("Users").child(mAuth.getUid()).child("OrderHistory").child(dataSnapshot.getKey()).setValue(order);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            // Really needed?
            paymentItems.addListenerForSingleValueEvent(paymentValues);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Config.PAYPAL_REQUEST_CODE)
        {
            if(resultCode == -1)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if(confirmation != null)
                {
                    try
                    {
                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        RemoveItemsFromCartByPendingList(currentPaymentID, false);

                        // Need to add here summary activity or something

                        Toast.makeText(this.getContext(), "Payment Accepted", Toast.LENGTH_SHORT).show();

                        // mDatabase.getReference().child("Users").child(mAuth.getUid()).child("PaymentsPending").child(currentPaymentID).removeValue();
                        // startActivity(new Intent(this.getContext(), PaymentDetails.class)
                        //        .putExtra("PaymentDetails", paymentDetails)
                        //        .putExtra("PaymentAmount", String.valueOf(cartPrice)));


                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }else
            {
                RemoveItemsFromCartByPendingList(currentPaymentID, true);
                Toast.makeText(this.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
    }

}