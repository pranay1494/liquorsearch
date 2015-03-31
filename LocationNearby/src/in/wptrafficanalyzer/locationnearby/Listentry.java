package in.wptrafficanalyzer.locationnearby;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Listentry extends ListActivity{
	String mPlacenames[];
	private final String KEY_NAME = "place_name";
	private final String KEY_VIC = "vicinity";
	HashMap<String, String> lmPlace;
	HashMap<String, String> hhmPlace;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		updatelist();
	}

	public void updatelist()
	{
		ArrayList<HashMap<String, String>> blogPosts = 
				new ArrayList<HashMap<String, String>>();
		int n = GlobalList.placentry.size();
		//mPlacenames = new String[n];
		for(int i=0;i<n;i++)
		{
			lmPlace = GlobalList.placentry.get(i);
//			mPlacenames[i] = lmPlace.get("place_name");
//			setListAdapter(new ArrayAdapter<String>(Listentry.this,android.R.layout.simple_list_item_1, mPlacenames));
			HashMap<String, String> Place = new HashMap<String, String>();
			Place.put(KEY_NAME,lmPlace.get(KEY_NAME));
			Place.put(KEY_VIC,lmPlace.get(KEY_VIC));
			blogPosts.add(Place);
		}
		String[] keys = { KEY_NAME, KEY_VIC };
		int[] ids = {android.R.id.text1,android.R.id.text2};
		SimpleAdapter adapter = new SimpleAdapter(this, blogPosts,
				android.R.layout.simple_list_item_2, 
				keys, ids);
		setListAdapter(adapter);
	
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		hhmPlace = GlobalList.placentry.get(position);
		String reference = hhmPlace.get("place_id");
		Intent in = new Intent(getApplicationContext(),
                PlaceDetailsActivity.class);

        in.putExtra("reference", reference);
        in.putExtra("pos", position);
        startActivity(in);
	}
	
}
