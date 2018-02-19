package com.lujuf.stado.mixup.Adapters;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.lujuf.stado.mixup.AudioPlayerClass;
import com.lujuf.stado.mixup.Objects.FirebaseDatabaseObject;
import com.lujuf.stado.mixup.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.List;

/**
 * Created by Gliniak on 15.02.2018.
 */

public class MyLibraryAdapter extends RecyclerView.Adapter<MyLibraryAdapter.MyViewHolder>
{
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

        private View mainView;

        private WeakReference<MyLibraryAdapter.ClickListener> listenerRef;

        public MyViewHolder(View view, MyLibraryAdapter.ClickListener listener) {
            super(view);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();

            title_author = view.findViewById(R.id.song_list_title_author);
            song_genre = view.findViewById(R.id.song_list_genre);

            play_song = view.findViewById(R.id.mylib_play_song);
            download_song = view.findViewById(R.id.download_song);

            mainView = view;

            listenerRef = new WeakReference<>(listener);
            download_song.setOnClickListener(this);
            play_song.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if(v.getId() == play_song.getId())
            {
                FirebaseDatabaseObject.DatabaseSongs song = songsList.get(getAdapterPosition());
                Toast.makeText(v.getContext(), "You Playing Song: " + song.songData.Name, Toast.LENGTH_SHORT).show();

                AudioPlayerClass.getInstance().PlaySong(this.mainView, song.GetSongData().SongLink);
            }
            else if (v.getId() == download_song.getId())
            {
                FirebaseDatabaseObject.DatabaseSongs song = songsList.get(getAdapterPosition());
                DownloadSong(song.songData.SongLink);
                Toast.makeText(v.getContext(), "You Downloading Song: " + song.songData.SongLink, Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
            if(listenerRef != null && listenerRef.get() != null)
                listenerRef.get().onPositionClicked(getAdapterPosition());
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
        holder.title_author.setText(song.GetSongData().GetSongTitle());
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public void DownloadSong(String path)
    {
        File sdDir = Environment.getExternalStorageDirectory();
       // URI link = new URI()

        // TODO: THis should be in main fragment.

        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }*/


        // TODO: Huge TODO xD
        if (fileNotNull && fileExist) {
            OutputStream ostream = null;

            InputStream stream = null;
            try {
                stream = new FileInputStream(mFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(stream != null){

                add_song.setEnabled(false);
                showProgressDialog(0);
                // Create a reference to "file"
                storageRef = storageRef.child(selectedUri.getLastPathSegment());

                UploadTask uploadTask = storageRef.putStream(stream);

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        float ts = (float) taskSnapshot.getBytesTransferred() / FileSize;
                        long progress = Math.round(ts * 100);
                        showProgressDialog(progress);
                    }
                });

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideProgressDialog();
                        Toast.makeText(getActivity(), "Uploading failed", Toast.LENGTH_LONG).show();
                        // Handle unsuccessful uploads
                    }
                });

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        add_song.setEnabled(true);
                        hideProgressDialog();
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.e("Url", "DownloadUrl: "+downloadUrl);
                        songUrl=downloadUrl;
                    }
                });
            }
            else
            {
                Toast.makeText(getActivity(), "Getting null file", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getActivity(), "File does not exist", Toast.LENGTH_LONG).show();
        }
    }
}
