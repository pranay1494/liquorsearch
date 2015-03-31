package in.wptrafficanalyzer.locationnearby;

import java.net.URLEncoder;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SinglePlaceActivity extends Activity{


	double mLat=0;
	double mLong=0;
	HashMap<String, String> lmPlace;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_place);
        
        Intent i = getIntent();
         
        String position = i.getStringExtra("pos");
        int pos = Integer.parseInt(position);
        lmPlace = GlobalList.placentry.get(pos);
//        TextView lbl_name = (TextView) findViewById(R.id.name);
//        TextView lbl_address = (TextView) findViewById(R.id.address);
//        mLat=Double.parseDouble(lmPlace.get("lat"));
//        mLong=Double.parseDouble(lmPlace.get("lng"));
//        lbl_name.setText(lmPlace.get("place_name"));
//        lbl_address.setText(lmPlace.get("vicinity"));
        String Placeid = lmPlace.get("place_id");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
	
	if (!lmPlace.get("place_name").equals("")) {
		uri.append("&q=" + URLEncoder.encode(lmPlace.get("place_name")));
	}
	
	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
	startActivity(intent);
}
	
}
