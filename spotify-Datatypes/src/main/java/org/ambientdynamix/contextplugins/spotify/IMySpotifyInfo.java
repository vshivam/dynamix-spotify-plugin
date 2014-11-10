package org.ambientdynamix.contextplugins.spotify;

import org.ambientdynamix.api.application.IContextInfo;

/**
 * Created by shivam on 11/7/14.
 */
public interface IMySpotifyInfo extends IContextInfo {

    public String getId();

    public String getArtist();

    public String getAlbum();

    public String getTrack();

    public Long getLength();
}
