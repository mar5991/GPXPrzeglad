package com.example.nap;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nap.HelloService;
import com.example.nap.HelloService.LocalBinder;
import com.example.nap.HelloService.interfejs;
import com.example.nap.SciezkaLoc.punktloc;
import com.example.nap.ServiceBis.LocalBinderBis;
public class SciezkaLoc
{
	class punktloc
	{
		double lat;
		double lon;
		double ele;
		long time;
		double time_from_start;
		double dist_przebyty;
	}
	
	public class TrkPt {
	    private final static String START_TAG = "<trkpt ";
	    private final static String START_ANY_TAG = "<";
	    private final static String END_TAG_EMPTY = "/>";
	    private final static String END_TAG_FULL = "</trkpt>";
	 
	    private final static String ATTR_LAT = "lat=\"";
	    private final static String ATTR_LON = "lon=\"";
	    private final static String ELEM_ELE = "<ele>";
	    private final static String ELEM_TIM = "<galakce>";
	 
	    private double mLat = 0.0;
	    private double mLon = 0.0;
	    private long mTim = 0;
	    private double mEle = Double.MIN_VALUE;
	 
	    public TrkPt () {
	    }
	 
	    private boolean parse (String s) {
	        int lat = s.indexOf(ATTR_LAT);
	        int lon = s.indexOf(ATTR_LON);
	        int ele = s.indexOf(ELEM_ELE);
	        int time =s.indexOf(ELEM_TIM);
	        if (lat < 0 || lon < 0) {
	            //throw new InvalidParameterException("trkpt without lat or lon attribute");
	            return false;
	        }
	 
	        int endLat = s.indexOf("\"", lat + ATTR_LAT.length());
	        int endLon = s.indexOf("\"", lon + ATTR_LON.length());
	 
	        mLat = Double.parseDouble(s.substring(lat+ATTR_LAT.length(), endLat));
	        mLon = Double.parseDouble(s.substring(lon+ATTR_LON.length(), endLon));
	 
	        if (ele > 0) {
	          mEle = Double.parseDouble(s.substring(ele + ELEM_ELE.length(), s.indexOf("<", ele + ELEM_ELE.length())));
	        }
	        if (time > 0)
	        {
		          mTim = Long.parseLong(s.substring(time + ELEM_TIM.length(), s.indexOf("<", time + ELEM_TIM.length())));
		    }
	        //System.out.println("lat " + (lat+ATTR_LAT.length()) + " endLat " + endLat);
	 
	        return true;
	    }
	 
	    public boolean parse (StringBuilder s) {
	        int startTag = s.indexOf(START_TAG);
	        if (startTag < 0) return false;
	 
	        int nextTag = s.indexOf(START_ANY_TAG, startTag);
	        int endTagEmpty = s.indexOf(END_TAG_EMPTY, startTag);
	        int endTagFull = s.indexOf(END_TAG_FULL, startTag);
	 
	        if (endTagEmpty + END_TAG_EMPTY.length() == s.length()) {
	            // It's like <trkpt .../>EOF
	            //System.out.println("matches 0 " + s.substring(startTag, endTagEmpty + END_TAG_EMPTY.length()));
	            return parse(s.substring(startTag, endTagEmpty + END_TAG_EMPTY.length()));
	        } else if (nextTag > 0 && endTagEmpty > 0 && nextTag < endTagEmpty) {
	            // It's like <trkpt ...><...
	            if (nextTag == endTagFull) {
	                // It's like <trkpt ...></trkpt>
	                return parse(s.substring(startTag, endTagFull + END_TAG_FULL.length()));
	            } else if (endTagFull > 0) {
	                // It's like <trkpt ...>...</trkpt>
	                //System.out.println("matches 2 " + s.substring(startTag, endTagFull + END_TAG_FULL.length()));
	                return parse(s.substring(startTag, endTagFull + END_TAG_FULL.length()));
	            } else {
	                return false;
	            }
	        } else if (nextTag > 0 && endTagFull > 0) {
	            //System.out.println("matches 3 " + s.substring(startTag, endTagFull + END_TAG_FULL.length()));
	            return parse(s.substring(startTag, endTagFull + END_TAG_FULL.length()));
	        } else {
	            //System.out.println("no match " + s.toString());
	            return false;
	        }
	 
	    }
	 
	    public double getLat() {
	        return mLat;
	    }
	 
	    public double getLon() {
	        return mLon;
	    }
	 
