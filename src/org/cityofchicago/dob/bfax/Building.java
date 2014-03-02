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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Building extends ListActivity   {

	private ProgressDialog pDialog;
	private static String url = null;
	private static String urlV = null;
	// JSON Node names
	private static final String TAG_ID = "permit_";
	private static final String TAG_STNAME = "street_name";
	private static final String TAG_STNUMBER = "street_number";
	private static final String TAG_STDIRECTION = "street_direction";
	private static final String TAG_DESCRIPTION = "work_description";
	private static final String TAG_ISSDATE = "_issue_date";
	private static final String TAG_ADDRESS = "address";
	private static final String TAG_INSP_CATEGORY = "inspection_category";
	private static final String TAG_VIOLATION_DT = "violation_date";
	private static final String TAG_INSP_STATUS = "inspection_status";
	private static final String TAG_VIOL_DESCR = "violation_description";
	private static final String TAG_VIOL_CODE = "violation_code";

	String temp1;
	int temp2;
	Double lat, lon;
	// permits JSONArray
	JSONArray permits = null;
	JSONArray violations = null;
	String address;
	// Hashmap for ListView
	ArrayList<HashMap<String, String>> permitsList;
	ArrayList<HashMap<String, String>> ViolationsList;
	TextView t1 = null;
	String msg = " ";
	Fragment fragment;
	ActionBar.Tab Tab1, Tab2, Tab3;
	ListAdapter adapterV = null;

	ListView lv = null;
	ListView lv2 = null;
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.building_page);
		permitsList = new ArrayList<HashMap<String, String>>();
		ViolationsList = new ArrayList<HashMap<String, String>>();
		
		Fragment fragmentTab1 = new FragmentTab1().newInstance("abc");
		Fragment fragmentTab2 = new FragmentTab2().newInstance("xyz");;

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
		// fragmentTab1.setRetainInstance(true);
		// Set Tab Listeners
		//Tab1.set
		//Fragment fr = (Fragment) getFragmentManager().findFragmentById(R.layout.fragment1);
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
		urlV = "https://data.cityofchicago.org/resource/22u3-xenr.json?%24select=address%2Cinspection_category%2cviolation_description%2Cinspection_number%2Cviolation_code%2Cviolation_date%2Cinspection_status%2Clongitude%2Clatitude%2Cviolation_ordinance&%24where=address%20=%20%27611%20S%20WELLS%20ST%27&$limit=3";


		// Calling async task to get json
		new GetPermits().execute();
		//new GetViolations().execute();
		
		

	}


	/**
	 * Async task class to get json by making HTTP call
	 * */
	private class GetPermits extends AsyncTask<Void, Void, Void> {

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
					permits = new JSONArray(jsonStr);
					// Getting JSON Array node

					if (permits.length() >= 1) {
						for (int i = 0; i < permits.length(); i++) {
							JSONObject c = permits.getJSONObject(i);

							String id = c.getString(TAG_ID);
							String st_number = c.getString(TAG_STNUMBER);
							String st_direction = c.getString(TAG_STDIRECTION);
							String st_name = c.getString(TAG_STNAME);
							String description = c.getString(TAG_DESCRIPTION);
							String iss_date = c.getString(TAG_ISSDATE);


							// tmp hashmap for permit details
							HashMap<String, String> permit_details = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							permit_details.put(TAG_ID, id);
							permit_details.put(TAG_ADDRESS, st_number+' '+st_direction+' '+st_name);
							permit_details.put(TAG_DESCRIPTION, description);
							permit_details.put(TAG_ISSDATE, iss_date);

							// adding detials to the list view
							permitsList.add(permit_details);
						}

					} else 
					{msg = "No permits issued for this address. Below are the nearest address for which a permit is issued.";
					getLatLong(address+", Chicago, IL");
					//if no data matching the search criteria is found - find addresses within 120 meters of the search location. User can get details on these nearby buildings
					url = "https://data.cityofchicago.org/resource/ydr8-5enu.json?%24select=street_number%2Cstreet_direction%2Cstreet_name&%24where=%20within_circle(location,"+lat+","+lon+",%20120)&$group=street_name,street_direction,street_number&$limit=10";
					jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

					if (jsonStr.length() >= 1) {			
						try {
							permits = new JSONArray(jsonStr);
							if (permits.length() > 1) {

								for (int i = 0; i < permits.length(); i++) {
									JSONObject c = permits.getJSONObject(i);

									String st_name = c.getString(TAG_STNAME);
									String st_number = c.getString(TAG_STNUMBER);
									String st_direction = c.getString(TAG_STDIRECTION);
									HashMap<String, String> nearby = new HashMap<String, String>();
									// adding each child node to HashMap key => value
									nearby.put(TAG_ADDRESS, st_number+' '+st_direction+' '+st_name);
									// adding permit details to list view
									permitsList.add(nearby);
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
			
	//to get violation data using violations url
	String jsonStrV = sh.makeServiceCall(urlV, ServiceHandler.GET);


			if (jsonStrV.length() >= 1) {	
				try{
					violations = new JSONArray(jsonStrV);
					if (violations.length() > 1) {

						for (int i = 0; i < violations.length(); i++) {
							JSONObject c = violations.getJSONObject(i);

							String insp_category = c.getString(TAG_INSP_CATEGORY);
							String address = c.getString(TAG_ADDRESS);
							String viol_dt = c.getString(TAG_VIOLATION_DT);
							String insp_stat = c.getString(TAG_INSP_STATUS);
							String viol_descr = c.getString(TAG_VIOL_DESCR);
							String viol_cd = c.getString(TAG_VIOL_CODE);
							
							HashMap<String, String> viol = new HashMap<String, String>();
							// adding each child node to HashMap key =>  	value
							viol.put(TAG_ADDRESS, address);
							viol.put(TAG_VIOLATION_DT, viol_dt);
							viol.put(TAG_INSP_CATEGORY, insp_category);
							viol.put(TAG_VIOL_DESCR, viol_descr);
							viol.put(TAG_VIOL_CODE, viol_cd);
							// adding violations details to list view
							ViolationsList.add(viol);}
						}
				
			} catch (JSONException e) {
				e.printStackTrace();}
			
			} else {
				Log.e("ServiceHandler", "Couldnt get any data from the urlV");
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
			//ListAdapter adapter = null;
			ListAdapter adapter = null;


			if (msg.equals(" ")){
				//if matching data is found in the data source run this set of code. or else use the code in the else section
				//which will display information on nearby buildings

				lv = (ListView) findViewById (R.id.list2);
				//lv2 = (ListView) findViewById (R.id.listView1);
				// Listview on item click listener
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// getting values from selected ListItem
						String address = ((TextView) view.findViewById(R.id.address))
								.getText().toString();
						String description = ((TextView) view.findViewById(R.id.description))
								.getText().toString();
						String iss_date = ((TextView) view.findViewById(R.id.issdate))
								.getText().toString();

						// Starting permit details activity
						Intent in = new Intent(getApplicationContext(),
								PermitDetails.class);
						in.putExtra("address", address);
						in.putExtra("description", description);
						in.putExtra("issdt", iss_date);
						startActivity(in);

					}
				});

				//setContentView(R.layout.fragment1);
				//lv2 = (ListView) findViewById (R.id.list2);
				//lv2.setAdapter(new ArrayAdapter<String> (Building.this, R.layout.list_item, s2));
				// lv2.setAdapter(new ArrayAdapter <HashMap<String, String>> (Building.this, R.layout.list_item, permitsList));
				//lv2.setAdapter(new ArrayAdapter<String> (Building.this, android.R.layout.simple_list_item_1, s2));
				//ArrayAdapter <HashMap<String, String>> t = new ArrayAdapter <HashMap<String, String>>(Building.this, R.layout.list_item, permitsList) ;
				//setListAdapter(t);
				// lv.setAdapter(adapter);

				adapter = new SimpleAdapter(
						Building.this, permitsList,
						R.layout.list_item, new String[] { TAG_ADDRESS, 
								TAG_DESCRIPTION, TAG_ISSDATE }, new int[] { R.id.address,
								R.id.description, R.id.issdate }
						);

			}
			else{


				//ListView lv = getListView();
				lv = (ListView) findViewById (R.id.list2);
				// Listview on item click listener
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// getting values from selected ListItem
						String Address = ((TextView) view.findViewById(R.id.address))
								.getText().toString();


						// Start building activity with a specific address
						Intent in = new Intent(getApplicationContext(),
								Building.class);

						Bundle b = new Bundle();
						b.putString("address", Address);
						in.putExtras(b); 
						startActivity(in);

					}
				});
				adapter = new SimpleAdapter(
						Building.this, permitsList, 
						R.layout.list_item, new String[] {TAG_ADDRESS}, new int[] { R.id.address});
			}
			t1.setText(address + " "+ msg);
			
			//setListAdapter(adapter);
		
			lv.setAdapter(adapter);
			
//for updating the violations			

			lv2 = (ListView) findViewById (R.id.list5);
			
			adapter = new SimpleAdapter(
					Building.this, ViolationsList, 
					R.layout.list_item, new String[] {TAG_ADDRESS}, new int[] {R.id.address});
			
			//lv2.setAdapter(adapter);//this does not work
			//lv2.setAdapter(adapter);//this works but put the list in the first tab itself.
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