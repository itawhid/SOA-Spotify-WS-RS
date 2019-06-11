package de.uniba.dsg.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO:
 * PlaylistRequest attributes should be
 * - title:String
 * - artistSeeds:List<String>, must be serialized as 'seeds'
 * - numberOfSongs:int, must be serialized as 'size'
 */
@XmlType(propOrder = {"title", "artistSeeds", "numberOfSongs"})
@XmlAccessorType(XmlAccessType.FIELD)
public class PlaylistRequest {
    @XmlElement(name = "title")
    private String title = "";
    @XmlElement(name = "seeds")
    private List<String> artistSeeds = new ArrayList<String>();
    @XmlElement(name = "size")
    private int numberOfSongs = 0;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getArtistsSeeds() { return artistSeeds; }
    public void setArtistSeeds(List<String> artistSeeds) { this.artistSeeds = artistSeeds; }

    public int getNumberOfSongs() { return numberOfSongs; }
    public void setNumberOfSongs(int numberOfSongs) { this.numberOfSongs = numberOfSongs; }
}
