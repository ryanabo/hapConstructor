package alun.markov;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import alun.graph.Graph;
import alun.graph.Network;
import alun.util.InputFormatter;

public class Product implements Graph<Variable,Object> 
{
	public Product()
	{
		hv = new LinkedHashMap<Variable, Set<Function>>();
		hf = new LinkedHashMap<Function, Set<Variable>>();
		fills = new Network<Variable,Object>();
	}

	public Product(Collection<Function> f, Collection<Variable> v)
	{
		this(f);
		Set<Variable> u = new LinkedHashSet<Variable>(getVariables());
		u.removeAll(v);
		removeVariables(u);
	}

	public Product(Collection<Function> fs)
	{
		this();
		add(fs);
	}

	public Product(InputFormatter f) throws IOException
	{
		this();

		Vector<Variable> list = new Vector<Variable>();
		f.newLine();
		while (f.newToken())
		{
			Variable v = new BasicVariable(f.getInt());
			list.add(v);
			hv.put(v,null);
		}

		while (f.newLine())
		{
			Set<Variable> v = new LinkedHashSet<Variable>();
			while (f.newToken())
				v.add(list.get(f.getInt()));
			f.newLine();

			Table t = new DenseTable(v);
			Variable[] u = (Variable[]) t.getVariables().toArray(new Variable[0]);
			if (u.length == 0)
				t.setValue(f.nextDouble());
			else
			{
				for (int i=0; i<u.length; i++)
					u[i].init();
				for (int i=0; i>=0; )
				{
					if (!u[i].next())
						i--;
					else
					{
						if (++i == u.length)
						{
							t.setValue(f.nextDouble());
							i--;
						}
					}
				}
			}

			add(t);
		}
	}

	public void clear()
	{
		hv.clear();
		hf.clear();
		fills.clear();
	}

	public boolean contains(Object v)
	{
		return hv.get(v) != null;
	}

	public boolean connects(Object u, Object v)
	{
		return contains(u) && contains(v) && (getVariables(getFunctions((Variable)u)).contains(v) || fills.connects(u,v));
	}

	public Set<Variable> getNeighbours(Object v)
	{
		Set<Function> f = hv.get(v);
		if (f == null)
			return null;
	
		Set<Variable> n = getVariables(f);
		n.remove(v);
		Collection<Variable> fl = fills.getNeighbours(v);
		if (fl != null)
			n.addAll(fl);
		return n;
	}

	public Set<Variable> outNeighbours(Object v)
	{
		return getNeighbours(v);
	}

	public Set<Variable> inNeighbours(Object v)
	{
		return getNeighbours(v);
	}

	public Set<Variable> getVertices()
	{
		Set<Variable> s = new LinkedHashSet<Variable>(hv.keySet());
		return s;
	}

	public Object connection(Object a, Object b)
	{
		return null;
	}

	public Collection<Object> getEdges()
	{
		return null;
	}

	public String toString()
	{
		return toString(0);
	}

	public String toString(boolean asgraph)
	{
		return toString(asgraph ? 1 : 0);
	}

	public String toString(int option)
	{
		StringBuffer s = new StringBuffer();

		switch(option)
		{
		case 2: Map<Variable,Integer> index = new LinkedHashMap<Variable,Integer>();
			int k = 0;
			for (Variable v : hv.keySet())
			{
				s.append(" "+v.getNStates());
				index.put(v,new Integer(k++));
			}
			for (Function t: getFunctions())
			{
				s.append("\n");
				Variable[] u = (Variable[])t.getVariables().toArray(new Variable[0]);
				
				for (int i=0; i<u.length; i++)
					s.append(" "+index.get(u[i]).intValue());
				s.append("\n");

				if (u.length == 0)
					s.append(" "+t.getValue());
				else
				{
					for (int i=0; i<u.length; i++)
						u[i].init();
					for (int i=0; i>=0; )
					{
						if (!u[i].next())
							i--;
						else
						{
							if (++i == u.length)
							{
								s.append(" "+t.getValue());
								i--;
							}
						}
					}
				}
			}

			return s.toString();

		case 1: for (Variable v : hv.keySet())
			{
				s.append(v+"\t");
				for (Variable u : getNeighbours(v))
					s.append(u+" ");
				if (s.length() > 0)
					s.deleteCharAt(s.length()-1);
				s.append("\n");
			}
			if (s.length() > 0)
				s.deleteCharAt(s.length()-1);
			return s.toString();

		case 0:
		default: s.append("PROD\n");
			for (Function i : getFunctions())
				s.append("\t"+i+"\n");
			if (!getFunctions().isEmpty())
				s.deleteCharAt(s.length()-1);
			return s.toString();
		}
	}

	public double logNStates()
	{
		double x = 0;
		for (Variable v : hv.keySet())
			x += Math.log(v.getNStates());
		return x;
	}

	public void add(Function f)
	{
		Set<Variable> v = hf.get(f);
		if (v == null)
		{
			v = new LinkedHashSet<Variable>();
			hf.put(f,v);
		}
		v.addAll(f.getVariables());

		for (Variable vv : v)
		{
			Set<Function> vvf = hv.get(vv);
			if (vvf == null)
			{
				vvf = new LinkedHashSet<Function>();
				hv.put(vv,vvf);
			}
			vvf.add(f);
		}
	}

	public void add(Collection<? extends Function> f)
	{
		for (Function i : f)
			add(i);
	}

	public void addProduct(Product p)
	{
		add(p.getFunctions());
	}

