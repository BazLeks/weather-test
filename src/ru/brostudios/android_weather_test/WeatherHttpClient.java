package ru.brostudios.android_weather_test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import ru.brostudios.android_weather_test.Weather.Clouds;
import ru.brostudios.android_weather_test.Weather.Location;
import ru.brostudios.android_weather_test.Weather.Wind;


public class WeatherHttpClient {
 
    private static String BASE_URL = "http://api.openWeathermap.org/data/2.5/Weather?q=";
    private static String FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?cnt=7&mode=json&q=";
    private static String SEARCH_URL = "http://api.openweathermap.org/data/2.5/find?mode=json&cnt=10&q=";
    private static String IMG_URL = "http://openWeathermap.org/img/w/";
    
    
    public static Weather[] parseForecast(String data) {
    	
    	Weather[] daysWeather = null;
    	
    	try {
			JSONObject jObj = new JSONObject(data);
			int count = getInt("cnt", jObj);
			if(count == 0) return null;
			
			daysWeather = new Weather[count];
			
			JSONArray listObj = getArray("list", jObj);
			for(int i=0;i<listObj.length();i++) {
				daysWeather[i] = new Weather();
				JSONObject dayObj = listObj.getJSONObject(i);
				daysWeather[i].m_time = getString("dt", dayObj);
				JSONObject tempObj = dayObj.getJSONObject("temp");
				daysWeather[i].m_temp = getFloat("day", tempObj);
				Log.d("", "");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return daysWeather;
    }
    
    public static Weather parseCurrent(String data) {
    	
    	Weather weather = new Weather();
    	try {
	    	JSONObject jObj = new JSONObject(data);
	    	
	    	Location location = new Location();
	    	JSONObject coordObj = getObject("coord", jObj);
	    	location.m_latitude = getFloat("lat", coordObj);
	    	location.m_longitude = getFloat("lon", coordObj);
	    	 
	    	JSONObject sysObj = getObject("sys", jObj);
	    	location.m_country= getString("country", sysObj);
	    	location.m_sunrise = getInt("sunrise", sysObj);
	    	location.m_sunset = getInt("sunset", sysObj);
	    	location.m_city = getString("name", jObj);
	    	weather.location = location;
	    	
	    	// We get Weather info (This is an array)
	    	JSONArray jArr = jObj.getJSONArray("weather");
	    	 
	    	// We use only the first value
	    	JSONObject JSONWeather = jArr.getJSONObject(0);
	    	weather.m_id = getInt("id", JSONWeather);
	    	weather.m_description = getString("description", JSONWeather);
	    	weather.m_condition = getString("main", JSONWeather);
	    	weather.m_icon = getString("icon", JSONWeather);
	    	 
	    	JSONObject mainObj = getObject("main", jObj);
	    	weather.m_humidity = getInt("humidity", mainObj);
	    	weather.m_pressure = getInt("pressure", mainObj);
	    	weather.m_tMax = getFloat("temp_max", mainObj);
	    	weather.m_tMin = getFloat("temp_min", mainObj);
	    	weather.m_temp = getFloat("temp", mainObj);
	    	
	    	// Wind
	    	JSONObject wObj = getObject("wind", jObj);
	    	Wind wind = new Wind();
	    	wind.m_speed = getFloat("speed", wObj);
	    	wind.m_deg = getFloat("deg", wObj);
	    	weather.wind = wind;
	    	
	    	// Clouds
	    	JSONObject cObj = getObject("clouds", jObj);
	    	Clouds clouds = new Clouds();
	    	clouds.m_percent = getInt("all", cObj);
	    	weather.clouds = clouds;
	    	
    	} catch(JSONException e) {
    		return null;
    	}
    	return weather;
    }
    public static Location[] parseLocation(String data) {
    	Location[] locations = null;
    	try {
			JSONObject jObj = new JSONObject(data);
			int count = getInt("count", jObj);
			JSONArray listObj = jObj.getJSONArray("list");
			locations = new Location[count];
			for(int i=0;i<count;i++) {
				JSONObject cityObj = listObj.getJSONObject(i);
				locations[i] = new Location();
				locations[i].m_city = getString("name", cityObj);
				JSONObject sysObj = cityObj.getJSONObject("sys");
				locations[i].m_country = getString("country", sysObj);
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return locations;
    }
    
    
    public static String getSearchResult(String quote) {
    	HttpURLConnection connection = null;
    	InputStream inputStream = null;
    	
    	try {
    		connection = (HttpURLConnection)(new URL(SEARCH_URL + quote).openConnection());
    		connection.setRequestMethod("GET");
    		connection.setDoInput(true);
    		connection.setDoOutput(true);
    		connection.connect();
    		
    		StringBuffer buffer = new StringBuffer();
    		inputStream = connection.getInputStream();
    		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    		String line = null;
    		while( (line = bufferedReader.readLine()) != null ) {
    			buffer.append(line +"\r\n");
    		}
    		
    		inputStream.close();
    		connection.disconnect();
    		return buffer.toString();
    	} catch(Throwable t) {
    		
    	} finally {
            try { inputStream.close(); } catch(Throwable t) {}
            try { connection.disconnect(); } catch(Throwable t) {}
        }
    	return null;
    }
    
    public static String getWeatherForecast(String location) {
    	HttpURLConnection connection = null;
    	InputStream inputStream = null;
    	
    	try {
    		connection = (HttpURLConnection)(new URL(FORECAST_URL + location).openConnection());
    		connection.setRequestMethod("GET");
    		connection.setDoInput(true);
    		connection.setDoOutput(true);
    		connection.connect();
    		
    		StringBuffer buffer = new StringBuffer();
    		inputStream = connection.getInputStream();
    		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    		String line = null;
    		while( (line = bufferedReader.readLine()) != null ) {
    			buffer.append(line +"\r\n");
    		}
    		
    		inputStream.close();
    		connection.disconnect();
    		return buffer.toString();
    	} catch(Throwable t) {
    		
    	} finally {
            try { inputStream.close(); } catch(Throwable t) {}
            try { connection.disconnect(); } catch(Throwable t) {}
        }
    	return null;
    }
    
    public static String getWeatherData(String location) {
        HttpURLConnection con = null;
        InputStream is = null;
 
        try {
            con = (HttpURLConnection) ( new URL(BASE_URL + location)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
             
            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )
                buffer.append(line + "\r\n");
             
            is.close();
            con.disconnect();
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
 
        return null;
                 
    }
     
    public byte[] getImage(String code) {
        HttpURLConnection con = null ;
        InputStream is = null;
        try {
            con = (HttpURLConnection) ( new URL(IMG_URL + code)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
             
            // Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
             
            while ( is.read(buffer) != -1)
                baos.write(buffer);
             
            return baos.toByteArray();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
         
        return null;
         
    }
    
    
    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }
     
    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }
     
    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }
     
    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }
    
    private static JSONArray getArray(String tagName, JSONObject jObj) throws JSONException {
    	return jObj.getJSONArray(tagName);
    }
    
    
   
    
}