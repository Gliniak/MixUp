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
        public boolean buyed;
        public int rated;
        public boolean IsMy;

        public FirebaseDatabaseObjectUserSongs()
        {
            this.SongID = 0;
            this.buyed = false;
            this.rated = -1;
            this.IsMy = false;
        }

        public FirebaseDatabaseObjectUserSongs(long SongId, boolean isBuyed, int rated, boolean IsMy)
        {
            this.SongID = SongId;
            this.buyed = isBuyed;
            this.rated = rated;
            this.IsMy = IsMy;
        }
    }
}

