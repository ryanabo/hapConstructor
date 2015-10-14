package alun.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MapAsCollection<K,V> implements Collection<K>
{
	public MapAsCollection()
	{
		setBackingMap(new LinkedHashMap<K,V>());
	}

	public V put(K k, V v)
	{
		return m.put(k,v);
	}

	public V get(Object k)
	{
		return m.get(k);
	}

	public Collection<V> values()
	{
		return m.values();
	}
	
	public boolean add(K x)
	{
		m.put(x,null);
		return true;
	}
	
	public boolean retainAll(Collection<?> c)
	{
		Set<K> x = new LinkedHashSet<K>(m.keySet());
		x.removeAll(c);
		for (K y : x)
			m.remove(y);
		return !x.isEmpty();
	}

	public boolean removeAll(Collection<?> c)
	{
		int s = m.size();
		for (Object y : c)
			m.remove(y);
		return m.size() != s;
	}

	public boolean addAll(Collection<? extends K> c)
	{
		for (K x : c)
			add(x);
		return true;
	}
		
	public boolean containsAll(Collection<?> c)
	{
		return m.keySet().containsAll(c);
	}

	public boolean contains(Object o)
	{
		return m.keySet().contains(o);
	}

	public boolean remove(Object x)
	{
		m.remove(x);
		return true;
	}

	public void clear()
	{
		m.clear();
	}

	public <T> T[] toArray(T[] x)
	{
		return m.keySet().toArray(x);
	}

	public Object[] toArray()
	{
		return m.keySet().toArray();
	}

	public Iterator<K> iterator()
	{
		return m.keySet().iterator();
	}

	public boolean isEmpty()
	{
		return m.keySet().isEmpty();
	}

	public int size()
	{
		return m.keySet().size();
	}

	public String toString()
	{
		return m.toString();
	}
		
// Private data and methods.

	private Map<K,V> m = null;

	protected void setBackingMap(Map<K,V> map)
	{
		m = map;
	}
}
