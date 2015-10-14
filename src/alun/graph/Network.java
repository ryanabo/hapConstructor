package alun.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import alun.util.MapAsCollection;

public class Network<V,E> extends AbstractGraph<V,E> implements MutableGraph<V,E>
{
	public Network(int initcap)
	{
		f = new LinkedHashMap<V,Collection<V>>(initcap);
		b = f;
	}

	public Network()
	{
		f = new LinkedHashMap<V,Collection<V>>();
		b = f;
	}

	public Network(Graph<? extends V,? extends E> g)
	{
		this();
		for (V x : g.getVertices())
		{
			add(x);
			for (V y : g.outNeighbours(x))
				connect(x,y,g.connection(x,y));
		}
	}

	public static Network<String,Object> read() throws IOException
	{
		return read(new BufferedReader(new InputStreamReader(System.in)));
	}

	public static Network<String,Object> read(BufferedReader b) throws IOException
	{
		Network<String,Object> g = new Network<String,Object>();
		for (String s = b.readLine(); s != null; s = b.readLine())
		{
			StringTokenizer t = new StringTokenizer(s);
			String v = null;
			if (t.hasMoreTokens())
				v = t.nextToken();
			g.add(v);
			while (t.hasMoreTokens())
				g.connect(v,t.nextToken());
		}
		return g;
	}

	public static Network<Integer,Object> readAsIntegers() throws IOException
	{
			return readAsIntegers(new BufferedReader(new InputStreamReader(System.in)));
	}

	public static Network<Integer,Object> readAsIntegers(BufferedReader b) throws IOException
	{
		Network<Integer,Object> g = new Network<Integer,Object>();
		for (String s = b.readLine(); s != null; s = b.readLine())
		{
			StringTokenizer t = new StringTokenizer(s);
			String v = null;
			if (t.hasMoreTokens())
				v = t.nextToken();
			g.add(new Integer(v));
			while (t.hasMoreTokens())
				g.connect(new Integer(v),new Integer(t.nextToken()));
		}
		return g;
	}

	public E connection(Object u, Object v)
	{
		MapAsCollection<V,E> m = (MapAsCollection<V,E>)f.get(u);
		if (m == null)
			return null;
		return m.get(v);
	}

	public Collection<V> getNeighbours(Object x)
	{
		return outNeighbours(x);
	}

	public boolean connect(V x, V y, E e)
	{
		if (!contains(x))
			add(x);
		if (!contains(y))
			add(y);
		
		((MapAsCollection<V,E>)f.get(x)).put(y,e);
		((MapAsCollection<V,E>)b.get(y)).put(x,e);

		return true;
	}

	public Collection<E> getEdges()
	{
		Collection<E> s = new Vector<E>();
		Set<V> todo = new LinkedHashSet<V>(getVertices());
		for (V v : getVertices())
		{
			Set<V> use = new LinkedHashSet<V>(getNeighbours(v));
			use.retainAll(todo);
			todo.remove(v);
			for (V u : use)
				s.add(connection(v,u));
		}

		return s;
	}

	public int nConnections()
	{
		int x = 0;
		for (V i : getVertices())
		{
			x += f.get(i).size();
			if (connects(i,i))
				x++;
		}
		return x/2;
	}

	public String toString()
	{
		StringBuffer b = new StringBuffer();

		for (V u : getVertices())
		{
			b.append(stripSpaces(u)+"\t");
			for (V v : getNeighbours(u))
				b.append(stripSpaces(v)+" ");
			b.append("\n");
		}

		if (b.length() > 0)
			b.deleteCharAt(b.length()-1);
		return b.toString();
	}

// Private data and methods.

	protected Collection<V> makeCollection()
	{
		return new MapAsCollection<V,E>();
	}

	protected String stripSpaces(Object s)
	{
		String[] t = s.toString().split(" ");
		StringBuffer b = new StringBuffer();
		for (int i=0; i<t.length; i++)
			b.append(t[i]);
		return b.toString();
	}
}
