package com.example.tom.filmesfamosos_parte1;

/**
 * Created by Tom on 18/02/2018.
 */

public class MovieSimple {

    private int id;
    private String post_path;


    public MovieSimple(int id, String post_path) {
        this.id = id;
        this.post_path = post_path;
    }

    public int getId() {
        return id;
    }

    public String getPost_path() {
        return post_path;
    }
}
