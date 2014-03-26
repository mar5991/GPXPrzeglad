package com.example.nap;
import android.graphics.Bitmap;
import android.graphics.PointF;
	public class ImageData implements Comparable <ImageData>
	{
		public PointF lewydolny;
		public String sciezka;
		public float rozdzielczoscx;
		public float rozdzielczoscy;
		public Bitmap bmp;
		public ImageData()
		{
			bmp=null;
		}
		public int compareTo(ImageData o)
		{
			if (rozdzielczoscx>o.rozdzielczoscx)
				return -1;
			if(rozdzielczoscx==o.rozdzielczoscx)
				return 0;
			return 1;
		}
	}