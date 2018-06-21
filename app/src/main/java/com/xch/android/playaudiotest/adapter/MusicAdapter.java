package com.xch.android.playaudiotest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xch.android.playaudiotest.R;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter{

    private LocalBroadcastManager localBroadcastManager;
    private ArrayList<File> mFiles; //当前全部文件数组
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public static class MyInflaterViewHolder extends RecyclerView.ViewHolder {

        View musicView;
        ImageView song_icon;
        TextView song_name;
        TextView song_author;

        public MyInflaterViewHolder(View view){
            super(view);
            musicView = view;
            song_icon = view.findViewById(R.id.song_icon);
            song_name = view.findViewById(R.id.song_name);
            song_author = view.findViewById(R.id.song_author);
        }
    }

    public MusicAdapter(Context context, ArrayList<File> files){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mFiles = files;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final MyInflaterViewHolder myInflaterViewHolder = new MyInflaterViewHolder(mLayoutInflater.inflate(R.layout.file_item,parent,false));
        myInflaterViewHolder.musicView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = myInflaterViewHolder.getAdapterPosition();
                localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
                Intent intent = new Intent("com.xch.android.playaudiotest.PLAY_CHOSENSONG");
                intent.putExtra("chosenFileId",position);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
        return myInflaterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        File file = mFiles.get(position);
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");//获得文件分隔符“.”的位置
        String fileType = fileName.substring(dotIndex,fileName.length());//从而获得文件扩展名
        if (fileType.equals(".mp3")) {
            ((MyInflaterViewHolder) holder).song_icon.setImageResource(R.mipmap.icon_music);
        } else {
            Log.d("fileType",fileType);
            ((MyInflaterViewHolder) holder).song_icon.setImageResource(R.mipmap.icon_file);
        }
        if (fileName.contains(" - ")) {
            String[] splited = fileName.split(" - ",2);
            ((MyInflaterViewHolder) holder).song_name.setText(splited[1].substring(0,splited[1].length() - 4));
            ((MyInflaterViewHolder) holder).song_author.setText(splited[0]);
        } else {
            ((MyInflaterViewHolder) holder).song_name.setText(fileName.substring(0,fileName.length() - 4));
            ((MyInflaterViewHolder) holder).song_author.setText("unknown");
        }
    }

    @Override
    public int getItemCount() {
        return mFiles == null ? 0 : mFiles.size();
    }


}
