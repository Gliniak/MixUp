package com.lujuf.stado.mixup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Gliniak on 09.01.2018.
 */

public class FirebaseDatabaseObject
{
    @IgnoreExtraProperties
    public static class FirebaseDatabaseObjectUser
    {
        public String nick;
        public String AvatarLink;

        public FirebaseDatabaseObjectUser()
        {
            this.nick = "";
            this.AvatarLink = "";
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

            // 1 - Rock
            // 2 - Pop etc
            // Maybe Enum?
            public int GenreFlags;

            // 1 - Not Visible
            public int Flags;

            public SongData() {
                this.AuthorID = "";
                this.AlbumID = "";
                this.Name = "";
                this.SongLink = "";
                this.GenreFlags = 0;
                this.Flags = 1;
            }

            public SongData(String Author, String Album, String Name, String link, int Genre, int Flags) {
                this.AuthorID = Author;
                this.AlbumID = Album;
                this.Name = Name;
                this.SongLink = link;
                this.GenreFlags = Genre;
                this.Flags = Flags;
            }

            public String GetSongTitle() { return this.Name; }
        }

        public DatabaseSongs()
        {
            this.SongID = "";
            this.songData = new SongData();
        }

        public DatabaseSongs(String SongId, String Author, String Album, String Name, String link, int Genre, int Flags)
        {
            this.SongID = SongId;
            this.songData = new SongData(Author, Album, Name, link, Genre, Flags);
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