	    public double getEle () {
	         return mEle;
	    }
	}
	public class TrkPtInputStream extends InputStream {
	    private GpxParser mParser;
	    private StringBuilder mBuffer = new StringBuilder();
	 
	    public TrkPtInputStream(GpxParser parser) {
	        mParser = parser;
	    }
	 
	    @Override
	    public int read() throws IOException {
	        if (mBuffer.length() == 0) {
	            TrkPt point = mParser.nextTrkPt();
	            if (point == null) {
	                return -1;
	            }
	            mBuffer.append(point.getLat() + "," + point.getLon() + "\n");
	        }
	 
	        int res = mBuffer.charAt(0);
	        mBuffer.deleteCharAt(0);
	        return res;
	    }
	}
	
	public class GpxParser {
	    private InputStream mIs = null;
	    private StringBuilder mStringBuilder = new StringBuilder();
	 
	    public GpxParser (InputStream is) {
	        mIs = is;
	    }
	 
	    public TrkPt nextTrkPt () throws IOException {
	        mStringBuilder.delete(0, mStringBuilder.length());
	 
	        int c;
	        while ( (c = mIs.read()) != -1 ) {
	            mStringBuilder.append((char)c);
	 
	            TrkPt trkpt = new TrkPt();
	            if (trkpt.parse(mStringBuilder)) {
	                return trkpt;
	            }
	        }
	        return null;
	    }
	 
	    public TrkPtInputStream getTrkPtStream () {
	        return new TrkPtInputStream(this);
	    }
	}
	
