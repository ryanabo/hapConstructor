package alun.mcld;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import alun.util.InputFormatter;
import alun.util.StringFormatter;

public class PerfectHaplotypes implements DataHaplotypeSource
{
	public PerfectHaplotypes()
	{
	}

	public PerfectHaplotypes(InputFormatter f) throws IOException
	{
		v = new Vector<int[]>();
		while(f.newLine())
		{
			int[] t = new int[f.itemsLeftOnLine()]; 
			for (int j=0; j<t.length; j++)
			{
				int x = f.nextInt();
				if (x < 1)
					throw new RuntimeException("\n\t\tData must be positive integers. No 0 = unknown for this application");
				t[j] = x - 1;
			}
			v.addElement(t);
		}

		Set<Integer>[] s = (Set<Integer>[]) new Set[v.get(0).length];
		for (int i=0; i<s.length; i++)
			s[i] = new LinkedHashSet<Integer>();

		for (int i=0; i<v.size(); i++)
			for (int j=0; j<v.get(i).length; j++)
				if (v.get(i)[j] >= 0)
					s[j].add(new Integer(v.get(i)[j]));

		locs = new Vector<Locus>();
		for (int i=0; i<s.length; i++)
		{
			int nstates = 1;
			for (Integer ii : s[i])
				if (nstates < ii.intValue())
					nstates = ii.intValue();
			nstates++;

			Locus l = new Locus(i,nstates,(1.0+i));
/*
			if (s[i].size() < 2)
				l = new Locus(i,2,(1.0+i));
			else
				l = new Locus(i,s[i].size(),(1.0+i));
*/

			locs.add(l);

/*
System.err.print(i+"  Locus "+l+" n seen states = "+s[i].size()+" n states set = "+l.getNStates()+"\t::\t");
for (Integer ii : s[i])
	System.err.print(ii+" ");
System.err.println();
*/
		}
	}

	public Collection<Locus> getLoci()
	{
		return locs;
/*
		Vector<Locus> l = new Vector<Locus>();
		for (int i=10; i<20; i++)
			l.add(locs.get(i));
		return l;
*/
	}

	public int nHaplotypes()
	{
		return v.size();
	}

	public int nLoci()
	{
		return locs.size();
	}

	public int nAlleles(int i)
	{
/*
		Vector<Locus> x = new Vector<Locus>(locs);
		return x.get(i).getNStates();
*/
		return locs.get(i).getNStates();
	}

	public int[] getHaplotype(int i)
	{
		return v.get(i);
	}

	public int getAllele(int i, int j)
	{
		return v.get(i)[j];
	}

	public boolean update(LDModel m)
	{
		return false;
	}

	public boolean maximize(LDModel m)
	{
		return false;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		for (int i=0; i<v.size(); i++)
		{
			int[] t = v.get(i);
			for (int j=0; j<t.length; j++)
			//	s.append(StringFormatter.format((1+t[j]),2));
				s.append(StringFormatter.format(t[j],2));
			s.append("\n");
		}

		if (v.size() > 0)
			s.deleteCharAt(s.length()-1);

		return s.toString();
	}

// Private data and methods.

	protected Vector<Locus> locs = null;
	protected Vector<int[]> v = null;

/** 
 Test main.
*/
	public static void main(String[] args)
	{
		try
		{
			System.out.println(new PerfectHaplotypes(new InputFormatter()));
		}
		catch (Exception e)
		{
			e.printStackTrace();	
		}
	}
}
