package ru.brostudios.android_weather_test;

import ru.brostudios.android_weather_test.Weather.Location;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextWatcher;
import android.text.Editable;
import android.os.AsyncTask;
import android.os.Bundle;

public class SettingsActivity extends FragmentActivity {
	
	private CitiesDatabase database;
	private AutoCompleteTextView textView;
	private RadioGroup radioGroup;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		database = new CitiesDatabase(this);
		database.open();
		
		// if database is empty - add two default cities
		if(database.isEmpty()) {
			database.insert("Saint Petersburg, Russia");
			database.insert("Moscow, Russia");
		}
		
		radioGroup = (RadioGroup)findViewById(R.id.radioGroup1);
		textView = ((AutoCompleteTextView)findViewById(R.id.editText1));
		textView.addTextChangedListener(textWatcher);
		
		Cursor cursor = database.selectAll();
		while(cursor.moveToNext()) {
			RadioButton button = new RadioButton(this);
			button.setText(cursor.getString(0));
			radioGroup.addView(button);
		}
		((Button)findViewById(R.id.button1)).setOnClickListener(clickListener);
		database.close();
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				View view = group.findViewById(checkedId);
				String text = ((TextView)view).getText().toString();
				Intent intent = new Intent(SettingsActivity.this, WeatherActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(WeatherActivity.KEY_CITY, text);
				startActivity(intent);
			}
		});
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {

				@Override
				protected String doInBackground(String... params) {
					return WeatherHttpClient.getSearchResult(params[0]);
				}
				@Override
				protected void onPostExecute(String result) {
					Location[] locations = WeatherHttpClient.parseLocation(result);
					if(locations.length == 0) return;
					RadioButton button = new RadioButton(SettingsActivity.this);
					String row = locations[0].m_city+", "+locations[0].m_country;
					button.setText(row);
					database.open();
					database.insert(row);
					radioGroup.addView(button);
				}
				
			};
			asyncTask.execute(textView.getText().toString());
		}
	};
	
	private TextWatcher textWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
			AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... params) {
					return WeatherHttpClient.getSearchResult(params[0]);
				}
				
				@Override
				protected void onPostExecute(String result) {
					Location[] locations = WeatherHttpClient.parseLocation(result);
					if(locations == null || locations.length == 0) return;
					String[] cities = new String[locations.length];
					for(int i=0;i<cities.length;i++) { cities[i] = locations[i].m_city+", "+locations[i].m_country; }
					
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_list_item_1, cities);
					textView.setAdapter(adapter);
				}
				
			};
			asyncTask.execute(s.toString());
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
		@Override
		public void afterTextChanged(Editable s) { }
	};
	
}
