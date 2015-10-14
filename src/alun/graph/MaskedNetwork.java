package alun.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class MaskedNetwork<V,E> implements MutableGraph<V,E>
{
	public MaskedNetwork(MutableGraph<V,E> g)
	{
		hide = g;
		show = new LinkedHashSet<V>();
		show.addAll(hide.getVertices());
	}

// Implement Graph interface

	public boolean contains(Object x)
	{
		return show.contains(x);
	}
	
	public boolean connects(Object x, Object y)
	{
		return show.contains(x) && show.contains(y) && hide.connects(x,y);
	}

	public LinkedHashSet<V> getVertices()
	{
		return new LinkedHashSet<V>(show);
	}

	public LinkedHashSet<V> getNeighbours(Object x)
	{
		LinkedHashSet<V> s = new LinkedHashSet<V>(hide.getNeighbours(x));
		if (s != null)
			s.retainAll(show);
		return s;
	}

	public LinkedHashSet<V> outNeighbours(Object x)
	{
		LinkedHashSet<V> s = new LinkedHashSet<V>(hide.outNeighbours(x));
		if (s != null)
			s.retainAll(show);
		return s;
	}

	public LinkedHashSet<V> inNeighbours(Object x)
	{
		LinkedHashSet<V> s = new LinkedHashSet<V>(hide.inNeighbours(x));
		if (s != null)
			s.retainAll(show);
		return s;
	}

	public E connection(Object x, Object y)
	{
		if (show.contains(x) && show.contains(y))
			return hide.connection(x,y);
		else
			return null;
	}

	public LinkedHashSet<E> getEdges()
	{
		LinkedHashSet<E> out = new LinkedHashSet<E>();
		for (Iterator<V> i = getVertices().iterator(); i.hasNext(); )
		{
			V x = i.next();
			for (Iterator<V> j = getNeighbours(x).iterator(); j.hasNext(); )
			{
				V y = j.next();
				out.add(connection(x,y));
			}
		}
		out.remove(null);
		return out;
	}

// Implement MutableGraph interface

	public void clear()
	{
		hide.clear();
		show.clear();
	}

	public boolean add(V v)
	{
		hide.add(v);
		show.add(v);
		return true;
	}

	public boolean remove(Object v)
	{
		hide.remove(v);
		show.remove(v);
		return true;
	}

	public boolean connect(V u, V v)
	{
		hide.connect(u,v);
		show.add(u);
		show.add(v);
		return true;
	}

	public boolean disconnect(Object u, Object v)
	{
		hide.disconnect(u,v);
		return true;
	}

	public boolean connect(V u, V v, E e)
	{
		hide.connect(u,v,e);
		show.add(u);
		show.add(v);
		return true;
	}

// Control showing and hiding

	public void hide(V x)
	{
		show.remove(x);
	}

	public void show(V x)
	{
		if (hide.contains(x))
			show.add(x);
	}

	public void hide(Collection<? extends V> x)
	{
		show.removeAll(x);
	}

	public void show(Collection<? extends V> x)
	{
		show.addAll(x);
		show.retainAll(hide.getVertices());
	}

// Private data.

	private LinkedHashSet<V> show = null;
	private MutableGraph<V,E> hide = null;
}
