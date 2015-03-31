package in.wptrafficanalyzer.locationnearby;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;

public class Splash extends Activity{

	private AlertDialog.Builder builder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	      setContentView(R.layout.splash);
	      
//	        // Font path
	        String fontPath = "fonts/Action_Man.ttf";
//	 
//	        // text view label
	        TextView txtGhost = (TextView) findViewById(R.id.ghost);
//	 
//	        // Loading Font Face
	        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
//	 
//	        // Applying font
	        txtGhost.setTypeface(tf);
	        
					check();		
	    }
	private void check() {
		if(isOnline())
		{
		Intent opens = new Intent("in.wptrafficanalyzer.locationnearby.MAINACTIVITY");
		startActivity(opens);
		}
		else
		{
			builder = new AlertDialog.Builder(Splash.this);

		    builder.setTitle("OOPS!");
		    builder.setMessage("NO! Internet Connection");

		    builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {

		        public void onClick(DialogInterface dialog, int which) {
		            // Do nothing but close the dialog
		        	check();
		            dialog.dismiss();
		        }

		    });

		    builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {

		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            // Do nothing
		            dialog.dismiss();
		            finish();
		        }
		    });

		    AlertDialog alert = builder.create();
		    alert.show();
		}
	}
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
	protected boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}
	}

