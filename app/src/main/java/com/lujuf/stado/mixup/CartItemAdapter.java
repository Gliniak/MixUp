package com.lujuf.stado.mixup;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Gliniak on 06.02.2018.
 */

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyViewHolder>
{
    public interface ClickListener {

        void onPositionClicked(int position);

        void onLongClicked(int position);
    }

    private final ClickListener listener;
    private List<FirebaseDatabaseObject.DatabaseSongs> songsList;

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title_author;

        public Button remove_from_cart;
        public Button buy_only_this;


        private WeakReference<ClickListener> listenerRef;

        public MyViewHolder(View view, ClickListener listener) {
            super(view);

            mDatabase = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();

            title_author = view.findViewById(R.id.song_list_title_author);

            remove_from_cart = view.findViewById(R.id.remove_from_cart);
            buy_only_this = view.findViewById(R.id.buy_item_from_cart);

            listenerRef = new WeakReference<>(listener);
            remove_from_cart.setOnClickListener(this);
            buy_only_this.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            if (v.getId() == remove_from_cart.getId()) {
                FirebaseDatabaseObject.DatabaseSongs song = songsList.get(getAdapterPosition());
                Toast.makeText(v.getContext(), "You removed a song from your Cart: " + song.songData.Name, Toast.LENGTH_SHORT).show();

                // Why it removes wrong one?
                songsList.remove(song);

                mDatabase.getReference().child("Users").child(mAuth.getUid()).child("Cart").child(song.SongID).removeValue();

            } else {
                //Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }


    public CartItemAdapter(List<FirebaseDatabaseObject.DatabaseSongs> songsList, ClickListener listener) {
        this.songsList = songsList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_row, parent, false);

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
        FirebaseDatabaseObject.DatabaseSongs song = songsList.get(position);

        holder.title_author.setText(song.GetSongData().GetSongTitle());

        if(song.GetSongData().price == 0.0f)
            holder.buy_only_this.setText("Free");
        else holder.buy_only_this.setText(String.valueOf(song.GetSongData().price) + " zł");
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

}