	public ArrayList <punktloc> data;
	double toRad(double wart)
	{
		 return wart * Math.PI / 180;
	}
	int rado;
	PointF lg;
	PointF pd;
    double calcdist(double lat1, double lon1, double lat2, double lon2) 
    {
      double R = 6371000;
      double dLat = toRad(lat2-lat1);
      double dLon = toRad(lon2-lon1);
      lat1 = toRad(lat1);
      lat2 = toRad(lat2);
      double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
      double d = R * c;
      return d;
    }
    punktloc mieszaj(punktloc alfa, punktloc beta, double stos)
    {
    	punktloc wynik=new punktloc();
    	wynik.lat=beta.lat*stos+alfa.lat*(1-stos);
    	wynik.lon=beta.lon*stos+alfa.lon*(1-stos);
    	wynik.ele=beta.ele*stos+alfa.ele*(1-stos);
    	wynik.time_from_start=(float) beta.time_from_start*stos+(float)alfa.time_from_start*(1-stos);
    	wynik.dist_przebyty=beta.dist_przebyty*stos+alfa.dist_przebyty*(1-stos);
    	double timetemp=(double)beta.time*stos+(double)alfa.time*(1-stos);
    	wynik.time=(long) timetemp;
    	return wynik;
    }
	void dodajpunkt(Location loc)
	{
		punktloc now=new punktloc();
		now.lat=loc.getLatitude();
		now.lon=loc.getLongitude();
		now.ele=loc.getAltitude();
		now.time=loc.getTime();
		if(size()==0)
		{
			now.time_from_start=0;
			now.dist_przebyty=0;
		}
		else
		{
			now.time_from_start=now.time/1000-data.get(0).time/1000;
			now.dist_przebyty=calcdist(now.lat, now.lon, data.get(size()-1).lat, data.get(size()-1).lon)+data.get(size()-1).dist_przebyty;
		}
		data.add(now);
		lg.x=(float) Math.min(lg.x, now.lon);
		pd.x=(float) Math.max(pd.x, now.lon);
		lg.y=(float) Math.max(lg.y, now.lat);
		pd.y=(float) Math.min(pd.y, now.lat);
	}
	SciezkaLoc()
	{
		Random rdr=new Random();
		rado=rdr.nextInt(1000);
		lg=new PointF();
		lg.x=200;
		lg.y=-200;
		pd=new PointF();
		pd.x=-200;
		pd.y=200;
		data=new ArrayList<punktloc>();
	}
	void copy (SciezkaLoc former)
	{
		data=new ArrayList<punktloc>();
		int s1=former.size();
		for(int i=0; i<s1; i++)
		{
			punktloc pko=former.data.get(i);
			punktloc now=new punktloc();
			now.lat=pko.lat;
			now.lon=pko.lon;
			now.ele=pko.ele;
			now.time=pko.time;
			if(size()==0)
			{
				now.time_from_start=0;
				now.dist_przebyty=0;
			}
			else
			{
				now.time_from_start=now.time/1000-data.get(0).time/1000;
				now.dist_przebyty=calcdist(now.lat, now.lon, data.get(size()-1).lat, data.get(size()-1).lon)+data.get(size()-1).dist_przebyty;
			}
			data.add(now);
			lg.x=(float) Math.min(lg.x, now.lon);
			pd.x=(float) Math.max(pd.x, now.lon);
			lg.y=(float) Math.max(lg.y, now.lat);
			pd.y=(float) Math.min(pd.y, now.lat);
		}
	}
	SciezkaLoc(String sciezka)
	{
		Random rdr=new Random();
		rado=rdr.nextInt(1000);
		lg=new PointF();
		lg.x=200;
		lg.y=-200;
		pd=new PointF();
		pd.x=-200;
		pd.y=200;
		data=new ArrayList<punktloc>();
		File f=new File(sciezka);
		FileInputStream is;
		try {
			is = new FileInputStream(f);
		GpxParser gpt=new GpxParser(is);
		TrkPt pdt;
		try {
			pdt = gpt.nextTrkPt();
		while(pdt!=null)
		{
			 punktloc now=new punktloc();
		now.lat=pdt.getLat();
		now.lon=pdt.getLon();
		now.ele=pdt.getEle();
		now.time=pdt.mTim;
		lg.x=(float) Math.min(lg.x, now.lon);
		pd.x=(float) Math.max(pd.x, now.lon);
		lg.y=(float) Math.max(lg.y, now.lat);
		pd.y=(float) Math.min(pd.y, now.lat);
		if(size()==0)
		{
			now.time_from_start=0;
			now.dist_przebyty=0;
		}
		else
		{
			now.time_from_start=now.time/1000-data.get(0).time/1000;
			now.dist_przebyty=calcdist(now.lat, now.lon, data.get(size()-1).lat, data.get(size()-1).lon)+data.get(size()-1).dist_przebyty;
		}
				data.add(now);
				pdt = gpt.nextTrkPt();
		}
		}
		 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		}
	}
	int size()
	{
		return data.size();
	}
	void clear()
	{
		data.clear();
		lg=new PointF();
		lg.x=200;
		lg.y=-200;
		pd=new PointF();
		pd.x=-200;
		pd.y=200;
	}
	punktloc getpkt(int id)
	{
		//TODO COPY
		return data.get(id);
	}
	/*punktloc najblizszy(PointF wsp)
	{
		
	}*/
	punktloc getpkt_t(double time)
	{
		int id_l=0;
		int id_r=size()-1;
		while(id_r-id_l>1)
		{
			int sr=(id_r+id_l)/2;
			double tmp=data.get(sr).time_from_start;
			if(tmp>time)
				id_r=sr;
			else
				id_l=sr;
		}
		double akttime=data.get(id_r).time_from_start;
		double poptime=data.get(id_l).time_from_start;
		double stos=0.5;
		if((akttime-poptime)!=0)
			stos=(time-poptime)/(akttime-poptime);
		return mieszaj(data.get(id_l), data.get(id_r), stos);
	}
	punktloc getpkt_d(double distance)
	{
		int id_l=0;
		int id_r=size()-1;
		while(id_r-id_l>1)
		{
			int sr=(id_r+id_l)/2;
			double tmp=data.get(sr).dist_przebyty;
			if(tmp>distance)
				id_r=sr;
			else
				id_l=sr;
		}
		double aktdist=data.get(id_r).dist_przebyty;
		double popdist=data.get(id_l).dist_przebyty;
		double stos=(distance-popdist)/(aktdist-popdist);
		return mieszaj(data.get(id_l), data.get(id_r), stos);
	}
	PointF getlg()
	{
		return lg;
	}
	PointF getpd()
	{
		return pd;
	}
	double dlugosc_all()
	{
		if(size()==0)
			return 0;
		return data.get(size()-1).dist_przebyty;
	}
	double czas_all()
	{
		if(size()==0)
			return 0;
		return data.get(size()-1).time_from_start;
	}
	double speedmph_t(double time1, double time2)
	{
		punktloc a1=getpkt_t(time1);
		punktloc a2=getpkt_t(time2);
		return (a2.dist_przebyty-a1.dist_przebyty)/(a2.time_from_start-a1.time_from_start);
	}
	double speedmph_p(double poz1, double poz2)
	{
		punktloc a1=getpkt_d(poz1);
		punktloc a2=getpkt_d(poz2);
		return (a2.dist_przebyty-a1.dist_przebyty)/(a2.time_from_start-a1.time_from_start);
	}
}