package com.example.nap;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.example.nap.ImageData;

public class ImageLoader
	{
	//MainActivity mac;
    private class DownloadWebpageTask extends AsyncTask<String, Void, byte[]>
    {
       	String wyj;
       	Obrazek obb;
        public DownloadWebpageTask (String wj, Obrazek ob)
        {
        	obb=ob;
        	wyj=wj;
        }
        protected byte[] doInBackground(String... urls)
        {
            try
            {
                return downloadUrl(urls[0], wyj);
            }
            catch (IOException e)
            {
                return null;
            }
        }
        @Override
        protected void onPostExecute(byte[] result)
        {
        	obb.update();
        }
    }
    private byte[] downloadUrl(String myurl, String wyj) throws IOException
    {
    	    InputStream is = null; 
    	    try
    	    {
    	        URL url = new URL(myurl);
    	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	        conn.setReadTimeout(30000);
    	        conn.setConnectTimeout(15000);
    	        conn.setRequestMethod("GET");
    	        conn.setDoInput(true);
    	        conn.connect();
                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(wyj);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
                output.close();
                byte[] wynik=null;
    	        return wynik;
    	    }
    	    finally
    	    {
    	        if (is != null)
    	        {
    	            is.close();
    	        } 
    	    }
    }
		String sciezka;
		ArrayList <ImageData> imadat;
		int long2tilex(double lon, int z)
		{
			return (int)( Math.floor((lon+180.0)/360.0*Math.pow(2.0, z)));
		}
		int lat2tiley (double lat, int z)
		{
			return (int)(Math.floor((1.0-Math.log(Math.tan(lat*Math.PI/180.0)+1.0/Math.cos(lat*Math.PI/180.0))/Math.PI)/2.0*Math.pow(2.0, z)));
		}
		public ImageLoader(String scz)
		{
			sciezka=new String(scz);
			imadat=new ArrayList<ImageData>();
				try {
					FileReader fx=new FileReader(scz+"/data.txt");
				BufferedReader buf = new BufferedReader(fx);
					while(buf.ready()==true)
					{
						String a1=buf.readLine();
						String a2=buf.readLine();
						String a3=buf.readLine();
						String a4=buf.readLine();
						String a5=buf.readLine();
						float b1=Float.parseFloat(a1);
						float b2=Float.parseFloat(a2);
						float b3=Float.parseFloat(a3);
						float b4=Float.parseFloat(a4);
						ImageData im=new ImageData();
						im.lewydolny=new PointF(b1, b2);
						im.rozdzielczoscx=b3;
						im.rozdzielczoscy=b4;
						im.sciezka=scz+"/"+a5;
						File fac=new File(im.sciezka);
						if(fac.exists())
							imadat.add(im);
					}
					Collections.sort(imadat);
					fx.close();
					buf.close();
				}
				catch (Exception e)
				{
				}
		}
		boolean szukaj(String nazwapliku)
		{
			int s2=imadat.size();
			for(int i=0; i<s2; i++)
			{
				if(imadat.get(i).sciezka.equals(nazwapliku))
				{
					return true;
				}
			}
			return false;
		}
		public void laduj(float minx, float maxx, float miny, float maxy, float zoom, Obrazek obb)
		{
			
			int level=(int)(Math.log((zoom*4))/Math.log(2.0)); //level TODO
			int x1=long2tilex(minx, level);
			x1=Math.max(x1, 0);
			int x2=long2tilex(maxx, level);
			int y1=lat2tiley(maxy, level);
			int y2=lat2tiley(miny, level);
			y2=Math.max(y2, 0);
			ArrayList <ImageData> wyn = new ArrayList <ImageData>();
			for(int i=x1; i<=x2; i++)
			{
				for(int j=y1; j<=y2; j++)
				{
					String nazwapliku=sciezka+"/"+String.valueOf(level)+"x"+String.valueOf(i)+"x"+String.valueOf(j)+".png";
					if(!szukaj(nazwapliku))
					{
						new DownloadWebpageTask(nazwapliku, obb).execute("http://a.tile.openstreetmap.org/"+String.valueOf(level)+"/"+String.valueOf(i)+"/"+String.valueOf(j)+".png");
						ImageData nowe=new ImageData();
						nowe.sciezka=nazwapliku;
						nowe.rozdzielczoscx=(float)(1000.0/Math.pow(2.0, level)/256.0);
						nowe.rozdzielczoscy=nowe.rozdzielczoscx;
						float lewy=(float)((float)(i)/Math.pow(2.0, level)*1000.0);
						float dolny=(float)((float)(j)/Math.pow(2.0, level)*1000.0);
						nowe.lewydolny=new PointF(lewy, dolny);
						wyn.add(nowe);
						imadat.add(nowe);
					}
				}
			}
			Collections.sort(imadat);
		}
		void zapisz()
		{
			try
			{
				File f=new File(sciezka+"/data.txt");
				FileWriter ww=new FileWriter(sciezka+"/data.txt");
				int s1=imadat.size();
				for(int i=0; i<s1; i++)
				{
					ImageData id=imadat.get(i);
					String wyn5=id.sciezka.substring(sciezka.length()+1);
					File file = new File(wyn5);
					/*if(file.exists()) TODO
					{*/
						ww.write(String.valueOf(id.lewydolny.x)+"\n");
						ww.write(String.valueOf(id.lewydolny.y)+"\n");
						ww.write(String.valueOf(id.rozdzielczoscx)+"\n");
						ww.write(String.valueOf(id.rozdzielczoscy)+"\n");
						ww.write(wyn5+"\n");
					/*}*/
				}
				ww.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}