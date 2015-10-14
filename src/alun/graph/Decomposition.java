package alun.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


/**
 A Decomposition is a directed graph of sets of vertices of 
 a given graph. Each set contains the vertices in a prime subgraph
 of the given graph. Each set is connected forward to one other set such that
 the intersection of the two sets is equal to the intersection of 
 the first set with all other sets later than it in the partial ordering.
 If the given graph is triangulated then the sets will contain its maximal
 cliques, and viewed as an undirected graph the Decomposition is a junction
 tree. 
 The iterator of the vertex set obtained by a call to getVertices() iterates
 over the prime subgraphs, or cliques, in an order such that running intersection 
 property holds. 
 This is an implementation of the lexicographic search method of Leimer (1992),
 Discrete Mathematics, 113, 1-25.
 In peeling terms, as per Thompson, Cannings and Skolnick (1978), iterating
 over the set of vertices of the Decomposition gives the correctly ordered 
 "invol" sets for a peeling sequence.
*/

public class Decomposition<V> extends DirectedNetwork<Set<V>,Set<V>>
{
	public DirectedNetwork<Set<V>,Set<V>> junctionTree()
	{
		DirectedNetwork<Set<V>,Set<V>> jt = new DirectedNetwork<Set<V>,Set<V>>();
		for (Set<V> u : getVertices())
		{
			jt.add(u);
			Set<V> v = next(u);
			if (v != null)
			{
				Set<V> c = new LinkedHashSet<V>(u);
				c.retainAll(v);
				jt.connect(u,v,c);
			}
		}

		return jt;
	}

	public static <T,D> boolean isTriangulated(Graph<T,D> g)
	{
		DirectedNetwork<Set<T>,Set<T>> jt = junctionTree(g);
		for (Set<T> u : jt.getVertices())
			if (!isClique(g,u))
				return false;
		return true;
	}

	public static <T,D> DirectedNetwork<Set<T>,Set<T>> junctionTree(Graph<T,D> g)
	{
		return new Decomposition<T>(g).junctionTree();
	}

	public <E> Decomposition(Graph<V,E> h, Collection<? extends V> kk)
	{
		Decomposition<V> d = new Decomposition<V>(h);

		List<Set<V>> v = new ArrayList<Set<V>>(d.getVertices());
		Set<V> keep = new LinkedHashSet<V>(kk);
		for (int i=0; i<v.size(); i++)
			if (d.next(v.get(i)) == null)
				d.connect(v.get(i),keep);

		Map<Set<V>,Set<V>> otn = new LinkedHashMap<Set<V>,Set<V>>();
		for (Set<V> x : d.getVertices())
			otn.put(x,new LinkedHashSet<V>(x));

		keep = new LinkedHashSet<V>(kk);
		for (Set<V> o : d.getVertices())
		{
			for (V k : o)
				if (keep.contains(k))
				{
					for (Set<V> oo = o; oo != null; oo = d.next(oo))
						otn.get(oo).add(k);
				}
		}

		for (Set<V> i : d.getVertices())
			add(otn.get(i));

		for (Set<V> o : d.getVertices())
		{	
			Set<V> x = d.next(o);
			if (x != null)
			{
				Set<V> c = new LinkedHashSet<V>(otn.get(o));
				c.retainAll(otn.get(x));
				connect(otn.get(o),otn.get(x),c);
			}
		}
	}

