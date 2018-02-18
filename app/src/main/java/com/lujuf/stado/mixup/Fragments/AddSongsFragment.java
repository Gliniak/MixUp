package com.lujuf.stado.mixup.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lujuf.stado.mixup.Adapters.DatabaseSongsAdapter;
import com.lujuf.stado.mixup.Adapters.ExpandableListAdapter;
import com.lujuf.stado.mixup.Objects.FirebaseDatabaseObject;
import com.lujuf.stado.mixup.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dnl on 13.02.2018.
 */

public class AddSongsFragment extends Fragment {

    private FirebaseDatabase mDatabase;

    private Button add_song;
    private Button upload_song;
    private Button choose_song;
    private List<FirebaseDatabaseObject.DatabaseSongs> songsList = new ArrayList<>();

    private DatabaseSongsAdapter mAdapter;
    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ = 10;

    private ProgressDialog mProgressDialog;
    private File mFile;

    Uri selectedUri;
    Uri songUrl;


    public static boolean checkPermissionForExternalStorage(Activity context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            return false;
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri selectedUri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, selectedUri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(selectedUri)) {
                final String docId = DocumentsContract.getDocumentId(selectedUri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(selectedUri)) {

                final String id = DocumentsContract.getDocumentId(selectedUri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(selectedUri)) {
                final String docId = DocumentsContract.getDocumentId(selectedUri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)

        else if ("content".equalsIgnoreCase(selectedUri.getScheme())) {
            return getDataColumn(context, selectedUri, null, null);
        }

        // File
        else if ("file".equalsIgnoreCase(selectedUri.getScheme())) {
            return selectedUri.getPath();
        }

        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==100){

            if(data != null) {
                selectedUri = data.getData();
                File sdDir = Environment.getExternalStorageDirectory();
                String filePath = selectedUri.getPath();
                // better way?
                filePath = filePath.replace("/document", "/storage");
                filePath = filePath.replace(":", "/");

                mFile = new File(filePath);

                upload_song.setEnabled(true);
                if (null != selectedUri) {
                    // Get the path from the Uri
                    ContentResolver cr = getActivity().getContentResolver();

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        Log.d("GUI", "Avatar onAttach!");
        // TODO Auto-generated method stub
        super.onAttach(context);
        //  onAttachToContext(context);
        //context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_songs, container, false);
      //  ExpandableListView listView = (ExpandableListView)rootView.findViewById(R.id.elv);
        initData();
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this.getContext(),listDataHeader,listHash);

        return rootView;
    }

    private void initData(){
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("D");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

       super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance();

        add_song = getView().findViewById(R.id.add_song_button);
        upload_song = getView().findViewById(R.id.upload_song);
        choose_song = getView().findViewById(R.id.choose_song);

        TextView userMail = getView().findViewById(R.id.user_email);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        mAdapter = new DatabaseSongsAdapter(songsList, new DatabaseSongsAdapter.ClickListener() {
            @Override
            public void onPositionClicked(int position) {

            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());


        upload_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressDialog();

                boolean fileNotNull = mFile != null;
                boolean fileExist = mFile.exists();
                boolean canRead = mFile.canRead();

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_CONTACTS)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }

                if (fileNotNull && fileExist) {
                    InputStream stream = null;
                    try {
                        stream = new FileInputStream(mFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                        if(stream != null){

                            // Create a reference to "file"
                            storageRef = storageRef.child(selectedUri.getLastPathSegment());

                            UploadTask uploadTask = storageRef.putStream(stream);
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



        });

        choose_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent i;
                i = new Intent();
                i.setType("audio/*");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(i, "Select Song"), 100 );
             //   data.getData();
             //   String path = getPath(getActivity(),selectedUri);
              //  mFile = new File(path);
            }
        });

        add_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etArtist = (EditText)getView().findViewById(R.id.etArtist);
                String author = etArtist.getText().toString();
                EditText etAlbum = (EditText)getView().findViewById(R.id.etAlbum);
                String album = etAlbum.getText().toString();
                EditText etName = (EditText)getView().findViewById(R.id.etName);
                String name = etName.getText().toString();
                Spinner spGenre = (Spinner)getView().findViewById(R.id.spinner);
                String genre = spGenre.getSelectedItem().toString();


                if( TextUtils.isEmpty(etArtist.getText())){


                    etArtist.setError( "Artist pseudonym is required!" );

                }
               if( TextUtils.isEmpty(etAlbum.getText())){

                    etAlbum.setError( "Album name is required!" );

                }
               if( TextUtils.isEmpty(etName.getText())){

                    etName.setError( "Song title is required!" );

                }

                else{
                    EditText etPrice = (EditText)getView().findViewById(R.id.etPrice);
                    float price = Float.parseFloat(etPrice.getText().toString());

                    Log.d("GUI", "Add_song_button");
                    String newSongId = mDatabase.getReference().child("Songs").push().getKey();

                    FirebaseDatabaseObject.DatabaseSongs defaultSong;
                    defaultSong = new FirebaseDatabaseObject.DatabaseSongs(newSongId, author, album, name, songUrl, genre, 1, price);

                    mDatabase.getReference().child("Songs").child(newSongId).setValue(defaultSong.GetSongData());
                    mDatabase.getReference().push();

                    Context context = getContext();
                    CharSequence text = "Song publishing succesfull!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }


            }
        });

            super.onViewCreated(view, savedInstanceState);
        }

    }

