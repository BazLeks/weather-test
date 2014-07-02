package ru.brostudios.android_weather_test;

public class Weather {
	
	public String m_time;
	public int m_id;
	public String m_description;
	public String m_condition;
	public String m_icon;
	public int m_humidity;
	public int m_pressure;
	public float m_tMax;
	public float m_tMin;
	public float m_temp;
	
	public Location location;
	public Wind wind;
	public Clouds clouds;
	
	
	public static class Location {
		public float m_latitude;
		public float m_longitude;
		public String m_country;
		public int m_sunrise;
	    public int m_sunset;
	    public String m_city;
	}
	
	public static class Wind {
		public float m_speed;
		public float m_deg;
	}
	
	public static class Clouds {
		public int m_percent;
	}
}
