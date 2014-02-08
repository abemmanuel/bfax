package org.cityofchicago.dob.bfax;
 
import org.cityofchicago.dob.bfax.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.cityofchicago.dob.bfax.TabListener;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
//import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Building extends ListActivity   {

	private ProgressDialog pDialog;
   private static String url = null;
	// JSON Node names
	private static final String TAG_CONTACTS = "contacts";
	private static final String TAG_ID = "permit_";
	private static final String TAG_NAME = "street_name";
	private static final String TAG_EMAIL = "street_number";
	private static final String TAG_ADDRESS = "street_direction";
	private static final String TAG_GENDER = "work_description";
	private static final String TAG_PHONE = "_issue_date";
	private static final String TAG_PHONE_MOBILE = "mobile";

	String temp1;
	int temp2;
	Double lat, lon;
	// contacts JSONArray
	JSONArray contacts = null;
	String address;
	// Hashmap for ListView
	ArrayList<HashMap<String, String>> contactList;
	TextView t1 = null;
	String msg = " ";
	  Fragment fragment;
	  ActionBar.Tab Tab1, Tab2, Tab3;
	    Fragment fragmentTab1 = new FragmentTab1();
	    Fragment fragmentTab2 = new FragmentTab2();
	   
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.building_page);
		contactList = new ArrayList<HashMap<String, String>>();
		
		 ActionBar actionBar = getActionBar();
	        // Hide Actionbar Icon
	        actionBar.setDisplayShowHomeEnabled(false);
	 
	        // Hide Actionbar Title
	        actionBar.setDisplayShowTitleEnabled(false);
	 
	        // Create Actionbar Tabs
		 actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		 
		// Set Tab Icon and Titles
	      Tab1 = actionBar.newTab().setText("Permits");
	        Tab2 = actionBar.newTab().setText("Violations");
	       // Tab3 = actionBar.newTab().setText("Tab3");
	 
	        // Set Tab Listeners
	        Tab1.setTabListener(new TabListener(fragmentTab1));
	        Tab2.setTabListener(new TabListener(fragmentTab2));
	        //Tab3.setTabListener(new TabListener(fragmentTab3));
	 
	        // Add tabs to actionbar
	        actionBar.addTab(Tab1);
	        actionBar.addTab(Tab2);
	        //actionBar.addTab(Tab3);
		 
		Bundle b = getIntent().getExtras();

		t1 = (TextView) findViewById(R.id.title);
		t1.setText( b.getString("address"));
		
		//url = "https://data.cityofchicago.org/resource/ydr8-5enu.json?%24select=street_number%2Cstreet_direction%2Cstreet_name%2C_issue_date%2Clatitude%2Clongitude%2C_permit_type%2Cwork_description%2Cpermit_&%24where=%20within_circle(location,%20"+lat+","+lon+",%2050)";
		address = b.getString("address");
		String[] adr = address.split(" ");
		String adrNo = adr[0]; 
		String[] adrRange = adrNo.split("-");
		if (adrRange.length >1){
			adrNo = adrRange[0];
		}
		String dir = adr[1]; 
		String street = adr[2]; 
		if (adr.length > 4){
			street= street+"%20"+adr[3];
		}
		if (street.equals("LaSalle")){street = "La%20Salle";}
		else if (street.equals("Des Plaines")){street = "Desplaines";}
		//pass the address of the location to data source 
		url = "https://data.cityofchicago.org/resource/building-permits.json?%24select=street_number%2Cstreet_direction%2Cstreet_name%2C_issue_date%2Clatitude%2Clongitude%2C_permit_type%2Cwork_description%2Cpermit_&%24where=street_number%20=%20"+adrNo+"%20and%20street_direction%20=%20%27"+dir+"%27and%20street_name%20=%20%27"+street+"%27";
		
		

		// Calling async task to get json
		new GetContacts().execute();

		
		
	}
	
	 

	/**
	 * Async task class to get json by making HTTP call
	 * */
	private class GetContacts extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(Building.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr.length() >= 1) {
				
				
				try {
					//JSONObject jsonObj = new JSONObject(jsonStr);
					 contacts = new JSONArray(jsonStr);
					// Getting JSON Array node
					
					 if (contacts.length() >= 1) {
					for (int i = 0; i < contacts.length(); i++) {
						JSONObject c = contacts.getJSONObject(i);
						
						String id = c.getString(TAG_ID);
						String name = c.getString(TAG_NAME);
						String email = c.getString(TAG_EMAIL);
						//String address = c.getString(TAG_ADDRESS);
						String gender = c.getString(TAG_GENDER);

						String mobile = c.getString(TAG_ADDRESS);
						String home = c.getString(TAG_PHONE);
						//String office = phone.getString(TAG_PHONE_OFFICE);

						// tmp hashmap for single contact
						HashMap<String, String> contact = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						contact.put(TAG_ID, id);
						contact.put(TAG_NAME, email+' '+mobile+' '+name);
						contact.put(TAG_EMAIL, gender);
						contact.put(TAG_PHONE_MOBILE, home);

						// adding contact to contact list
						contactList.add(contact);
					}
					
					 } else 
					 {msg = "No permits issued for this address. Below are the nearest address for which a permit is issued.";
					 getLatLong(address+", Chicago, IL");
					 //if no data matching the search criteria is found - find addresses within 120 feet of the search criteria. User can get details on these nearby buildings
					 url = "https://data.cityofchicago.org/resource/ydr8-5enu.json?%24select=street_number%2Cstreet_direction%2Cstreet_name&%24where=%20within_circle(location,"+lat+","+lon+",%20120)&$group=street_name,street_direction,street_number&$limit=10";
					 jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
					 
					 if (jsonStr.length() >= 1) {			
							try {
								 contacts = new JSONArray(jsonStr);
								 if (contacts.length() > 1) {
	
					 for (int i = 0; i < contacts.length(); i++) {
							JSONObject c = contacts.getJSONObject(i);

							String name = c.getString(TAG_NAME);
							String email = c.getString(TAG_EMAIL);
							String mobile = c.getString(TAG_ADDRESS);
							HashMap<String, String> contact = new HashMap<String, String>();
							// adding each child node to HashMap key => value
							//contact.put(TAG_NAME, email+' '+mobile+' '+name);
							contact.put(TAG_NAME, email+' '+mobile+' '+name);
							// adding contact to contact list
							contactList.add(contact);
						}
					 
								 }
					 
							} catch (JSONException e) {
								e.printStackTrace();
							}
					 
					 }
					 
					 }
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldnt get any data from the url");
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */
			ListAdapter adapter = null;
			if (msg.equals(" ")){
				//if matching data is found in the data source run this set of code. or else use the code in the else section
				//which will display information on nearby buildings
//View vw = findViewById(R.layout.fragment1);
				ListView lv = getListView();
//ListView lv = (ListView) vw;
				// Listview on item click listener
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// getting values from selected ListItem
						String name = ((TextView) view.findViewById(R.id.name))
								.getText().toString();
						String cost = ((TextView) view.findViewById(R.id.email))
								.getText().toString();
						String description = ((TextView) view.findViewById(R.id.mobile))
								.getText().toString();

						// Starting single contact activity
						Intent in = new Intent(getApplicationContext(),
								SingleContactActivity.class);
						in.putExtra(TAG_NAME, name);
						in.putExtra(TAG_EMAIL, cost);
						in.putExtra(TAG_PHONE_MOBILE, description);
						startActivity(in);

					}
				});
				
				//setContentView(R.layout.fragment1);
		
			adapter = new SimpleAdapter(
					Building.this, contactList,
					R.layout.list_item, new String[] { TAG_NAME, TAG_EMAIL,
							TAG_PHONE_MOBILE }, new int[] { R.id.name,
							R.id.email, R.id.mobile });
			
			}
			else{
				
				
				ListView lv = getListView();
				//View vw = findViewById(R.layout.fragment1);
//ListView lv = (ListView) vw;
				// Listview on item click listener
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// getting values from selected ListItem
						String name = ((TextView) view.findViewById(R.id.name))
								.getText().toString();
						String cost = ((TextView) view.findViewById(R.id.email))
								.getText().toString();
						String description = ((TextView) view.findViewById(R.id.mobile))
								.getText().toString();

						// Starting single contact activity
						Intent in = new Intent(getApplicationContext(),
								Building.class);
						
						Bundle b = new Bundle();
						b.putString("address", name);
		                 in.putExtras(b); 
						startActivity(in);

					}
				});
				adapter = new SimpleAdapter(
						Building.this, contactList, 
						R.layout.list_item, new String[] {TAG_NAME}, new int[] { R.id.name});
			}
			t1.setText(address + " "+ msg);
			setListAdapter(adapter);
		}

	}
	
	@Override
	public void onBackPressed() {
		Intent in = new Intent(Building.this,MainActivity.class);
		 Bundle b = new Bundle();
         //b.putDouble("lat", lat); 
         //b.putDouble("lon", lon); 
		 b.putString("address",address);
         in.putExtras(b); 
		startActivity(in);
		finish();
	}
	
    private  void getLatLong(String youraddress) {
    	Geocoder coder = new Geocoder(this);
    	List<Address> address;

    	try {
    	    address = coder.getFromLocationName(youraddress,5);
    	    if (address == null) {
    	       // return null;
    	    }
    	    Address location = address.get(0);

lat=location.getLatitude();
lon=location.getLongitude();
//LatLng point = new LatLng(lat,lon);

    	}catch (Exception e) {
            //return false;
        }
 
    }
    
    
    
}