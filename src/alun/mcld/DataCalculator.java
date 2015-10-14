package alun.mcld;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import alun.graph.Graph;
import alun.markov.DenseTable;
import alun.markov.Table;
import alun.markov.Variable;
import alun.util.DoubleValue;
import alun.util.IntArray;

public class DataCalculator extends LikelihoodCalculator
{
	public DataCalculator(DataHaplotypeSource s)
	{
		snpd = s;
		ldm = new LDModel(snpd.getLoci());
	}

	protected double logLikelihood(Set<Locus> s)
	{
		int[] index = new int[s.size()];
		int k = 0;
		for (Locus v : s)
			index[k++] = v.getIndex();

		Map<IntArray,DoubleValue> hh = new HashMap<IntArray,DoubleValue>();
		for (int i=0; i<snpd.nHaplotypes(); i++)
		{
			int[] xx = new int[index.length];
			for (int j=0; j<xx.length; j++)
				xx[j] = snpd.getAllele(i,index[j]);

			IntArray a = new IntArray(xx);
			DoubleValue cc = hh.get(a);
			if (cc == null)
			{
				cc = new DoubleValue(0);
				hh.put(a,cc);
			}

			cc.x += 1;
		}

		double ll = 0;
		double nn = 0;
		for (DoubleValue c : hh.values())
		{
			ll += c.x * Math.log(c.x);
			nn += c.x;
		}

		return ll - nn*Math.log(nn);
	}

	public LDModel getLDModel(Graph<Locus,Object> g, boolean sample)
	{
		Locus[] s = maxCardinality(g);
		if (s == null)
			return null;

		Vector<Table> tops = new Vector<Table>();
		Vector<Table> bots = new Vector<Table>();

		for (int i=0; i<s.length; i++)
		{
			Set<Variable> ct = new LinkedHashSet<Variable>(s[i].getInvol());
			Set<Variable> cl = new LinkedHashSet<Variable>();
			cl.add(s[i]);
			cl.addAll(ct);
			tops.add(new DenseTable(cl));
			bots.add(new DenseTable(ct));
		}

		if (sample)
			for (int j=0; j<tops.size(); j++)
				setUniform(tops.get(j),bots.get(j));

		for (int i=0; i<snpd.nHaplotypes(); i++)
		{
			for (int j=0; j<s.length; j++)
				s[j].setState(snpd.getAllele(i,s[j].getIndex()));

			for (int j=0; j<tops.size(); j++)
			{
				double x = 1;
				if (sample)
					x = -Math.log(Math.random());
				bots.get(j).increase(x);
				tops.get(j).increase(x);
			}
		}

		for (int i=0; i<s.length; i++)
			for (tops.get(i).init(); tops.get(i).next(); )
			{
				double bott = bots.get(i).getValue();
				tops.get(i).setValue(bott > 0 ? tops.get(i).getValue()/bott : 0);
			}

		ldm.clear();
		for (int i=0; i<s.length; i++)
			ldm.add(tops.get(i));
		return ldm;
	}

	public HaplotypeSource getHaplotypeSource()
	{
		return snpd;
	}

// Private data.

	private DataHaplotypeSource snpd = null;
	private LDModel ldm = null;

	private void setUniform(Table a, Table b)
	{
		a.initToZero();
		b.initToZero();

		Variable[] u = a.getVariables().toArray(new Variable[0]);
		for (int i=0; i<u.length; i++)
			u[i].init();

		for (int i=0; i>=0; )
		{
			if (!u[i].next())
			{
				i--;
			}
			else
			{
				if (++i == u.length)
				{
					double x = -Math.log(Math.random());
					a.increase(x);
					b.increase(x);
					i--;
				}
			}
		}
	}
}
