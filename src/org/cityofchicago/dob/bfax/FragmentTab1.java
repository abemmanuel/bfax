package org.cityofchicago.dob.bfax;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
 
public class FragmentTab1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment1, container, false);
    	//View rootView = inflater.inflate(R.layout.building_page, container, false);
        return rootView; 
    }
 
}
