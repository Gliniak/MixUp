package com.lujuf.stado.mixup;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gliniak on 19.02.2018.
 */

public class AudioPlayerClass {
    private static final AudioPlayerClass ourInstance = new AudioPlayerClass();

    public static AudioPlayerClass getInstance() {
        return ourInstance;
    }

    private MediaPlayer player;
    private List<String> songsQueue;

    private AudioPlayerClass() {
        Log.d("PLAYER", "Creating Singleton Player Instance");
        player = new MediaPlayer();
        songsQueue = new ArrayList<String>();

    }

    public void SetSongPath(Context context, String Path)
    {
        Log.d("PLAYER", "Set Path To Song: " + Path);

        try {
            player.setDataSource(Path);
        } catch (IOException e) {
            Toast.makeText(context, "Missing Sound File", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    public MediaPlayer getPlayer() { return player; }
    public void AddNewSongToQueue(String Path)
    {
        songsQueue.add(Path);
    }

    public void PlaySong(View view, String Path)
    {
        // Pause actual song
        //getPlayer().pause();
        getPlayer().stop();
        ImageButton playButton = view.findViewById(R.id.player_play_button);
        ImageButton stopButton = view.findViewById(R.id.player_stop_button);

        if(playButton != null)
            playButton.setVisibility(View.INVISIBLE);

        if(stopButton != null)
            stopButton.setVisibility(View.VISIBLE);

        SetSongPath(view.getContext(), Path);

        getPlayer().start();
    }
}
