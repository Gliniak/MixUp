package com.lujuf.stado.mixup.Objects;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Gliniak on 09.01.2018.
 */

public class FirebaseDatabaseObject
{
    public enum GenreTypes
    {
        GENRE_TYPE_NOTDEFINED,
        GENRE_TYPE_ROCK,
        GENRE_TYPE_POP,
        GENRE_TYPE_RAP,
        GENRE_TYPE_METAL,
        // itd
    };

    public static final EnumSet<GenreTypes> GenreTypesSet = EnumSet.allOf(GenreTypes.class);

    public enum SongFlags
    {
        SONG_FLAG_NOTDEFINED,
        SONG_FLAG_FREE,
        // more later?
    };

    public static final EnumSet<SongFlags> songFlagsSet = EnumSet.allOf(SongFlags.class);

    @IgnoreExtraProperties
    public static class FirebaseDatabaseObjectUser
    {
        public String nick;
        public String AvatarLink;

        public List<UserSongs> songs;
        public List<UserCartItem> cart;

        public FirebaseDatabaseObjectUser()
        {
            this.nick = "";
            this.AvatarLink = "";

            cart = null;
            songs = null;
        }

        public static FirebaseDatabaseObjectUser ConvertFromSnapshot(DataSnapshot snap)
        {
            FirebaseDatabaseObjectUser user = new FirebaseDatabaseObjectUser();

            user = snap.getValue(FirebaseDatabaseObjectUser.class);
            return user;
        }
    }

    @IgnoreExtraProperties
    public static class UserPendingPayments
    {
        public List<String> elements;

        public UserPendingPayments()
        {
            elements = new ArrayList<String>();
        }

        public static UserPendingPayments ConvertFromSnapshot(DataSnapshot snap)
        {
            UserPendingPayments payment = new UserPendingPayments();
            payment.elements = snap.getValue(ArrayList.class);

            return payment;
        }
    }

    @IgnoreExtraProperties
    public static class UserOrderHistoryElement
    {
        public String timeStamp;
        public float price;

        public List<String> buyedSongs;

        public UserOrderHistoryElement(){ buyedSongs = new ArrayList<String>(); }

        public static UserOrderHistoryElement ConvertFromSnapshot(DataSnapshot snap)
        {
            UserOrderHistoryElement item;
            item = snap.getValue(UserOrderHistoryElement.class);
            return item;
        }
    }

    public static class UserLibrarySongs
    {
        public List<String> songList;

        public UserLibrarySongs(){songList = new ArrayList<String>(); }
    }

    @IgnoreExtraProperties
    public static class UserCartItem
    {
        public String itemID; // Aka. Song or Album

        public UserCartItem(){
            this.itemID = null;
        }

        public UserCartItem(String itemId){
            this.itemID = itemId;
        }
    }

    // USER DATABASE SONGS STUFF
    @IgnoreExtraProperties
    public static class UserSongs
    {
        public String SongID;
        public UserSongData songData;

        public static class UserSongData {
            public boolean buyed;
            public int rated;
            public boolean IsMy;

            public UserSongData() {
                this.buyed = false;
                this.rated = -1;
                this.IsMy = false;
            }

            public UserSongData(boolean isBuyed, int rated, boolean IsMy) {
                this.buyed = isBuyed;
                this.rated = rated;
                this.IsMy = IsMy;
            }
        }

        public UserSongs()
        {
            this.SongID = " ";
            this.songData = new UserSongData();
        }

        public UserSongs(String SongId, boolean isBuyed, int rated, boolean IsMy)
        {
            this.SongID = SongId;
            this.songData = new UserSongData(isBuyed, rated, IsMy);
        }

        public String GetSongID() { return SongID; }
        public UserSongData GetSongData() { return songData; }
    }

    @IgnoreExtraProperties
    public static class DatabaseSongs
    {

        public String SongID;
        public SongData songData;

        public static class SongData {
            public String AuthorID;
            public String AlbumID;
            public String Name;
            public String SongLink;
            public String Owner_id;
            public float price;

            public String GenreFlags;
           // public GenreTypes GenreFlags;

            // 1 - Not Visible
            public int Flags;

            public SongData() {
                this.AuthorID = "";
                this.AlbumID = "";
                this.Name = "";
                this.GenreFlags = "";       //GenreTypes.GENRE_TYPE_NOTDEFINED;
                this.Flags = 1;
                this.price = 0.0f;
                this.Owner_id="";
            }

            public SongData(String Author, String Album, String Name, String link, String Genre, int Flags, float price) {
                this.AuthorID = Author;
                this.AlbumID = Album;
                this.Name = Name;
                this.SongLink = link;
                this.GenreFlags = "";//GenreFlags.valueOf(String.valueOf(Genre));
                this.Flags = Flags;
                this.price = price;
                this.Owner_id=Owner_id;
            }

            public String GetSongTitle() { return this.Name; }
        }

        public DatabaseSongs()
        {
            this.SongID = "";
            this.songData = new SongData();
        }

        public DatabaseSongs(String SongId, String Author, String Album, String Name, String link, String Genre, int Flags, float price)
        {
            this.SongID = SongId;
            this.songData = new SongData(Author, Album, Name, link, Genre, Flags, price);
        }

        public String GetSongID() { return SongID; }
        public SongData GetSongData() { return songData; }

        public static DatabaseSongs ConvertFromSnapshot(DataSnapshot snap)
        {
            DatabaseSongs song = new DatabaseSongs();

            song.SongID = snap.getKey();

            song.songData.AlbumID = snap.child("AlbumID").getValue().toString();
            song.songData.AuthorID = snap.child("AuthorID").getValue().toString();
            song.songData.Flags = Integer.parseInt(snap.child("Flags").getValue().toString());
            song.songData.Name = snap.child("Name").getValue().toString();
            song.songData.GenreFlags = snap.child("GenreFlags").getValue().toString();
            song.songData.price = Float.parseFloat(snap.child("price").getValue().toString());
            song.songData.SongLink = snap.child("SongLink").getValue().toString();
            //song.songData.SongLink = Uri.parse(snap.child("SongLink").getValue().toString());

            return song;
        }
    }
}

