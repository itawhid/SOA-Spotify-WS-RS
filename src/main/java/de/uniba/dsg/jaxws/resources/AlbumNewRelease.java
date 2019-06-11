package de.uniba.dsg.jaxws.resources;

import java.util.List;

import de.uniba.dsg.interfaces.AlbumApi;
import de.uniba.dsg.jaxws.MusicApiImpl;
import de.uniba.dsg.jaxws.exceptions.MusicRecommenderFault;
import de.uniba.dsg.models.ErrorMessage;
import de.uniba.dsg.models.Release;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AlbumNewRelease implements AlbumApi {
	
	@Override
    public List<Release> getNewReleases(String country, int size) {
        
		Client client = ClientBuilder.newClient();
        Response response = client.target(MusicApiImpl.restServerUri)
                .path("/albums/new-releases")
                .resolveTemplate("country", country)
                .resolveTemplate("size", size)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        
        if (response.getStatus() == 200) {
            List<Release> albums = response.readEntity(new GenericType<List<Release>>(){});
            return albums;
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
}
