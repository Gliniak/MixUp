package com.lujuf.stado.mixup.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lujuf.stado.mixup.Objects.FirebaseDatabaseObject;
import com.lujuf.stado.mixup.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Gliniak on 06.02.2018.
 */

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder>
{
    public interface ClickListener {

        void onPositionClicked(int position);

        void onLongClicked(int position);
    }

    private final ClickListener listener;
    private List<Pair<String, FirebaseDatabaseObject.UserOrderHistoryElement> > ordersList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView paymentId;
        public TextView paymentDate;
        public TextView paymentPrice;

        //public Button history_payment_test_button;

        private WeakReference<ClickListener> listenerRef;

        public MyViewHolder(View view, ClickListener listener) {
            super(view);

            paymentId = view.findViewById(R.id.history_payment_id);
            paymentDate = view.findViewById(R.id.history_payment_time);
            paymentPrice = view.findViewById(R.id.history_payment_price);
            //history_payment_test_button = view.findViewById(R.id.history_payment_test_button);

            listenerRef = new WeakReference<>(listener);
        }

        @Override
        public void onClick(View v) {

            Log.d("onClick - MyViewHolder", "Pressed OrderId");
            //if(history_payment_test_button.getVisibility() == View.GONE)
            //    history_payment_test_button.setVisibility(View.VISIBLE);
            //else history_payment_test_button.setVisibility(View.GONE);

            if(listenerRef != null && listenerRef.get() != null)
                listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }


    public OrderHistoryAdapter(List<Pair<String, FirebaseDatabaseObject.UserOrderHistoryElement> > ordersList, ClickListener listener) {
        this.ordersList = ordersList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_history_row, parent, false);

        return new MyViewHolder(itemView, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        FirebaseDatabaseObject.UserOrderHistoryElement order = ordersList.get(position).second;

        holder.paymentId.setText(ordersList.get(position).first);

        // Need To Fix this!
        //DateFormat df = new SimpleDateFormat("yyyy.MM.dd, HH:mm");
        //String date = df.format(order.timeStamp);


        holder.paymentDate.setText(order.timeStamp);
        holder.paymentPrice.setText(String.format("%.3f", order.price) + "zł");

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

}
