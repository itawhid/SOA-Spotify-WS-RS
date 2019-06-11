package de.uniba.dsg.jaxrs.resources;

import de.uniba.dsg.interfaces.PlaylistApi;
import de.uniba.dsg.jaxrs.exceptions.ClientRequestException;
import de.uniba.dsg.models.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("playlists")
public class PlaylistResource implements PlaylistApi {
    @Override
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPlaylist(PlaylistRequest request) {
        try {
            List<String> artistSeeds = request.getArtistsSeeds();
            ArtistResource ar = new ArtistResource();
            List<Song> songs = new ArrayList<>();
            Playlist pl = new Playlist();

            int numOfSongs = 0;
            numOfSongs = Integer.valueOf(request.getNumberOfSongs());
            int defaultSize = 10;
            if(numOfSongs > 0) {
                defaultSize = numOfSongs;
            }

            artistSeeds.forEach(artist -> {
                List<Song> topTracks = ar.getTopTracks(artist);
                songs.addAll(topTracks);
            });

            for(String artistId: artistSeeds) {
                if(songs.size() < defaultSize) {
                    Interpret artist = ar.getSimilarArtist(artistId);
                    List<Song> topTracks = ar.getTopTracks(artist.getId());
                    songs.addAll(topTracks);
                }
            }

            Collections.shuffle(songs);
            if(songs.size() > defaultSize) {
                songs.subList(defaultSize, songs.size()).clear();
            }

            pl.setTitle(request.getTitle());
            pl.setSize(songs.size());
            pl.setTracks(songs);
            return Response.status(Response.Status.CREATED.getStatusCode()).entity(pl).build();

        } catch (NumberFormatException e) {
            throw new ClientRequestException(new ErrorMessage("Please enter a number for playlist size"));
        } catch(Exception e){
            //catch any exception here
            throw new ClientRequestException(new ErrorMessage(e.getMessage()));
        }

    }
}