	public <E> Decomposition(Graph<V,E> g)
	{
		// First perform a lexicographic depth first search.
		Map<V,IntList> h = lexSearch(g);

		
		// Then get from the lexicographic search the sets of 
		// vertices corresponding to each vertex elimination and put these
		// sets into a directed network.
		DirectedNetwork<Set<V>,Set<V>> net = new DirectedNetwork<Set<V>,Set<V>>();
		List<V> u = new ArrayList<V>(h.keySet());

		if (u.size() == 0)
			return;

		int[] t = new int[u.size()];
		int nt = 0;
		t[nt++] = u.size()-1;
		for (int i=u.size()-2; i>=0; i--)
			if (h.get(u.get(i+1)).compareTo(h.get(u.get(i))) >= 0)
				t[nt++] = i;

		int[] f = new int[nt];
		int[] e = new int[nt];
		e[nt-1] = 0;
		f[nt-1] = t[nt-1];
		for (int i=e.length-2; i>=0; i--)
		{
			f[i] = t[i];
			e[i] = f[i+1]+1;
		}

		List<Set<V>> invol = new ArrayList<Set<V>>();
		List<Set<V>> cuts = new ArrayList<Set<V>>();
		for (int i=0; i<f.length; i++)
		{
			invol.add(new LinkedHashSet<V>());
			cuts.add(new LinkedHashSet<V>());

			int[] x = h.get(u.get(f[i])).asArray();
			for (int j=0; j<x.length; j++)
				cuts.get(i).add(u.get(x[j]));

			for (int j=e[i]; j<=f[i]; j++)
				invol.get(i).add(u.get(j));

			invol.get(i).addAll(cuts.get(i));

			net.add(invol.get(i));
			if (!cuts.get(i).isEmpty())
				for (int j=0; j<i; j++)
					if (invol.get(j).containsAll(cuts.get(i)))
					{
						net.connect(invol.get(i),invol.get(j));
						break;
					}
		}

		// This step merges the sets found above so that the merged sets
		// contain the prime subgraphs.
		for (int i=0; i<f.length; i++)
		{
			if (isClique(g,cuts.get(i)))
				continue;

			Set<V> next = net.next(invol.get(i));
			Set<V> both = new LinkedHashSet<V>();
			both.addAll(invol.get(i));
			both.addAll(next);
			net.add(both);
			
			for (Set<V> j : net.outNeighbours(next))
				net.connect(both,j);
			for (Set<V> j : net.inNeighbours(next))
				net.connect(j,both);
			for (Set<V> j : net.inNeighbours(invol.get(i)))
				net.connect(j,both);
		
			net.remove(invol.get(i));
			net.remove(next);
		}

		// The final stage is to put the sets into the directed network so that the
		// order has the running intersection property.
		// First add the sets.
		for (Set<V> cur : net.getVertices())
		{
			do
			{
				if (net.next(cur) == null)
					break;
				else
					cur = net.next(cur);
			}
			while (true);

			insertAdd(cur,net);
		}

		// The add the connections between the sets.
		for (Set<V> cur : net.getVertices())
		{
			if (net.next(cur) != null)
			{
				Set<V> c = new LinkedHashSet<V>(cur);
				c.retainAll(net.next(cur));
				connect(cur,net.next(cur),c);
			}
		}
	}

	protected void insertAdd(Set<V> x, DirectedNetwork<Set<V>,Set<V>> net)
	{
		if (!contains(x))
		{
			for (Set<V> j : net.inNeighbours(x))
				insertAdd(j,net);
			add(x);
		}
	}

	public static <T,D> boolean isClique(Graph<T,D> g, Set<T> xx)
	{
		ArrayList<T> x = new ArrayList<T>(xx);
		for (int i=0; i<x.size(); i++)
			for (int j=0; j<i; j++)
				if (!g.connects(x.get(i),x.get(j)))
					return false;
		return true;
	}

/**
 This method performs a lexicographic depth first search on the vertices of the graph
 and returns a map linking each vertex to its label.
 The order of of the vertices given by the iterator of the key set of the map 
 corresponds to the order found by the search.
*/

	protected <E> Map<V,IntList> lexSearch(Graph<V,E> g)
	{
		List<V> u = new ArrayList<V>(g.getVertices());

		Map<V,IntList> hh = new LinkedHashMap<V,IntList>();
		for (int i=0; i<u.size(); i++)
			hh.put(u.get(i),new IntList());
		Set<V> done = new LinkedHashSet<V>();

		for (int i=u.size()-1; i>0; i--)
		{
			done.add(u.get(i));
			Set<V> reached = new LinkedHashSet<V>();

			List<V> v = new ArrayList<V>();
			Map<V,IntList> h = new LinkedHashMap<V,IntList>();

			PriorityQueue<V> pq = new PriorityQueue<V>(100,new Sorter(h));
			for (V n : g.getNeighbours(u.get(i)))
			{
				if (!done.contains(n))
				{
					hh.get(n).append(i);
					reached.add(n);
					h.put(n,hh.get(n));
					pq.add(n); 
				}
			}

			while (!pq.isEmpty())
			{
				V w = pq.poll();
				for (V z : g.getNeighbours(w))
				{
					if (!done.contains(z) && !reached.contains(z))
					{
						if (h.get(w).compareTo(hh.get(z)) < 0)
						{
							hh.get(z).append(i);
							h.put(z,hh.get(z));
						}
						else
						{
							h.put(z,h.get(w));
						}
						reached.add(z);
						pq.add(z);
					}
				}
			}

			for (int j=0; j<i; j++)
				for (int k=j; k>0 && hh.get(u.get(k)).compareTo(hh.get(u.get(k-1))) < 0; k--)
				{
					V x = u.get(k);
					u.set(k,u.get(k-1));
					u.set(k-1,x);
				}
		}

		LinkedHashMap<V,IntList> hhh = new LinkedHashMap<V,IntList>();
		for (int i=0; i<u.size(); i++)
			hhh.put(u.get(i),hh.get(u.get(i)));

		return hhh;
	}

