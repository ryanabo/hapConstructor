package alun.util;

import java.util.LinkedHashSet;
import java.util.Set;

import alun.graph.Graph;
import alun.graph.Network;

public class IntervalTree<V extends Interval> 
{
	public IntervalTree(double l, double h)
	{
		low = l;
		high = h;
		mid = (l+h)/2;
		hits = new LinkedHashSet<V>();
		left = null;
		right = null;
		nint = 0;
	}

	public <E> Graph<V,E> subGraph(double low, double high, double minoverlap)
	{	
		Set<V> s = intersectors(low,high);
		Network<V,E> g = new Network<V,E>();
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
	}

	public int usage()
	{
		int s = 1;
		if (left != null)
			s += left.size();
		if (right != null)
			s += right.size();
		return s;
	}

	public int size()
	{
		return nint;
	}

	public void remove(V i)
	{
		nint--;
		switch(where(i))
		{
		case -1:
			if (left != null)
			{
				left.remove(i);
				if (left.size() == 0)
					left = null;
			}
			break;
		case 0:
			hits.remove(i);
			break;
		case 1:
			if (right != null)
			{
				right.remove(i);
				if (right.size() == 0)
					right = null;
			}
			break;
		}
	}

	public void add(V i)
	{
		nint++;
		switch(where(i))
		{
		case -1:
			if (left == null)
				left = new IntervalTree<V>(low,mid);
			left.add(i);
			break;
		case 0:
			hits.add(i);
			break;
		case 1:
			if (right == null)
				right = new IntervalTree<V>(mid,high);
			right.add(i);
			break;
		}
	}
	
	public Set<V> intersectors(double low, double hi)
	{
		return intersectors(new BasicInterval(low,hi));
	}

	public Set<V> intersectors(Interval i)
	{
		Set<V> s = new LinkedHashSet<V>();
		addIntersectors(i,s);
		s.remove(i);
		return s;
	}

	public boolean contains(Object x)
	{
		return hits.contains(x) || (left != null && left.contains(x)) || (right != null && right.contains(x));
	}

	public double lowerBound()
	{
		return low;
	}

	public double upperBound()
	{
		return high;
	}

	public Set<V> getIntervals()
	{
		Set<V> s = new LinkedHashSet<V>();
		s.addAll(hits);
		if (left != null) 
			s.addAll(left.getIntervals());
		if (right != null)
			s.addAll(right.getIntervals());
		return s;
	}

// Private data.
	
	private double low = 0;
	private double high = 0;
	private double mid = 0;
	private Set<V> hits = null;
	private IntervalTree<V> left = null;
	private IntervalTree<V> right = null;
	private int nint = 0;

	private void addIntersectors(Interval x, Set<V> s)
	{
		switch(where(x))
		{
		case -1: for (V mine : hits)
				if (mine.getLeft() < x.getRight())
					s.add(mine);
			if (left != null)
				left.addIntersectors(x,s);
			break;
		case 0: for (V mine : hits)
				s.add(mine);
			if (left != null)
				left.addRightEndRightOf(x.getLeft(),s);
			if (right != null)
				right.addLeftEndLeftOf(x.getRight(),s);
			break;
		case 1: for (V mine : hits)
				if (mine.getRight() > x.getLeft())
					s.add(mine);
			if (right != null)
				right.addIntersectors(x,s);
			break;
		}
	}

	private void addRightEndRightOf(double p, Set<V> s)
	{
		switch(where(p))
		{
		case -1: if (left != null)
				left.addRightEndRightOf(p,s);
		case 0: for (V mine : hits)
				s.add(mine);
			if (right != null)
				right.addAll(s);
			break;
		case 1: for (V mine : hits)
				if (mine.getRight() > p)
					s.add(mine);
			if (right != null)
				right.addRightEndRightOf(p,s);
			break;
		}
	}

	private void addLeftEndLeftOf(double p,Set<V> s)
	{
		switch(where(p))
		{
		case 1: if (right != null)
				right.addLeftEndLeftOf(p,s);
		case 0: for (V mine : hits)
				s.add(mine);
			if (left != null)
				left.addAll(s);
			break;
		case -1:for (V mine : hits)
				if (mine.getLeft() < p)
					s.add(mine);
			if (left != null)
				left.addLeftEndLeftOf(p,s);
			break;
		}
	}
		
	private void addAll(Set<V> s)
	{
		if (left != null)
				left.addAll(s);
		s.addAll(hits);
		if (right != null)
				right.addAll(s);
	}

	private int where(double p)
	{
		if (p < mid)
			return -1;
		if (p > mid)
			return 1;
		return 0;
	}

	private int where(Interval i)
	{
		if (i.getRight() < mid)
			return -1;
		if (i.getLeft() > mid)
			return 1;
		return 0;
	}
}
