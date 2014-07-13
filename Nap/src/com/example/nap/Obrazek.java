package com.example.nap;
import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nap.ImageLoader;
import com.example.nap.VectorMapManager;


	@SuppressLint("ShowToast")
	class Obrazek extends ImageView
	{
		class VectorMapObrazek extends VectorMapManager
		{
			float[] pts;
			int licznik;
			void rysujWszystko()
			{
				pts=new float[300000];
				licznik=0;
				super.rysujWszystko();
				Paint p=new Paint();
				canm.drawLines(pts, p);
			}
			Canvas canm;
			VectorMapObrazek(String rootDir, String rootFile, TextView pre)
			{
				super(rootDir, rootFile, pre);
			}
			void rysujlinie(double xStart, double yStart, double xStop, double yStop, int id, String name)
			{
				PointF start=new PointF();
				start.x=(float)xStart;
				start.y=(float)yStart;
				PointF stop=new PointF();
				stop.x=(float)xStop;
				stop.y=(float)yStop;
				PointF start2=TimeConvert.transform(start, mx);
				PointF stop2=TimeConvert.transform(stop, mx);
				pts[licznik*4]=start2.x;
				pts[licznik*4+1]=start2.y;
				pts[licznik*4+2]=stop2.x;
				pts[licznik*4+3]=stop2.y;
				//canm.drawLine(start2.x, start2.y, stop2.x, stop2.y, p);
				licznik++;
			}
		}
		VectorMapObrazek wektorMapa;
		private Canvas can;
		private Bitmap btu;
		private Bitmap mbtu;
		private Matrix mx;
		private Matrix old_matrix;
		private float oldx;
		private float oldy;
		private float oldxBIS;
		private float oldyBIS;
		int szer;
		int wys;
		ImageLoader ig;
		boolean jedentouch;
		long actiontime;
		long recentrepaint;
		long sreptime;
		private boolean follow;
		boolean netavail()
		{
			return false;
		}
		boolean przesAvailable()
		{
			return !(follow);
		}
		public void setfollow(PointF loc)
		{	
			follow=true;	
			ustawwsp(loc, aktualzoom());
		}
		void unsetfollow()
		{
			follow=false;
		}
		void repaint4()
		{
			long aktt=System.currentTimeMillis();
			/*if(aktt-5000>recentrepaint)
			{
				repaint();
			}*/
			//else
			//{
				if(aktt-30>=sreptime)
				{
					repaint2();
				}
			//}
		}
		void ustawwsp(PointF wsp, float zoox)
		{
			PointF kpk=TimeConvert.WspGeoToWspEkr(wsp);
			mx.reset();
			mx.postTranslate((kpk.x*-1)+szer/2, wys-kpk.y-(wys/2));
			mx.postScale(zoox, zoox, szer/2, wys/2);
			old_matrix=new Matrix(mx);
			repaint();
		}
		public void update()
		{
			repaint();
		}
		void ustawwsp(PointF wsplg, PointF wsppd)
		{
			PointF lg=TimeConvert.WspGeoToWspEkr(wsplg);
			PointF pd=TimeConvert.WspGeoToWspEkr(wsppd);
			float zoomx=1;
			float rozx=Math.abs(pd.x-lg.x);
			float rozy=Math.abs(lg.y-pd.y);
			if((rozx/rozy)>(szer/wys))
			{
				zoomx=szer/rozx;
			}
			else
			{
				zoomx=wys/rozy;
			}
			zoomx/=1.1;
			PointF kpk=new PointF((pd.x+lg.x)/2, (pd.y+lg.y)/2);
			mx.reset();
			mx.postTranslate((kpk.x*-1)+szer/2, wys-kpk.y-(wys/2));
			mx.postScale(zoomx, zoomx, szer/2, wys/2);
			old_matrix=new Matrix(mx);
			repaint();
		}
		float aktualzoom()
		{
			float scale1=TimeConvert.distance(TimeConvert.transform (new PointF(0, 0), mx), TimeConvert.transform (new PointF(1, 1), mx));
			return (scale1/(float) Math.sqrt(2));
		}
		private void repaint()
		{
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
			PointF wpta=TimeConvert.WspEkrtoWspGeo(TimeConvert.transform(new PointF(0,0), ing));
			PointF wptb=TimeConvert.WspEkrtoWspGeo(TimeConvert.transform(new PointF(0,wys), ing));
			PointF wptc=TimeConvert.WspEkrtoWspGeo(TimeConvert.transform(new PointF(szer,wys), ing));
			PointF wptd=TimeConvert.WspEkrtoWspGeo(TimeConvert.transform(new PointF(szer,0), ing));
			PointF apta=TimeConvert.transform(new PointF(0,0), ing);
			PointF aptb=TimeConvert.transform(new PointF(0,wys), ing);
			PointF aptc=TimeConvert.transform(new PointF(szer,wys), ing);
			PointF aptd=TimeConvert.transform(new PointF(szer,0), ing);
			float minx=TimeConvert.min(TimeConvert.min(wpta.x, wptb.x), TimeConvert.min(wptc.x, wptd.x));
			float maxx=TimeConvert.max(TimeConvert.max(wpta.x, wptb.x), TimeConvert.max(wptc.x, wptd.x));
			float miny=TimeConvert.min(TimeConvert.min(wpta.y, wptb.y), TimeConvert.min(wptc.y, wptd.y));
			float maxy=TimeConvert.max(TimeConvert.max(wpta.y, wptb.y), TimeConvert.max(wptc.y, wptd.y));
			PointF cclewydolny=new PointF(TimeConvert.min(TimeConvert.min(apta.x, aptb.x), TimeConvert.min(aptc.x, aptd.x)), TimeConvert.min(TimeConvert.min(apta.y, aptb.y), TimeConvert.min(aptc.y, aptd.y)));
			PointF ccprawygorny=new PointF(TimeConvert.max(TimeConvert.max(apta.x, aptb.x), TimeConvert.max(aptc.x, aptd.x)), TimeConvert.max(TimeConvert.max(apta.y, aptb.y), TimeConvert.max(aptc.y, aptd.y)));
			float scale1=TimeConvert.distance(TimeConvert.transform (new PointF(0, 0), mx), TimeConvert.transform (new PointF(1, 1), mx));
			float scale3=TimeConvert.distance(TimeConvert.transform (new PointF(0, 0), mx), TimeConvert.transform (new PointF(1, 0), mx));
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
					double scale2=TimeConvert.distance(new PointF(0, 0), new PointF(im.rozdzielczoscx, im.rozdzielczoscy));
					double scale=scale1*scale2;
					//UWAGA MNOŻNIK 256 W NASTĘPNEJ LINIJCE JEST TYMCZASOWY I NIEPRAWIDŁOWY
					if(scale>=1.6 && TimeConvert.czy_pokrywajo(cclewydolny, ccprawygorny, im.lewydolny, new PointF(im.lewydolny.x+im.rozdzielczoscx*256, im.lewydolny.y+im.rozdzielczoscy*256)))
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
			if(wektorMapa!=null)
			{
				wektorMapa.canm=can;
				int level=(int)(Math.log((scale3*4))/Math.log(2.0));
				wektorMapa.laduj(minx, miny, maxx, maxy, level);
			}
			rysujsciezke(canm, mx);
			Matrix zero=new Matrix();
			canp.drawBitmap(btu, zero, null);
			canp.drawBitmap(mbtu, zero, null);
			this.setImageBitmap(btp);
		}
		void rysujsciezke(Canvas canm, Matrix mrx)
		{
		}
		private void repaint2()
		{
			sreptime=System.currentTimeMillis();
			Bitmap btp=Bitmap.createBitmap(szer, wys, Bitmap.Config.ARGB_8888);
			Bitmap mbtu=Bitmap.createBitmap(szer, wys, Bitmap.Config.ARGB_8888);
			Canvas canm = new Canvas(mbtu);
			rysujsciezke(canm, mx);
			Matrix pkt=new Matrix(old_matrix);
			pkt.invert(pkt);
			pkt.postConcat(mx);
			can=new Canvas(btp);
			can.drawBitmap(btu, pkt, null);
			Matrix zero=new Matrix();
			can.drawBitmap(mbtu, zero, null);
			this.setImageBitmap(btp);
		}
		public Obrazek(Context context, int sz, int wy, TextView pre) 
		{
			super(context);
			String rku = Environment.getExternalStorageDirectory().toString();
            File gendir = new File(rku + "/nap_wektor");
            String sciez=gendir+"";
			wektorMapa = new VectorMapObrazek(sciez, "tile0x12.zuo", pre);
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
					repaint4();
				}
				if(action==MotionEvent.ACTION_POINTER_DOWN)
				{
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
						float divid=TimeConvert.distance(new PointF(oldx, oldy), new PointF(newxBIS, newyBIS))/TimeConvert.distance(new PointF(oldx, oldy), new PointF(oldxBIS, oldyBIS));
						float kierun=(float) (TimeConvert.kierunek(new PointF(oldx, oldy), new PointF(newxBIS, newyBIS))-TimeConvert.kierunek(new PointF(oldx, oldy), new PointF(oldxBIS, oldyBIS)));
						mx.postRotate(kierun, oldx, oldy);
						mx.postScale(divid, divid, oldx, oldy);
						divid=TimeConvert.distance(new PointF(newxBIS, newyBIS), new PointF(newx, newy))/TimeConvert.distance(new PointF(newxBIS, newyBIS), new PointF(oldx, oldy));
						kierun=(float) (TimeConvert.kierunek(new PointF(newxBIS, newyBIS), new PointF(newx, newy))-TimeConvert.kierunek(new PointF(newxBIS, newyBIS), new PointF(oldx, oldy)));
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
					repaint4();
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