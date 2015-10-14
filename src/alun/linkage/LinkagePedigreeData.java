package alun.linkage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;

import alun.genio.Pedigree;
import alun.util.Triplet;

/**
 A structure representing the data from a linkage .ped file.
*/
public class LinkagePedigreeData implements LinkConstants
{
	public LinkagePedigreeData()
	{
	}

	public LinkagePedigreeData(LinkageFormatter b, LinkageParameterData par, int format) throws IOException
	{
		Vector<LinkageIndividual> v = new Vector<LinkageIndividual>();
		for (; b.newLine(); )
			v.addElement(new LinkageIndividual(b,par,format));
		set(v);
	}

	public LinkagePedigreeData(LinkageFormatter b, LinkageParameterData par, boolean premake) throws IOException
	{
		this(b,par,(premake ? PREMAKE : STANDARD));
	}

	public LinkagePedigreeData(LinkageFormatter b, LinkageParameterData par) throws IOException
	{
		this(b,par,false);
	}

	public LinkagePedigreeData(LinkagePedigreeData p, int[] x)
	{
		Vector<LinkageIndividual> v = new Vector<LinkageIndividual>();
		
		LinkageIndividual[] ind = p.getIndividuals();
		for (int i=0; i<ind.length; i++)
			v.add(new LinkageIndividual(ind[i],x));

		set(v);
	}

	public LinkagePedigreeData(Collection<LinkageIndividual> v)
	{
		set(v);
	}

	public void set(Collection<LinkageIndividual> v)
	{
		Map<String,LinkageIndividual> h = new LinkedHashMap<String,LinkageIndividual>();

		for (LinkageIndividual cur : v)
		{
			String s = cur.pedid+"::"+cur.id;
			if (h.get(s) != null)
			{
				System.err.println("Warning: Multiple input lines for "+cur.pedid+" "+cur.id);
				System.err.println("Ignoring all but the first.");
			}
			else 
				h.put(s,cur);
		}
		
		Collection<Triplet> t = new LinkedHashSet<Triplet>();
		for (LinkageIndividual cur : v)
		{
			LinkageIndividual pa = null;
			LinkageIndividual ma = null;

			if (cur.paid != 0)
			{
				String s = cur.pedid+"::"+cur.paid;
				pa = h.get(s);
				if (pa == null)
				{
					System.err.println("Warning: Input line for father of "+cur.uniqueName()+" is missing: "+cur.paid);
					System.err.println("Adding a new input line for "+cur.paid);
					pa = new LinkageIndividual(cur,cur.paid);
					pa.sex = MALE;
					h.put(s,pa);
				}
				if (pa.sex != MALE)
				{
					//System.err.println("Warning: Father of "+cur.uniqueName()+" is not listed as male: "+pa.id+" "+pa.sex);
					//System.err.println("Setting father's sex to male");
					pa.sex = MALE;
				}
			}

			if (cur.maid != 0)
			{
				String s = cur.pedid+"::"+cur.maid;
				ma = h.get(s);
				if (ma == null)
				{
					System.err.println("Warning: Input line for mother of "+cur.uniqueName()+" is missing: "+cur.maid);
					System.err.println("Adding a new input line for "+cur.maid);
					ma = new LinkageIndividual(cur,cur.maid);
					ma.sex = FEMALE;
					h.put(s,ma);
				}
				if (ma.sex != FEMALE)
				{
					//System.err.println("Warning: Mother of "+cur.uniqueName()+" is not listed as female: "+ma.id+" "+ma.sex);
					//System.err.println("Setting mother's sex to female");
					ma.sex = FEMALE;
				}
			}

			t.add(new Triplet(cur,pa,ma));
		}

		ped = new Pedigree();
		ped.addTriplets(t);

		Collection<LinkageIndividual> u = h.values();
		ind = (LinkageIndividual[])u.toArray(new LinkageIndividual[u.size()]);
		for (int i=0; i<ind.length; i++)
			ind[i].index = i;
	}

	public LinkageIndividual pa(LinkageIndividual x)
	{
		for (int i=0; i<ind.length; i++)
			if (ind[i].pedid == x.pedid && ind[i].id == x.paid)
				return ind[i];
		return null;
	}

	public LinkageIndividual ma(LinkageIndividual x)
	{
		for (int i=0; i<ind.length; i++)
			if (ind[i].pedid == x.pedid && ind[i].id == x.maid)
				return ind[i];
		return null;
	}
	
/**
 Returns the array of individual data structures.
*/
	public LinkageIndividual[] getIndividuals()
	{
		return ind;
	}

	public int nIndividuals()
	{
		return ind.length;
	}

	public int firstPedid()
	{
		if (ind == null || ind.length == 0)
			return -1;
		else
			return ind[0].pedid;
	}

/**
 Returns a string representing the data for the given pedigrees in
 the same format as it was read in from the linkage .ped file.
*/
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		if (outputpremake)
			for (int i=0; i<ind.length; i++)
				s.append(ind[i].shortString()+"\n");
		else 
			for (int i=0; i<ind.length; i++)
				s.append(ind[i].longString()+"\n");
	
		if (ind.length > 0)
			s.deleteCharAt(s.length()-1);
		return s.toString();
	}

	public void writeTo(PrintStream s)
	{
		writeTo(new PrintWriter(s));
	}

	public void writeTo(PrintWriter p)
	{
		if (outputpremake)
		{
			for (int i=0; i<ind.length; i++)
				if (i == ind.length-1)
					p.print(ind[i].shortString());
				else
					p.println(ind[i].shortString());
		}
		else 
		{
			for (int i=0; i<ind.length; i++)
			{
				ind[i].writeTo(p);
				if (i != ind.length-1)
					p.println();
			}
		}

		p.flush();
	}

/**
 Creates a checked Pedigree structure from the list of individuals.
*/
	public Pedigree getPedigree()
	{
		return ped;
	}

	public LinkagePedigreeData[] splitByPedigree()
	{
		Map<Integer,Vector<LinkageIndividual>> h = new LinkedHashMap<Integer,Vector<LinkageIndividual>>();
		for (int i=0; i<ind.length; i++)
		{
			Integer x = new Integer(ind[i].pedid);
			Vector<LinkageIndividual> v = h.get(x);
			if (v == null)
			{
				v = new Vector<LinkageIndividual>();
				h.put(x,v);
			}
			v.add(ind[i]);
		}

		Vector<LinkageIndividual>[] u = (Vector<LinkageIndividual>[]) h.values().toArray(new Vector[0]);
		LinkagePedigreeData[] lpd = new LinkagePedigreeData[u.length];
		for (int i=0; i<lpd.length; i++)
			lpd[i] = new LinkagePedigreeData(u[i]);

		return lpd;
	}

// Private data.

	private LinkageIndividual[] ind = null;
	private Pedigree ped = null;
	private boolean outputpremake = false;

/** 
 Main reads in a linkage parameter file and a linkage pedigree file which is
 in short form and output the linkage pedigree file in long form.
*/

	public static void main(String[] args)
	{
		try
		{
			boolean premake = false;
			
			switch(args.length)
			{
			case 1: if (args[0].equals("-s"))
					premake = true;
				break;
			}

			LinkageFormatter f = new LinkageFormatter(new BufferedReader(new InputStreamReader(System.in)),"Ped file");
			LinkagePedigreeData ped = new LinkagePedigreeData(f,null,premake);
			System.out.println(ped);
		}
		catch (Exception e)
		{
			System.err.println("Caught in LinkagePedigreeData:main()");
			e.printStackTrace();
		}
	}
}
