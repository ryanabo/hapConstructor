package alun.graph;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Vector;

public class GraphFunction
{
	public static <V,E> Vector<V> shortestPath(Graph<V,E> g, V y, V x)
	{
		if (!g.contains(x) || !g.contains(y))
			return null;

		DirectedNetwork<V,E> d = new DirectedNetwork<V,E>();
		Vector<V> v = new Vector<V>();
		v.add(x);
		for (int i=0, j=0; i<v.size() && j == 0; i++)
			for (V w : g.getNeighbours(v.get(i)))
				if (!d.contains(w))
				{
					d.connect(v.get(i),w);
					v.add(w);
					if (w == y)
						j++;
				}

		if (!d.contains(y))
			return null;

		v.clear();
		v.add(y);
		for (V i = y ; i != x;)
		{
			i = d.inNeighbours(i).iterator().next();
			v.add(i);
		}

		return v;
	}
	
	public static <V,E> void addInducedSubgraph(MutableGraph<? super V,? super E> f, Collection<V> v, Graph<V,E> g)
	{
		for (V x : v)
			if (g.contains(x))
			{
				f.add(x);
				for (V y : g.getNeighbours(x))
					if (v.contains(y) && g.connects(x,y)) // check connection in case of directed graphs
						f.connect(x,y);
			}
	}

/*
	public static <V,E>  void addInducedSubgraph(MutableGraph<? super V,? super E> f, Collection<V> v, Graph<V,E> g)
	{
		for (V x : v)
			if (g.contains(x))
			{
				f.add(x);
				for (V y : g.getNeighbours(x))
					if (v.contains(y) && g.connects(x,y)) // check connection in case of directed graphs
						f.connect(x,y,g.connection(x,y));
			}
	}
*/

	public static <V,E> void unite(MutableGraph<? super V,? super E> f, Graph<V,E> g)
	{
		addInducedSubgraph(f,g.getVertices(),g);
	}

/*
	public static <V,E> void unite(MutableGraph<? super V,? super E> f, Graph<V,E> g)
	{
		addInducedSubgraph(f,g.getVertices(),g);
	}
*/

	public static <V,E> void triangulate(MutableGraph<V,E> g)
	{
		Network<V,E> h = new Network<V,E>(g);

		while (!h.getVertices().isEmpty())
		{
			V v = null;
			double best = Double.MAX_VALUE;

			for (V u : h.getVertices())
			{
				
				double c = cost(h,u);
				if (c == 0)
				{
					v = u;
					break;
				}
				if (c < best)
				{
					v = u;
					best = c;
				}
			}

			for (V u : h.getNeighbours(v))
				if (u != v)
					for (V w : h.getNeighbours(v))
						if (w != v)
							if (u != w && !h.connects(u,w))
							{
								h.connect(u,w);
								g.connect(u,w);
							}

			h.remove(v);
		}
	}

	public static <V,E> double cost(Graph<V,E> g, V v)
	{
		//return g.getNeighbours(v).size();

		V[] n = (V[]) g.getNeighbours(v).toArray(new Object[0]);
		int count = 0;
		for (int i=0; i<n.length; i++)
			for (int j=0; j<i; j++)
				if (!g.connects(n[i],n[j]))
					count += 1;
		return count;
	}

	

	public static <V,E> void addComponent(MutableGraph<? super V, ? super E> f, V x, Graph<V,E> g)
	{
		addInducedSubgraph(f,reachables(g,x),g);
	}
	
	public static <V,E> LinkedHashSet<V> reachables(Graph<V,E> g, V x)
	{
          	LinkedHashSet<V> c = new LinkedHashSet<V>();
                Vector<V> v = new Vector<V>();

                v.add(x);
                for (int i=0; i<v.size(); i++)
                {
			for (V w: g.getNeighbours(v.get(i)))
			{
				if (!c.contains(w))
				{
					c.add(w);
					v.add(w);
				}
			}
                }

		return c;
	}

/**
 Test main.
*/
	public static void main(String[] args)
	{
		try
		{
			Network<String,Object> w = Network.read();
			System.out.println(GraphFunction.shortestPath(w,"UP","BY"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
