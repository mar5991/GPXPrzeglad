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

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.widget.Toast;
import android.os.Environment;
/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousXbis;
    private float mPreviousYbis;
    private float oldzoom;
	private boolean czyMomentPrzejsciaNaJedenPalec;
	private boolean czyAkcjaZPrzesunieciemPalca;
	private long recentClickTime;
    public MyGLSurfaceView(Context context) 
    {
        super(context);
        System.out.println("Tro 1");
        czyMomentPrzejsciaNaJedenPalec=false;
        mRenderer = new MyGLRenderer(context, this);
        setRenderer(mRenderer);
        czyAkcjaZPrzesunieciemPalca=false;
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    int szerokosc()
    {
    	return mRenderer.width;
    }
    int wysokosc()
    {
    	return mRenderer.height;
    }
    public void dodajWarstwe(RasterLayer layer)
    {
    	mRenderer.dodajWarstwe(layer);
    }
    public void ustawWspolrzedne(float xCenter, float yCenter, float zoom)
    {
    	mRenderer.xCenter=xCenter;
    	mRenderer.yCenter=yCenter;
    	mRenderer.zoom=zoom;
    	mRenderer.partial=false;
		requestRender();
    }
    public void update()
    {
    	mRenderer.partial=false;
		requestRender();
    }
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        System.out.println("Tro start");
    	if(czyMomentPrzejsciaNaJedenPalec)
		{			
    		mPreviousX=e.getX();
			mPreviousY=e.getY();
			czyMomentPrzejsciaNaJedenPalec=false;
		}
        mRenderer.partial=true;
            if(e.getActionMasked()==MotionEvent.ACTION_MOVE)
            {
	            	if(e.getPointerCount() == 1 && System.currentTimeMillis()-recentClickTime>500)
	            	{
		            	czyAkcjaZPrzesunieciemPalca=true;
	                    float x = e.getX();
	                    float y = e.getY();
		                float dx = x - mPreviousX;
		                float dy = y - mPreviousY;
		                mRenderer.xCenter-=dx*mRenderer.zoom;
		                mRenderer.yCenter-=dy*mRenderer.zoom;
		                requestRender();
		                mPreviousX = x;
		                mPreviousY = y;
	            	}
	            	if(e.getPointerCount() > 1)
	            	{
		            	czyAkcjaZPrzesunieciemPalca=true;
	                	float xbis = e.getX(e.getPointerId(1));
	                	float ybis = e.getY(e.getPointerId(1));
	                	float x = e.getX(e.getPointerId(0));
	                	float y = e.getY(e.getPointerId(0));
	                	float distance1=(float) Math.sqrt((x-xbis)*(x-xbis)+(y-ybis)*(y-ybis));
	                	float distance2=(float) Math.sqrt((mPreviousX-mPreviousXbis)*(mPreviousX-mPreviousXbis)+(mPreviousY-mPreviousYbis)*(mPreviousY-mPreviousYbis));
	                	if(oldzoom!=0)
	                		mRenderer.zoom=oldzoom/(distance1/distance2);
	                	requestRender();
	            	}
            }
            if(e.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN)
            {
            	mPreviousXbis = e.getX(e.getPointerId(1));
            	mPreviousYbis = e.getY(e.getPointerId(1));
            	mPreviousX = e.getX(e.getPointerId(0));
            	mPreviousY = e.getY(e.getPointerId(0));
            	oldzoom=mRenderer.zoom;
            }
            if(e.getActionMasked()==MotionEvent.ACTION_DOWN)
            {
            	recentClickTime=System.currentTimeMillis();
                float x = e.getX();
                float y = e.getY();
                mPreviousX = x;
                mPreviousY = y;
            }
            if(e.getActionMasked()==MotionEvent.ACTION_POINTER_UP)
            {
            		czyMomentPrzejsciaNaJedenPalec=true;
            }
            if(e.getActionMasked()==MotionEvent.ACTION_UP)
            {
            	System.out.println("Koczargi up");
            		if(!czyAkcjaZPrzesunieciemPalca)
            		{
            			float zoom=mRenderer.zoom;
                        float x = e.getX();
                        float y = e.getY();
                        float x2, y2;
                        x2=mRenderer.xCenter-(mRenderer.width/2)*zoom+x*zoom;
                        y2=mRenderer.yCenter-(mRenderer.height/2)*zoom+y*zoom;
                        mRenderer.setpoint(x2, y2);
            		}
                	mRenderer.partial=false;
            		requestRender();
            		czyMomentPrzejsciaNaJedenPalec=true;
                	czyAkcjaZPrzesunieciemPalca=false;
            }
            System.out.println("Tro stop");
        return true;
    }
}
