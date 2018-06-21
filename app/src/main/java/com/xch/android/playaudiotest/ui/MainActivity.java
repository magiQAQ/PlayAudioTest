package com.xch.android.playaudiotest.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xch.android.playaudiotest.R;
import com.xch.android.playaudiotest.adapter.MusicAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int fileID = 0;//当前播放的文件序号
    private File myMusicPath;
    private File[] files;
    private ArrayList<File> curFileList;
    private MusicAdapter musicAdapter;
    private RecyclerView song_list;
    private MediaPlayer mediaPlayer;
    private ImageButton button_previous;
    private ImageButton button_play_pause;
    private ImageButton button_next;
    private IntentFilter intentFilter;
    private SongChangerRecevier songChangerRecevier;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {//若无权限，则再次请求
            initReadWritePermission();
        } else {
            initMusicList();
            initControlButton();
            initBroadcastReceiver();
        }
    }

    private void initReadWritePermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
    }//获取读写权限

    private void initMusicList() {
        myMusicPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        files = myMusicPath.listFiles();
        curFileList = new ArrayList<>();
        if (files != null) {
            for (File file : files){
                curFileList.add(file);
            }
        }
        //Log.i("MusicList",curFileList.toString());
        musicAdapter = new MusicAdapter(this,curFileList);
        song_list = findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        song_list.setLayoutManager(linearLayoutManager);
        song_list.setAdapter(musicAdapter);
    }//初始化歌曲列表

    private void initControlButton() {
        button_previous = findViewById(R.id.button_previous);
        button_play_pause = findViewById(R.id.button_play_pause);
        button_next = findViewById(R.id.button_next);
    }//初始化按钮

    private void initMediaPlayer(){//初始化音乐播放器
        mediaPlayer = new MediaPlayer();
        File file = curFileList.get(fileID);
        try {
            mediaPlayer.setDataSource(file.getPath());//指定音频文件的路径
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();//准备
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {//播放完成后自动播放下一首，若已经是最后一首，则回到第一首
            if (fileID == curFileList.size() - 1){
                fileID = 0;
            } else {
                fileID = fileID + 1;
            }
            try {
                mp.reset();
                mp.setDataSource(curFileList.get(fileID).getPath());
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.start();
        }
    });
    }//初始化音乐播放器

    private void initBroadcastReceiver(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.xch.android.playaudiotest.PLAY_CHOSENSONG");
        songChangerRecevier = new SongChangerRecevier();
        localBroadcastManager.registerReceiver(songChangerRecevier,intentFilter);
    }//初始化广播接收器

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_previous:
                if (fileID == 0){//回到列表地步开始播放
                    fileID = curFileList.size() - 1;
                } else {
                    fileID = fileID - 1;
                }
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(curFileList.get(fileID).getPath());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                break;
            case R.id.button_play_pause:
                if (mediaPlayer == null){
                    initMediaPlayer();
                }
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    button_play_pause.setBackgroundResource(R.mipmap.button_pause);
                } else if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    button_play_pause.setBackgroundResource(R.mipmap.button_play);
                }
                break;
            case R.id.button_stop:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                    button_play_pause.setBackgroundResource(R.mipmap.button_play);
                    try {
                        mediaPlayer.setDataSource(curFileList.get(fileID).getPath());//指定音频文件的路径
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.prepareAsync();
                }
            case R.id.button_next:
                if (fileID == curFileList.size() - 1){//回到列表顶部开始播放
                    fileID = 0;
                } else {
                    fileID = fileID + 1;
                }
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(curFileList.get(fileID).getPath());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                break;
            default:
                break;
        }
    }//各按钮按下后的操作
    @Override
    protected void onDestroy() {
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        localBroadcastManager.unregisterReceiver(songChangerRecevier);
        super.onDestroy();
    }

    class SongChangerRecevier extends BroadcastReceiver {//音乐改变接收器
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer == null){
                mediaPlayer = new MediaPlayer();
            }
            if (mediaPlayer != null){
                int position = intent.getIntExtra("chosenFileId",0);
                fileID = position;
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
                try {
                    mediaPlayer.setDataSource(files[position].getPath());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                button_play_pause.setBackgroundResource(R.mipmap.button_pause);
            }
        }
    }
}
