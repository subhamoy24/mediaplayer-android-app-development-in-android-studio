package com.example.biplab.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdopter extends RecyclerView.Adapter <CustomAdopter.SongHolder>{
    ArrayList<SongInfo>_songs;
    Context context;
    OnItemClickListener onItemClickListener;
    CustomAdopter(Context context,ArrayList<SongInfo>_songs){
        this.context=context;
        this._songs=_songs;
    }
    public interface OnItemClickListener{
        void onItemClick(Button b,View v,SongInfo obj,int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate(R.layout.layout,viewGroup,false);
        return new SongHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongHolder songHolder, final int i) {
        final SongInfo c=_songs.get(i);
        songHolder.songName.setText(c.songName);
        songHolder.Artistname.setText(c.artistName);
        songHolder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(songHolder.btn,v,c,i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return _songs.size();
    }

    public  class SongHolder extends RecyclerView.ViewHolder{
        TextView songName,Artistname;
        Button btn;
        public SongHolder(@NonNull View itemView) {
            super(itemView);
            songName=itemView.findViewById(R.id.song);
            Artistname=itemView.findViewById(R.id.artist);
            btn=itemView.findViewById(R.id.btn);
        }
    }

}
