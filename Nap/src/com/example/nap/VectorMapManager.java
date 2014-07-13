package com.example.nap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import android.graphics.PointF;
import android.widget.TextView;

class VectorMapManager
{
	String SciezkaKatalogZPlikami;
	double x1Ramka;
	double y1Ramka;
	double x2Ramka;
	double y2Ramka;
	int aktlevel;
	TextView pre2;
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
		public int hashCode() {return 999;}
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
	class Kafelek
	{
		boolean zaladowane;
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
		class LadowanieStart extends Ladowanie
		{
			LadowanieStart(String sciezka)
			{
				super(sciezka);
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
			void ladujLiniePunkt(String[] lista)
			{
				double x=Double.parseDouble(lista[1]);
				double y=Double.parseDouble(lista[2]);
				long id=Long.parseLong(lista[3]);
				PointF pf=new PointF();
				pf.x=(float)x;
				pf.y=(float)y;
				pf=TimeConvert.WspGeoToWspEkr(pf);
				x=pf.x;
				y=pf.y;
				punkty.put(id, new Punkt(x,y));
				
			}
			void ladujLinieKrzywa(String[] lista)
			{
				long id1=Long.parseLong(lista[1]);
				long id2=Long.parseLong(lista[2]);
				int typ=Integer.parseInt(lista[3]);
				Punkt p1=punkty.get(id1);
				Punkt p2=punkty.get(id2);
				Linia ln = new Linia(typ);
				ln.x1=p1.x;
				ln.y1=p1.y;
				ln.x2=p2.x;
				ln.y2=p2.y;
				linie.put(new DoubleString(id1, id2), ln);
			}
		}
		class LadowanieStop extends Ladowanie
		{	
			LadowanieStop(String sciezka)
			{
				super(sciezka);
			}
			void ladujLiniePunkt(String[] lista)
			{
				long id=Long.parseLong(lista[3]);
				punkty.remove(id);
			}
			void ladujLinieKrzywa(String[] lista)
			{
				long id1=Long.parseLong(lista[1]);
				long id2=Long.parseLong(lista[2]);
				linie.remove(new DoubleString(id1, id2));
			}
		}
		void ladujKafelek()
		{
			if(zaladowane)
				return;
			String sciezka=SciezkaKatalogZPlikami+"/"+daneKafelka.sciezka;
			LadowanieStart lad = new LadowanieStart(sciezka);
			zaladowane=true;
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
			String sciezka=SciezkaKatalogZPlikami+"/"+daneKafelka.sciezka;
			LadowanieStop lad = new LadowanieStop(sciezka);
			zaladowane=false;
		}
		void ladujRamke()
		{
			if(daneKafelka.czyJestWRamce())
			{
				if(!zaladowane)
					ladujKafelek();
				Iterator <Kafelek> it=dzieci.iterator();
				while(it.hasNext())
				{
					Kafelek kaf=(Kafelek)it.next();
					kaf.ladujRamke();
				}
			}
			else
			{
				usunKafelek();
			}
		}
		Kafelek(KafelekDane daneKafelkaTmp, Kafelek ojciecTmp)
		{
			dzieci=new HashSet<Kafelek>();
			ojciec=ojciecTmp;
			if(ojciec!=null)
			{
				ojciec.dzieci.add(this);
			}
			daneKafelka=new KafelekDane(daneKafelkaTmp);
			zaladowane=false;
		}
	}
	Kafelek root;
	VectorMapManager(String rootDir, String rootFile, TextView pre)
	{
		pre2=pre;
		SciezkaKatalogZPlikami=rootDir;
		linie=new HashMap<DoubleString, Linia>();
		punkty=new HashMap<Long, Punkt>();
		KafelekDane tmp = new KafelekDane(rootFile);
		root = new Kafelek(tmp, null);
		root.ladujKafelek();
	}
	void rysujWszystko()
	{
		int licznik=0;
		pre2.setText(String.valueOf(aktlevel)+" "+String.valueOf(linie.size())+" "+punkty.size());
		for(DoubleString ds : linie.keySet())
		{
			Punkt p1=punkty.get(ds.id1);
			Punkt p2=punkty.get(ds.id2);
			if(p1!=null && p2!=null)
			{
				Linia ln=linie.get(ds);
				rysujlinie(ln.x1, ln.y1, ln.x2, ln.y2, licznik, "");
			}
			licznik++;
		}
	}
	void laduj(double x1, double y1, double x2, double y2, int level)
	{
		x1Ramka=x1;
		y1Ramka=y1;
		x2Ramka=x2;
		y2Ramka=y2;
		aktlevel=level;
		root.ladujRamke();
		//rysujWszystko();
	}
	void rysujlinie(double xStart, double yStart, double xStop, double yStop, int id, String name)
	{
		//ABSTRACT
	}
}