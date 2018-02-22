package com.lujuf.stado.mixup;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
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

    private int queuePos;

    private AudioPlayerClass() {
        Log.d("PLAYER", "Creating Singleton Player Instance");
        player = new MediaPlayer();
        songsQueue = new ArrayList<String>();

        player.reset();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if(!songsQueue.isEmpty())
                {
                    //player.reset();
                    //NextSong();
                }
            }
        });
    }

    public void AddSongsFromLocation(String loc)
    {
        if(loc != null)
        {
            File readDir = new File(loc);
            if(readDir.exists() && readDir.isDirectory())
            {
                File[] files = readDir.listFiles();
                for (File file : files) {
                    if(file.getName().endsWith(".mp3")) {

                        // /storage/emulated/0/
                        String location = file.getAbsolutePath();
                        location = location.replace("/storage/emulated/0/", "/sdcard/");
                        AddSongToList(location);
                    }
                }
            }
        }
    }

    public void AddSongToList(String path)
    {
        if(IsInList(path))
            return;

        songsQueue.add("file://" + path);
        Log.d("APC", "Added Song: " + path);
    }

    public boolean IsInList(String Path)
    {
        return songsQueue.contains(Path);
    }

    public void SetSongPath(String Path)
    {
        Log.d("PLAYER", "Set Path To Song: " + Path);

        try {
            player.setDataSource(Path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MediaPlayer getPlayer() { return player; }

    public void AddNewSongToQueue(String Path)
    {
        songsQueue.add(Path);
    }

    public void NextSong()
    {
        if(++queuePos >= songsQueue.size())
            queuePos = 0;

        player.reset();
        PlaySong(queuePos);
    }

    public void PrevSong()
    {
        if(--queuePos < 0)
            queuePos = songsQueue.size()-1;

        player.reset();
        PlaySong(queuePos);
    }

    public void PlaySong(int pos)
    {
        if(player.isPlaying())
            return;

        // TODO: HACKFIX: Let's say there won't be songs longer than hour
        if(!player.isPlaying() && player.getCurrentPosition() > 1 && player.getCurrentPosition() < (60*60*1000))
        {
            // Something is paused?
            player.start();
            return;
        }

        if(songsQueue.isEmpty())
            return;

        SetSongPath(songsQueue.get(pos));

        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.start();
    }

    public void PauseSong()
    {
        player.pause();
        //pausePosition = player.getCurrentPosition();
    }

    public void StopSong(View view)
    {
        player.stop();
        player.reset();

        // TODO: This is always null
        ImageButton playButton = view.findViewById(R.id.player_play_button);
        ImageButton stopButton = view.findViewById(R.id.player_stop_button);

        if(playButton != null)
            playButton.setVisibility(View.VISIBLE);

        if(stopButton != null)
            stopButton.setVisibility(View.INVISIBLE);

    }

    public void PlaySong(View view, String Path)
    {
        if(player.isPlaying() || player.getCurrentPosition() > 1)
            StopSong(view);

    // TODO: Same here
        ImageButton playButton = view.findViewById(R.id.player_play_button);
        ImageButton stopButton = view.findViewById(R.id.player_stop_button);

        if(playButton != null)
            playButton.setVisibility(View.INVISIBLE);

        if(stopButton != null)
            stopButton.setVisibility(View.VISIBLE);

        SetSongPath(Path);

        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.start();
    }
}
