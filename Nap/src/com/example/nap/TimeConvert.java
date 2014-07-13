package com.example.nap;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.graphics.Matrix;
import android.graphics.PointF;

public class TimeConvert
{
	public static String timetxt(long alfa)
	{
		Date date = new Date(alfa);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatter2 = new SimpleDateFormat("HH:mm:ssZ");
		String dateFormatted = formatter.format(date)+"T"+formatter2.format(date);
		return dateFormatted;
	}
	public static String timetxt2(long alfa)
	{
		Date date = new Date(alfa);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formatter2 = new SimpleDateFormat("HH-mm-ss");
		String dateFormatted = formatter.format(date)+"T"+formatter2.format(date);
		return dateFormatted;
	}
	public static String timedifftxt(long alfa)
	{
		alfa=alfa/1000;
		int sec=(int)alfa%60;
		alfa/=60;
		int min=(int)alfa%60;
		alfa/=60;
		String Dsec=String.valueOf(sec);
		if(sec<10)
			Dsec="0"+Dsec;
		String Dmin=String.valueOf(min);
		if(min<10)
			Dmin="0"+Dmin;
		return String.valueOf(alfa)+":"+Dmin+":"+Dsec;
	}
	public static PointF transform(PointF old, Matrix mx)
	{
		float[] tab=new float[3];
		tab[0]=old.x;
		tab[1]=old.y;
		mx.mapPoints(tab);
		return new PointF(tab[0], tab[1]);
	}
	public static boolean czy_pokrywajo(PointF lewydolny1, PointF prawygorny1, PointF lewydolny2, PointF prawygorny2)
	{
		if(lewydolny1.x<prawygorny2.x && lewydolny2.x<prawygorny1.x)
			if(lewydolny1.y<prawygorny2.y && lewydolny2.y<prawygorny1.y)
				return true;
		return false;
	}
	public static float min(float alfa, float beta)
	{
		if(alfa>beta)
			return beta;
		return alfa;
	}
	public static float max(float alfa, float beta)
	{
		if(alfa<beta)
			return beta;
		return alfa;
	}
	public static PointF WspGeoToWspEkr(PointF old)
	{
		return new PointF((float)((old.x/360.0*1000.0)+500), (float) (500.0-Math.log(Math.tan((Math.PI/4.0)+((old.y/180.0*Math.PI)/2.0)))/Math.PI*500.0 ));
	}
	public static PointF WspEkrtoWspGeo(PointF old)
	{
		return new PointF((float)((old.x-500)*360.0/1000.0), (float) ((2*Math.atan(Math.exp((500.0-old.y)/500.0*Math.PI))-Math.PI/2.0)/Math.PI*180.0));
	}
	public static float distance(PointF first, PointF second)
	{
		return (float) Math.sqrt((second.x-first.x)*(second.x-first.x)+(second.y-first.y)*(second.y-first.y));
	}
	public static double kierunek(PointF first, PointF second)
	{
		double deltaX=second.x-first.x;
		double deltaY=second.y-first.y;
		return Math.atan2(deltaY, deltaX)*180/Math.PI;
	}
}