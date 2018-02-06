package com.lujuf.stado.mixup;

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

    @IgnoreExtraProperties
    public static class FirebaseDatabaseObjectUserSongs
    {
        public long SongID;
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

        public FirebaseDatabaseObjectUserSongs()
        {
            this.SongID = 0;
            this.songData = new UserSongData();
        }

        public FirebaseDatabaseObjectUserSongs(long SongId, boolean isBuyed, int rated, boolean IsMy)
        {
            this.SongID = SongId;
            this.songData = new UserSongData(isBuyed, rated, IsMy);
        }

        public long GetSongID() { return SongID; }
        public UserSongData GetSongData() { return songData; }
    }
}

