package com.hala.myrestaurantapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Contact extends FragmentActivity implements OnMapReadyCallback {

	// declare view objects
	ImageView imgNavBack;
	TextView txtText, txtSubText;
	Button btnCall, btnEmail;
	FrameLayout lytMap;

	// declare GoogleMap object
	GoogleMap mMap;
	GoogleMapOptions options = new GoogleMapOptions();
	SupportMapFragment fragMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact);

		// connect view objects with xml id
		imgNavBack = (ImageView) findViewById(R.id.imgNavBack);
		txtText = (TextView) findViewById(R.id.txtText);
		txtSubText = (TextView) findViewById(R.id.txtSubText);
		btnCall = (Button) findViewById(R.id.btnCall);
		btnEmail = (Button) findViewById(R.id.btnEmail);
		lytMap = (FrameLayout) findViewById(R.id.lytMap);
		fragMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

		// get screen device width and height
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int wPix = dm.widthPixels;
		int hPix = wPix / 2 + 50;

		// change Map width and height
		LayoutParams lp = lytMap.getLayoutParams();
		lp.width = wPix;
		lp.height = hPix;

		// set up map
		setUpMapIfNeeded();

		// show restaurant info
		showInfo();

		// event listener to handle back button when clicked
		imgNavBack.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// back to previous page
				finish();
			}
		});

		// event listener to handle call button when clicked
		btnCall.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// check whether device support phone feature
				// if support call restaurant phone number
				// otherwise, show toast message
				if (!(((TelephonyManager) Contact.this.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
						== TelephonyManager.PHONE_TYPE_NONE)) {
					String number = "tel:" + getString(R.string.phone);
					Intent iCall = new Intent(Intent.ACTION_CALL, Uri.parse(number));
					if (ActivityCompat.checkSelfPermission(Contact.this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						//    Activity#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for Activity#requestPermissions for more details.
						return;
					}
					startActivity(iCall);
				}else{
					Toast.makeText(Contact.this, getString(R.string.no_phone), Toast.LENGTH_SHORT).show();
				}
				
			}
		});
        
        // event listener to handle email button when clicked
        btnEmail.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				// call email client app that installed in android device
				String[] email = new String[]{getString(R.string.email)};
				Intent iSend = new Intent(Intent.ACTION_SEND);
				iSend.putExtra(Intent.EXTRA_EMAIL, email);
				iSend.putExtra(Intent.EXTRA_SUBJECT, "");
				iSend.setType("plain/text");
				iSend.putExtra(Intent.EXTRA_TEXT, "");
				startActivity(iSend);
			}
		});
        
    }
    
    // method to check map fragment
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
        	//this is old code..hala edited that
			// map = fragMap.getMap();
			//with that line
        	fragMap.getMapAsync(this);
            // Check if we were successful in obtaining the map.

        }
    }
    
    // method to set up map
    void setUpMap(){
    	options.mapType(GoogleMap.MAP_TYPE_NORMAL);
		options.compassEnabled(false);
		options.rotateGesturesEnabled(false);
		options.tiltGesturesEnabled(false);
		options.zoomControlsEnabled(false);
		SupportMapFragment.newInstance(options);
		
	}
    
    // method to show restaurant info
    void showInfo(){
    	txtText.setText(R.string.restaurant_name);
    	txtSubText.setText(getString(R.string.address)+"\n"+
    				getString(R.string.phone)+"\n"+
    				getString(R.string.email));
    }
    
    // method to show restaurant location on map
    void showLocation(){

    	// get restaurant latitude & longitude from strings.xml
    	double Latitude = Double.parseDouble(getString(R.string.latitude));
    	double Longitude = Double.parseDouble(getString(R.string.longitude));
			
		LatLng latlng = new LatLng(Latitude, Longitude);
		
		// add marker to the map
		mMap.addMarker(new MarkerOptions()
			.title(getString(R.string.restaurant_name))
			.snippet(getString(R.string.address))
			.position(latlng)
			.icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_icon)
					));
		
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15.0f));
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {

		if (googleMap != null) {
			mMap = googleMap;
			setUpMap();
			showLocation();
		}

	}
	
    
}
