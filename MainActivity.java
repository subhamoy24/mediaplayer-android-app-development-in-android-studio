package com.example.biplab.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<SongInfo> _songs = new ArrayList<SongInfo>();
    RecyclerView recyclerView;
    public SeekBar seekBar;
    CustomAdopter songAdapter;
    MediaPlayer mediaPlayer;
    private int STORAGE_PERMISSION_CODE=1;
    private Handler handler;
    Thread updateseekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        seekBar = findViewById(R.id.seekBar);
        updateseekbar=new MyThread();
        handler=new Handler();
        songAdapter = new CustomAdopter(this, _songs);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        songAdapter.setOnItemClickListener(new CustomAdopter.OnItemClickListener() {
            @Override
            public void onItemClick(final Button b, View v, final SongInfo obj, int position) {
                Runnable r=new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (b.getText().toString().equals("play")){
                                mediaPlayer = new MediaPlayer();
                                updateseekbar=new MyThread();
                                mediaPlayer.setDataSource(obj.getSongURL());
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(final MediaPlayer mp) {
                                        mp.start();
                                        seekBar.setProgress(0);
                                        seekBar.setMax(mp.getDuration());
                                        updateseekbar.start();
                                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                            @Override
                                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                            }

                                            @Override
                                            public void onStartTrackingTouch(SeekBar seekBar) {


                                            }
                                            @Override
                                            public void onStopTrackingTouch(SeekBar seekBar) {
                                                mp.seekTo(seekBar.getProgress());
                                            }
                                        });

                                    }
                                });
                                b.setText("stop");
                            } else if (b.getText().toString().equals("stop")) {
                                b.setText("play");
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                                mediaPlayer = null;
                                updateseekbar=null;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                };
                handler.postDelayed(r,100);

            }
        });
        checkpermission();




    }
      public class MyThread extends Thread{
        @Override
        public void run() {
            int totalDuration=mediaPlayer.getDuration();
            int currentposition=0;
            seekBar.setMax(totalDuration);
            while (currentposition<totalDuration)
            {
                try{
                    Thread.sleep(1000);
                    if (mediaPlayer!=null)
                    currentposition=mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentposition);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkpermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                requestStoragePermission();
            }
        } else {
            loadSongs();
        }

    }
    private void requestStoragePermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("permission needed").setMessage("this permission is needed becascuse of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                    ,STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        {
            if (requestCode==STORAGE_PERMISSION_CODE){
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"permission Granted",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this,"permission not granted",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadSongs() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        if (uri==null){
            Toast.makeText(this,"music are disable",Toast.LENGTH_SHORT).show();
        }
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    SongInfo songInfo = new SongInfo(name, artist, url);
                    _songs.add(songInfo);
                } while (cursor.moveToNext());
            }
            cursor.close();
            songAdapter = new CustomAdopter(this, _songs);

        }

    }


}
