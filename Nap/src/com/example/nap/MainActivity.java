package com.example.nap;

import java.io.File;
import java.text.DateFormat.Field;
import java.util.ArrayList;
import java.util.Random;

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
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.nap.HelloService;
import com.example.nap.HelloService.LocalBinder;
import com.example.nap.HelloService.interfejs;
import com.example.nap.SciezkaLoc.punktloc;
import com.example.nap.ServiceBis.LocalBinderBis;

@SuppressLint("ShowToast")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity
{
	@SuppressLint("ShowToast")
	public class Mapa extends LinearLayout
	{
		MyGLSurfaceView obraz;
		//ObrazekW obr;
		public Mapa (Context context, TextView pre) 
		{
			super(context);
			obraz=new MyGLSurfaceView(context);
			addView(obraz);
		}
	}
	public class DaneLokalizacji extends RasterLayer
	{
		Location aktloc;
		DaneLokalizacji(MyGLSurfaceView surView)
		{
			super(surView);
		}
		public Bitmap repaint(float x1, float y1, float x2, float y2, float zoom)
		{
			Canvas canm=new Canvas();
			Matrix mx=new Matrix();
			mx.reset();
        	System.out.println("edwin 2");
			Bitmap wynik=Bitmap.createBitmap(szer, wys, Bitmap.Config.ARGB_8888);
			canm.setBitmap(wynik);
			canm.drawColor(Color.argb(0, 255, 255, 255));
			
			PointF wpta=new PointF(x1,y1);
			PointF wptb=new PointF(x1,y2);
			PointF wptc=new PointF(x2,y2);
			PointF wptd=new PointF(x2,y1);
			float minx=TimeConvert.min(TimeConvert.min(wpta.x, wptb.x), TimeConvert.min(wptc.x, wptd.x));
			float maxx=TimeConvert.max(TimeConvert.max(wpta.x, wptb.x), TimeConvert.max(wptc.x, wptd.x));
			float miny=TimeConvert.min(TimeConvert.min(wpta.y, wptb.y), TimeConvert.min(wptc.y, wptd.y));
			float maxy=TimeConvert.max(TimeConvert.max(wpta.y, wptb.y), TimeConvert.max(wptc.y, wptd.y));	
			mx.postScale(zoom, zoom, minx, miny);
			mx.postTranslate(minx*-1, miny*-1);
			Paint p=new Paint();
			p.setStrokeWidth(3);
			p.setStyle(Paint.Style.FILL);
			p.setARGB(129, 0, 153, 204);
			if(doble!=null && doble.size()>1)
			{
				int s1=doble.size();
				p.setStyle(Paint.Style.STROKE);
				p.setARGB(255, 0, 0, 0);
				p.setStrokeWidth(20);
				    p.setDither(true);                    // set the dither to true
				    p.setStyle(Paint.Style.STROKE);       // set to STOKE
				    p.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
				    p.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
				    p.setPathEffect(new CornerPathEffect(10) );   // set the path effect when they join.
				    p.setAntiAlias(true);                         // set anti alias so it smooths
				Path pth=new Path();
				Path pth2=new Path();
				punktloc alocb=doble.getpkt(0);
				PointF p77=TimeConvert.transform(TimeConvert.WspGeoToWspEkr(new PointF((float)alocb.lon, (float)alocb.lat)), mx);
				pth.moveTo(p77.x, p77.y);
				pth2.moveTo(p77.x, p77.y);
				for(int i=1; i<s1; i++)
				{
					punktloc alocbis=doble.getpkt(i);
					PointF p2=TimeConvert.transform(TimeConvert.WspGeoToWspEkr(new PointF((float)alocbis.lon, (float)alocbis.lat)), mx);
					pth.lineTo(p2.x, p2.y);
				}
				int cz_diff=60;
				canm.drawPath(pth, p);
				p.setStrokeWidth(10);
				p.setARGB(255, 51, 181, 229);
				canm.drawPath(pth, p);
				int oldprt=0;
				for(int i=1; i<s1; i++)
				{
					punktloc alocbis=doble.getpkt(i);
					PointF p2=TimeConvert.transform(TimeConvert.WspGeoToWspEkr(new PointF((float)alocbis.lon, (float)alocbis.lat)), mx);
					pth2.lineTo(p2.x, p2.y);
					int prt=(int)alocbis.time_from_start;
					prt=prt%(cz_diff*2);
					prt/=60;
					if(oldprt!=prt)
					{
						canm.drawPath(pth2, p);
						pth2=new Path();
						pth2.moveTo(p2.x,  p2.y);
						oldprt=prt;
						if(prt==1)
						{
							p.setARGB(255, 0, 153, 204);
						}
						else
						{
							p.setARGB(255, 51, 181, 229);
						}
					}
				}
				canm.drawPath(pth2, p);
				double dlkcal=doble.dlugosc_all();
				for(double i=0; i<dlkcal; i+=1000)
				{
					punktloc tmk=doble.getpkt_d(i);
					p.setARGB(255, 0, 0, 0);
					p.setStyle(Paint.Style.FILL);
					PointF pa=TimeConvert.transform(TimeConvert.WspGeoToWspEkr(new PointF((float)tmk.lon, (float)tmk.lat)), mx);
					RectF oval=new RectF(pa.x-8, pa.y+8, pa.x+8, pa.y-8);
					canm.drawOval(oval, p);
				}
			}
			p.setARGB(255, 0, 153, 204);
			if(aktloc!=null)
			{
				p.setStrokeWidth(3);
				PointF p6=TimeConvert.WspGeoToWspEkr(new PointF((float)aktloc.getLongitude(), (float)aktloc.getLatitude()));
				PointF p65=TimeConvert.WspGeoToWspEkr(new PointF((float)aktloc.getLongitude(), (float)aktloc.getLatitude()+1));
				PointF p7=TimeConvert.transform(p6, mx);
				PointF p75=TimeConvert.transform(p65, mx);
				double kie0=TimeConvert.kierunek(p7, p75);
				p.setStyle(Paint.Style.STROKE);
				RectF oval=new RectF(p7.x-20, p7.y+20, p7.x+20, p7.y-20);
				canm.drawOval(oval, p);
				p.setStrokeWidth(2);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				Path path = new Path();
				kie0+=90;
				double kierunek=((double)aktloc.getBearing()+kie0)/180*Math.PI;
				path.moveTo((float)Math.sin(-kierunek-Math.PI)*20+p7.x, (float)Math.cos(-kierunek-Math.PI)*20+p7.y);
				path.lineTo((float)Math.sin(-kierunek+Math.PI*1/4)*20+p7.x, (float)Math.cos(-kierunek+Math.PI*1/4)*20+p7.y);
				path.lineTo((float)p7.x, (float)p7.y);
				path.lineTo((float)Math.sin(-kierunek-Math.PI*1/4)*20+p7.x, (float)Math.cos(-kierunek-Math.PI*1/4)*20+p7.y);
				path.lineTo((float)Math.sin(-kierunek-Math.PI)*20+p7.x, (float)Math.cos(-kierunek-Math.PI)*20+p7.y);
				path.close();
				canm.drawPath(path, p);
				p.setStrokeWidth(5);
			}
			return wynik;
		}
	}
	DaneLokalizacji danelokalizacji;
	/*public class ObrazekW extends Obrazek
	{
		Location aktloc;
		public ObrazekW(Context context, int sz, int wy, TextView pre) {
			super(context, sz, wy, pre);
		}
		void setaktloc(Location loc)
		{
			aktloc=loc;
			if(!przesAvailable())
			{
				PointF pf=new PointF();
				pf.x=(float)loc.getLongitude();
				pf.y=(float)loc.getLatitude();
				setfollow(pf);
			}
		}
		void rysujsciezke(Canvas canm, Matrix mx)
		{
			Paint p=new Paint();
			p.setStrokeWidth(3);
			p.setStyle(Paint.Style.FILL);
			p.setARGB(129, 0, 153, 204);
			if(doble!=null && doble.size()>1)
			{
				int s1=doble.size();
				p.setStyle(Paint.Style.STROKE);
				p.setARGB(255, 0, 0, 0);
				p.setStrokeWidth(20);
				    p.setDither(true);                    // set the dither to true
				    p.setStyle(Paint.Style.STROKE);       // set to STOKE
				    p.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
				    p.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
				    p.setPathEffect(new CornerPathEffect(10) );   // set the path effect when they join.
				    p.setAntiAlias(true);                         // set anti alias so it smooths
				Path pth=new Path();
				Path pth2=new Path();
				punktloc alocb=doble.getpkt(0);
				PointF p77=TimeConvert.transform(TimeConvert.WspGeoToWspEkr(new PointF((float)alocb.lon, (float)alocb.lat)), mx);
				pth.moveTo(p77.x, p77.y);
				pth2.moveTo(p77.x, p77.y);
				for(int i=1; i<s1; i++)
				{
					punktloc alocbis=doble.getpkt(i);
					PointF p2=TimeConvert.transform(TimeConvert.WspGeoToWspEkr(new PointF((float)alocbis.lon, (float)alocbis.lat)), mx);
					pth.lineTo(p2.x, p2.y);
					//canm.drawLine(p1.x, p1.y, p2.x, p2.y, p);
				}
				int cz_diff=60;
				canm.drawPath(pth, p);
				p.setStrokeWidth(10);
				p.setARGB(255, 51, 181, 229);
				canm.drawPath(pth, p);
				int oldprt=0;
				for(int i=1; i<s1; i++)
				{
					punktloc alocbis=doble.getpkt(i);
					PointF p2=TimeConvert.transform(TimeConvert.WspGeoToWspEkr(new PointF((float)alocbis.lon, (float)alocbis.lat)), mx);
					pth2.lineTo(p2.x, p2.y);
					int prt=(int)alocbis.time_from_start;
					prt=prt%(cz_diff*2);
					prt/=60;
					if(oldprt!=prt)
					{
						canm.drawPath(pth2, p);
						pth2=new Path();
						pth2.moveTo(p2.x,  p2.y);
						oldprt=prt;
						if(prt==1)
						{
							p.setARGB(255, 0, 153, 204);
						}
						else
						{
							p.setARGB(255, 51, 181, 229);
						}
					}
				}
				canm.drawPath(pth2, p);
				double dlkcal=doble.dlugosc_all();
				for(double i=0; i<dlkcal; i+=1000)
				{
					punktloc tmk=doble.getpkt_d(i);
					p.setARGB(255, 0, 0, 0);
					p.setStyle(Paint.Style.FILL);
					PointF pa=TimeConvert.transform(TimeConvert.WspGeoToWspEkr(new PointF((float)tmk.lon, (float)tmk.lat)), mx);
					RectF oval=new RectF(pa.x-8, pa.y+8, pa.x+8, pa.y-8);
					canm.drawOval(oval, p);
				}
			}
			p.setARGB(255, 0, 153, 204);
			if(aktloc!=null)
			{
				p.setStrokeWidth(3);
				PointF p6=TimeConvert.WspGeoToWspEkr(new PointF((float)aktloc.getLongitude(), (float)aktloc.getLatitude()));
				PointF p65=TimeConvert.WspGeoToWspEkr(new PointF((float)aktloc.getLongitude(), (float)aktloc.getLatitude()+1));
				PointF p7=TimeConvert.transform(p6, mx);
				PointF p75=TimeConvert.transform(p65, mx);
				double kie0=TimeConvert.kierunek(p7, p75);
				p.setStyle(Paint.Style.STROKE);
				RectF oval=new RectF(p7.x-20, p7.y+20, p7.x+20, p7.y-20);
				canm.drawOval(oval, p);
				p.setStrokeWidth(2);
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				Path path = new Path();
				kie0+=90;
				double kierunek=((double)aktloc.getBearing()+kie0)/180*Math.PI;
				path.moveTo((float)Math.sin(-kierunek-Math.PI)*20+p7.x, (float)Math.cos(-kierunek-Math.PI)*20+p7.y);
				path.lineTo((float)Math.sin(-kierunek+Math.PI*1/4)*20+p7.x, (float)Math.cos(-kierunek+Math.PI*1/4)*20+p7.y);
				path.lineTo((float)p7.x, (float)p7.y);
				path.lineTo((float)Math.sin(-kierunek-Math.PI*1/4)*20+p7.x, (float)Math.cos(-kierunek-Math.PI*1/4)*20+p7.y);
				path.lineTo((float)Math.sin(-kierunek-Math.PI)*20+p7.x, (float)Math.cos(-kierunek-Math.PI)*20+p7.y);
				path.close();
				canm.drawPath(path, p);
				p.setStrokeWidth(5);
			}
		}
	}*/
	Mapa mapa1;
	Menu menu;
	SciezkaLoc doble;
	int datex;

	public boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager) 
	      getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	boolean follow;

	boolean netavail()
	{
		return lay1.rad1.isChecked();
	}
	public class DaneLokalizacyjne extends LinearLayout
	{
		private TextView sze;
		private TextView dlu;
		private TextView dok;
		private TextView pre;
		private TextView pro;
		private TextView alt;
		private TextView tim;
		private TextView bea;
		private TextView scdat;
		EditText ed1;
		Button t4;
		CheckBox rad1;
		CheckBox rad2;
		public DaneLokalizacyjne (Context context) 
		{
			super(context);
			sze = new TextView(context);
			dlu = new TextView(context);
			dok = new TextView(context);
			pre = new TextView(context);
			rad1 = new CheckBox(context);
			rad2 = new CheckBox(context);
			this.addView(sze);
			this.addView(dlu);
			this.addView(dok);
			this.addView(pre);
			pro = new TextView(context);
			alt = new TextView(context);
			tim = new TextView(context);
			bea = new TextView(context);
			ed1 = new EditText(context);
			scdat = new TextView(context);
			t4 = new Button(context);
			this.addView(pro);
			this.addView(alt);
			this.addView(tim);
			this.addView(bea);
			this.addView(scdat);
			this.addView(rad1);
			this.addView(rad2);
			t4.setText("TU NIC NIE MA");
			this.addView(ed1);
			this.addView(t4);
			rad1.setText("INTERNET DOWNLOAD");
			rad2.setText("FOLLOW");
		}
		public void sendData(Location location)
		{
			if(location==null)
				return;
			double szeX = location.getLongitude();
			double dluX = location.getLatitude();
			float dokX = location.getAccuracy();
			float preX = location.getSpeed();
			String proX= location.getProvider();
			double altX= location.getAltitude();
			long timX=location.getTime();
			float beaX=location.getBearing();
			preX*=3.6;
			sze.setText("Szerokosc: "+String.valueOf(szeX));
			dlu.setText("Dlugosc: "+String.valueOf(dluX));
			dok.setText("Dokladnosc: "+String.valueOf(dokX)+" m");
			pre.setText("Predkosc: "+String.valueOf(preX)+" km/h");
			pro.setText("Zrodlo: "+String.valueOf(proX));
			alt.setText("Wysokosc: "+String.valueOf(altX)+" m");
			tim.setText("Czas: "+TimeConvert.timetxt(timX));
			bea.setText("Kierunek: "+String.valueOf(beaX));
			if(lay1.rad2.isChecked())
			{
				PointF kaka=new PointF();
				kaka.x=(float)szeX;
				kaka.y=(float)dluX;
				//lay2.obr.ustawwsp(kaka, (float)lay2.obr.aktualzoom());
			}
		}
		public void sendError()
		{
			/*sze.setText("Brak danych...");
			dlu.setText(" ");
			dok.setText(" ");
			pre.setText(" ");
			pro.setText(" ");
			alt.setText(" ");
			tim.setText(" ");
			bea.setText(" ");*/
		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		  if (resultCode == RESULT_OK) {
	            if (requestCode == 1) {
	                Uri selectedUri = data.getData();
	                String ssa=selectedUri.getPath();
	                if(!mService.nagr())
	                	doble=new SciezkaLoc(ssa);
	                //TODO WSPÓŁRZĘDNE USTAWIĆ
	    			scdat_text2(ssa);
	           }
	        }
	}
    class Sck implements ServiceConnection
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
           LocalBinder binder = (LocalBinder) service;
            mService = (HelloService) binder.getService(intbis);
            mBound = true;
            lay1.sendData(mService.last);
        }
        public void onServiceDisconnected(ComponentName arg0)
        {
            mBound = false;
        }
    };
    void noti(String txt, int id)
    {
		Notification.Builder mBuilder =
		        new Notification.Builder(this)
		        .setSmallIcon(R.drawable.znak)
		        .setContentTitle("kaczmar")
		        .setContentText(txt);
		NotificationManager mNotifyMgr = 
		        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.notify(id, mBuilder.build());
    }
    class SckBis implements ServiceConnection
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
        	LocalBinderBis binder = (LocalBinderBis) service;
            ServiceBis bisservice = (ServiceBis) binder.getService();
            if(bisservice.nagrywanie && bisservice!=null && bisservice.sciezka!=null && mService!=null)
            {
            	mService.sciezka=new SciezkaLoc();
            	mService.sciezka.copy(bisservice.sciezka);
            	mService.setnagrywanie(true);
            }
            unbindService(mConnection2);
            Intent intent2 = new Intent(MainActivity.this, ServiceBis.class);
            intent2.addCategory("kupabis");
            stopService(intent2);
        }
        public void onServiceDisconnected(ComponentName arg0)
        {
        }
    };
    ScrollView v1;
    LinearLayout vu1;
	DaneLokalizacyjne lay1;
	File gendir;
	File netdir;
    private Sck mConnection;
    HelloService mService;
    public String scdat_text()
    {
    	String str="";
    	if(doble!=null)
    	{
    		str+="Czas: "+String.valueOf(TimeConvert.timedifftxt((long)doble.czas_all()*1000))+'\n';
    		str+="Długość (m): "+String.valueOf(doble.dlugosc_all())+'\n';
    		str+="Liczba punktów: "+String.valueOf(doble.size())+'\n';
    	}
    	return str;
    }
    public void scdat_text1()
    {
    	String str=scdat_text();
    	str="NAGRYWANIE"+'\n'+str;
		lay1.scdat.setText(str);
    }
    public void scdat_text2(String sciezka)
    {
    	String str=scdat_text();
    	str=sciezka+'\n'+str;
		lay1.scdat.setText(str);
    }
    public class interfejsbis implements interfejs
    {
    	public void zmianalokalizacji(Location arg0)
    	{
    		lay1.sendData(arg0);
            danelokalizacji.aktloc=arg0;
            danelokalizacji.update();
            if(mService.nagr())
            {
    			scdat_text1();
            }
    	}

		@Override
		public void nagsta()
		{
			if(kleva2!=null)
				kleva2.setIcon(R.drawable.znak3);
			doble=mService.sciezka;
			scdat_text1();
		}

		@Override
		public void nagsto() {
			if(kleva2!=null)
				kleva2.setIcon(R.drawable.znak);
			doble=null;
			lay1.scdat.setText("");
		}
    }
    Tab tab1, tab2;
    SckBis mConnection2;
    interfejsbis intbis;
    MenuItem kleva1;
    MenuItem kleva2;
    MenuItem kleva3;
    MenuItem kleva4;
    boolean trybview;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public boolean onCreateOptionsMenu (Menu menut)
	{
		menu=menut;
        kleva1=menu.add(0, 1, 0, "Mapa");
        kleva1.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        kleva2=menu.add(0, 2, 0, "Start nagrywanie");
        kleva2.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        kleva3=menu.add(0, 3, 0, "Start śledzenie");
        kleva3.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        kleva4=menu.add(0, 4, 0, "Pobierz ścieżkę");
        kleva4.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        kleva1.setIcon(R.drawable.znak);
        kleva2.setIcon(R.drawable.znak);
        kleva3.setIcon(R.drawable.znak);
        kleva4.setIcon(R.drawable.znak);
        return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		Toast.makeText(MainActivity.this, "DOŁOWIEC", Toast.LENGTH_SHORT);
	    if(item.getItemId()==1)
	    {
	    	if(trybview==true)
	    	{
	    		kleva1.setTitle("Mapa");
	    		setContentView(vu1);
	    		trybview=false;
	    	}
	    	else
	    	{
	    		kleva1.setTitle("Dane");
	    		setContentView(mapa1);
	    		//TODO UPDATE
	    		trybview=true;
	    	}
	    }
	    if(item.getItemId()==2)
	    {
			if(mService!=null)
			{
				if(mService.nagr()==false)
				{
		    		kleva2.setTitle("Stop nagrywanie");
					mService.setnagrywanie(true);
				}
				else
				{
		    		kleva2.setTitle("Start nagrywanie");
					mService.setnagrywanie(false);
				}
			}
	    }
	    if(item.getItemId()==3)
	    {
	    	if(!follow)
	    	{
	    		follow=true;
	    		kleva3.setTitle("Stop śledzenie");
	    		kleva3.setIcon(R.drawable.znak3);
	    		//TODO UPDATE
	    	}
	    	else
	    	{
	    		follow=false;
	    		kleva3.setTitle("Start śledzenie");
	    		kleva3.setIcon(R.drawable.znak);
	    		//TODO UPDATE
	    	}
	    }
	    if(item.getItemId()==4)
	    {
			if(mService!=null && !mService.nagr())
			{
				mService.sciezka.clear();
				Intent wpIntent = new Intent();
				wpIntent.setType("file/gpx");
				wpIntent.setAction(Intent.ACTION_GET_CONTENT);
				wpIntent.putExtra("return-data", true); //added snippet
				startActivityForResult(Intent.createChooser(wpIntent, "xxx"),1);
			}
	    }
	    return true;
	}
	    private void getOverflowMenu()
	    {

	        try {
	           ViewConfiguration config = ViewConfiguration.get(this);
	           java.lang.reflect.Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	           if(menuKeyField != null) {
	               menuKeyField.setAccessible(true);
	               menuKeyField.setBoolean(config, false);
	           }
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	     }
	    Obrazek glownyObrazek;
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	follow=false;
    	trybview=false;
    	getOverflowMenu();
        vu1=new LinearLayout(this);
        String rku = Environment.getExternalStorageDirectory().toString();
        gendir = new File(rku + "/nap_program");    
        gendir.mkdirs();
        netdir =new File(gendir.getAbsolutePath()+ "/net_download");
        netdir.mkdirs();
        setTheme(android.R.style.Theme_Holo);
		v1=new ScrollView(this);
		vu1.addView(v1);
		lay1=new DaneLokalizacyjne(this);
		lay1.setOrientation (LinearLayout.VERTICAL);
		v1.addView(lay1);
		mapa1=new Mapa(this, lay1.pre);
		glownyObrazek=new Obrazek(mapa1.obraz, netdir.getAbsolutePath());
		danelokalizacji = new DaneLokalizacji(mapa1.obraz);
		mapa1.obraz.dodajWarstwe(glownyObrazek);
		mapa1.obraz.dodajWarstwe(danelokalizacji);
		setContentView(vu1);
		lay1.sendError();
	    ActionBar atk=getActionBar();
	    atk.setDisplayHomeAsUpEnabled(true);
	    atk.setHomeButtonEnabled(true);
		String newpath=gendir.getAbsolutePath()+"/"+"net_download";
		Random r=new Random();
		datex=(r.nextInt(1000));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	
        return super.onPrepareOptionsMenu(menu);
    }
    boolean mBound = false;
    protected void onStart()
    {
        super.onStart();
		intbis=new interfejsbis();
        Intent intent = new Intent(MainActivity.this, HelloService.class);
        intent.addCategory("kupa");
        mConnection = new Sck();
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Intent intent2 = new Intent(MainActivity.this, ServiceBis.class);
        intent2.addCategory("kupabis");
        mConnection2 = new SckBis();
        bindService(intent2, mConnection2, Context.BIND_AUTO_CREATE);
    }
    protected void onStop()
    {
        super.onStop();
        if(mBound && mService.nagr())
        {
        	String rku = Environment.getExternalStorageDirectory().toString();
            File gendir = new File(rku + "/nap_program");
            String sciez=gendir+"/tmp.gpx";
        	mService.zapisz(sciez);
        	Intent intent2 = new Intent(MainActivity.this, ServiceBis.class);
            intent2.addCategory("kupabis");
            startService(intent2);
        }
        mService.setnagrywanie2(false);
        unbindService(mConnection);
        mBound = false;
        Intent intent = new Intent(MainActivity.this, HelloService.class);
        intent.addCategory("kupa");
        stopService(intent);
    }
    protected void onDestroy()
    {
    	super.onDestroy();
    	glownyObrazek.zapisz();
    }
}
