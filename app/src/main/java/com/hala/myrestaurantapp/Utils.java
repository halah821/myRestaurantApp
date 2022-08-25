package com.hala.myrestaurantapp;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	
	// Google Play configuration
	static String GPlayURL = "https://play.google.com/store/apps/details?id=your.package.com";
	
	// API URL configuration
	static String AdminPageURL = "http://192.168.8.102:80/admin_panel/";
	static String CategoryAPI = "http://192.168.8.102:80/admin_panel/api/get-all-category-data.php";
	static String MenuAPI = "http://192.168.8.102:80/admin_panel/api/get-menu-data-by-category-id.php";
	static String TaxCurrencyAPI = "http://192.168.8.102:80/admin_panel/api/get-tax-and-currency.php";
	static String MenuDetailAPI = "http://192.168.8.102:80/admin_panel/api/get-menu-detail.php";
	static String SendDataAPI = "http://192.168.8.102:80/admin_panel/api/add-reservation.php";
	static String MenuSharingAPI = "http://192.168.8.102:80/admin_panel/menu-sharing.php";
	
	// change this access similar with accesskey in admin panel for security reason
	static String AccessKey = "12345";
	
	// database path configuration
	static String DBPath = "/data/data/com.hala.myrestaurantapp/databases/";
	
	// method to check internet connection
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// method to handle images from server
	public static void CopyStream(InputStream is, OutputStream os)
	    {
	        final int buffer_size=1024;
	        try
	        {
	            byte[] bytes=new byte[buffer_size];
	            for(;;)
	            {
	              int count=is.read(bytes, 0, buffer_size);
	              if(count==-1)
	                  break;
	              os.write(bytes, 0, count);
	            }
	        }
	        catch(Exception ex){}
	    }
}
