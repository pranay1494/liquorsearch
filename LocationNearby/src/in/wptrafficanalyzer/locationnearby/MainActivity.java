package in.wptrafficanalyzer.locationnearby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements LocationListener {

	GoogleMap mGoogleMap;
	Spinner mSprPlaceType;

	String[] mPlaceType = null;
	String[] mPlaceTypeName = null;

	double mLatitude = 0;
	double mLongitude = 0;

	JSONObject jObject;
	String mPlace[];
	int k = 0;
	protected MenuItem mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mapintit();

	}

	private void mapintit() {
		mPlaceType = getResources().getStringArray(R.array.place_type);
		mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);

		mSprPlaceType = (Spinner) findViewById(R.id.spr_place_type);
		mSprPlaceType.setAdapter(adapter);

		Button btnFind;
		btnFind = (Button) findViewById(R.id.btn_find);

		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else {
			SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

			mGoogleMap = fragment.getMap();

			mGoogleMap.setMyLocationEnabled(true);

			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			Criteria criteria = new Criteria();

			String provider = locationManager.getBestProvider(criteria, true);

			Location location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				onLocationChanged(location);
			}

			locationManager.requestLocationUpdates(provider, 20000, 0, this);

			btnFind.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					int selectedPosition = mSprPlaceType
							.getSelectedItemPosition();
					String type = mPlaceType[selectedPosition];

					StringBuilder sb = new StringBuilder(
							"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
					sb.append("location=" + mLatitude + "," + mLongitude);
					sb.append("&radius=5000");
					sb.append("&types=" + type);
					sb.append("&sensor=true");
					sb.append("&key=AIzaSyAbhl6HsWRtSSyPot-58p6i6YeXsHUNY6I");

					// Creating a new non-ui thread task to download Google
					// place json data
					PlacesTask placesTask = new PlacesTask();

					// Invokes the "doInBackground()" method of the class
					// PlaceTask
					placesTask.execute(sb.toString());

				}
			});

		}
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.connect();
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}

		return data;
	}

	/** A class, to download Google Places */
	private class PlacesTask extends AsyncTask<String, Integer, String> {

		String data = null;

		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			ParserTask parserTask = new ParserTask();

			parserTask.execute(result);
		}

	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			// List<HashMap<String, String>> places = null;
			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a List construct */
				// places = placeJsonParser.parse(jObject);
				GlobalList.placentry = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return GlobalList.placentry;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {
			mGoogleMap.clear();

			for (int i = 0; i < list.size(); i++) {

				MarkerOptions markerOptions = new MarkerOptions();

				HashMap<String, String> hmPlace = list.get(i);

				double lat = Double.parseDouble(hmPlace.get("lat"));

				double lng = Double.parseDouble(hmPlace.get("lng"));

				String name = hmPlace.get("place_name");

				String vicinity = hmPlace.get("vicinity");

				LatLng latLng = new LatLng(lat, lng);

				markerOptions.position(latLng);

				markerOptions.title(name + " : " + vicinity);

				mGoogleMap.addMarker(markerOptions);
				mMenu.setVisible(true);
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		mMenu = menu.getItem(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.action_get_data) {
			Intent i = new Intent(
					"in.wptrafficanalyzer.locationnearby.LISTENTRY");
			startActivity(i);
		}
		if (item.getItemId() == R.id.action_find) {
			int selectedPosition = mSprPlaceType.getSelectedItemPosition();
			String type = mPlaceType[selectedPosition];

			StringBuilder sb = new StringBuilder(
					"https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
			sb.append("location=" + mLatitude + "," + mLongitude);
			sb.append("&radius=5000");
			sb.append("&types=" + type);
			sb.append("&sensor=true");
			sb.append("&key=AIzaSyAbhl6HsWRtSSyPot-58p6i6YeXsHUNY6I");

			PlacesTask placesTask = new PlacesTask();

			placesTask.execute(sb.toString());

		}
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		mLatitude = location.getLatitude();
		mLongitude = location.getLongitude();
		LatLng latLng = new LatLng(mLatitude, mLongitude);

		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
