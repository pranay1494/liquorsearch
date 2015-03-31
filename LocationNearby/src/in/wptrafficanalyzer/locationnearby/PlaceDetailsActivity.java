package in.wptrafficanalyzer.locationnearby;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlaceDetailsActivity extends Activity {
	
	TextView lbl_name;
	TextView lbl_address;
	TextView lbl_vicinity;
	TextView lbl_phone;
	TextView lbl_rating;
	TextView lbl_url;
	TextView lbl_web;
	double mLat=0;
	double mLong=0;
	HashMap<String, String> llmPlace;
	protected ProgressBar mProgressBar; 

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_place);
		 lbl_name = (TextView) findViewById(R.id.name);
		 lbl_address = (TextView) findViewById(R.id.address);
		 lbl_vicinity = (TextView) findViewById(R.id.vicinity);
		 lbl_phone = (TextView) findViewById(R.id.phone);
		 lbl_rating = (TextView) findViewById(R.id.rating);
		 lbl_url = (TextView) findViewById(R.id.url);
		 lbl_web = (TextView) findViewById(R.id.website);
		 mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		 
		String reference = getIntent().getStringExtra("reference");
		Intent i = getIntent();
        int position = i.getIntExtra("pos", 0);
        llmPlace = GlobalList.placentry.get(position);
        mLat=Double.parseDouble(llmPlace.get("lat"));
        mLong=Double.parseDouble(llmPlace.get("lng"));

		
		StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
		sb.append("placeid="+reference);
		sb.append("&sensor=true");
		sb.append("&key=AIzaSyAbhl6HsWRtSSyPot-58p6i6YeXsHUNY6I");
		
		mProgressBar.setVisibility(View.VISIBLE);
        PlacesTask placesTask = new PlacesTask();		        			        
        placesTask.execute(sb.toString());	
		
	};
	
	
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);                
                

                urlConnection = (HttpURLConnection) url.openConnection();                

                urlConnection.connect();                

                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }

                data = sb.toString();
                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }

        return data;
    }         

	
	/** A class, to download Google Place Details */
	private class PlacesTask extends AsyncTask<String, Integer, String>{

		String data = null;
		
		@Override
		protected String doInBackground(String... url) {
			try{
				data = downloadUrl(url[0]);
			}catch(Exception e){
				 Log.d("Background Task",e.toString());
			}
			return data;
		}
		
		@Override
		protected void onPostExecute(String result){			
			ParserTask parserTask = new ParserTask();
			
			parserTask.execute(result);
		}
	}
	
	
	/** A class to parse the Google Place Details in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, HashMap<String,String>>{

		JSONObject jObject;
		@Override
		protected HashMap<String,String> doInBackground(String... jsonData) {
		
			HashMap<String, String> hPlaceDetails = null;
			PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
        
	        try{
	        	jObject = new JSONObject(jsonData[0]);
	            hPlaceDetails = placeDetailsJsonParser.parse(jObject);
	            
	        }catch(Exception e){
	                Log.d("Exception",e.toString());
	        }
	        return hPlaceDetails;
		}
		
		@Override
		protected void onPostExecute(HashMap<String,String> hPlaceDetails){			
			
			mProgressBar.setVisibility(View.INVISIBLE);
			
			String name = hPlaceDetails.get("name");
			String icon = hPlaceDetails.get("icon");
			String vicinity = hPlaceDetails.get("vicinity");
			String formatted_address = hPlaceDetails.get("formatted_address");
			String formatted_phone = hPlaceDetails.get("formatted_phone");
			String website = hPlaceDetails.get("website");
			String rating = hPlaceDetails.get("rating");
			String url = hPlaceDetails.get("url");
			
			lbl_name.setText(name);
			lbl_address.setText(formatted_address);
			lbl_vicinity.setText(vicinity);
			lbl_phone.setText(formatted_phone);
			lbl_rating.setText(rating);
			lbl_url.setText(url);
			lbl_web.setText(website);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.single, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.menu_goto_action)
		{
			sendToActionIntent();
		}
		return false;
	}

@SuppressWarnings("deprecation")
public void sendToActionIntent() {
	StringBuilder uri = new StringBuilder("geo:");
	uri.append(mLat);
	uri.append(",");
	uri.append(mLong);
	uri.append("?z=10");
	
	if (!llmPlace.get("place_name").equals("")) {
		uri.append("&q=" + URLEncoder.encode(llmPlace.get("place_name")));
	}
	
	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
	startActivity(intent);
}
}