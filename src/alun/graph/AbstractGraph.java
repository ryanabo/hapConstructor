package alun.graph;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class AbstractGraph<V,E> implements MutableGraph<V,E>
{
	protected abstract Collection<V> makeCollection();

	public boolean contains(Object x)
	{
		return f.containsKey(x);
	}

	public boolean connects(Object x, Object y)
	{
		Collection<V> n = f.get(x);
		return n == null ? false : n.contains(y);
	}

	public Set<V> getVertices()
	{
		return f.keySet();
	}

	public Collection<V> outNeighbours(Object x)
	{
		return f.get(x);
	}

	public Collection<V> inNeighbours(Object x)
	{
		return b.get(x);
	}

	public void clear()
	{
		f.clear();
		b.clear();
	}

	public boolean add(V x)
	{
		if (f.get(x) == null)
		{
			f.put(x,makeCollection());
			if (b != f)
				b.put(x,makeCollection());
		}
		return true;
	}

	public boolean disconnect(Object x)
	{
		Collection<V> ns = f.get(x);
		if (ns != null)
		{
			for (V n : ns)
				if (n != x)
					b.get(n).remove(x);
			f.get(x).clear();
		}

		if (b != f)
		{
			ns = b.get(x);
			if (ns != null)
			{
				for (V n : ns)
					if (n != x)
						f.get(n).remove(x);
				b.get(x).clear();
			}
		}

		return true;
	}

	public boolean remove(Object x)
	{
		Collection<V> ns = f.get(x);
		if (ns != null)
		{
			for (V n : ns)
				if (n != x)
					b.get(n).remove(x);
			f.remove(x);
		}

		if (b != f)
		{
			ns = b.get(x);
			if (ns != null)
			{
				for (V n : ns)
					if (n != x)
						f.get(n).remove(x);
				b.remove(x);
			}
		}

		return true;
	}

	public boolean connect(V x, V y)
	{
		if (!contains(x))
			add(x);
		if (!contains(y))
			add(y);

		f.get(x).add(y);
		b.get(y).add(x);

		return true;
	}

	public boolean disconnect(Object x, Object y)
	{
		if (contains(x) && contains(y))
		{
			f.get(x).remove(y);
			b.get(y).remove(x);
		}
		return true;
	}

// Protected data.

	protected Map<V,Collection<V>> f = null;
	protected Map<V,Collection<V>> b = null;
}
