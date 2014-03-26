package com.example.nap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import com.example.nap.SciezkaLoc.punktloc;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

@SuppressLint("SimpleDateFormat")
public class ServiceBis extends Service implements LocationListener
{
	boolean nagrywanie;
    private final IBinder mBinder = new LocalBinderBis();
    LocationManager locationManager;
    SciezkaLoc sciezka;
    public void zapisz(String sciez)
    {
    	try {
			FileWriter fki = new FileWriter(sciez);
			fki.write("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1");
			fki.write("http://www.topografix.com/GPX/1/1/gpx.xsd\" version=\"1.1\" creator=\"YourCompanyName\">\n<trk><trkseg>");
			int s1=sciezka.size();
			for(int i=0; i<s1; i++)
			{
				punktloc akt=sciezka.getpkt(i);
				fki.write("<trkpt lat=\"");
				fki.write(String.valueOf(akt.lat));
				fki.write("\" lon=\"");
				fki.write(String.valueOf(akt.lon));
				fki.write("\">");
				fki.write("<time>");
				Date date = new Date(akt.time);
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				DateFormat formatter2 = new SimpleDateFormat("HH:mm:ssZ");
				String dateFormatted = formatter.format(date)+"T"+formatter2.format(date);
				fki.write(dateFormatted);
				fki.write("</time>");
				fki.write("<galakce>");
				fki.write(String.valueOf(akt.time));
				fki.write("</galakce>");	
				fki.write("</trkpt>\n");
			}
			fki.write("</trkseg></trk>\n</gpx>");
			fki.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    public interface interfejs
    {
    	public void zmianalokalizacji(Location loc);
    }
	public class LocalBinderBis extends Binder
    {
    	ServiceBis getService()
    	{
    		return ServiceBis.this;
        }
    }
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }
    public void onCreate ()
    {
    	String rku = Environment.getExternalStorageDirectory().toString();
        File gendir = new File(rku + "/nap_program");
        String sciez=gendir+"/tmp.gpx";
        File ost=new File(sciez);
    	sciezka=new SciezkaLoc();
        if(ost.exists())
        {
        	nagrywanie=true;
        	sciezka=new SciezkaLoc(sciez);
        	ost.delete();
        }
    	nagrywanie=false;
    	//data=new ArrayList<Location>();
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		Notification.Builder mBuilder =
		        new Notification.Builder(this)
		        .setSmallIcon(R.drawable.znak)
		        .setContentTitle("GPX Przegląd")
		        .setContentText("Trwa zapisywanie trasy.");
		Notification buld=mBuilder.build();
		buld.flags |= Notification.FLAG_ONGOING_EVENT;
		startForeground(509, buld);
		//onLocationChanged(location);
	}
    public int onStartCommand(Intent intent, int flags, int startId)
    {
    	return 0;
    }
    
	public void onDestroy()
    {
		stopForeground(false);
    }
	public void onLocationChanged(Location arg0)
	{
			nagrywanie=true;
			sciezka.dodajpunkt(arg0);
	}
	public void onProviderDisabled(String arg0)
	{
	}
	public void onProviderEnabled(String arg0)
	{
	}
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
	}
}