package org.cityofchicago.dob.bfax;


import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
 
public class TabListener implements ActionBar.TabListener {
 
    public Fragment fragment;
    public Context context;
    
    /*   public TabListener(Fragment fragment, Context context) {
        this.fragment = fragment;
        this.context = context;

}*/

    
    public TabListener(Fragment fragment) {
      //  TODO Auto-generated constructor stub
        this.fragment = fragment;

    }
 
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    	if (fragment != null) {
    	ft.replace(R.id.fragment_container, fragment);
    	   
    	}
    }
 
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    	if (fragment != null) {
       ft.remove(fragment);
       //ft.hide(fragment);
       }
    }
 
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }
    
    
}
