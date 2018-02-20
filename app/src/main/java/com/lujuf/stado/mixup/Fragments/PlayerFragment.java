package com.lujuf.stado.mixup.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.lujuf.stado.mixup.AudioPlayerClass;
import com.lujuf.stado.mixup.R;

/**
 * Created by Gliniak on 18.02.2018.
 */

public class PlayerFragment extends Fragment {

    ImageButton prevSong;
    ImageButton nextSong;
    ImageButton playSong;
    ImageButton pauseSong;
    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;
    SeekBar positionBar;

    int totalTime;

    @Override
    public void onAttach(Context context) {
        Log.d("GUI", "Avatar onAttach!");
        // TODO Auto-generated method stub
        super.onAttach(context);
        //context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        return rootView;
    }

    //@Override
    //public void onAttach(Context context) {
    //    super.onAttach(context);
    // }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            int currentPosition=msg.what;
            //Update positionBar
            positionBar.setProgress(currentPosition);

            //Update labels
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime-currentPosition);
            remainingTimeLabel.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time){
        String timeLabel ="";
        int min = time / 1000/60;
        int sec = time/1000%60;

        timeLabel = min + ":";
        if (sec<10) timeLabel += "0";
        timeLabel+=sec;

        return timeLabel;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        prevSong = view.findViewById(R.id.player_prev_song);
        nextSong = view.findViewById(R.id.player_next_song);
        playSong = view.findViewById(R.id.player_play_button);
        pauseSong = view.findViewById(R.id.player_stop_button);

        elapsedTimeLabel= getView().findViewById(R.id.timePlayed);
        remainingTimeLabel= getView().findViewById(R.id.timeRemaining);

        // Mediaplayer
        // Kiedy bedzie skonczony upload plików wypełnic requestedSongUri

        //player.getPlayer().setVolume(0.5f,0.5f);
        totalTime = AudioPlayerClass.getInstance().getPlayer().getDuration();

        //PositionBar

        positionBar= getView().findViewById(R.id.positionBar);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    AudioPlayerClass.getInstance().getPlayer().seekTo(progress);
                    positionBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Thread (Update positionBar & timeLabel)
        new Thread(new Runnable(){
                @Override
                        public void run(){
            while(AudioPlayerClass.getInstance().getPlayer()!=null){
                try{
                    Message msg =new Message();
                    msg.what = AudioPlayerClass.getInstance().getPlayer().getCurrentPosition();
                    handler.sendMessage(msg);
                    Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
             }
        }).start();

        prevSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MIXUP - MP3PLAYER:", "Playing Previous Song");
            }
        });

        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MIXUP - MP3PLAYER:", "Playing Next Song");
            }
        });

        playSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!AudioPlayerClass.getInstance().getPlayer().isPlaying())
                {
                    AudioPlayerClass.getInstance().getPlayer().start();
                    playSong.setVisibility(View.INVISIBLE);
                    pauseSong.setVisibility(View.VISIBLE);
                } else {
                    AudioPlayerClass.getInstance().getPlayer().pause();
                    pauseSong.setVisibility(View.INVISIBLE);
                    playSong.setVisibility(View.VISIBLE);
                }

            }
        });

        pauseSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!AudioPlayerClass.getInstance().getPlayer().isPlaying())
                    AudioPlayerClass.getInstance().getPlayer().start();

                pauseSong.setVisibility(View.INVISIBLE);
                playSong.setVisibility(View.VISIBLE);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

}
