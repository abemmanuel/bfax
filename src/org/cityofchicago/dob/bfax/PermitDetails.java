package org.cityofchicago.dob.bfax;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import org.cityofchicago.dob.bfax.R;

public class PermitDetails  extends Activity {
	
	// JSON node keys
	private static final String TAG_NAME = "address";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_ISSDT = "issdt";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permit_details);
        
        // getting intent data
        Intent in = getIntent();
        
        // Get JSON values from previous intent
        String name = in.getStringExtra(TAG_NAME);
        String email = in.getStringExtra(TAG_DESCRIPTION);
        String mobile = in.getStringExtra(TAG_ISSDT);
        
        // Displaying all values on the screen
        TextView lblName = (TextView) findViewById(R.id.name_label);
        TextView lblEmail = (TextView) findViewById(R.id.email_label);
        TextView lblMobile = (TextView) findViewById(R.id.mobile_label);
        
        lblName.setText(name);
        lblEmail.setText(email);
        lblMobile.setText(mobile);
    }
}
