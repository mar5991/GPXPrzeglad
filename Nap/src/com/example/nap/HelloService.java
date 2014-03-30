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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Process;

@SuppressLint("SimpleDateFormat")
public class HelloService extends Service implements LocationListener
{
	Location last;
    private final IBinder mBinder = new LocalBinder();
    LocationManager locationManager;
    SciezkaLoc sciezka;
    boolean nagrywanie;
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
    public void setnagrywanie(boolean wartosc)
    {
    	if(nagrywanie==false && wartosc==true)
    	{
    		Notification.Builder mBuilder =
    		        new Notification.Builder(this)
    		        .setSmallIcon(R.drawable.znak)
    		        .setContentTitle("GPX Przegląd")
    		        .setContentText("Trwa zapisywanie trasy (AE)");
    		Notification buld=mBuilder.build();
    		buld.flags |= Notification.FLAG_ONGOING_EVENT;
    		startForeground(527, buld);
    		inc.nagsta();
    	}
    	if(nagrywanie==true && wartosc==false)
    	{
            String rku = Environment.getExternalStorageDirectory().toString();
            File gendir = new File(rku + "/nap_program");
            Date date=new Date(System.currentTimeMillis());
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat formatter2 = new SimpleDateFormat("HH-mm-ss");
			String dateFormatted = formatter.format(date)+"T"+formatter2.format(date);
    		String sciez=gendir+"/"+dateFormatted+"mpx.gpx";
    		zapisz(sciez);
    		stopForeground(true);
    		inc.nagsto();
    	}
    	nagrywanie=wartosc;
    }
    public void setnagrywanie2(boolean wartosc)
    {
    	if(nagrywanie==false && wartosc==true)
    	{
    	}
    	if(nagrywanie==true && wartosc==false)
    	{
    		stopForeground(false);
    	}
    	nagrywanie=wartosc;
    }
    public boolean nagr()
    {
    	return nagrywanie;
    }
    public interface interfejs
    {
    	public void zmianalokalizacji(Location loc);
    	public void nagsta();
    	public void nagsto();
    }
    interfejs inc;
	public class LocalBinder extends Binder
    {
    	HelloService getService(interfejs inp)
    	{
            inc=inp;
    		return HelloService.this;
        }
    }
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }
    public void onCreate ()
    {
    	sciezka=new SciezkaLoc();
    	nagrywanie=false;
    	//data=new ArrayList<Location>();
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
    public int onStartCommand(Intent intent, int flags, int startId)
    {
    	return 0;
    }
	public void onDestroy()
    {
    }
	public void onLocationChanged(Location arg0)
	{
		last=arg0;
		if(nagrywanie)
		{
			sciezka.dodajpunkt(arg0);
		}
		if(inc!=null)
			inc.zmianalokalizacji(arg0);
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