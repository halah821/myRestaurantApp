package com.hala.myrestaurantapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class YourOrder extends Activity {
	
	// declare view objects
	ImageButton imgNavBack;
	ListView listOrder;
	ProgressBar prgLoading;
	TextView txtTotalLabel, txtTotal, txtAlert;
	Button btnClear;
	RelativeLayout lytOrder;
	
	// declate dbhelper and adapter objects
	DBHelper dbhelper;
	YourOrderListAdapter mola;
	
	
	// declare static variables to store tax and currency data
	static double Tax;
	static String Currency;

	// declare arraylist variable to store data
	ArrayList<ArrayList<Object>> data;
	static ArrayList<Integer> Menu_ID = new ArrayList<Integer>();
	static ArrayList<String> Menu_name = new ArrayList<String>();
	static ArrayList<Integer> Quantity = new ArrayList<Integer>();
	static ArrayList<Double> Sub_total_price = new ArrayList<Double>();

	double Total_price;
	final int CLEAR_ALL_ORDER = 0;
	final int CLEAR_ONE_ORDER = 1;
	int FLAG;
	int ID;
	String TaxCurrencyAPI;
	int IOConnect = 0;
	
	// create price format
	DecimalFormat formatData = new DecimalFormat("#.##");

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_order);
        
        // connect view objects with xml id
        imgNavBack = (ImageButton) findViewById(R.id.imgNavBack);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        listOrder = (ListView) findViewById(R.id.listOrder);
        txtTotalLabel = (TextView) findViewById(R.id.txtTotalLabel);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        btnClear = (Button) findViewById(R.id.btnClear);
        lytOrder = (RelativeLayout) findViewById(R.id.lytOrder);
        
        // tax and currency API url
        TaxCurrencyAPI = Utils.TaxCurrencyAPI+"?accesskey="+Utils.AccessKey;
    	
        mola = new YourOrderListAdapter(this);
        dbhelper = new DBHelper(this);
        
        // open database
        try{
			dbhelper.openDataBase();
		}catch(SQLException sqle){
			throw sqle;
		}

        // call asynctask class to request tax and currency data from server
        new getTaxCurrency().execute();

        // event listener to handle clear button when clicked
		btnClear.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// show confirmation dialog
				showClearDialog(CLEAR_ALL_ORDER, 1111);
			}
		});
		
        // event listener to handle list when clicked
		listOrder.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// show confirmation dialog
				showClearDialog(CLEAR_ONE_ORDER, Menu_ID.get(position));
			}
		});
        
        // event listener to handle back button when clicked
        imgNavBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// close database and back to previous page
				dbhelper.close();
				finish();
			}
		});
      
    }
    
    // method to create dialog
    void showClearDialog(int flag, int id){
    	FLAG = flag;
    	ID = id;
		AlertDialog.Builder builder = 	new AlertDialog.Builder(this);
		builder.setTitle(R.string.confirm);
		switch(FLAG){
		case 0:
			builder.setMessage(getString(R.string.clear_all_order));
			break;
		case 1:
			builder.setMessage(getString(R.string.clear_one_order));
			break;
		}
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch(FLAG){
				case 0:
					// clear all menu in order table
					dbhelper.deleteAllData();
	    			listOrder.invalidateViews();
	    			clearData();
					new getDataTask().execute();
					break;
				case 1:
					// clear selected menu in order table
					dbhelper.deleteData(ID);
	    			listOrder.invalidateViews();
	    			clearData();
					new getDataTask().execute();
					break;
				}
				
			}
		});
		
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// close dialog
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	
    }
    
    // asynctask class to handle parsing json in background
    public class getTaxCurrency extends AsyncTask<Void, Void, Void>{
    	
    	// show progressbar first
		getTaxCurrency(){
	 		if(!prgLoading.isShown()){
	 			prgLoading.setVisibility(View.VISIBLE);
				txtAlert.setVisibility(View.GONE);
	 		}
	 	}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			// parse json data from server in background
			parseJSONDataTax();
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// when finish parsing, hide progressbar
			prgLoading.setVisibility(View.GONE);
			// if internet connection available request data form server
			// otherwise, show alert text
			if(IOConnect == 0){
				new getDataTask().execute();
			}else{
				txtAlert.setVisibility(View.VISIBLE);
				txtAlert.setText(R.string.alert);
			}
			
		}
    }

    // method to parse json data from server
	public void parseJSONDataTax(){

		
		try {
			// request data from tax and currency API

			URL requestUrl=new URL(TaxCurrencyAPI);
			URLConnection con = requestUrl.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
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


	        // parse json data and store into tax and currency variables
			JSONObject json = new JSONObject(str);

			JSONArray data = json.getJSONArray("data"); // this is the "items: [ ] part
			
			JSONObject object_tax = data.getJSONObject(0); 
			JSONObject tax = object_tax.getJSONObject("tax_n_currency");
			    
			Tax = Double.parseDouble(tax.getString("Value"));
			
			JSONObject object_currency = data.getJSONObject(1); 
			JSONObject currency = object_currency.getJSONObject("tax_n_currency");
			
			Currency = currency.getString("Value");
			    
			
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

	// clear arraylist variables before used
    void clearData(){
    	Menu_ID.clear();
    	Menu_name.clear();
    	Quantity.clear();
    	Sub_total_price.clear();
    }
    
    // asynctask class to handle parsing json in background
    public class getDataTask extends AsyncTask<Void, Void, Void>{
    	
    	// show progressbar first
    	getDataTask(){
    		if(!prgLoading.isShown()){
    			prgLoading.setVisibility(View.VISIBLE);
    			lytOrder.setVisibility(View.GONE);
    			txtAlert.setVisibility(View.GONE);
    		}
    	}
    	
    	@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
    		// get data from database
    		getDataFromDatabase();
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// show data
			txtTotal.setText(Total_price+" "+Currency);
			txtTotalLabel.setText(getString(R.string.total_order)+" (Tax "+Tax+"%)");
			prgLoading.setVisibility(View.GONE);
			// if data available show data on list
			// otherwise, show alert text
			if(Menu_ID.size() > 0){
				lytOrder.setVisibility(View.VISIBLE);
				listOrder.setAdapter(mola);
			}else{
				txtAlert.setVisibility(View.VISIBLE);
			}
			
		}
    }
    
    // method to get data from server
    public void getDataFromDatabase(){
    	
    	Total_price = 0;
    	clearData();
    	data = dbhelper.getAllData();
    	
    	// store data to arraylist variables
    	for(int i=0;i<data.size();i++){
    		ArrayList<Object> row = data.get(i);
    		
    		Menu_ID.add(Integer.parseInt(row.get(0).toString()));
    		Menu_name.add(row.get(1).toString());
    		Quantity.add(Integer.parseInt(row.get(2).toString()));
    		Sub_total_price.add(Double.parseDouble(formatData.format(Double.parseDouble(row.get(3).toString()))));
    		Total_price += Sub_total_price.get(i);
    	}
    	
    	// count total order
    	Total_price += (Total_price * (Tax/100));
    	Total_price = Double.parseDouble(formatData.format(Total_price));
    }
    
    // when back button pressed close database and back to previous page
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	dbhelper.close();
    	finish();
    }
	 
    @Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	}

    
}
