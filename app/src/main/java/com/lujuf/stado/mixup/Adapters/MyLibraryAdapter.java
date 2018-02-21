package com.lujuf.stado.mixup.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.lujuf.stado.mixup.AudioPlayerClass;
import com.lujuf.stado.mixup.Objects.FirebaseDatabaseObject;
import com.lujuf.stado.mixup.R;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import static android.os.Environment.DIRECTORY_MUSIC;
import static com.lujuf.stado.mixup.Fragments.AddSongsFragment.MY_PERMISSIONS_REQUEST_READ;

/**
 * Created by Gliniak on 15.02.2018.
 */

public class MyLibraryAdapter extends RecyclerView.Adapter<MyLibraryAdapter.MyViewHolder> {
    public interface ClickListener {

        void onPositionClicked(int position);

        void onLongClicked(int position);
    }

    private final MyLibraryAdapter.ClickListener listener;
    private List<FirebaseDatabaseObject.DatabaseSongs> songsList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title_author;
        public TextView song_genre;

        public ImageButton play_song;
        public ImageButton download_song;

        public ProgressBar download_progress_bar;

        private final View mainView;

        private WeakReference<MyLibraryAdapter.ClickListener> listenerRef;

        public MyViewHolder(View view, MyLibraryAdapter.ClickListener listener) {
            super(view);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();

            title_author = view.findViewById(R.id.song_list_title_author);
            song_genre = view.findViewById(R.id.song_list_genre);

            play_song = view.findViewById(R.id.mylib_play_song);
            download_song = view.findViewById(R.id.download_song);

            download_progress_bar = view.findViewById(R.id.download_progress_bar);

            mainView = view;

            listenerRef = new WeakReference<>(listener);
            download_song.setOnClickListener(this);
            play_song.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == play_song.getId()) {
                if(play_song.getTag() == "PLAYING") {
                    play_song.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                    play_song.setTag("PAUSED");
                    AudioPlayerClass.getInstance().PauseSong();
                    return;
                }

                if(play_song.getTag() == "PAUSED") {
                    play_song.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    play_song.setTag("PLAYING");
                    AudioPlayerClass.getInstance().PlaySong(0);
                    return;
                }

                FirebaseDatabaseObject.DatabaseSongs song = songsList.get(getAdapterPosition());
                Toast.makeText(v.getContext(), "You Playing Song: " + song.songData.Name, Toast.LENGTH_SHORT).show();

                CheckPermission(mainView, Manifest.permission.READ_EXTERNAL_STORAGE);
                DownloadTempSong(v, song.songData.SongLink, true);

            } else if (v.getId() == download_song.getId()) {
                CheckPermission(mainView, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                FirebaseDatabaseObject.DatabaseSongs song = songsList.get(getAdapterPosition());
                DownloadSong(v, song.songData.SongLink);
                Toast.makeText(v.getContext(), "You Downloading Song: " + song.songData.SongLink, Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
            if (listenerRef != null && listenerRef.get() != null)
                listenerRef.get().onPositionClicked(getAdapterPosition());
        }

        public void DownloadTempSong(final View view, String path, final boolean autoPlay) {

            // Something wrong with Path just return
            if(!path.contains("/"))
                return;

            final String fileName = path.substring(path.lastIndexOf("/")+1, path.length());

            FirebaseStorage storage = FirebaseStorage.getInstance();

            path = path.replace("/v0/b/mixup-fbafb.appspot.com/o", "");
            StorageReference gsReference = storage.getReferenceFromUrl("gs://mixup-fbafb.appspot.com/" + path);

            File localFile = null;

            try {
                localFile = File.createTempFile("audio", ".mp3");
            } catch (IOException e) {
                e.printStackTrace();
            }

            final String localPath = localFile.getPath();

            gsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d("FIREBASE DOWNLOAD", "FILE DOWNLOADING COMPLETED");

                    if(autoPlay) {
                        AudioPlayerClass.getInstance().PlaySong(view, localPath);
                        play_song.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                        play_song.setTag("PLAYING");
                    }
                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d("FIREBASE DOWNLOAD", "FILE DOWNLOADING...");
                }
            });
        }

        public void DownloadSong(final View view, String path) {

            // Something wrong with Path just return
            if(!path.contains("/"))
                return;

            FirebaseStorage storage = FirebaseStorage.getInstance();
            final String fileName = path.substring(path.lastIndexOf("/")+1, path.length());

            path = path.replace("/v0/b/mixup-fbafb.appspot.com/o", "");

            StorageReference gsReference = storage.getReferenceFromUrl("gs://mixup-fbafb.appspot.com/" + path);

            String sdPath = view.getContext().getApplicationContext().getExternalFilesDir(DIRECTORY_MUSIC).toString();
            final File file = new File(sdPath, fileName);

            download_progress_bar.setVisibility(View.VISIBLE);
            download_song.setVisibility(View.INVISIBLE);

            gsReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    download_progress_bar.setVisibility(View.INVISIBLE);
                    download_song.setImageResource(R.drawable.ic_done_black_24dp);
                    download_song.setVisibility(View.VISIBLE);
                    Log.d("FIREBASE DOWNLOAD", "FILE DOWNLOADING COMPLETED: " +  file.length());//localPath);
                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    float ts = (float) taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                    int progress = Math.round(ts * 100);

                    download_progress_bar.setProgress(progress);
                    Log.d("FIREBASE DOWNLOAD", "FILE DOWNLOADING...");
                }
            });
        }
    }


    public MyLibraryAdapter(List<FirebaseDatabaseObject.DatabaseSongs> songsList, MyLibraryAdapter.ClickListener listener) {
        this.songsList = songsList;
        this.listener = listener;
    }

    @Override
    public MyLibraryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mylib_song_row, parent, false);

        return new MyLibraryAdapter.MyViewHolder(itemView, new MyLibraryAdapter.ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
    }

    @Override
    public void onBindViewHolder(MyLibraryAdapter.MyViewHolder holder, int position) {
        FirebaseDatabaseObject.DatabaseSongs song = songsList.get(position);

        String path = song.songData.SongLink;
        if(!path.isEmpty() && path.contains("/")) {
            final String fileName = path.substring(path.lastIndexOf("/"), path.length());

            String sdPath = holder.mainView.getContext().getApplicationContext().getExternalFilesDir(DIRECTORY_MUSIC).toString();

            final File file = new File(sdPath + fileName);

            if (file.exists())
                holder.download_song.setImageResource(R.drawable.ic_done_black_24dp);
        }

        holder.title_author.setText(song.GetSongData().GetSongTitle());
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }



    public void CheckPermission(View view, String permission) {
        if (ContextCompat.checkSelfPermission(view.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) view.getContext(), permission)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions((Activity) view.getContext(),
                        new String[]{permission},
                        MY_PERMISSIONS_REQUEST_READ);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}
