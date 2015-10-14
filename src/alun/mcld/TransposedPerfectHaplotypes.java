package alun.mcld;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import alun.util.InputFormatter;

public class TransposedPerfectHaplotypes extends PerfectHaplotypes
{

	public TransposedPerfectHaplotypes(InputFormatter f) throws IOException
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

		Vector<int[]> u = new Vector<int[]>();
		for (int i=0; i<v.get(0).length; i++)
			u.add(new int[v.size()]);

		for (int i=0; i<v.size(); i++)
			for (int j=0; j<v.get(i).length; j++)
				u.get(j)[i] = v.get(i)[j];

		v = u;

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
			locs.add(l);
		}
	}
}
