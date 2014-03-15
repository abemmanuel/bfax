package org.cityofchicago.dob.bfax;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.app.Fragment;

 
public class FragmentTab2 extends Fragment {
	
	String str;
	static Bundle args;
	
	  public static FragmentTab2 newInstance(String str) {
		  FragmentTab2 f = new FragmentTab2();
	       args = new Bundle();
	        args.putCharSequence("string", str);
	        f.setArguments(args);
	        return f;
	    }
	  
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView2 = inflater.inflate(R.layout.fragment2, container, false);
        
        TextView test = (TextView) rootView2.findViewById(R.id.t2);
        test.setText( args.getCharSequence("string"));
        
        return rootView2;
    }
 
}