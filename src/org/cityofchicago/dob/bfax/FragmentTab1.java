package org.cityofchicago.dob.bfax;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.Fragment;
 
public class FragmentTab1 extends Fragment {
	
	String str;
	static Bundle args;
	
	  public static FragmentTab1 newInstance(String str) {
		  FragmentTab1 f = new FragmentTab1();
	       args = new Bundle();
	        args.putCharSequence("string", str);
	        f.setArguments(args);
	        return f;
	    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment1, container, false);
       
        TextView test = (TextView) rootView.findViewById(R.id.t1);
        test.setText( args.getCharSequence("string"));
        
       // super.onCreate(savedInstanceState);
        //Fragment fr = (Fragment) findViewById(R.id.fr1);
       
    	//View rootView = inflater.inflate(R.layout.building_page, container, false);
        return rootView; 
    }
    
 
}
