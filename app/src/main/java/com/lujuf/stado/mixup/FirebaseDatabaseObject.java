package com.lujuf.stado.mixup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

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
           // user.songs.
           // song.SongID = snap.getKey();
           // song.songData = snap.getValue(SongData.class);

            return user;
        }
    }


    @IgnoreExtraProperties
    public static class UserCartItem
    {
        public String itemID; // Aka. Song or Album
        public float price;

        public UserCartItem(){
            this.itemID = null;
            this.price = 0.0f;
        }

        public UserCartItem(String itemId, float price){
            this.itemID = itemId;
            this.price = price;
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

            public int GenreFlags;
           // public GenreTypes GenreFlags;

            // 1 - Not Visible
            public int Flags;

            public SongData() {
                this.AuthorID = "";
                this.AlbumID = "";
                this.Name = "";
                this.SongLink = "";
                this.GenreFlags = 0;//GenreTypes.GENRE_TYPE_NOTDEFINED;
                this.Flags = 1;
                this.price = 0.0f;
                this.Owner_id="";
            }

            public SongData(String Author, String Album, String Name, String link, int Genre, int Flags, float price, String Owner_id) {
                this.AuthorID = Author;
                this.AlbumID = Album;
                this.Name = Name;
                this.SongLink = link;
                this.GenreFlags = 0;//GenreFlags.valueOf(String.valueOf(Genre));
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

        public DatabaseSongs(String SongId, String Author, String Album, String Name, String link, int Genre, int Flags, float price, String Owner_id)
        {
            this.SongID = SongId;
            this.songData = new SongData(Author, Album, Name, link, Genre, Flags, price,Owner_id);
        }

        public String GetSongID() { return SongID; }
        public SongData GetSongData() { return songData; }

        public static DatabaseSongs ConvertFromSnapshot(DataSnapshot snap)
        {
            DatabaseSongs song = new DatabaseSongs();

            song.SongID = snap.getKey();
            song.songData = snap.getValue(SongData.class);

            return song;
        }
    }
}

