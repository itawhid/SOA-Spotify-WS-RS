package de.uniba.dsg.jaxrs.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.glassfish.jersey.server.internal.scanning.ResourceFinderException;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.data.browse.GetListOfNewReleasesRequest;

import de.uniba.dsg.CustomSpotifyApi;
import de.uniba.dsg.interfaces.AlbumApi;
import de.uniba.dsg.jaxrs.exceptions.ClientRequestException;
import de.uniba.dsg.jaxrs.exceptions.RemoteApiException;
import de.uniba.dsg.models.ErrorMessage;
import de.uniba.dsg.models.Release;

@Path("albums/new-releases") //Specify API url path

public class AlbumNewRelease implements AlbumApi {

	private static final Logger logger = Logger.getLogger(AlbumNewRelease.class.getName());
	
	@Override
	@GET
	public List<Release> getNewReleases(@QueryParam("country") String country, @QueryParam("size") int size) throws ClientRequestException {
		
		CountryCode countryCode = null;
		
		if(country != null) {
			countryCode = CountryCode.getByCodeIgnoreCase(country);
			if(countryCode == null) {
				throw new ClientRequestException(new ErrorMessage("Invalid Country Code"));
			} 
		}
		
		/**
		 * size param between 01 and 50
		 */
		if(size<0 || size>50) {
			throw new ClientRequestException(new ErrorMessage("Invalid size parameter. Value should be between 01 and 50"));
		}
		
		GetListOfNewReleasesRequest latestReleasedAlbumsRequest = null;
		 
		if(country == null && size == 0) {
			latestReleasedAlbumsRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().build();
		}
		else if(country == null && size > 0) {
			latestReleasedAlbumsRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().limit(size).build();
		}
		else if(country != null && size == 0) {
			latestReleasedAlbumsRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().country(countryCode).build();
		}
		else if(country != null && size > 0) {
			latestReleasedAlbumsRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().country(countryCode).limit(size).build();
		}
		
		Paging<AlbumSimplified> albums = null;
		List<Release> listOfLatestRelease = new ArrayList<>();
		
		try {
			albums = latestReleasedAlbumsRequest.execute();
			
			logger.info("Total number of albums returned by the request: "+albums.getItems().length);
			
			if(albums.getTotal() > 0) {
				AlbumSimplified[] albumSimplified = albums.getItems();
				
				for(AlbumSimplified album : albumSimplified){
					
					Release release = new Release();
					
					release.setTitle(album.getName());
					
					ArtistSimplified[] artists = album.getArtists();
					
					StringBuilder sb = new StringBuilder("");
					
					for(ArtistSimplified artist : artists) {
						sb.append(artist.getName()).append(", ");
					}
					String artistName = sb.substring(0, sb.lastIndexOf(","));
					
					release.setArtist(artistName);
					
					listOfLatestRelease.add(release);
				}
			}
			else {
				throw new ResourceFinderException("No latest released album found for the specified country");
			}
		} catch (SpotifyWebApiException e) {
			throw new RemoteApiException(new ErrorMessage("Spotify wasn't able to find any new album for the specified country"));
		} catch (IOException e) {
			throw new ClientRequestException(new ErrorMessage("Network error occurred"));
		}
        
		logger.info(listOfLatestRelease.toString());
		
		return listOfLatestRelease;
	}

}
