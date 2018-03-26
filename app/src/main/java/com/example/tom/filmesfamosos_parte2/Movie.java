package com.example.tom.filmesfamosos_parte2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tom on 16/02/2018.
 */

public class Movie implements Parcelable {

    private int vote_count;
    private int id;
    private int vote_average;
    private String title;
    private int popularity;
    private String poster_path;
    private String original_language;
    private String original_title;
    private String backdrop_path;
    private String overview;
    private String release_date;


    public Movie(int vote_count, int id, int vote_average, String title, int popularity, String poster_path, String original_language, String original_title, String backdrop_path, String overview, String release_date) {
        this.vote_count = vote_count;
        this.id = id;
        this.vote_average = vote_average;
        this.title = title;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.original_language = original_language;
        this.original_title = original_title;
        this.backdrop_path = backdrop_path;
        this.overview = overview;
        this.release_date = release_date;
    }


    public int getId() {
        return id;
    }

    public int getVote_average() {
        return vote_average;
    }

    public String getTitle() {
        return title;
    }

    public int getPopularity() {
        return popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }


    public Movie(Parcel in) {
        // the order needs to be the same as in writeToParcel() method
        this.vote_count = in.readInt();
        this.id = in.readInt();
        this.vote_average = in.readInt();
        this.title = in.readString();
        this.popularity = in.readInt();
        this.poster_path = in.readString();
        this.original_language = in.readString();
        this.original_title = in.readString();
        this.backdrop_path = in.readString();
        this.overview = in.readString();
        this.release_date = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(vote_count);
        dest.writeInt(id);
        dest.writeInt(vote_average);
        dest.writeString(title);
        dest.writeInt(popularity);
        dest.writeString(poster_path);
        dest.writeString(original_language);
        dest.writeString(original_title);
        dest.writeString(backdrop_path);
        dest.writeString(overview);
        dest.writeString(release_date);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
