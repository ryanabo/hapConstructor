package alun.markov;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alun.graph.Decomposition;

public class GraphicalModel
{
	public GraphicalModel(Product p)
	{
		this(p,(Collection<Variable>)null);
	}

	public GraphicalModel(Product p, Variable keep)
	{
		this(p,Collections.singleton(keep));
	}

	public GraphicalModel(Product p, Collection<? extends Variable> keep)
	{
//System.err.print("G");
		Decomposition<Variable> d = null;
		if (keep == null)
			d = new Decomposition<Variable>(p);
		else
		{
			if (keep.contains(null))
				throw new RuntimeException("\n\tGraphicalModel: Keep variables can't include null.\n");
			d = new Decomposition<Variable>(p,keep);
		}

		List<Set<Variable>> v = new ArrayList<Set<Variable>>(d.getVertices());
		Map<Set<Variable>,Clique> h = new LinkedHashMap<Set<Variable>,Clique>();
		jtree = new Clique[v.size()];
		for (int i=v.size()-1; i>=0; i--)
		{
			jtree[i] = makeClique(v.get(i),h.get(d.next(v.get(i))),p);
			h.put(v.get(i),jtree[i]);
		}

		Set<Function> f = p.getFunctions();
		for (int i=0; i<v.size(); i++)
		{
			Set<Function> g = p.getFunctions(jtree[i].peeled());
			g.retainAll(f);
			f.removeAll(g);
			jtree[i].inputs().addAll(g);
		}
	}

	public double time()
	{
		double x = 0;
		for (int i=0; i<jtree.length; i++)
			x += jtree[i].involSize();
		return x;
	}

	public double store()
	{
		double x = 0;
		for (int i=0; i<jtree.length; i++)
			x += jtree[i].outputSize();
		return x;
	}

	public int[] nInvols()
	{
		int[] n = new int[jtree.length];
		for (int i=0; i<jtree.length; i++)
			n[i] = jtree[i].invol().size();
		return n;
	}
	
	public int[] nCuts()
	{
		int[] n = new int[jtree.length];
		for (int i=0; i<jtree.length; i++)
			n[i] = jtree[i].cutset().size();
		return n;
	}
	
	public double[] sizeInvols()
	{
		double[] n = new double[jtree.length];
		for (int i=0; i<jtree.length; i++)
			n[i] = jtree[i].involSize();
		return n;
	}
	
	public double[] sizeCuts()
	{
		double[] n = new double[jtree.length];
		for (int i=0; i<jtree.length; i++)
			n[i] = jtree[i].outputSize();
		return n;
	}
	
	public void allocateOutputTables()
	{
		for (int i=0; i<jtree.length; i++)
			jtree[i].getOutputTable().allocateSpace();
	}

	public void allocateInvolTables()
	{
		for (int i=0; i<jtree.length; i++)
			jtree[i].getOutputTable().allocateSpace();
	}

	public void clearOutputTables()
	{
		for (int i=0; i<jtree.length; i++)
			jtree[i].getOutputTable().freeSpace();
	}

	public void clearInvolTables()
	{
		for (int i=0; i<jtree.length; i++)
			jtree[i].getInvolTable().freeSpace();
	}

	public double peel()
	{
		double x = 1;
		for (int i=0; i<jtree.length && x>0 ; i++)
			x *= jtree[i].peel();
		return x;
	}

	public double logPeel()
	{
		double x = 0;
		for (int i=0; i<jtree.length; i++)
		{
			double y = jtree[i].peel();
			x += Math.log(y);
		}
		return x;
	}

	public double allocLogPeel()
	{
		double x = 0;
		for (int i=0; i<jtree.length; i++)
		{
			jtree[i].getOutputTable().allocateSpace();
			double y = jtree[i].peel();
			jtree[i].freeUp();
			x += Math.log(y);
		}
		return x;
	}

	public double log10Peel()
	{
		return logPeel()/Math.log(10);
	}

	public void collect()
	{
		for (int i=0; i<jtree.length; i++)
			jtree[i].collect();
	}

	public void distribute()
	{
		for (int i=jtree.length-1; i>=0; i--)
			jtree[i].distribute();
	}

