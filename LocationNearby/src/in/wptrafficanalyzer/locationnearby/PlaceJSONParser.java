package in.wptrafficanalyzer.locationnearby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceJSONParser {
	
//	String mPlace[];
//	int k=0;
//	/** Receives a JSONObject and returns a list */
	public List<HashMap<String,String>> parse(JSONObject jObject){		
		
		JSONArray jPlaces = null;
		try {			
			jPlaces = jObject.getJSONArray("results");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getPlaces(jPlaces);
	}
	
	
	private List<HashMap<String, String>> getPlaces(JSONArray jPlaces){
		int placesCount = jPlaces.length();
		List<HashMap<String, String>> placesList = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> place = null;	
	//	mPlace = new String[placesCount];
		for(int i=0; i<placesCount;i++){
			try {
				place = getPlace((JSONObject)jPlaces.get(i));
				placesList.add(place);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return placesList;
	}
	
	private HashMap<String, String> getPlace(JSONObject jPlace){

		HashMap<String, String> place = new HashMap<String, String>();
		String placeName = "-NA-";
		String vicinity="-NA-";
		String latitude="";
		String longitude="";
		String placeid ="";		
		
		try {
			if(!jPlace.isNull("name")){
				placeName = jPlace.getString("name");
			//	mPlace[k++] = placeName; 
			}
			
			if(!jPlace.isNull("vicinity")){
				vicinity = jPlace.getString("vicinity");
			}	
			
			latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
			longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");			
			
			if(!jPlace.isNull("place_id")){
				placeid = jPlace.getString("place_id");
			//	mPlace[k++] = placeName; 
			}
			
			
			place.put("place_name", placeName);
			place.put("vicinity", vicinity);
			place.put("lat", latitude);
			place.put("lng", longitude);
			place.put("place_id", placeid);
			
			
		} catch (JSONException e) {			
			e.printStackTrace();
		}		
		return place;
	}
}
