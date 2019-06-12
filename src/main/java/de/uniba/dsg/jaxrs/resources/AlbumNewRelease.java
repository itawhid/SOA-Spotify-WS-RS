package de.uniba.dsg.jaxrs.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

@Path("albums/new-releases")

public class AlbumNewRelease implements AlbumApi {
	
	@Override
	@GET
	public List<Release> getNewReleases(@QueryParam("country") String country, @QueryParam("size") int size) throws ClientRequestException {
		
		CountryCode countryCode = null;
		//
		GetListOfNewReleasesRequest listOfNewAlbumReleasesRequest = null;
		//Result set
		List<Release> latestReleaseList = new ArrayList<>();
		
		
		/**
		 * Check for Invalid Size
		 * API requirement: size between 01 and 50
		 */
		if(size < 0 || size > 50) {
			throw new ClientRequestException(new ErrorMessage(" Bad Request: Invalid Size(should be between 01 and 50) "));
		}
		
		/**
		 * Check for Invalid Country code
		 */
		if(country != null) {
			//Ignore upper/lower cases for Country string
			countryCode = CountryCode.getByCodeIgnoreCase(country);
			if(countryCode == null) {
				throw new ClientRequestException(new ErrorMessage(" Bad Request: Invalid Country Code "));
			}
		}
		
		//Request builder based on parameters
		if(country == null && size == 0) {
			listOfNewAlbumReleasesRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().build();
		}else if(country == null && size > 0) {
			listOfNewAlbumReleasesRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().limit(size).build();
		}else if(country != null && size == 0) {
			listOfNewAlbumReleasesRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().country(countryCode).build();
		}else if(country != null && size > 0) {
			listOfNewAlbumReleasesRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().country(countryCode).limit(size).build();
		}


		//Request
		try {

			Paging<AlbumSimplified> albumListResponse = listOfNewAlbumReleasesRequest.execute();
			
			//Response check
			if(albumListResponse.getTotal() > 0) {
				//Add album items from the response to List
				AlbumSimplified[] albumSimplified = albumListResponse.getItems();

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

					latestReleaseList.add(release);
				}
			}
			else {
				throw new ResourceFinderException("No Result found for the specified request");
			}
		} catch (SpotifyWebApiException e) {
			throw new RemoteApiException(new ErrorMessage("No Result found for the specified request"));
		} catch (IOException e) {
			throw new ClientRequestException(new ErrorMessage("400 : Bad Request"));
		}
        //Return result album list
		return latestReleaseList;
	}

}
