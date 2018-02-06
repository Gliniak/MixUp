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
        public String buyed;
        public String rated;

        public FirebaseDatabaseObjectUserSongs()
        {
            this.buyed = "";
            this.rated = "";
        }
    }
}