	private class Sorter implements java.util.Comparator<V>
	{
		private Map<V,IntList> h;

		public Sorter(Map<V,IntList> hh)
		{
			h = hh;
		}
		
		public int compare(V x, V y)
		{
			return h.get(x).compareTo(h.get(y));
		}
	
	//	public boolean equals(V x, V y)
	//	{
	//		return h.get(x).equals(h.get(y));
	//	}
	}

/** 
 Test main.
*/
	public static void main(String[] args)
	{
		try
		{
			Network<String,Object> nn = Network.read();
			Decomposition<String> dd = new Decomposition<String>(nn);
			//Network<Set<String>,Set<String>> jt = dd.junctionTree();
			Network<Set<String>,Set<String>> jt = new Network<Set<String>,Set<String>>(dd.junctionTree());
			System.out.println(jt);

/*
			System.out.println();
			for (Set<String> e : jt.getEdges())
				System.out.println(e);
*/
/*
			IntList a = new IntList();
			IntList b = new IntList();
			IntList c = new IntList();
			IntList d = new IntList();
			
			for (int i=0; i<8*17; i+=17)
			{
				a.append(i);
				b.append(i%5);
				if (i < 50)
					c.append(i%7);
				d.append(i%7);
			}

			//java.util.Collection s = new java.util.TreeSet();
			java.util.PriorityQueue s = new java.util.PriorityQueue();
			s.add(a);
			s.add(b);
			s.add(c);
			s.add(d);
		
			for (Object x = s.poll(); x != null; x = s.poll())
				System.out.println(x);

			System.out.println(a);
			System.out.println(b);
			System.out.println(a.compareTo(b));
*/

/*
			Integer[] x = new Integer[50];
			for (int i=0; i<x.length; i++)
				x[i] = new Integer(i);

			Network<Integer> g = new Network<Integer>();
			g.connect(x[1],x[2]);
			g.connect(x[2],x[3]);
			g.connect(x[3],x[1]);
			g.connect(x[3],x[4]);
			g.connect(x[4],x[5]);
			g.connect(x[6],x[6]);
			
			Decomposition<Integer> d = new Decomposition<Integer>(g);

			LinkedHashMap<Integer,IntList> h = d.lexSearch(g);
			for (Integer v: h.keySet())
			{
				System.out.println(v+" \t"+h.get(v));
			}

			System.out.println();

			for (Set<Integer> s: d.getVertices())
			{
				for (Integer i: s)
					System.out.print(" "+i);
				System.out.print("\t:\t");
				Set<Integer> t = d.next(s);
				if (t != null)
					for (Integer i: t)
						System.out.print(" "+i);
				System.out.println();
			}
*/
		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

class IntList implements Comparable<IntList>
{
	public IntList()
	{
		head = null;
		tail = null; 
	}

	public void append(int i)
	{
		Link l = new Link(i);

		if (head == null)
		{
			head = l;
			tail = l;
		}
		else
		{
			tail.next = l;
			tail = l;
		}
	}

	public int size()
	{
		int i=0;
		for (Link l=head; l!=null; l=l.next)
			i++;
		return i;
	}

	public int[] asArray()
	{
		int[] x = new int[size()];
		int i=0;
		for (Link l=head; l!=null; l=l.next)
			x[i++] = l.i;
		return x;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		int[] x = asArray();
		for (int i=0; i<x.length; i++)
			s.append(" "+x[i]);
		return s.toString();
	}

	public int compareTo(IntList l)
	{
		Link i = head;
		Link j = l.head;

		do
		{
			if (i == null && j == null)
				return 0;
			if (i == null)
				return -1;
			if (j == null)
				return 1;
			if (j.i < i.i)
				return 1;
			if (i.i < j.i)
				return -1;
			j = j.next;
			i = i.next;
		} while (true);
	}

	private Link head = null;
	private Link tail = null;

	private class Link
	{
		public Link(int ii)
		{
			i = ii;
		}
	
		public Link next = null;
		public int i = 0;
	}
}
