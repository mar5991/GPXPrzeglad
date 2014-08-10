package com.example.nap;
import android.graphics.Bitmap;
abstract class RasterLayer
{
	private MyGLSurfaceView viewForUpdate;
	public int szer;
	public int wys;
	void update()
	{
		viewForUpdate.update();
	}
	abstract public Bitmap repaint(float x1, float y1, float x2, float y2, float zoom);
	public RasterLayer(MyGLSurfaceView surView) 
	{
		viewForUpdate=surView;
		szer=surView.szerokosc();
		wys=surView.wysokosc();
        System.out.println("Tro 3 "+szer+" "+wys);
	}
}