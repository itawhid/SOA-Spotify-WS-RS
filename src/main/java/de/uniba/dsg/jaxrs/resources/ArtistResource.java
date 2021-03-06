package de.uniba.dsg.jaxrs.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsRelatedArtistsRequest;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Artist;

import com.wrapper.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import de.uniba.dsg.CustomSpotifyApi;
import de.uniba.dsg.interfaces.ArtistApi;
import de.uniba.dsg.jaxrs.exceptions.ClientRequestException;
import de.uniba.dsg.models.ErrorMessage;
import de.uniba.dsg.models.Interpret;
import de.uniba.dsg.jaxrs.exceptions.RemoteApiException;
import de.uniba.dsg.jaxrs.exceptions.ResourceNotFoundException;
import de.uniba.dsg.models.Song;

import javax.ws.rs.*;

@Path("artists")
public class ArtistResource implements ArtistApi {
    @Override
    @GET
    @Path("{artist-id}")
    public Interpret getArtist(@PathParam("artist-id") String artistId) {
        if(artistId == null){
            throw new ClientRequestException(new ErrorMessage("Required query parameter is missing: artist-id"));
        }
        GetArtistRequest artistRequest = CustomSpotifyApi.getInstance().getArtist(artistId).build();
        try {
            Artist artist = artistRequest.execute();
            // exception: no artist found for provided artistId
            if (artist == null) {
                throw new ResourceNotFoundException(new ErrorMessage(String.format("No artist found for query param: %s", artistId)));
            }
            Interpret result = new Interpret();
            result.setId(artist.getId());
            result.setName(artist.getName());
            result.setGenres(Arrays.asList(artist.getGenres()));
            result.setPopularity(artist.getPopularity());


            return result;
            // exception: remote api exception
        }
        catch (SpotifyWebApiException | IOException e) {
            if(e instanceof SpotifyWebApiException)
                throw new ResourceNotFoundException(new ErrorMessage(String.format("No artist found for query param: %s", artistId)));
            else
                throw new RemoteApiException(new ErrorMessage("cannot find remote server"));
        }
    }

    @Override
    @GET
    @Path("{artist-id}/top-tracks")
    public List<Song> getTopTracks(@PathParam("artist-id") String artistId) {
        if(artistId == null){
            throw new ClientRequestException(new ErrorMessage("Required query parameter is missing: artist-id"));
        }
        GetArtistsTopTracksRequest artistTopTracksRequest = CustomSpotifyApi.getInstance().getArtistsTopTracks(artistId,CountryCode.DE).build();
        try {
            Track[] tracks = artistTopTracksRequest.execute();
            int len = tracks.length;
            if(len > 4 )
                len = 5;
            List<Song> songs = new ArrayList<Song>();
            for(int i=0; i<len ; i++) {
                Song song = new Song();

                song.setArtist(tracks[i].getArtists()[0].getName());
                song.setTitle(tracks[i].getName());
                song.setDuration(tracks[i].getDurationMs());

                songs.add(song);
            }
            return songs;
        }
        catch (SpotifyWebApiException | IOException e) {
            if(e instanceof SpotifyWebApiException)
                throw new ResourceNotFoundException(new ErrorMessage(String.format("No artist found for query param: %s", artistId)));
            else
                throw new RemoteApiException(new ErrorMessage("cannot find remote server"));
        }
    }
    
    @Override
    @GET
    @Path("{artist-id}/similar-artist")
    public Interpret getSimilarArtist(@PathParam("artist-id") String artistId) {
        if(artistId == null){
            throw new ClientRequestException(new ErrorMessage("Required query parameter is missing: artist-id"));
        }
        
        GetArtistsRelatedArtistsRequest getArtistsRelatedArtistsRequest = CustomSpotifyApi.getInstance().getArtistsRelatedArtists(artistId).build();
        
        try {
          
			Artist[] artists = getArtistsRelatedArtistsRequest.execute();

            // exception: no artist found for provided artistId
            if (artists == null) {
                throw new ResourceNotFoundException(new ErrorMessage(String.format("No artist found for query param: %s", artistId)));
            }
            Artist artist = artists[new Random().nextInt(artists.length)];

            Interpret result = new Interpret();
            result.setId(artist.getId());
            result.setName(artist.getName());
            result.setGenres(Arrays.asList(artist.getGenres()));
            result.setPopularity(artist.getPopularity());


            return result;
            // exception: remote api exception
        } catch (SpotifyWebApiException | IOException e) {
            if(e instanceof SpotifyWebApiException)
                throw new ResourceNotFoundException(new ErrorMessage(String.format("No artist found for query param: %s", artistId)));
            else
                throw new RemoteApiException(new ErrorMessage("cannot find remote server"));
        }
    }
}

