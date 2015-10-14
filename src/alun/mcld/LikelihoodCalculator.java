package alun.mcld;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import alun.graph.Graph;

abstract public class LikelihoodCalculator
{
	public LikelihoodCalculator()
	{
		hashcapacity = 200000;
		hash = new LinkedHashMap<Set<Locus>,LogLikelihood>(hashcapacity);
		maxstore = 100;
	}

	abstract protected double logLikelihood(Set<Locus> s);
	abstract public LDModel getLDModel(Graph<Locus,Object> g, boolean sample);
	abstract public HaplotypeSource getHaplotypeSource();

	public void setCache(int c)
	{
		maxstore = c;
	}

	public LDModel getLDModel(Graph<Locus,Object> g)
	{
		return getLDModel(g,true);
	}

	public LogLikelihood calc(Graph<Locus,Object> g)
	{
		Locus[] s = maxCardinality(g);
		if (s == null)
			return null;
		return calc(s);
	}

	public LogLikelihood calc(Locus[] s)
	{
		LogLikelihood res = new LogLikelihood(0,0);
		for (int i=0; i<s.length; i++)
		{
			s[i].getInvol().add(s[i]);
			res.add(calc(s[i].getInvol()));
			s[i].getInvol().remove(s[i]);
			res.subtract(calc(s[i].getInvol()));
		}
		return res;
	}

	public void clear()
	{
		hash.clear();
	}

	public void compress()
	{
		if (hash.size() > hashcapacity/2)
			hash.clear();
	}

	public int size()
	{
		return hash == null ? 0 : hash.size();
	}

// Protected methods.

	protected Locus[] maxCardinality(Graph<Locus,Object> g)
	{
		Locus[] v = (Locus[]) g.getVertices().toArray(new Locus[0]);
		Set<Locus>[] l = (Set<Locus>[]) new Set[v.length];
		for (int i=0; i<l.length; i++)
		{
			l[i] = new LinkedHashSet<Locus>();
			v[i].reset();
			l[0].add(v[i]);
		}

		for (int i=0, j=0; i<v.length; i++)
		{
			Locus x = l[j].iterator().next();
			l[j].remove(x);

			Locus[] n = (Locus[]) g.getNeighbours(x).toArray(new Locus[0]);
			for (int k=0; k<n.length; k++)
			{
				if (n[k].getDone())
				{
					for (Iterator<Locus> it=x.getInvol().iterator(); it.hasNext(); )
						if (!g.connects(n[k],it.next()))
							return null;
					x.getInvol().add(n[k]);
				}
				else
				{
					int cc = n[k].getCount();
					l[cc].remove(n[k]);
					cc++;
					n[k].setCount(cc);
					l[cc].add(n[k]);
				}
			}
			
			v[i] = x;
			x.setDone(true);

			if (++j == l.length)
				j--;
			for (; j>=0 && l[j].isEmpty(); j--);
		}

		return v;
	}

	protected double degreesOfFreedom(Set<Locus> s)
	{
		double df = 1;
		for (Locus v : s)
			df *= v.getNStates();
		return df - 1;
	}

	protected double squaredDistances(Set<Locus> s)
	{
		double ss = 0;
		for (Locus x : s)
			for (Locus y : s)
			{
				if (x.isLocated() && y.isLocated())
				{
					double z = x.getPosition()-y.getPosition();
					ss += z*z;
				}
			}
		return ss / 2.0;
	}

	protected LogLikelihood calc(Set<Locus> s)
	{
		LogLikelihood res = null;
		if (s.size() <= maxstore)
			res = hash.get(s);

		if (res == null)
		{
			res = new LogLikelihood(logLikelihood(s),degreesOfFreedom(s),squaredDistances(s));
			if (s.size() <= maxstore)
				hash.put(s,res);
		}

		return res;
	}

// Private data.

	private Map<Set<Locus>,LogLikelihood> hash;
	private int maxstore = 0;
	private int hashcapacity = 100000;

}
