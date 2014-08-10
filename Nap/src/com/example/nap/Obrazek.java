package com.example.nap;

import java.io.File;
import java.io.FileOutputStream;
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
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
	class Obrazek extends RasterLayer
	{
		ImageLoader ig;
		public Bitmap repaint(float x1, float y1, float x2, float y2, float zoom)
		{
        	System.out.println("edwin 2");
			Bitmap wynik=Bitmap.createBitmap(szer, wys, Bitmap.Config.ARGB_8888);
			Canvas plotno=new Canvas(wynik);
			PointF wpta=new PointF(x1,y1);
			PointF wptb=new PointF(x1,y2);
			PointF wptc=new PointF(x2,y2);
			PointF wptd=new PointF(x2,y1);
			PointF apta=TimeConvert.WspEkrtoWspGeo(wpta);
			PointF aptb=TimeConvert.WspEkrtoWspGeo(wptb);
			PointF aptc=TimeConvert.WspEkrtoWspGeo(wptc);
			PointF aptd=TimeConvert.WspEkrtoWspGeo(wptd);
			float minx=TimeConvert.min(TimeConvert.min(wpta.x, wptb.x), TimeConvert.min(wptc.x, wptd.x));
			float maxx=TimeConvert.max(TimeConvert.max(wpta.x, wptb.x), TimeConvert.max(wptc.x, wptd.x));
			float miny=TimeConvert.min(TimeConvert.min(wpta.y, wptb.y), TimeConvert.min(wptc.y, wptd.y));
			float maxy=TimeConvert.max(TimeConvert.max(wpta.y, wptb.y), TimeConvert.max(wptc.y, wptd.y));
			PointF cclewydolny=new PointF(TimeConvert.min(TimeConvert.min(apta.x, aptb.x), TimeConvert.min(aptc.x, aptd.x)), TimeConvert.min(TimeConvert.min(apta.y, aptb.y), TimeConvert.min(aptc.y, aptd.y)));
			PointF ccprawygorny=new PointF(TimeConvert.max(TimeConvert.max(apta.x, aptb.x), TimeConvert.max(aptc.x, aptd.x)), TimeConvert.max(TimeConvert.max(apta.y, aptb.y), TimeConvert.max(aptc.y, aptd.y)));
			PointF ddlewydolny=new PointF(TimeConvert.min(TimeConvert.min(wpta.x, wptb.x), TimeConvert.min(wptc.x, wptd.x)), TimeConvert.min(TimeConvert.min(wpta.y, wptb.y), TimeConvert.min(wptc.y, wptd.y)));
			PointF ddprawygorny=new PointF(TimeConvert.max(TimeConvert.max(wpta.x, wptb.x), TimeConvert.max(wptc.x, wptd.x)), TimeConvert.max(TimeConvert.max(wpta.y, wptb.y), TimeConvert.max(wptc.y, wptd.y)));
			
			Matrix mx=new Matrix();
			mx.reset();
			mx.postScale(zoom, zoom, minx, miny);
			mx.postTranslate(minx*-1, miny*-1);
			float scale1=zoom*(float)1.7;
			float scale3=zoom;
			System.out.println("edwin "+minx+" "+miny+" "+maxx+" "+maxy);
        	System.out.println("edwin "+x1+" "+y1+" "+x2+" "+y2);
			System.out.println("edwin 3");
			if(ig!=null)
			{
				if(true)
				{
					ig.laduj( cclewydolny.x, ccprawygorny.x, cclewydolny.y, ccprawygorny.y, scale3, this);
				}
				int s2=ig.imadat.size();
	        	System.out.println("edwin 3a "+s2);
				for(int i=0; i<s2; i++)
				{
					ImageData im=ig.imadat.get(i);
					double scale2=TimeConvert.distance(new PointF(0, 0), new PointF(im.rozdzielczoscx, im.rozdzielczoscy));
					double scale=scale1*scale2;
		        	System.out.println("edwin "+scale+" "+scale1+" "+scale2+" "+scale3);
					if(scale>=1.6 && TimeConvert.czy_pokrywajo(ddlewydolny, ddprawygorny, im.lewydolny, new PointF(im.lewydolny.x+im.rozdzielczoscx*256, im.lewydolny.y+im.rozdzielczoscy*256)))
					{
			        	System.out.println("edwin bis "+im.sciezka+" "+scale+" "+scale1+" "+scale2+" "+scale3);
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
							plotno.drawBitmap(nbt, nmax, paintx);
					}
					else
					{
						im.bmp=null;
					}
				}
			}
        	System.out.println("edwin "+minx+" "+miny+" "+maxx+" "+maxy);
        	System.out.println("edwin 4");
			return wynik;
		}
		public Obrazek(MyGLSurfaceView surView, String path) 
		{
			super(surView);
			ig=new ImageLoader(path);
		}
		public void zapisz()
		{
			ig.zapisz();
		}
	}