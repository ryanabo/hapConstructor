package alun.mcld;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import alun.genepi.Allele;
import alun.genepi.Error;
import alun.genepi.ErrorPrior;
import alun.markov.GraphicalModel;
import alun.markov.Product;
import alun.markov.Variable;
import alun.util.InputFormatter;
import alun.util.StringFormatter;

public class Haplotypes extends ImperfectHaplotypes
{
	public Haplotypes(InputFormatter f, double errprob) throws IOException
	{
//		super(f);

// constructor copies here.

		v = new Vector<int[]>();
		while(f.newLine())
		{
			int[] t = new int[f.itemsLeftOnLine()]; 
			for (int j=0; j<t.length; j++)
			{
				f.newToken();
				t[j] = f.getInt() - 1;
			}
			v.addElement(t);
		}

		Set<Integer>[] ss = (Set<Integer>[]) new Set[v.get(0).length];
		for (int i=0; i<ss.length; i++)
			ss[i] = new LinkedHashSet<Integer>();

		for (int i=0; i<v.size(); i++)
			for (int j=0; j<v.get(i).length; j++)
				if (v.get(i)[j] >= 0)
					ss[j].add(new Integer(v.get(i)[j]));

		locs = new Vector<Locus>();
		for (int i=0; i<ss.length; i++)
		{
			if (ss[i].size() < 2)
				locs.add(new Locus(i,2,(1.0+i)));
			else
				locs.add(new Locus(i,ss[i].size(),(1.0+i)));
		}
	
// constructor copied here.


		u = v;
		v = new Vector<int[]>();
		for (int i=0; i<u.size(); i++)
			v.add(new int[u.get(i).length]);

		prod = new Product[nLoci()];
		obs = new Vector<Observation>();
		alleles = new Vector<Allele>();
		hash = new HashMap<Variable,Variable>();

		for (int j=0; j<nLoci(); j++)
		{
			Set<Integer> s = new TreeSet<Integer>();
			for (int i=0; i<u.size(); i++)
				if (u.get(i)[j] >= 0)
					s.add(new Integer(u.get(i)[j]));
			int k = 0;
			int[] pos = new int[nAlleles(j)];
			for (Integer i : s)
				pos[k++] = i.intValue();
			int kk = 0;
			while (k < pos.length)
				pos[k++] = --kk;
	
			prod[j] = new Product();

			Error e = new Error();
			prod[j].add(new ErrorPrior(e,errprob));

			Allele a = new Allele(nAlleles(j));
			alleles.add(a);
			Observation o = new Observation(a,e,pos);
			prod[j].add(o);
			obs.add(o);

			hash.put(locs.get(j),a);
		}

		update(null);
	}

	public boolean update(LDModel m)
	{
		return update(m,true);
	}

	public boolean maximize(LDModel m)
	{
		return update(m,false);
	}

	public boolean update(LDModel m, boolean sample)
	{
		if (m == null)
			return startup(sample);

		Product q = m.replicate(hash);
		for (Variable v : m.getVariables())
			q.addProduct(prod[((Locus)v).getIndex()]);
		
		GraphicalModel g = new GraphicalModel(q);
		g.allocateOutputTables();
		g.allocateInvolTables();

		for (int i=0; i<u.size(); i++)
		{
			for (Variable var : m.getVariables())
			{
				int j = ((Locus)var).getIndex();
				obs.get(j).fix(u.get(i)[j]);
			}

			if (sample)
				g.collect();
			else
				g.max();
			g.drop();

			for (Variable var : m.getVariables())
			{
				int j = ((Locus)var).getIndex();
				v.get(i)[j] = alleles.get(j).getState();
			}
		}

		g.clearOutputTables();
		g.clearInvolTables();

		return true;
	}

	public boolean startup(boolean sample)
	{
		GraphicalModel[] g = new GraphicalModel[prod.length];
		for (int j=0; j<g.length; j++)
		{
			g[j] = new GraphicalModel(prod[j]);
			g[j].allocateOutputTables();
			g[j].allocateInvolTables();
		}

		for (int i=0; i<u.size(); i++)
		{
			for (int j=0; j<u.get(i).length; j++)
				obs.get(j).fix(u.get(i)[j]);

			for (int j=0; j<g.length; j++)
			{
				if (sample)
					g[j].collect();
				else
					g[j].max();
				g[j].drop();
			}

			for (int j=0; j<v.get(i).length; j++)
				v.get(i)[j] = alleles.get(j).getState();
		}

		for (int j=0; j<g.length; j++)
		{
			g[j].clearOutputTables();
			g[j].clearInvolTables();
		}

		return true;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		for (int i=0; i<v.size(); i++)
		{
			int[] t = v.get(i);
			for (int j=0; j<t.length; j++)
				s.append(StringFormatter.format((1+t[j]),2));
			s.append("\n");
		}

		if (v.size() > 0)
			s.deleteCharAt(s.length()-1);

		return s.toString();
	}


// Private data and methods.

	private Map<Variable,Variable> hash = null;
	private Vector<Allele> alleles = null;
	private Vector<int[]> u = null;
	private Vector<Observation> obs = null;
	private Product[] prod = null;

	public static void main(String[] args)
	{
		try
		{
			System.out.println(new Haplotypes(new InputFormatter(),0.00));
		}
		catch (Exception e)
		{
			e.printStackTrace();	
		}
	}
}
