package com.example.nap;

import java.io.File;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.nap.ImageLoader;
import com.example.nap.ImageData;
import com.example.nap.HelloService.LocalBinder;
import com.example.nap.HelloService.interfejs;
import com.example.nap.SciezkaLoc.punktloc;
import com.example.nap.ServiceBis.LocalBinderBis;

@SuppressLint("ValidFragment")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity
{
	@SuppressLint("ShowToast")
	public class Myfragment extends Fragment {
		public class Mapa extends LinearLayout
		{
			Obrazek obr;
			public Mapa (Context context) 
			{
				super(context);
				obr = new Obrazek(context, 600, 1000);
				this.addView(obr);
			}
			void mapix()
			{

			}
		}
		Mapa mapa1;
	    Myfragment()
	    {
	        mapa1=new Mapa(MainActivity.this);
	    }
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	    	Toast.makeText(MainActivity.this, "VIEWI", Toast.LENGTH_SHORT);
	        return mapa1;
	    }
	}
	public class ArticleFragment2 extends Fragment {
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	        return vu1;
	    }
	}
	Myfragment frtest1;
	ArticleFragment2 frtest2;
	int datex;
	public PointF transform(PointF old, Matrix mx)
	{
		float[] tab=new float[3];
		tab[0]=old.x;
		tab[1]=old.y;
		mx.mapPoints(tab);
		return new PointF(tab[0], tab[1]);
	}
	public boolean czy_pokrywajo(PointF lewydolny1, PointF prawygorny1, PointF lewydolny2, PointF prawygorny2)
	{
		if(lewydolny1.x<prawygorny2.x && lewydolny2.x<prawygorny1.x)
			if(lewydolny1.y<prawygorny2.y && lewydolny2.y<prawygorny1.y)
				return true;
		return false;
	}
	float min(float alfa, float beta)
	{
		if(alfa>beta)
			return beta;
		return alfa;
	}
	float max(float alfa, float beta)
	{
		if(alfa<beta)
			return beta;
		return alfa;
	}
	public PointF WspGeoToWspEkr(PointF old)
	{
		return new PointF((float)((old.x/360.0*1000.0)+500), (float) (500.0-Math.log(Math.tan((Math.PI/4.0)+((old.y/180.0*Math.PI)/2.0)))/Math.PI*500.0 ));
	}
	public PointF WspEkrtoWspGeo(PointF old)
	{
		return new PointF((float)((old.x-500)*360.0/1000.0), (float) ((2*Math.atan(Math.exp((500.0-old.y)/500.0*Math.PI))-Math.PI/2.0)/Math.PI*180.0));
	}
	public float distance(PointF first, PointF second)
	{
		return (float) Math.sqrt((second.x-first.x)*(second.x-first.x)+(second.y-first.y)*(second.y-first.y));
	}
	public double kierunek(PointF first, PointF second)
	{
		double deltaX=second.x-first.x;
		double deltaY=second.y-first.y;
		return Math.atan2(deltaY, deltaX)*180/Math.PI;
	}
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
	class Obrazek extends ImageView
	{
		private Canvas can;
		private Bitmap btu;
		private Bitmap mbtu;
		private Matrix mx;
		private Matrix old_matrix;
		float oldx;
		float oldy;
		float oldxBIS;
		float oldyBIS;
		int index1;
		int index2;
		int szer;
		int wys;
		ImageLoader ig;
		boolean jedentouch;
		boolean follow;
		long actiontime;
		long recentrepaint;
		long sreptime;
		private Location aktloc;
		ArrayList <Location> punkty2;
		SciezkaLoc doble;
		boolean przesAvailable()
		{
			return true;
		}
		void repaint4()
		{
			long aktt=System.currentTimeMillis();
			if(aktt-30>=sreptime)
			{
				repaint2();
			}
		}
		void repaint3()
		{
			long aktt=System.currentTimeMillis();
			if(aktt-1000>recentrepaint)
			{
				repaint();
			}
		}
		void ustawwsp(PointF wsp, float zoox)
		{
			PointF kpk=WspGeoToWspEkr(wsp);
			mx.reset();
			mx.postTranslate((kpk.x*-1)+szer/2, wys-kpk.y-(wys/2));
			mx.postScale(zoox, zoox, szer/2, wys/2);
			old_matrix=new Matrix(mx);
			repaint();
		}
		void ustawwsp(PointF wsplg, PointF wsppd)
		{
			PointF lg=WspGeoToWspEkr(wsplg);
			PointF pd=WspGeoToWspEkr(wsppd);
			float zoomx=1;
			float rozx=pd.x-lg.x;
			float rozy=lg.y-pd.y;
			if((rozx/rozy)>(szer/wys))
			{
				zoomx=szer/rozx;
			}
			else
			{
				zoomx=wys/rozy;
			}
			PointF kpk=new PointF((pd.x+lg.x)/2, (pd.y+lg.y)/2);
			mx.reset();
			mx.postTranslate((kpk.x*-1)+szer/2, wys-kpk.y-(wys/2));
			mx.postScale(zoomx, zoomx, szer/2, wys/2);
			old_matrix=new Matrix(mx);
			repaint();
		}
		double aktualzoom()
		{
			float scale1=distance(transform (new PointF(0, 0), mx), transform (new PointF(1, 1), mx));
			return (double) (scale1/Math.sqrt(2));
		}
		void repaint()
		{
			//BitmapFactory
			recentrepaint=System.currentTimeMillis();
			sreptime=recentrepaint;
			old_matrix=new Matrix(mx);
			btu=Bitmap.createBitmap(szer, wys, Bitmap.Config.ARGB_8888);
			Bitmap btp=Bitmap.createBitmap(szer, wys, Bitmap.Config.ARGB_8888);
			mbtu=Bitmap.createBitmap(szer, wys, Bitmap.Config.ARGB_8888);
			can=new Canvas(btu);
			Canvas canp=new Canvas(btp);
			Canvas canm=new Canvas(mbtu);
			Matrix ing=new Matrix(mx);
			ing.invert(ing);
			PointF wpta=WspEkrtoWspGeo(transform(new PointF(0,0), ing));
			PointF wptb=WspEkrtoWspGeo(transform(new PointF(0,wys), ing));
			PointF wptc=WspEkrtoWspGeo(transform(new PointF(szer,wys), ing));
			PointF wptd=WspEkrtoWspGeo(transform(new PointF(szer,0), ing));
			PointF apta=transform(new PointF(0,0), ing);
			PointF aptb=transform(new PointF(0,wys), ing);
			PointF aptc=transform(new PointF(szer,wys), ing);
			PointF aptd=transform(new PointF(szer,0), ing);
			float minx=min(min(wpta.x, wptb.x), min(wptc.x, wptd.x));
			float maxx=max(max(wpta.x, wptb.x), max(wptc.x, wptd.x));
			float miny=min(min(wpta.y, wptb.y), min(wptc.y, wptd.y));
			float maxy=max(max(wpta.y, wptb.y), max(wptc.y, wptd.y));
			PointF cclewydolny=new PointF(min(min(apta.x, aptb.x), min(aptc.x, aptd.x)), min(min(apta.y, aptb.y), min(aptc.y, aptd.y)));
			PointF ccprawygorny=new PointF(max(max(apta.x, aptb.x), max(aptc.x, aptd.x)), max(max(apta.y, aptb.y), max(aptc.y, aptd.y)));
			float scale1=distance(transform (new PointF(0, 0), mx), transform (new PointF(1, 1), mx));
			float scale3=distance(transform (new PointF(0, 0), mx), transform (new PointF(1, 0), mx));
			if(ig!=null)
			{
				
			if(netavail())
			{
				ig.laduj(minx, maxx, miny, maxy, scale3, this);
			}
			int s2=ig.imadat.size();
			for(int i=0; i<s2; i++)
			{
				ImageData im=ig.imadat.get(i);
				double scale2=distance(new PointF(0, 0), new PointF(im.rozdzielczoscx, im.rozdzielczoscy));
				double scale=scale1*scale2;
				//UWAGA MNOŻNIK 256 W NASTĘPNEJ LINIJCE JEST TYMCZASOWY I NIEPRAWIDŁOWY
				if(scale>=1.6 && czy_pokrywajo(cclewydolny, ccprawygorny, im.lewydolny, new PointF(im.lewydolny.x+im.rozdzielczoscx*256, im.lewydolny.y+im.rozdzielczoscy*256)))
				{
					Matrix nmax=new Matrix();
					nmax.reset();
					nmax.postTranslate(im.lewydolny.x, im.lewydolny.y);
					nmax.postScale(im.rozdzielczoscx, im.rozdzielczoscy, im.lewydolny.x, im.lewydolny.y);
					Bitmap nbt;
					if(im.bmp==null)
					{
						nbt=BitmapFactory.decodeFile(im.sciezka);
						im.bmp=nbt;
					}
					else
					{
						nbt=im.bmp;
					}
					nmax.postConcat(mx);
					Paint paintx = new Paint();
					//paintx.setAlpha(100);
					if(nbt!=null)
						can.drawBitmap(nbt, nmax, paintx);
				}
				else
				{
					im.bmp=null;
				}
			}
			}
			rysujsciezke(canm);
			Matrix zero=new Matrix();
			canp.drawBitmap(btu, zero, null);
			canp.drawBitmap(mbtu, zero, null);
			this.setImageBitmap(btp);
		}
		void rysujsciezke(Canvas canm)
		{
			Paint p=new Paint();
			p.setStrokeWidth(3);
			p.setStyle(Paint.Style.FILL);
			p.setARGB(129, 0, 153, 204);
			
			if(mService!=null && mService.sciezka!=null && mService.nagr())
			{
				int s1=mService.sciezka.size();
				p.setStyle(Paint.Style.STROKE);
				p.setARGB(255, 51, 181, 229);
				for(int i=1; i<s1; i++)
				{
					punktloc aloc=mService.sciezka.getpkt(i-1);
					punktloc alocbis=mService.sciezka.getpkt(i);
					PointF p1=transform(WspGeoToWspEkr(new PointF((float)aloc.lon, (float)aloc.lat)), mx);
					PointF p2=transform(WspGeoToWspEkr(new PointF((float)alocbis.lon, (float)alocbis.lat)), mx);
					canm.drawLine(p1.x, p1.y, p2.x, p2.y, p);
				}
				double dlkcal=mService.sciezka.dlugosc_all();
				for(double i=0; i<dlkcal; i+=1000)
				{
					punktloc tmk=mService.sciezka.getpkt_d(i);
					p.setARGB(220, 255, 68, 68);
					p.setStyle(Paint.Style.FILL_AND_STROKE);
					PointF pa=transform(WspGeoToWspEkr(new PointF((float)tmk.lon, (float)tmk.lat)), mx);
					RectF oval=new RectF(pa.x-10, pa.y+10, pa.x+10, pa.y-10);
					canm.drawOval(oval, p);
				}
				double tikcal=mService.sciezka.czas_all();
				for(double i=0; i<tikcal; i+=60)
				{
					punktloc tmk=mService.sciezka.getpkt_t(i);
					p.setARGB(220, 255, 187, 51);
					p.setStyle(Paint.Style.FILL_AND_STROKE);
					PointF pa=transform(WspGeoToWspEkr(new PointF((float)tmk.lon, (float)tmk.lat)), mx);
					RectF oval=new RectF(pa.x-10, pa.y+10, pa.x+10, pa.y-10);
					canm.drawOval(oval, p);
				}
			}
			if(doble!=null && !mService.nagr())
			{
				int s1=doble.size();
				p.setStyle(Paint.Style.STROKE);
				p.setARGB(255, 51, 181, 229);
				for(int i=1; i<s1; i++)
				{
					punktloc aloc=doble.getpkt(i-1);
					punktloc alocbis=doble.getpkt(i);
					PointF p1=transform(WspGeoToWspEkr(new PointF((float)aloc.lon, (float)aloc.lat)), mx);
					PointF p2=transform(WspGeoToWspEkr(new PointF((float)alocbis.lon, (float)alocbis.lat)), mx);
					canm.drawLine(p1.x, p1.y, p2.x, p2.y, p);
				}
				double dlkcal=doble.dlugosc_all();
				for(double i=0; i<dlkcal; i+=1000)
				{
					punktloc tmk=doble.getpkt_d(i);
					p.setARGB(220, 255, 68, 68);
					p.setStyle(Paint.Style.FILL_AND_STROKE);
					PointF pa=transform(WspGeoToWspEkr(new PointF((float)tmk.lon, (float)tmk.lat)), mx);
					RectF oval=new RectF(pa.x-10, pa.y+10, pa.x+10, pa.y-10);
					canm.drawOval(oval, p);
				}
				double tikcal=doble.czas_all();
				for(double i=0; i<tikcal; i+=60)
				{
					punktloc tmk=doble.getpkt_t(i);
					p.setARGB(220, 255, 187, 51);
					p.setStyle(Paint.Style.FILL_AND_STROKE);
					PointF pa=transform(WspGeoToWspEkr(new PointF((float)tmk.lon, (float)tmk.lat)), mx);
					RectF oval=new RectF(pa.x-10, pa.y+10, pa.x+10, pa.y-10);
					canm.drawOval(oval, p);
				}
			}
			p.setARGB(255, 0, 153, 204);
			if(aktloc!=null)
			{
				p.setStrokeWidth(3);
				PointF p6=WspGeoToWspEkr(new PointF((float)aktloc.getLongitude(), (float)aktloc.getLatitude()));
				PointF p65=WspGeoToWspEkr(new PointF((float)aktloc.getLongitude(), (float)aktloc.getLatitude()+1));
				PointF p7=transform(p6, mx);
				PointF p75=transform(p65, mx);
				double kie0=kierunek(p7, p75);
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
		void repaint2()
		{
			sreptime=System.currentTimeMillis();
			Bitmap btp=Bitmap.createBitmap(szer, wys, Bitmap.Config.ARGB_8888);
			Bitmap mbtu=Bitmap.createBitmap(szer, wys, Bitmap.Config.ARGB_8888);
			Canvas canm = new Canvas(mbtu);
			rysujsciezke(canm);
			Matrix pkt=new Matrix(old_matrix);
			pkt.invert(pkt);
			pkt.postConcat(mx);
			can=new Canvas(btp);
			can.drawBitmap(btu, pkt, null);
			Matrix zero=new Matrix();
			can.drawBitmap(mbtu, zero, null);
			this.setImageBitmap(btp);
		}
		public void setaktloc(Location loc)
		{
			aktloc=new Location(loc);
		}
		public Obrazek(Context context, int sz, int wy) 
		{
			super(context);
			follow=false;
			recentrepaint=System.currentTimeMillis();
			sreptime=recentrepaint;
			jedentouch=false;
			szer=sz;
			wys=wy;
			mx=new Matrix();
			mx.reset();
			old_matrix=new Matrix();
			old_matrix.reset();
			repaint();
		}
		public boolean onTouchEvent(MotionEvent event)
		{
			if(przesAvailable())
			{
				int action=event.getActionMasked();
				if(jedentouch)
				{
					oldx=event.getX();
					oldy=event.getY();
					jedentouch=false;
				}
				if(action==MotionEvent.ACTION_DOWN)
				{
					oldx=event.getX();
					oldy=event.getY();
					boolean ack=false;
					if(event.getEventTime()<actiontime+500)
					{
						ack=true;
						mx.postScale(2, 2, oldx, oldy);
					}
					actiontime=event.getEventTime();
					if(ack)
						actiontime=0;
					index1=event.getActionIndex();
					repaint();
				}
				if(action==MotionEvent.ACTION_POINTER_DOWN)
				{
					index2=event.getActionIndex();
					oldxBIS = event.getX(event.getPointerId(1));
				    oldyBIS = event.getY(event.getPointerId(1));
					oldx = event.getX(event.getPointerId(0));
				    oldy = event.getY(event.getPointerId(0));
				}
				if(action==MotionEvent.ACTION_MOVE)
				{
					actiontime=0;
					if(event.getPointerCount() == 1)
					{
						float newx=event.getX();
						float newy=event.getY();
						mx.postTranslate(newx-oldx, newy-oldy);
						oldx=newx;
						oldy=newy;
						repaint4();
					}
					if(event.getPointerCount() > 1)
					{
						float newx=event.getX(event.getPointerId(0));
						float newy=event.getY(event.getPointerId(0));
						float newxBIS=event.getX(event.getPointerId(1));
						float newyBIS=event.getY(event.getPointerId(1));
						float divid=distance(new PointF(oldx, oldy), new PointF(newxBIS, newyBIS))/distance(new PointF(oldx, oldy), new PointF(oldxBIS, oldyBIS));
						float kierun=(float) (kierunek(new PointF(oldx, oldy), new PointF(newxBIS, newyBIS))-kierunek(new PointF(oldx, oldy), new PointF(oldxBIS, oldyBIS)));
						mx.postRotate(kierun, oldx, oldy);
						mx.postScale(divid, divid, oldx, oldy);
						divid=distance(new PointF(newxBIS, newyBIS), new PointF(newx, newy))/distance(new PointF(newxBIS, newyBIS), new PointF(oldx, oldy));
						kierun=(float) (kierunek(new PointF(newxBIS, newyBIS), new PointF(newx, newy))-kierunek(new PointF(newxBIS, newyBIS), new PointF(oldx, oldy)));
						mx.postRotate(kierun, newxBIS, newyBIS);
						mx.postScale(divid, divid, newxBIS, newyBIS);
						oldxBIS=newxBIS;
						oldyBIS=newyBIS;
						oldx=newx;
						oldy=newy;
						repaint4();
					}
				}
				if(action==MotionEvent.ACTION_POINTER_UP)
				{
					jedentouch=true;
					repaint();
				}
				if(action==MotionEvent.ACTION_UP)
				{
					repaint();
				}
				return true;
			}
			return false;
		}
	}
	public void onWindowFocusChanged(boolean hasFocus)
	{
		  super.onWindowFocusChanged(hasFocus);
		  if(frtest1!=null && frtest1.mapa1!=null && frtest1.mapa1.obr!=null)
		  {
			frtest1.mapa1.obr.szer=vu1.getWidth();
			frtest1.mapa1.obr.wys=vu1.getHeight();
		  }
			//PointF kuc=new PointF();
			//kuc.set((float)21.12, (float)52.2);
	}
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
		private Button t3;
		private Button t9;
		EditText ed1;
		Button t4;
		CheckBox rad1;
		CheckBox rad2;
		private Button t6;
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
			t4 = new Button(context);
			t6 = new Button(context);
			this.addView(pro);
			this.addView(alt);
			this.addView(tim);
			this.addView(bea);
			t3=new Button(context);
			t9=new Button(context);
			this.addView(rad1);
			this.addView(rad2);
			t4.setText("POBIERZ FOLDER");
			t9.setText("TXX");
			this.addView(ed1);
			this.addView(t4);
			this.addView(t9);
			rad1.setText("INTERNET DOWNLOAD");
			rad2.setText("FOLLOW");
			t3.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v)
			{
				if(mService!=null)
				{
					if(mService.nagr()==false)
					{
						mService.setnagrywanie(true);
		            	lay1.t3.setText("STOP ZAPIS");
		            	lay1.t6.setActivated(false);
					}
					else
					{
						mService.setnagrywanie(false);
		            	lay1.t3.setText("START ZAPIS");
		            	lay1.t6.setActivated(true);
					}
				}
			}
	        });
			t6.setText("ŚCIEŻKA GPX");
			t6.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v)
			{
				if(mService!=null && !mService.nagr())
				{
					mService.sciezka.clear();
					Intent wpIntent = new Intent();
					wpIntent.setType("file/gpx");
					wpIntent.setAction(Intent.ACTION_GET_CONTENT);
					wpIntent.putExtra("return-data", true); //added snippet
					startActivityForResult(Intent.createChooser(wpIntent, "xxx"),1);  }
			}
	        });
			t4.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v)
			{
				String newpath=gendir.getAbsolutePath()+"/"+ed1.getText();
				if(frtest1.mapa1!=null)
					frtest1.mapa1.obr.ig=new ImageLoader(newpath);
			}
	        });
			t9.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v)
			{
	        	noti(String.valueOf(mService.sciezka.size()), 166);
			}
	        });
			t3.setText("NAGRYWANIE");
			this.addView(t3);
			this.addView(t6);
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
			tim.setText("Czas: "+String.valueOf(timX));
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
			sze.setText("Brak danych...");
			dlu.setText(" ");
			dok.setText(" ");
			pre.setText(" ");
			pro.setText(" ");
			alt.setText(" ");
			tim.setText(" ");
			bea.setText(" ");
		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		  if (resultCode == RESULT_OK) {
	            if (requestCode == 1) {
	                Uri selectedUri = data.getData();
	                String ssa=selectedUri.getEncodedPath();
	                if(!mService.nagr() && frtest1!=null)
	                	frtest1.mapa1.obr.doble=new SciezkaLoc(ssa);
	                noti(String.valueOf(mService.sciezka.size()), 121);
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
            if(mService.nagr())
            {
            	lay1.t3.setText("STOP ZAPIS");
            	lay1.t6.setActivated(false);
            }
            else
            {
            	lay1.t3.setText("START ZAPIS");
            	lay1.t6.setActivated(true);
            }
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
		Notification buld=mBuilder.build();
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
            	noti(String.valueOf(bisservice.sciezka.size()), 111);
            	mService.sciezka.copy(bisservice.sciezka);
            	mService.setnagrywanie(true);
            	lay1.t3.setText("STOP ZAPIS");
            }
            unbindService(mConnection2);
            Intent intent2 = new Intent(MainActivity.this, ServiceBis.class);
            intent2.addCategory("kupabis");
            stopService(intent2);
        	noti(String.valueOf(mService.sciezka.size()), 112);
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
    public class interfejsbis implements interfejs
    {
    	public void zmianalokalizacji(Location arg0)
    	{
    		lay1.sendData(arg0);
			if(frtest1.mapa1.obr!=null)
			{
				frtest1.mapa1.obr.setaktloc(arg0);
				frtest1.mapa1.obr.repaint2();
			}
    	}
    }
    /*public class interfejsbis implements interfejs
    {
    	public void zmianalokalizacji(Location arg0)
    	{
    		lay1.sendData(arg0);
			if(lay2.obr!=null)
			{
				lay2.obr.setaktloc(arg0);
				lay2.obr.repaint2();
			}
    	}
    }*/
    Tab tab1, tab2;
    SckBis mConnection2;
    interfejsbis intbis;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        String rku = Environment.getExternalStorageDirectory().toString();
        vu1=new LinearLayout(this);
        gendir = new File(rku + "/nap_program");    
        gendir.mkdirs();
        netdir =new File(gendir.getAbsolutePath()+ "/net_download");
        netdir.mkdirs();
        setTheme(android.R.style.Theme_Holo);
		ActionBar bar = getActionBar();
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
		v1=new ScrollView(this);
		vu1.addView(v1);
		lay1=new DaneLokalizacyjne(this);
		lay1.setOrientation (LinearLayout.VERTICAL);
		v1.addView(lay1);
	    	frtest1=new Myfragment();
	    	frtest2=new ArticleFragment2();
	    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    tab1 = bar.newTab();
	    tab1.setText("USTAWIENIA");
	    tab1.setTag("1");
	    tab1.setTabListener(new MyTabListener(frtest2));
	    bar.addTab(tab1);
	    tab2 = bar.newTab();
	    tab2.setText("MAPA");
	    tab2.setTag("2");
	    tab2.setTabListener(new MyTabListener(frtest1));
	    bar.addTab(tab2);
		//setContentView(vu1);
		//setContentView((Fragment)frtest1);
		lay1.sendError();
		Random r=new Random();
		datex=(r.nextInt(1000));
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
        if(frtest1.mapa1.obr.ig!=null)
        	frtest1.mapa1.obr.ig.zapisz();
    }
	public class MyTabListener implements ActionBar.TabListener {
		Fragment fragment;
		
		public MyTabListener(Fragment fragment) {
			this.fragment = fragment;
		}
		
	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	    	ft.add(0, fragment, "siejbik");
	    	ft.replace(android.R.id.content, fragment);
		}
		
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		}
		
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// nothing done here
		}
	}
	/*public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1)
	{
		
		if(arg0.getTag().equals("1"))
			MainActivity.this.setContentView(vu1);
		if(arg0.getTag().equals("2"))
			MainActivity.this.setContentView(lay2);
	}
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		if(arg0.getTag().equals("1"))
			arg1.detach(frtest1);
		if(arg0.getTag().equals("2"))
			arg1.detach(frtest2);
	}*/
}
