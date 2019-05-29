package de.uniba.dsg.jaxrs.resources;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("artists/{artist-id}")
public class ArtistResource implements ArtistApi {
    @Override
    @GET
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
        } catch (SpotifyWebApiException | IOException e) {
            throw new RemoteApiException(new ErrorMessage("cannot find remote server"));
        }
    }

//    @Override
//    @GET
//    public List<Song> getTopTracks(@PathParam("artist-id") String artistId) {
//        if(artistId == null){
//            throw new ClientRequestException(new ErrorMessage("Required query parameter is missing: artist-id"));
//        }
//        GetArtistsTopTracksRequest artistTopTracksRequest = CustomSpotifyApi.getInstance(). .limit(5).build();
//        return null;
//    }
}

