package com.lujuf.stado.mixup.Database;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by Gliniak on 15.02.2018.
 */


public class FirebaseQueries  {

    FirebaseQueries() {};


    public static Query GetUserCart(FirebaseDatabase db, String UserID) { return db.getReference().child("Users").child(UserID).child("Cart"); }
    public static Query GetUserSongs(FirebaseDatabase db, String UserID) { return db.getReference().child("Users").child(UserID).child("Owned"); }
    public static Query GetUserSong(FirebaseDatabase db, String UserID, String SongID) { return db.getReference().child("Users").child(UserID).child("Owned").child(SongID); }
    public static void AddSongToUser(FirebaseDatabase db, String UserID, String songID) { db.getReference().child("Users").child(UserID).child("Owned").child(songID).setValue(""); }

    //
    public static Query GetPaymentQuery(FirebaseDatabase db, String UserID, String paymentid) { return db.getReference().child("Users").child(UserID).child("PaymentsPending").child(paymentid); }
    public static String GetPaymentID(FirebaseDatabase db, String UserID) { return db.getReference().child("Users").child(UserID).child("PaymentsPending").push().getKey(); }
    public static void AddNewItemToPendingPayment(FirebaseDatabase db, String UserID, String payment, String songId) {
        db.getReference().child("Users").child(UserID).child("PaymentsPending").child(payment).child(songId).setValue("");
    }

    public static Query GetSongs(FirebaseDatabase db) { return db.getReference().child("Songs"); }
    public static Query GetSong(FirebaseDatabase db, String SongID) { return db.getReference().child("Songs").child(SongID); }

}
