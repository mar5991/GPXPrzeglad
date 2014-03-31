package com.example.nap;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
}