package org.ambientdynamix.contextplugins.spotify;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Set;

/**
 * Created by shivam on 11/7/14.
 */
public class MySpotifyInfo implements IMySpotifyInfo {
    public static String CONTEXT_TYPE = "org.ambientdynamix.contextplugins.spotify.listen";

    private String id;
    private String artist;
    private String album;
    private String track;
    private Long length;

    public static Parcelable.Creator<MySpotifyInfo> CREATOR = new Parcelable.Creator<MySpotifyInfo>() {
        /**
         * Create a new instance of the Parcelable class, instantiating it from the given Parcel whose data had
         * previously been written by Parcelable.writeToParcel().
         */
        public MySpotifyInfo createFromParcel(Parcel in) {
            return new MySpotifyInfo(in);
        }

        /**
         * Create a new array of the Parcelable class.
         */
        public MySpotifyInfo[] newArray(int size) {
            return new MySpotifyInfo[size];
        }
    };


    @Override
    public String getContextType() {
        return CONTEXT_TYPE;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getAlbum() {
        return album;
    }

    @Override
    public String getTrack() {
        return track;
    }

    @Override
    public Long getLength() {
        return length;
    }

    @Override
    public String getImplementingClassname() {
        return null;
    }

    @Override
    public Set<String> getStringRepresentationFormats() {
        return null;
    }

    @Override
    public String getStringRepresentation(String s) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(id);
        out.writeString(artist);
        out.writeString(album);
        out.writeString(track);
        out.writeLong(length);
    }

    public MySpotifyInfo(Intent intent) {
        id = intent.getStringExtra("id");
        artist = intent.getStringExtra("artist");
        album = intent.getStringExtra("album");
        track = intent.getStringExtra("track");
        length = intent.getLongExtra("length", 0);
    }

    private MySpotifyInfo(final Parcel in) {
        id = in.readString();
        artist = in.readString();
        album = in.readString();
        track = in.readString();
        length = in.readLong();
    }
}
