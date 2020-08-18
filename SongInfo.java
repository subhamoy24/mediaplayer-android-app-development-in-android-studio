package com.example.biplab.myapplication;

public class SongInfo {
    public String songName,artistName,songURL;
    public SongInfo(String name,String artistName,String songURL)
    {
        this.songName=name;
        this.artistName=artistName;
        this.songURL=songURL;
    }
    public String getSongURL(){
        return songURL;
    }
    public String getSongName(){
        return songName;
    }
    public String getArtistName(){
        return artistName;
    }


}
