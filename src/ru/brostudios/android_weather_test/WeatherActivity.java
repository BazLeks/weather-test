package ru.brostudios.android_weather_test;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;

public class WeatherActivity extends FragmentActivity {
	
	public static String KEY_CITY = "ru.brostudios.key_city";
	private String city = null;
	
	private ViewPager mViewPager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
		String city = sharedPref.getString(KEY_CITY, null);
		
		Bundle extras = getIntent().getExtras();

		if(extras == null) { // start an activity
			if(city == null) {
				Intent intent = new Intent(this, SettingsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		} else {
			// after settings
			city = extras.getString(KEY_CITY);
			sharedPref = getSharedPreferences(KEY_CITY, MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString(KEY_CITY, city);
			editor.commit();
		}
		
		WeatherPageAdapter currentFragment = new WeatherPageAdapter(getSupportFragmentManager(), extras);
		mViewPager = (ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(currentFragment);
		mViewPager.setOnPageChangeListener(pageChangeListener);
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		actionBar.addTab(actionBar.newTab().setText("Current").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Forecast").setTabListener(tabListener));
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CITY, city);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_update:
			break;
		}
	    return super.onOptionsItemSelected(item);
	}
	
	private ActionBar.TabListener tabListener = new TabListener() {
		
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			
		}
		
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			mViewPager.setCurrentItem(tab.getPosition());
		}
		
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			
		}
	};
	
	private ViewPager.SimpleOnPageChangeListener pageChangeListener = new SimpleOnPageChangeListener() {
		 @Override
         public void onPageSelected(int position) {
             getActionBar().setSelectedNavigationItem(position);
         }
	};
}
