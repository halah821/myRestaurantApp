package com.hala.myrestaurantapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;



public class CategoryList extends Activity {
	
	// declare view objects
	ImageButton imgNavBack, imgRefresh;
	ListView listCategory;
	ProgressBar prgLoading;
	TextView txtAlert;

	// declare adapter object to create custom category list
	com.hala.myrestaurantapp.CategoryListAdapter cla;
	
	// create arraylist variables to store data from server
	static ArrayList<Long> Category_ID = new ArrayList<Long>();
	static ArrayList<String> Category_name = new ArrayList<String>();
	static ArrayList<String> Category_image = new ArrayList<String>();
	
	String CategoryAPI;
	int IOConnect = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

        // connect view objects and xml id
        imgNavBack = (ImageButton) findViewById(R.id.imgNavBack);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        listCategory = (ListView) findViewById(R.id.listCategory);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        imgRefresh = (ImageButton) findViewById(R.id.imgRefresh);
        
        cla = new com.hala.myrestaurantapp.CategoryListAdapter(CategoryList.this);

        // category API url
    	CategoryAPI = com.hala.myrestaurantapp.Utils.CategoryAPI+"?accesskey="+ com.hala.myrestaurantapp.Utils.AccessKey;
        
        // call asynctask class to request data from server
        new getDataTask().execute();
        
        // event listener to handle list when clicked
		listCategory.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				// go to menu page
				Intent iMenuList = new Intent(CategoryList.this, com.hala.myrestaurantapp.MenuList.class);
				iMenuList.putExtra("category_id", Category_ID.get(position));
				iMenuList.putExtra("category_name", Category_name.get(position));
				startActivity(iMenuList);
			}
		});
		
		// event listener to handle refresh button when clicked
		imgRefresh.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// re-request data from server by calling asynctask class
				IOConnect = 0;
				listCategory.invalidateViews();
    			clearData();
				new getDataTask().execute();
			}
		});
        
		// event listener to handle back button when clicked
        imgNavBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// go back to previous page
				finish();
			}
		});
        
    }
    
    // clear arraylist variables before used
    void clearData(){
    	Category_ID.clear();
    	Category_name.clear();
    	Category_image.clear();
    }
    
    // asynctask class to handle parsing json in background
    public class getDataTask extends AsyncTask<Void, Void, Void>{
    	
    	// show progressbar first
    	getDataTask(){
    		if(!prgLoading.isShown()){
    			prgLoading.setVisibility(View.VISIBLE);
				txtAlert.setVisibility(View.GONE);
    		}
    	}
    	
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			// parse json data from server in background
			parseJSONData();
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// when finish parsing, hide progressbar
			prgLoading.setVisibility(View.GONE);
			
			// if internet connection and data available show data on list
			// otherwise, show alert text
			if((Category_ID.size() > 0) && (IOConnect == 0)){
				listCategory.setVisibility(View.VISIBLE);
				listCategory.setAdapter(cla);
			}else{
				txtAlert.setVisibility(View.VISIBLE);
			}
		}
    }
    
    // method to parse json data from server
    public void parseJSONData(){
    	
    	clearData();
    	
    	try {
    		// request data from Category API

            URL requestUrl=new URL(CategoryAPI);
			//HttpsURLConnection con = (HttpsURLConnection) requestUrl.openConnection();
            URLConnection con = requestUrl.openConnection();
			InputStream response = con.getInputStream();
            InputStreamReader x =new InputStreamReader(response);
            BufferedReader in = new BufferedReader(x);
            String line;
            String str = "";
            try {
                while ((line = in.readLine()) != null) {
                    str += line;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        
	        // parse json data and store into arraylist variables
			JSONObject json = new JSONObject(str);
			JSONArray data = json.getJSONArray("data");

			for (int i = 0; i < data.length(); i++) {
			    JSONObject object = data.getJSONObject(i); 
			    
			    JSONObject category = object.getJSONObject("Category");
			    
			    Category_ID.add(Long.parseLong(category.getString("Category_ID")));
			    Category_name.add(category.getString("Category_name"));
			    Category_image.add(category.getString("Category_image"));
			    Log.d("Category name", Category_name.get(i));
				    
			}
				
				
		} catch (MalformedURLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
			IOConnect = 1;
		    e.printStackTrace();
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}	
    }

    
    @Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	}
    
}
