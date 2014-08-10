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

import android.graphics.PointF;
import android.widget.TextView;

class VectorMapManager
{
	int WARSTWY = 16;
	String SciezkaKatalogZPlikami;
	double x1Ramka;
	double y1Ramka;
	double x2Ramka;
	double y2Ramka;
	int aktlevel;
	class KafelekDane
	{
		String sciezka;
		int level;
		double x1, y1, x2, y2;
		KafelekDane(KafelekDane orginal)
		{
			sciezka=orginal.sciezka;
			level=orginal.level;
			x1=orginal.x1;
			y1=orginal.y1;
			x2=orginal.x2;
			y2=orginal.y2;
		}
		KafelekDane(String sciezka2)
		{
			sciezka=sciezka2;
		}
		boolean czyJestWRamce()
		{
			if(level<=aktlevel)
				if(x1<=x2Ramka)
					if(x2>=x1Ramka)
						if(y1<=y2Ramka)
							if(y2>=y1Ramka)
								return true;
			return false;
		}
	}
	class Punkt
	{
		double x, y;
		Punkt(double X, double Y)
		{
			x=X;
			y=Y;
		}
	}
	class DoubleString
	{
		long id1, id2;
		DoubleString(long Id1, long Id2)
		{
			//TODO
			id1=Id1;
			id2=Id2;
		}
		public boolean equals(Object obj) {
	        if (obj == this) {
	            return true;
	        }
	        if (obj == null || obj.getClass() != this.getClass()) {
	            return false;
	        }
	        DoubleString ds=(DoubleString)obj;
	        if(id1==ds.id1 && id2==ds.id2)
	        	return true;
	        return false;
		}
		public int hashCode() {return (int)(id1+id2);}
	}
	class Linia
	{
		int typ;
		double x1, x2, y1, y2;
		Linia(int Typ)
		{
			typ=Typ;
		}
	}
	Map <Long, Punkt> punkty;
	Map <DoubleString, Linia> linie;
	void ladujdijkstra(Dijkstra dij)
	{
		Iterator <DoubleString> it=linie.keySet().iterator();
		while(it.hasNext())
		{
    		System.out.println("kania t1");
			DoubleString kat=(DoubleString)it.next();
    		System.out.println("kania t2");
    		if(punkty.get(kat.id1)!=null && punkty.get(kat.id2)!=null)
    		{
    			dij.dodajkrawedz(kat.id1, kat.id2, linie.get(kat).typ, punkty.get(kat.id1).x, punkty.get(kat.id1).y, punkty.get(kat.id2).x, punkty.get(kat.id2).y);
    		}
			System.out.println("kania t3");
		}
	}
	class Kafelek
	{
		float[][] pts;
		int[] liczniki;
		FloatBuffer[] vertexBuffer;
		boolean zaladowane;
		boolean wyswietlane;
		Kafelek ojciec;
		KafelekDane daneKafelka;
		Set <Kafelek> dzieci;
		class Ladowanie
		{
			void ladujLinieOgraniczenia(String[] lista)
			{
				daneKafelka.x1=Double.parseDouble(lista[1]);
				daneKafelka.y1=Double.parseDouble(lista[2]);
				daneKafelka.x2=Double.parseDouble(lista[3]);
				daneKafelka.y2=Double.parseDouble(lista[4]);
			}
			void ladujLinieChildren(String[] lista)
			{
				
			}
			void ladujLiniePunkt(String[] lista)
			{
				
			}
			void ladujLinieKrzywa(String[] lista)
			{
				
			}
			void ladujLinieLevel(String[] lista)
			{
				daneKafelka.level=Integer.parseInt(lista[1]);
			}
			void ladujLinie(String linia)
			{
				String[] words = linia.split(" ");  
				if(words.length>0)
				{
					if(words[0].equals("OGR"))
						ladujLinieOgraniczenia(words);
					if(words[0].equals("CZ"))
						ladujLinieChildren(words);
					if(words[0].equals("P"))
						ladujLiniePunkt(words);
					if(words[0].equals("K"))
						ladujLinieKrzywa(words);
					if(words[0].equals("LEVEL"))
						ladujLinieLevel(words);
				}
			}
			Ladowanie(String sciezka)
			{
				FileReader fx;
				try
				{
					fx = new FileReader(sciezka);
					File f;
					BufferedReader buf = new BufferedReader(fx);
					while(buf.ready())
					{
						String linia=buf.readLine();
						ladujLinie(linia);
					}
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		class LadowanieByte
		{
			void ladujLinieHeader(int level, double x1, double y1, double x2, double y2, int ks)
			{
				
			}
			void ladujLiniePunkt(RandomAccessFile in)
			{
				
			}
			void ladujLinieKrzywa(RandomAccessFile in)
			{
				
			}
			void ladujLinieChildren(String[] data)
			{
				
			}
			int little2big(int i) {
			    return((i&0xff)<<24)+((i&0xff00)<<8)+((i&0xff0000)>>8)+((i>>24)&0xff);
			}
			LadowanieByte(String sciezka)
			{
				try 
				{
		        	System.out.println("kubacki qq "+sciezka);
					RandomAccessFile in = new RandomAccessFile(sciezka, "r");
		        	System.out.println("kubacki aa "+sciezka);
					int level=in.readInt();
					int punktySize = in.readInt();
					int krzyweSize = in.readInt();
					double x1=in.readDouble();
					double y1=in.readDouble();
					double x2=in.readDouble();
					double y2=in.readDouble();
					ladujLinieHeader(level, x1, y1, x2, y2, krzyweSize);
		        	System.out.println("kubacki bb ");
					for(int i=0; i<punktySize; i++)
					{
						ladujLiniePunkt(in);
					}
					for(int i=0; i<krzyweSize; i++)
					{
						ladujLinieKrzywa(in);
					}
		        	System.out.println("kubacki cc ");
					int dzieciStringSize=in.readInt();
					byte bbt[]=new byte[dzieciStringSize];
					in.read(bbt, 0, dzieciStringSize);
					String str = new String(bbt, "UTF-8");
					String a[] = str.split("\n"); 
					for(int i=0; i<a.length; i++)
					{
						String[] words = a[i].split(" ");
						if(words.length==6)
						{
							ladujLinieChildren(words);
						}
					}
					in.close();
				}
				catch (Exception e1)
				{
					System.out.println("kubacki "+e1.getMessage());
				}
			}
		}
		class LadowanieByteStart extends LadowanieByte
		{
			void ladujLinieHeader(int level, double x1, double y1, double x2, double y2, int ks)
			{
				pts=new float[WARSTWY][ks*4];
			}
			void ladujLiniePunkt(RandomAccessFile in)
			{
				try 
				{
					double x=in.readDouble();
					double y=in.readDouble();
					long id=in.readLong();
					PointF pf=new PointF();
					pf.x=(float)x;
					pf.y=(float)y;
					pf=TimeConvert.WspGeoToWspEkr(pf);
					x=pf.x;
					y=pf.y;
					punkty.put(id, new Punkt(x,y));
				}
				catch (Exception e1)
				{
				}
			}
			void ladujLinieKrzywa(RandomAccessFile in)
			{
				try 
				{
					
					long id1=in.readLong();
					long id2=in.readLong();
					int typ=in.readInt();
					Punkt p1=punkty.get(id1);
					Punkt p2=punkty.get(id2);
					Linia ln = new Linia(typ);
					ln.x1=p1.x;
					ln.y1=p1.y;
					ln.x2=p2.x;
					int typ2=0;
					if(typ==8000000)
						typ2=1;
					if(typ==6000000)
						typ2=3;
					if(typ==7000000)
						typ2=2;
					pts[typ2][liczniki[typ2]*4]=(float)p1.x;
					pts[typ2][liczniki[typ2]*4+1]=(float)p1.y;
					pts[typ2][liczniki[typ2]*4+2]=(float)p2.x;
					pts[typ2][liczniki[typ2]*4+3]=(float)p2.y;
					liczniki[typ2]++;
					linie.put(new DoubleString(id1, id2), ln);
				}
				catch (Exception e1)
				{
				}
			}
			void ladujLinieChildren(String[] lista)
			{
			}
			LadowanieByteStart(String sciezka)
			{
				super(sciezka);
			}
		}
		class LadowanieByteStart2 extends LadowanieByte
		{
			void ladujLinieHeader(int level, double x1, double y1, double x2, double y2, int ks)
			{
				daneKafelka.level=level;
				daneKafelka.x1=x1;
				daneKafelka.y1=y1;
				daneKafelka.x2=x2;
				daneKafelka.y2=y2;
			}
			void ladujLiniePunkt(RandomAccessFile in)
			{
				try 
				{
					double x=in.readDouble();
					double y=in.readDouble();
					long id=in.readLong();
				}
				catch (Exception e1)
				{
				}
			}
			void ladujLinieKrzywa(RandomAccessFile in)
			{
				try 
				{
					long id1=in.readLong();
					long id2=in.readLong();
					int typ=in.readInt();
				}
				catch (Exception e1)
				{
				}
			}
			void ladujLinieChildren(String[] lista)
			{
				KafelekDane nowe = new KafelekDane(lista[1]);
				nowe.x1=Double.parseDouble(lista[2]);
				nowe.y1=Double.parseDouble(lista[3]);
				nowe.x2=Double.parseDouble(lista[4]);
				nowe.y2=Double.parseDouble(lista[5]);
				nowe.level=daneKafelka.level+1;
				Kafelek nowy = new Kafelek(nowe, Kafelek.this);
			}
			LadowanieByteStart2(String sciezka)
			{
				super(sciezka);
			}
		}
		class LadowanieByteStop extends LadowanieByte
		{
			void ladujLinieHeader(int level, double x1, double y1, double x2, double y2, int ks)
			{
			}
			void ladujLiniePunkt(RandomAccessFile in)
			{
				System.out.println("kubacki p0");
				try 
				{
					double x=in.readDouble();
					double y=in.readDouble();
					long id=in.readLong();
					System.out.println("kubacki p1 "+punkty.size());
					punkty.remove(id);
					System.out.println("kubacki p2 "+punkty.size());
				}
				catch (Exception e1)
				{
				}
			}
			void ladujLinieKrzywa(RandomAccessFile in)
			{
				try 
				{
					long id1=in.readLong();
					long id2=in.readLong();
					int typ=in.readInt();
					linie.remove(new DoubleString(id1, id2));
				}
				catch (Exception e1)
				{
				}
			}
			void ladujLinieChildren(String[] lista)
			{
			}
			LadowanieByteStop(String sciezka)
			{
				super(sciezka);
	        	System.out.println("kubacki yy "+sciezka);
			}
		}
		void ladujKafelek()
		{
			if(zaladowane)
				return;
			String sciezka=SciezkaKatalogZPlikami+"/"+daneKafelka.sciezka+".bi";
			LadowanieByteStart2 lad = new LadowanieByteStart2(sciezka);
			zaladowane=true;
		}
		void kafelekDoWyswietleniaDodaj()
		{
			if(wyswietlane)
				return;
			if(!zaladowane)
				return;
			for(int i=0; i<WARSTWY; i++)
				liczniki[i]=0;
			String sciezka=SciezkaKatalogZPlikami+"/"+daneKafelka.sciezka+".bi";
			LadowanieByteStart lad = new LadowanieByteStart(sciezka);
			for(int i=0; i<WARSTWY; i++)
			{
				ByteBuffer bb = ByteBuffer.allocateDirect(pts[0].length * 8);
				bb.order(ByteOrder.nativeOrder());
				vertexBuffer[i] = bb.asFloatBuffer();
				vertexBuffer[i].put(pts[i]);
				vertexBuffer[i].position(0);
			}
			pts=null;
			wyswietlane=true;
		}
		void kafelekDoWyswietleniaUsun()
		{
			if(!wyswietlane)
				return;
			String sciezka=SciezkaKatalogZPlikami+"/"+daneKafelka.sciezka+".bi";
			LadowanieByteStop lad = new LadowanieByteStop(sciezka);
			for(int i=0; i<WARSTWY; i++)
				vertexBuffer[i] = null;
			wyswietlane=false;
		}
		void usunDzieci()
		{
			Iterator <Kafelek> it=dzieci.iterator();
			while(it.hasNext())
			{
				Kafelek kaf=(Kafelek)it.next();
				kaf.usunKafelek();
			}
			dzieci.clear();
		}
		void usunKafelek()
		{
			if(!zaladowane)
				return;
			usunDzieci();
        	System.out.println("kubacki sort5");
			kafelekDoWyswietleniaUsun();
			zaladowane=false;
		}
		void ladujRamke()
		{
			if(daneKafelka.czyJestWRamce())
			{
				ladujKafelek();
				boolean czyWyswietlac = false;
				if(daneKafelka.level==aktlevel)
					czyWyswietlac=true;
				if(dzieci.size()==0)
					czyWyswietlac=true;
				if(!czyWyswietlac)
				{
		        	System.out.println("kubacki sort6");
					kafelekDoWyswietleniaUsun();
				}
				Iterator <Kafelek> it=dzieci.iterator();
				while(it.hasNext())
				{
					Kafelek kaf=(Kafelek)it.next();
					kaf.ladujRamke();
				}
				if(czyWyswietlac)
				{
					kafelekDoWyswietleniaDodaj();
				}
			}
			else
			{
				usunKafelek();
			}
		}
		Kafelek(KafelekDane daneKafelkaTmp, Kafelek ojciecTmp)
		{
			String sciezka2=SciezkaKatalogZPlikami+"/"+daneKafelkaTmp.sciezka+".bi";
			File f= new File(sciezka2);
			if(f.exists())
			{
				vertexBuffer = new FloatBuffer[WARSTWY];
				liczniki=new int[WARSTWY];
				dzieci=new HashSet<Kafelek>();
				ojciec=ojciecTmp;
				if(ojciec!=null)
				{
					ojciec.dzieci.add(this);
				}
				daneKafelka=new KafelekDane(daneKafelkaTmp);
				zaladowane=false;
				wyswietlane=false;
			}
		}
	}
	Kafelek root;
	VectorMapManager(String rootDir, String rootFile)
	{

		SciezkaKatalogZPlikami=rootDir;
		linie=new HashMap<DoubleString, Linia>();
		punkty=new HashMap<Long, Punkt>();
		KafelekDane tmp = new KafelekDane(rootFile);
		root = new Kafelek(tmp, null);
		root.ladujKafelek();
	}
	void rysujWszystko()
	{
		if(root!=null)
			rysujKafelek(root);
	}
	void rysujKafelek(Kafelek kaf)
	{
		Iterator <Kafelek> it=kaf.dzieci.iterator();
		while(it.hasNext())
		{
			Kafelek kaf2=(Kafelek)it.next();
			if(kaf2.zaladowane)
				rysujKafelek(kaf2);
		}
	}
	double distance (double x1, double y1, double x2, double y2)
	{
		return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	}
	long setpoint(double x, double y)
	{
		long val_min=-1;
		double odl_min=1000000;
		
		Iterator <Long> it=punkty.keySet().iterator();
		if(it!=null)
		{
			while(it.hasNext())
			{
				Long lt1=(Long)it.next();
				long val=lt1.longValue();
				Punkt alfa=punkty.get(val);
				if(alfa!=null)
				{
					double x1=alfa.x;
					double x2=alfa.y;
					double dist_akt=distance(x1, x2, x, y);
					if(dist_akt<odl_min)
					{
						odl_min=dist_akt;
						val_min=val;
					}
				}
			}
		}
		return val_min;
	}
	void laduj(double x1, double y1, double x2, double y2, int level)
	{
		x1Ramka=x1;
		y1Ramka=y1;
		x2Ramka=x2;
		y2Ramka=y2;
		aktlevel=level;
		root.ladujRamke();
	}
}