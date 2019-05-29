package de.uniba.dsg.jaxws.resources;

import de.uniba.dsg.interfaces.ArtistApi;
import de.uniba.dsg.jaxws.MusicApiImpl;
import de.uniba.dsg.jaxws.exceptions.MusicRecommenderFault;
import de.uniba.dsg.models.ErrorMessage;
import de.uniba.dsg.models.Interpret;
import de.uniba.dsg.models.Song;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class ArtistResource implements ArtistApi {
    @Override
    public Interpret getArtist(String artistId) {

        Client client = ClientBuilder.newClient();
        Response response = client.target(MusicApiImpl.restServerUri)
                .path("/artists/{artist-id}")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        if (response.getStatus() == 200) {
            Interpret artistInf = response.readEntity(Interpret.class);
            return artistInf;
        } else if (response.getStatus() == 400) {
            String errorMessage = response.readEntity(ErrorMessage.class).getMessage();
            throw new MusicRecommenderFault("A client side error occurred", errorMessage);
        } else if (response.getStatus() == 404) {
            String errorMessage = response.readEntity(ErrorMessage.class).getMessage();
            throw new MusicRecommenderFault("The requested resource was not found", errorMessage);
        } else if (response.getStatus()== 500) {
            String errorMessage = response.readEntity(ErrorMessage.class).getMessage();
            throw new MusicRecommenderFault("An internal server error occurred", errorMessage);
        } else {
            String errorMessage = response.readEntity(ErrorMessage.class).getMessage();
            throw new MusicRecommenderFault("An unknown remote server error occurred", errorMessage);
        }
    }

//    @Override
//    public List<Song> getTopTracks(String artistId) {
//        Client client = ClientBuilder.newClient();
//        Response response = client.target(MusicApiImpl.restServerUri)
//                .path("/artists/{artist-id}/top-tracks")
//                .request(MediaType.APPLICATION_JSON_TYPE)
//                .get();
//
//        if (response.getStatus() == 200) {
//           return null;
//        } else if (response.getStatus() == 400) {
//            String errorMessage = response.readEntity(ErrorMessage.class).getMessage();
//            throw new MusicRecommenderFault("A client side error occurred", errorMessage);
//        } else if (response.getStatus() == 404) {
//            String errorMessage = response.readEntity(ErrorMessage.class).getMessage();
//            throw new MusicRecommenderFault("The requested resource was not found", errorMessage);
//        } else if (response.getStatus()== 500) {
//            String errorMessage = response.readEntity(ErrorMessage.class).getMessage();
//            throw new MusicRecommenderFault("An internal server error occurred", errorMessage);
//        } else {
//            String errorMessage = response.readEntity(ErrorMessage.class).getMessage();
//            throw new MusicRecommenderFault("An unknown remote server error occurred", errorMessage);
//        }
//    }
}