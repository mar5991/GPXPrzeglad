/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.nap;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {
	boolean partial;
	class VectorMapObrazek extends VectorMapManager
	{
		GL10 gl;
		VectorMapObrazek(String rootDir, String rootFile)
		{
			super(rootDir, rootFile);
		}
		void rysujKafelek(Kafelek kaf)
		{
			if(kaf.wyswietlane && kaf.vertexBuffer!=null)
			{
				for(int i=0; i<WARSTWY; i++)
				{
					 gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
					 	gl.glLineWidth(1);
				        gl.glColor4f(1,1,1,1);
				        if(i==1)
				        	gl.glColor4f(0.2f,0.2f,0.2f,1);
				        if(i==3)
				        {
				        	gl.glColor4f(0.3f,0.3f,1,1);
				        	gl.glLineWidth(5);
				        }
				        if(i==2)
				        {
				        	gl.glColor4f(1,0.3f,0.3f,1);
						 	gl.glLineWidth(5);
				        }
				        gl.glVertexPointer( // point to vertex data:
				                2,
				                GL10.GL_FLOAT, 0, kaf.vertexBuffer[i]);
				        gl.glDrawArrays(  // draw shape:
				                GL10.GL_LINES,
				                0, kaf.liczniki[i]*4);
				        // Disable vertex array drawing to avoid
				        // conflicts with shapes that don't use it
				        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
				}
			}
			super.rysujKafelek(kaf);
		}
	}
	VectorMapObrazek vmo;
    int width;
    int height;
    float xCenter;
    float yCenter;
    float zoom;
    ArrayList <TeksturaEverywhere> tekstury;
    ArrayList <RasterLayer> rasterLayers;
    long[] pointsetted;
    Vector <Long> posrednie;
    int licznik_point_setted;
    Square sq1=new Square(100, 100, 200);
    void setpoint(double x, double y)
	{
    	long wynik=vmo.setpoint(x, y);
    	if(wynik!=-1)
    	{
    		pointsetted[licznik_point_setted%2]=wynik;
        	licznik_point_setted++;
        	if(pointsetted[0]!=-1 && pointsetted[1]!=-1 && licznik_point_setted%2==1)
        	{
        		System.out.println("kania start");
        		Dijkstra dij=new Dijkstra();
        		vmo.ladujdijkstra(dij);
        		System.out.println("kania middle");
        		posrednie=dij.wyniki(pointsetted[0], pointsetted[1]);
        		System.out.println("kania stop");
        	}
        	else
        	{
        		posrednie=null;
        	}
    	}
	}
    MyGLSurfaceView viv;
    MyGLRenderer(Context cont,  MyGLSurfaceView vive)
    {
    	tekstury=new ArrayList<TeksturaEverywhere>();
    	rasterLayers=new ArrayList<RasterLayer>();
    	viv=vive;
    	pointsetted=new long[2];
    	pointsetted[0]=-1;
    	pointsetted[1]=-1;
    	licznik_point_setted=0;
    	partial=false;
    	String rku = Environment.getExternalStorageDirectory().toString();
        File gendir = new File(rku + "/nap_wektor");
        String sciez=gendir+"";
		vmo = new VectorMapObrazek(sciez, "tile0x6.zuo");
    	width=100;
    	height=100;
    	PointF wspWarszawa = new PointF();
    	wspWarszawa.x=21;
    	wspWarszawa.y=52.2f;
    	PointF ekrWarszawa = TimeConvert.WspGeoToWspEkr(wspWarszawa);
    	xCenter=ekrWarszawa.x;
    	yCenter=ekrWarszawa.y;
    	zoom=0.02f;
    }
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        System.out.println("Tro 4 ");
        gl.glDisable(GL10.GL_DITHER);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_ALPHA_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST);
         gl.glEnable(GL10.GL_CULL_FACE);
         //gl.glBlendFunc(GL10.GL_ONE, GL10.GL_DST_COLOR);
        // gl.glEnable(GL10.GL_DEPTH_TEST);
    }
    public void dodajWarstwe(RasterLayer layer)
    {
        System.out.println("Tro P");
    	rasterLayers.add(layer);
    	TeksturaEverywhere nowa = new TeksturaEverywhere(xCenter-(width/2)*zoom, yCenter-(height/2)*zoom, xCenter+(width/2)*zoom, yCenter+(height/2)*zoom);
	    tekstury.add(nowa);
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glEnable(GL10.GL_BLEND);
        System.out.println("Tro Y");
        // Draw background color
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glClearColor(0, 1, 1, 1);
        // Set GL_MODELVIEW transformation mode
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();   // reset the matrix to its default state
        GLU.gluOrtho2D(gl, xCenter-(width/2)*zoom,  xCenter+(width/2)*zoom,  yCenter+(height/2)*zoom,  yCenter-(height/2)*zoom);
        System.out.println("Tro Z");
        if(true)
        {
	       	gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		    int s1=rasterLayers.size();
	        if(!partial)
		    { 
		       	gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		       	tekstury.clear();
		       	for(int i=0; i<s1; i++)
		       	{
		            System.out.println("Tro A");
		       		TeksturaEverywhere nowa = new TeksturaEverywhere(xCenter-(width/2)*zoom, yCenter-(height/2)*zoom, xCenter+(width/2)*zoom, yCenter+(height/2)*zoom);
		            System.out.println("Tro B");
		       		tekstury.add(nowa);
		            System.out.println("Tro C");
				    RasterLayer teraz = (RasterLayer)rasterLayers.get(i);
		            System.out.println("Tro D");
				    nowa.loadGLTexture(gl, teraz.repaint(xCenter-(width/2)*zoom, yCenter-(height/2)*zoom, xCenter+(width/2)*zoom, yCenter+(height/2)*zoom, 1/zoom));
		            nowa.draw(gl);
		       	}
			}
	        else
	        {
	        	for(int i=0; i<s1; i++)
	        	{
	        		TeksturaEverywhere akt=(TeksturaEverywhere)tekstury.get(i);
	        		akt.draw(gl);
	        	}
	        }
	       	gl.glDisable(GL10.GL_TEXTURE_2D);
	        gl.glDisable(GL10.GL_BLEND);
        }
        System.out.println("Tro F");
        vmo.gl=gl;
        int level=(int)(Math.log((1/zoom*4))/Math.log(2.0));
        PointF p1=new PointF();
        PointF p2=new PointF();
        p1.x=xCenter-(width/2)*zoom;
        p1.y=yCenter-(height/2)*zoom;
        p2.x=xCenter+(width/2)*zoom;
        p2.y=yCenter+(height/2)*zoom;
        PointF p3=TimeConvert.WspEkrtoWspGeo(p1);
        PointF p4=TimeConvert.WspEkrtoWspGeo(p2);
		System.out.println("SAKWA 0");
        System.out.println("Tro G");
        sq1.draw(gl); 
        if(partial)
        {
        	vmo.rysujWszystko();
        }
        else
        {
        	vmo.laduj(Math.min(p3.x, p4.x), Math.min(p3.y, p4.y), Math.max(p3.x, p4.x), Math.max(p3.y, p4.y), Math.min(Math.max(level, 6), 14));
        	vmo.rysujWszystko();
        	partial=true;
        }
        System.out.println("Tro H");
        if(pointsetted[0]!=-1)
        {
        	VectorMapManager.Punkt pit=vmo.punkty.get(pointsetted[0]);
        	if(pit!=null)
        	{
	        	double x1=pit.x;
	        	double y1=pit.y;
	            Square sqt=new Square((float)x1, (float)y1, zoom*10);
	        	sqt.draw(gl);
        	}
        }
        if(pointsetted[1]!=-1)
        {
        	VectorMapManager.Punkt pit=vmo.punkty.get(pointsetted[1]);
        	if(pit!=null)
        	{
	        	double x1=pit.x;
	        	double y1=pit.y;
	            Square sqt=new Square((float)x1, (float)y1, zoom*10);
	        	sqt.draw(gl);
        	}
        }
        System.out.println("Tro I");
        if(posrednie!=null)
        {
        	int s1=posrednie.size();
        	for(int i=1; i<s1; i++)
        	{
        		long apt=posrednie.get(i);
        		long apt2=posrednie.get(i-1);
                if(apt!=-1 && apt2!=-1)
                {
                	VectorMapManager.Punkt pit=vmo.punkty.get(apt);
                	VectorMapManager.Punkt pit2=vmo.punkty.get(apt2);
                	if(pit!=null && pit2!=null)
                	{
        	        	double x1=pit.x;
        	        	double y1=pit.y;
        	        	double x2=pit2.x;
        	        	double y2=pit2.y;
        	            Line ln=new Line((float)x1, (float)y1, (float)x2, (float)y2);
        	        	ln.draw(gl);
                	}
                }
        	}
        }
        System.out.println("Tro J");
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    	this.width=width;
    	this.height=height;
        int s1=rasterLayers.size();
        System.out.println("Tro size: "+s1);
        for(int i=0; i<s1; i++)
        {
        	RasterLayer teraz = (RasterLayer)rasterLayers.get(i);
        	teraz.szer=width;
        	teraz.wys=height;
        }
    }
}