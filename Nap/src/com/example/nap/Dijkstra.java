package com.example.nap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.*;
class Dijkstra
{
	Map <Long, Integer> refs;
	ArrayList <Wierzcholek> data;
	class Linka
	{
		int punkt1;
		int punkt2;
		int time;
		void selfAdd()
		{
			data.get(punkt1).linki.add(this);
		}
	}
	class Wierzcholek implements Comparable<Wierzcholek>
	{
		int id;
		long oldid;
		int distance;
		int poprzedni;
		Vector <Linka> linki;
		Wierzcholek(int Id, long Oldid)
		{
			id=Id;
			poprzedni=-1;
			distance=1000000000;
			oldid=Oldid;
			linki=new Vector<Linka>();
		}
		public int compareTo(Wierzcholek other)
		{
			if(distance<other.distance)
				return -1;
			if(distance>other.distance)
				return 1;
			return 0;
		}
	}
	double distance (double x1, double y1, double x2, double y2)
	{
		return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	}
	void dodajkrawedz(long start, long stop, int typ, double x1, double y1, double x2, double y2)
	{
		System.out.println("Kania ya");
		if(refs.get(start)==null)
		{
			refs.put(start, data.size());
			Wierzcholek nowa=new Wierzcholek(data.size(), start);
			data.add(nowa);
		}
		if(refs.get(stop)==null)
		{
			refs.put(stop, data.size());
			Wierzcholek nowa=new Wierzcholek(data.size(), stop);
			data.add(nowa);
		}
		double dist=distance(x1,y1,x2,y2);
		dist*=100000;
		Linka nowa=new Linka();
		nowa.punkt1=refs.get(start);
		nowa.punkt2=refs.get(stop);
		nowa.time=(int)dist;
		nowa.selfAdd();
		Linka nowa2=new Linka();
		nowa2.punkt1=refs.get(stop);
		nowa2.punkt2=refs.get(start);
		nowa2.time=(int)dist;
		nowa2.selfAdd();
		System.out.println("Kania yb");
	}
	Dijkstra()
	{
        refs=new HashMap <Long, Integer> ();
		data=new ArrayList <Wierzcholek>();
	}
	void odwiedz(PriorityQueue<Wierzcholek> vertexQueue, Set <Integer> odwiedzone)
	{
		System.out.println("Kania y11");
		Wierzcholek w = vertexQueue.poll();
		int akt=w.id;
		if(odwiedzone.contains(akt))
			return;
		int czasakt=w.distance;
		System.out.println("Kania p "+czasakt);
		odwiedzone.add(akt);
		vertexQueue.remove(w);
		int s1=data.get(akt).linki.size();
		System.out.println("Kania y12");
		for(int i=0; i<s1; i++)
		{
			System.out.println("Kania y13");
			int nowy=data.get(akt).linki.get(i).punkt2;
			int nowytime=data.get(akt).linki.get(i).time+czasakt;
			if(!odwiedzone.contains(nowy))
			{
				if(data.get(nowy).distance>nowytime)
				{
					data.get(nowy).distance=nowytime;
					System.out.println(nowy+" Kania b "+data.get(nowy).distance);
					data.get(nowy).poprzedni=akt;
					vertexQueue.add(data.get(nowy));
				}
			}
		}
		System.out.println("Kania y14");
	}
	Vector <Long> wyniki(long alfa, long beta)
	{
		System.out.println("dijkstra start");
		if(refs.get(alfa)==null || refs.get(beta)==null)
			return null;
		int start=refs.get(alfa);
		int stop=refs.get(beta);
		PriorityQueue<Wierzcholek> vertexQueue = new PriorityQueue<Wierzcholek>();
		Set <Integer> odwiedzone = new HashSet<Integer>();
		vertexQueue.add(data.get(start));
		data.get(start).distance=0;
		System.out.println("Kania y1");
		boolean okok=true;
		while(!vertexQueue.isEmpty() && okok)
		{
			odwiedz(vertexQueue, odwiedzone);
			if(odwiedzone.contains(stop))
				okok=false;
		}
		System.out.println("Kania y2");
		Vector <Long> rel=new Vector<Long>();
		while(stop>=0)
		{
			System.out.println("Kania z "+stop+" "+" "+data.get(stop).distance+" "+data.get(stop).poprzedni);
			rel.add(data.get(stop).oldid);
			stop=data.get(stop).poprzedni;
		}
		System.out.println("dijkstra stop");
		return rel;
	}
}