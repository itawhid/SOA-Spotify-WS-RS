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
		
		/**
		 * country = list of new releases from a specific country, empty is accepted
		 */
		CountryCode countryCode = null;
		if(country != null) {
			//Ignore cases for Country
			countryCode = CountryCode.getByCodeIgnoreCase(country);
			if(countryCode == null) {
				throw new ClientRequestException(new ErrorMessage("No valid country found with specified value"));
			}
		}

		//Request builder based on parameters
		GetListOfNewReleasesRequest listOfNewAlbumReleasesRequest = null;

		if(country == null && size == 0) {
			listOfNewAlbumReleasesRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().build();
		}else if(country == null && size > 0) {
			listOfNewAlbumReleasesRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().limit(size).build();
		}else if(country != null && size == 0) {
			listOfNewAlbumReleasesRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().country(countryCode).build();
		}else if(country != null && size > 0) {
			listOfNewAlbumReleasesRequest = CustomSpotifyApi.getInstance().getListOfNewReleases().country(countryCode).limit(size).build();
		}

		//Returned list from response
		List<Release> latestAlbumList = new ArrayList<>();

		try {

			//Response 
			Paging<AlbumSimplified> albumListResponse = listOfNewAlbumReleasesRequest.execute();

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

					latestAlbumList.add(release);
				}
			}
			else {
				throw new ResourceFinderException("No latest release for the specified country");
			}
		} catch (SpotifyWebApiException e) {
			throw new RemoteApiException(new ErrorMessage("Sorry,Could not find any new album for the specified country"));
		} catch (IOException e) {
			throw new ClientRequestException(new ErrorMessage("Some network error occurred while requesting"));
		}
        //Return result album list
		return latestAlbumList;
	}

}
