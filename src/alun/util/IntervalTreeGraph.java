package alun.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import alun.graph.Graph;

public class IntervalTreeGraph<V extends Interval,E> extends IntervalTree<V> implements Graph<V,E>
{
	public IntervalTreeGraph(double l, double h, double min)
	{
		super(l,h);
		minoverlap = min;
		everin = new LinkedHashSet<V>();
	}

	public Collection<E> getEdges()
	{
		return null;
	}

	public boolean connect(V x, V y, E e)
	{
		return false;
	}

	public E connection(Object x, Object y)
	{
		return null;
	}

	public void add(V x)
	{
		everin.add(x);
		super.add(x);
	}

	public boolean connects(Object x, Object y)
	{
		if (minoverlap == 0)
			return contains(x) && contains(y) && intersectors((V)x).contains(y);

		return contains(x) && contains(y) && ((V)x).intersection((V)y) > minoverlap;
	}

	public Set<V> inNeighbours(Object x)
	{
		return getNeighbours(x);
	}

	public Set<V> outNeighbours(Object x)
	{
		return getNeighbours(x);
	}

	public Set<V> getNeighbours(Object x)
	{
		if (minoverlap == 0)
			return !contains(x) ? null : intersectors((V)x);

		if (!contains(x))
			return null;
		Set<V> s = new LinkedHashSet<V>();
		for (V u : intersectors((V)x))
			if (((V)x).intersection(u) > minoverlap)
				s.add(u);
		return s;
	}

	public Set<V> getVertices()
	{
		LinkedHashSet<V> s = new LinkedHashSet<V>(everin);
		s.retainAll(getIntervals());
		return s;
	}

	public Graph<V,E> subGraph(double low, double high)
	{	
/*
		Set<V> s = intersectors(low,high);
		Network<V> g = new Network<V>();
		Set<V> t = new LinkedHashSet<V>();
		t.addAll(s);

		for (V v : s)
		{
			t.remove(v);
			g.add(v);
			for (V u : t)
				if (v.intersection(u) > minoverlap)
					g.connect(v,u);
		}
		
		return g;
*/
		return subGraph(low,high,minoverlap);
	}

// Private data.
	
	private double minoverlap = 0;
	private Set<V> everin = null;
}
