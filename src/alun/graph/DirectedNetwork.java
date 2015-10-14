
package alun.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Vector;

public class DirectedNetwork<V,E> extends Network<V,E>
{
	public DirectedNetwork()
	{
		super();
		b = new LinkedHashMap<V,Collection<V>>();
	}

	public Collection<V> getNeighbours(Object x)
	{
		LinkedHashSet<V> s = new LinkedHashSet<V>();
		s.addAll(f.get(x));
		s.addAll(b.get(x));
		return s;
	}

        public Collection<E> getEdges()
        {
                Vector<E> s = new Vector<E>();
                for (V v : getVertices())
                        for (V u : outNeighbours(v))
                                s.add(connection(v,u));
                return s;
	}

	public V next(Object x)
	{
		Collection<V> c = f.get(x);
		if (c == null)
			return null;
		Iterator<V> i = c.iterator();
		if (i.hasNext())
			return i.next();
		return null;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		for (V v : getVertices())
		{
			s.append(v+"\t");

//			for (V u : inNeighbours(v))
//				s.append(stripSpaces(u)+" ");
//			s.append("\t\t");

			for (V u : outNeighbours(v))
				s.append(stripSpaces(u)+" ");
			s.append("\n");
		}
		if (!getVertices().isEmpty())
			s.deleteCharAt(s.length()-1);
		return s.toString();
	}
}
