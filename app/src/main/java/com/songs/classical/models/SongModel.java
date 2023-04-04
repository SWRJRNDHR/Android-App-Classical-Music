package com.songs.classical.models;


/**
 * Created by Swaraj on 4/11/2017.
 */

public class SongModel {

    private String song;
    private String address;
    private int Id;
    private String author;

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;

    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;

    }
    public int getId(){
        return Id;
    }

    public void setId(int Id){
        this.Id = Id;
    }
}