	public void remove(Function f)
	{
		for (Variable i : getVariables(f))
			hv.get(i).remove(f);
		hf.remove(f);
	}

	public void remove(Collection<Function> f)
	{
		for (Function i : f)
			remove(i);
	}

	public void removeProduct(Product p)
	{
		remove(p.getFunctions());
	}

	public void removeVariable(Variable v)
	{
		for (Function i : getFunctions(v))
			hf.get(i).remove(v);
		hv.remove(v);
		fills.remove(v);
	}

	public void removeVariables(Collection<? extends Variable> v)
	{
		for (Variable i : v)
			removeVariable(i);
	}

	public Set<Variable> getVariables()
	{
		return new LinkedHashSet<Variable>(hv.keySet());
	}

	public Set<Variable> getVariables(Function f)
	{
		return new LinkedHashSet<Variable>(hf.get(f));
	}

	public Set<Variable> getVariables(Collection<Function> f)
	{
		Set<Variable> s = new LinkedHashSet<Variable>();
		for (Function i : f)
			s.addAll(hf.get(i));
		return s;
	}

	public Set<Function> getFunctions()
	{
		return new LinkedHashSet<Function>(hf.keySet());
	}

	public Set<Function> getFunctions(Variable v)
	{
		return new LinkedHashSet<Function>(hv.get(v));
	}

	public Set<Function> getFunctions(Collection<Variable> v)
	{
		Set<Function> s = new LinkedHashSet<Function>();
		for (Variable i : v)
		{
			Set<Function> f = hv.get(i);
			if (f != null)
				s.addAll(f);
		}
		return s;
	}

	public Set<Function> getFunctionsOnly(Variable v)
	{
		return getFunctions(Collections.singleton(v));
	}

	public Set<Function> getFunctionsOnly(Collection<? extends Variable> v)
	{
		Set<Function> s = new LinkedHashSet<Function>();
		for (Function f : getFunctions())
			if (v.containsAll(f.getVariables()))
				s.add(f);
		return s;
	}

	public double getValue()
	{
		double x = 1;
		for (Function i : hf.keySet())
			x *= i.getValue();
		return x;
	}

	public double getLogValue()
	{
		double x = 0;
		for (Function i : hf.keySet())
			x += Math.log(i.getValue());
		return x;
	}

	public Product subProduct(Collection<Variable> v)
	{
		Product p = new Product();
		p.add(getFunctions(v));
		Collection<Variable> u = p.getVariables();
		u.removeAll(v);
		p.removeVariables(u);
		return p;
	}

	public void fillIn(Variable u, Variable v)
	{
		if (contains(u) && contains(v))
			fills.connect(u,v);
	}

	public void triangulate()
	{
		Network<Variable,Object> g = new Network<Variable,Object>(this);
		Set<Variable> peel = new LinkedHashSet<Variable>(g.getVertices());
		tripeel(g,peel);
	}

	public void triangulate(Collection<Variable> keep)
	{
		Network<Variable,Object> g = new Network<Variable,Object>(this);
		Set<Variable> peel = new LinkedHashSet<Variable>(g.getVertices());
		peel.removeAll(keep);
		tripeel(g,peel);
	}

	public void triangulate(Collection<Variable>[] part)
	{
		Network<Variable,Object> g = new Network<Variable,Object>(this);
		for (int i=0; i<part.length; i++)
		{
			Set<Variable> peel = new LinkedHashSet<Variable>(part[i]);
			peel.retainAll(g.getVertices());
			tripeel(g,peel);
		}
	}

	public void clearTriangulation()
	{
		fills.clear();
	}

// Private data.

	private Map<Variable, Set<Function>> hv = null;
	private Map<Function, Set<Variable>> hf = null;
	private Network<Variable,Object> fills = null;

	private void tripeel(Network<Variable,Object> g, Set<Variable> peel)
	{
		while (!peel.isEmpty())
		{
			Variable u = cheapest(g,peel);
			peel.remove(u);

			Variable[] n = (Variable[]) g.getNeighbours(u).toArray(new Variable[0]);
			g.remove(u);

			for (int i=0; i<n.length; i++)
				for (int j=i+1; j<n.length; j++)
					if (!connects(n[i],n[j]))
					{
						fillIn(n[i],n[j]);
						g.connect(n[i],n[j]);
					}
		}
	}

	private Variable cheapest(Network<? extends Variable,? extends Object> g, Collection<? extends Variable> ppp)
	{
		Collection<? extends Variable> peel = ppp;
		
		double best = Double.MAX_VALUE;
		Variable u = null;
		for (Variable w : peel)
		{
			int ff = nFills(g,w);
			if (ff == 0)
				return w;

			if (ff < best)
			{
				best = ff;
				u = w;
			}
		}
		return u;
	}

	private  int nFills(Network<? extends Variable,? extends Object> g, Variable v)
	{
		Variable[] n = (Variable[]) g.getNeighbours(v).toArray(new Variable[0]);
		int count = 0;
		for (int i=0; i<n.length; i++)
			for (int j=0; j<i; j++)
				if (!g.connects(n[i],n[j]))
					count += 1;
		return count;
	}

        public static void main(String[] args)
        {
                try
                {
			Product p = new Product(new InputFormatter());
			System.out.println(p.toString(2));
                }
                catch (Exception e)
                {
                        System.err.println("Caught in Product.main()");
                        e.printStackTrace();
                }
        }
}
