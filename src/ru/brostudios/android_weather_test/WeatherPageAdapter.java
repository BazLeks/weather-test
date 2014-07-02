package ru.brostudios.android_weather_test;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherPageAdapter extends FragmentStatePagerAdapter {
	
	public static final int TABS_FRAGMENT_COUNT = 2;
	public static final double PRESSURE_RATIO = 0.0075006375541921;	// 1 Pa
	private String city = null;
	
	public WeatherPageAdapter(FragmentManager fm, Bundle bundle) {
		super(fm);
		if(bundle!=null) city = bundle.getString(WeatherActivity.KEY_CITY);
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment = null;
		switch(arg0) {
			case 0: fragment = CurrentFragment.newInstance(city); break;
			case 1: fragment = ForecastFragment.newInstance(city); break;
		} 
		// TODO: add bundle arguments here (may be city name)
		return fragment;
	}

	@Override
	public int getCount() {
		return TABS_FRAGMENT_COUNT;
	}
	
	public static class CurrentFragment extends Fragment {
		
		private View rootView;
		private String city;
		
		
		public static CurrentFragment newInstance(String city) {
			CurrentFragment fragment = new CurrentFragment();
			fragment.city = city;
			return fragment;
		}
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			AsyncTask<String, Void, Weather>asyncTask = new AsyncTask<String, Void, Weather>() {

				@Override
				protected Weather doInBackground(String... params) {
					String result = WeatherHttpClient.getWeatherData(city);
					return WeatherHttpClient.parseCurrent(result);
				}
				
				@Override
				protected void onPostExecute(Weather weatherCurrent) {
					if(weatherCurrent!=null) {
						TextView textCity = ((TextView)getActivity().findViewById(R.id.current_city));
						textCity.setText(weatherCurrent.location.m_city+", "+weatherCurrent.location.m_country);
						int radius = 5;
						textCity.setShadowLayer(radius, 0, 0, Color.BLACK);
						textCity.setAnimation(newAnimation(0));
						
						TextView textTemp = ((TextView)getActivity().findViewById(R.id.current_temperature));
						DecimalFormat decimalFormat = new DecimalFormat("#.0");
						String temp = decimalFormat.format(weatherCurrent.m_temp-273.15f);
						textTemp.setText(temp+" °C");
						textTemp.setShadowLayer(radius, 0, 0, Color.BLACK);
						textTemp.setAnimation(newAnimation(50));
						
						TextView textHumidity = ((TextView)getActivity().findViewById(R.id.current_humidity));
						textHumidity.setText("Humidity: "+Float.toString(weatherCurrent.m_humidity)+"%");
						textHumidity.setShadowLayer(radius, 0, 0, Color.BLACK);
						textHumidity.setAnimation(newAnimation(100));
						
						TextView textPressure = ((TextView)getActivity().findViewById(R.id.current_pressure));
						textPressure.setText("Pressure: "+Math.round(weatherCurrent.m_pressure*PRESSURE_RATIO*100)+" mm Hg");
						textPressure.setShadowLayer(radius, 0, 0, Color.BLACK);
						textPressure.setAnimation(newAnimation(150));
						
						TextView textWind = ((TextView)getActivity().findViewById(R.id.current_wind));
						textWind.setText("Wind speed: "+Float.toString(weatherCurrent.wind.m_speed));
						textWind.setShadowLayer(radius, 0, 0, Color.BLACK);
						textWind.setAnimation(newAnimation(200));
						
						TextView textDescription = ((TextView)getActivity().findViewById(R.id.current_wind));
						textDescription.setText("Today is: "+weatherCurrent.m_description);
						textDescription.setShadowLayer(radius, 0, 0, Color.BLACK);
						textDescription.setAnimation(newAnimation(250));
						
						try {
							String condition = weatherCurrent.m_condition;
							Bitmap bitmap = null;
							if(condition.equals("Clouds")) bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("cloudy.png"));
							if(condition.equals("Clear")) bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("sunny.png"));
							if(condition.equals("Rain")) bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("rain.png"));
							rootView.setBackgroundDrawable(new BitmapDrawable(bitmap));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
					
				}
			};
			asyncTask.execute();
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_current, container, false);
			return rootView;
		}
		
		private AnimationSet newAnimation(long delay) {
			AnimationSet set = new AnimationSet(false);
			TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 100, 0);
			translateAnimation.setStartOffset(delay);
			translateAnimation.setDuration(1000);
			AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
			alphaAnimation.setStartOffset(delay);
			alphaAnimation.setDuration(1000);
			set.addAnimation(translateAnimation);
			set.addAnimation(alphaAnimation);
			return set;
		}
	}
	
	public static class ForecastFragment extends Fragment {
		
		private LinearLayout container;
		private String city;
		
		public static ForecastFragment newInstance(String city) {
			ForecastFragment fragment = new ForecastFragment();
			fragment.city = city;
			return fragment;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
			this.container = (LinearLayout)rootView.findViewById(R.id.forecast_container);
			
			
			return rootView;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			new AsyncTask<String, Void, Weather[]>() {

				@Override
				protected Weather[] doInBackground(String... params) {
					String result = WeatherHttpClient.getWeatherForecast(city);
					
					// TODO: load pictures of weather
					
					return WeatherHttpClient.parseForecast(result);
				}
				
				@Override
				protected void onPostExecute(Weather[] days) {
					LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					for(int i=0;i<days.length;i++) {
						View addView = inflater.inflate(R.layout.forecast_day, null);
						((TextView)addView.findViewById(R.id.forecast_day)).setText(filterTimestamp(days[i].m_time, "EEEE"));
						DecimalFormat decimalFormat = new DecimalFormat("#.0");
						String temperature = decimalFormat.format(days[i].m_temp-273.15f);
						((TextView)addView.findViewById(R.id.forecast_temp)).setText(temperature+" °C");
						container.addView(addView);
					}
				}
				
			}.execute();
		}
		
		
		private String filterTimestamp(String time, String filter) {
			long timestamp = Long.parseLong(time)*1000;
			return DateFormat.format(filter, new Date(timestamp)).toString();			
		}
	}
}