	public void max()
	{
		for (int i=0; i<jtree.length; i++)
			jtree[i].max();
	}

	public void drop()
	{
		for (int i=jtree.length-1; i>= 0; i--)
			jtree[i].drop();
	}

	public void findMarginals()
	{
		collect();
		distribute();
	}
		
	public Table getMarginal(Variable v)
	{
		return getMarginal(Collections.singleton(v));
	}

	public Table getMarginal(Set<? extends Variable> v)
	{
		return getMarginal(v,false);
	}

	public Table getMarginal(Set<? extends Variable> v, boolean sparse)
	{
		Table t = null;
		Table u = null;

		for (int i=0; t==null && i<jtree.length; i++)
			if (jtree[i].invol().containsAll(v))
			{
				t = jtree[i].getInvolTable();
				u = jtree[i].getOutputTable();
			}
	
		if (t == null)
		{
			// Can only currently get marginals for subsets of cliques.
			return null;
		}

		Table s = TableMaker.makeTable(v,sparse);
		for (t.init(); t.next(); )
			s.increase(t.getValue()*u.getValue());
		return s;
	}

	public Table getConditional(Set<? extends Variable> u, Set<? extends Variable> v, boolean sparse)
	{
		if (!u.containsAll(v))
			return null;

		Table s = getMarginal(u,sparse);
		Table t = getMarginal(v,sparse);
		
		for (s.init(); s.next(); )
		{
			double x = t.getValue();
			if (x > 0)
				s.multiply(1/x);
		}

		return s;
	}

	public void reduceStates()
	{
		findMarginals();

		Set<Variable> s = new LinkedHashSet<Variable>();
		for (int j=0; j<jtree.length; j++)
			s.addAll(jtree[j].invol());
		
		for (Variable v : s)
			v.setStates(getMarginal(v));
	}
	
	public Clique makeClique(Set<Variable> v, Clique next, Product p)
	{
		return new BasicClique(v,next);
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		for (int i=0; i<jtree.length; i++)
			s.append(jtree[i]+"\n");
		if (jtree.length > 0)
			s.deleteCharAt(s.length()-1);
		return s.toString();
	}

	public double getMax()
	{
		return jtree[jtree.length-1].getOutputTable().getValue();
	}

	public Function getFinal()
	{
		return jtree[jtree.length-1].getInvolTable();
	}

	public Product getFinals()
	{
		Product p = new Product();
		p.add(jtree[jtree.length-1].inputs());
		for (Clique c : jtree[jtree.length-1].previous())
			p.add(c.getOutputTable());
		return p;
	}

	public Product meanGetFinals()
	{
		for (int i=0; i<jtree.length-1; i++)
		{
			jtree[i].getOutputTable().allocateSpace();
			jtree[i].peel();
			for (Clique c : jtree[i].previous())
				c.getOutputTable().freeSpace();
		}

		Product p = new Product();
		p.add(jtree[jtree.length-1].inputs());
		for (Clique c : jtree[jtree.length-1].previous())
			p.add(c.getOutputTable());
		return p;
	}

	public Set<Function> cliqueConditionals()
	{
		return cliqueConditionals(true);
	}

	public Set<Function> cliqueConditionals(boolean sparse)
	{
		Set<Function> s = new LinkedHashSet<Function>();

		for (int j=0; j<jtree.length; j++)
			s.add(TableMaker.makeTable(jtree[j].getInvolTable(),sparse));

		return s;
	}

	public double totalComplexity()
	{
		double x = 0;
		for (int i=0; i<jtree.length; i++)
			x += jtree[i].involSize();
		return x;
	}

	public double maxComplexity()
	{
		double x = 0;
		for (int i=0; i<jtree.length; i++)
			if (x < jtree[i].involSize())
				x = jtree[i].involSize();
		return x;
	}
		
/*
	public Graph<Variable> markovGraph()
	{
		Network<Variable> g = new Network<Variable>();
		for (int i=0; i<jtree.length; i++)
			for (Variable u : jtree[i].invol())
				for (Variable v : jtree[i].invol())
					if (u == v)
						g.add(u);
					else
						g.connect(u,v);
		return g;
	}
*/
		

// Private data.

	protected Clique[] jtree = null;
}
