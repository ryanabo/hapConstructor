package alun.mcld;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import alun.graph.Decomposition;
import alun.graph.DirectedNetwork;
import alun.graph.Graph;
import alun.graph.Network;

public class JunctionTree extends Network<Set<Locus>,Set<Locus>>
{
	public JunctionTree(Graph<Locus,Object> g)
	{
		super(Decomposition.junctionTree(g));
		clique = new LinkedHashMap<Locus,Set<Locus>>();
		component = new LinkedHashMap<Set<Locus>,ComponentMarker>();
		for (Set<Locus> c : getVertices())
		{
			for (Locus l : c)
				clique.put(l,c);
			if (component.get(c) == null)
				setComponent(c,new ComponentMarker());
		}
	}

	public String toString()
	{
		return getVertices().size() + " " + getEdges().size() + " " + component.size() + " " + clique.size();
	}

	public LogLikelihood costSwitch(Locus x, Locus y, LikelihoodCalculator calc)
	{
		DirectedNetwork<Set<Locus>,Object> d = allCliques(x,y);
		Map<Set<Locus>,Set<Locus>> map = new LinkedHashMap<Set<Locus>,Set<Locus>>();
		for (Set<Locus> p : d.getVertices())
		{
			Set<Locus> q = new LinkedHashSet<Locus>(p);
			if (p.contains(x) && !p.contains(y))
			{
				q.remove(x);
				q.add(y);
			}
			if (p.contains(y) && !p.contains(x))
			{
				q.remove(y);
				q.add(x);
			}
			map.put(p,q);
		}

		LogLikelihood ll = new LogLikelihood();
		for (Set<Locus> p : d.getVertices())
		{
			ll.subtract(calc.calc(p));
			ll.add(calc.calc(map.get(p)));
			Set<Locus> q = d.next(p);
			if (q != null)
			{
				Set<Locus> r = new LinkedHashSet<Locus>(p);
				r.retainAll(q);
				ll.add(calc.calc(r));
				r.clear();
				r.addAll(map.get(p));
				r.retainAll(map.get(q));
				ll.subtract(calc.calc(r));
			}
		}

		return ll;
	}

	public void makeSwitch(Locus x, Locus y)
	{
		DirectedNetwork<Set<Locus>,Object> d = allCliques(x,y);
		Network<Set<Locus>,Object> jtc = new Network<Set<Locus>,Object>();
		Map<Set<Locus>,Set<Locus>> map = new LinkedHashMap<Set<Locus>,Set<Locus>>();
		Map<Set<Locus>,ComponentMarker> mycomp = new LinkedHashMap<Set<Locus>,ComponentMarker>();

		for (Set<Locus> p : d.getVertices())
		{
			Set<Locus> q = new LinkedHashSet<Locus>(p);
			if (p.contains(x) && !p.contains(y))
			{
				q.remove(x);
				q.add(y);
			}
			if (p.contains(y) && !p.contains(x))
			{
				q.remove(y);
				q.add(x);
			}

			map.put(p,q);
			mycomp.put(p,component.get(p));
			component.remove(p);

			jtc.add(p);
			for (Set<Locus> n : getNeighbours(p))
				if (!d.contains(n))
					jtc.connect(n,p);
		}

		for (Set<Locus> p : d.getVertices())
			remove(p);

		for (Set<Locus> p : d.getVertices())
		{
			Set<Locus> q = map.get(p);
			add(q);
			for (Set<Locus> n : jtc.getNeighbours(p))
				connect(n,q,intersection(n,q));
			for (Set<Locus> n : d.getNeighbours(p))
				connect(map.get(n),q,intersection(map.get(n),q));

			for (Locus l : q)
				clique.put(l,q);

			component.put(q,mycomp.get(p));
		}
	}

	public LogLikelihood costMove(Locus x, Locus y, Locus z, LikelihoodCalculator calc)
	{
		LogLikelihood ll = costDisconnect(x,y,calc);
		if (ll == null)
			return null;
		boolean broken = makeDisconnect(x,y,false);

		LogLikelihood lll = costConnect(x,z,calc,broken);
		if (lll == null)
			ll = null;
		else
			ll.add(lll);

		makeConnect(x,y,broken);

		return ll;
	}

	public void makeMove(Locus x, Locus y, Locus z)
	{
		//boolean resetcomps = (component.get(clique.get(y)) != component.get(clique.get(z)));
		//boolean broken = makeDisconnect(x,y,resetcomps);
		//makeConnect(x,z,broken && !resetcomps);
		//makeDisconnect(x,y);
		//makeConnect(x,z);
		boolean broken = makeDisconnect(x,y);
		makeConnect(x,z,broken);

	}

	public LogLikelihood costDisconnect(Locus x, Locus y, LikelihoodCalculator calc)
	{
		Set<Set<Locus>> h = new LinkedHashSet<Set<Locus>>(allCliques(x).getVertices());
		h.retainAll(allCliques(y).getVertices());
		if (h.size() != 1)
			 return null;

		Set<Locus> Sxy = h.iterator().next();
		Set<Locus> Sx = new LinkedHashSet<Locus>(Sxy);
		Sx.remove(y);
		Set<Locus> Sy = new LinkedHashSet<Locus>(Sxy);
		Sy.remove(x);
		Set<Locus> S = new LinkedHashSet<Locus>(Sxy);
		S.remove(y);
		S.remove(x);

		LogLikelihood ll = new LogLikelihood(0,0);
		ll.subtract(calc.calc(Sxy));
		ll.add(calc.calc(Sx));
		ll.add(calc.calc(Sy));
		ll.subtract(calc.calc(S));

		return ll;
	}

	public boolean makeDisconnect(Locus x, Locus y)
	{
		return makeDisconnect(x,y,true);
	}

/*
 Returns a boolean indicating whether the graph was split
 into smaller components.
*/
	public boolean makeDisconnect(Locus x, Locus y, boolean resetcomps)
	{
		Set<Set<Locus>> h = new LinkedHashSet<Set<Locus>>(allCliques(x).getVertices());
		h.retainAll(allCliques(y).getVertices());
		if (h.size() != 1)
			 return false;

		Set<Locus> Sxy = h.iterator().next();
		Set<Locus> Sx = new HashSet<Locus>(Sxy);
		Sx.remove(y);
		Set<Locus> Sy = new HashSet<Locus>(Sxy);
		Sy.remove(x);
		Set<Locus> S = new HashSet<Locus>(Sxy);
		S.remove(y);
		S.remove(x);

		add(Sx);
		add(Sy);
		for (Set<Locus> n : getNeighbours(Sxy))
		{
			if (n.contains(x))
				connect(n,Sx,intersection(n,Sx));
			else
				connect(n,Sy,intersection(n,Sy));
		}
		remove(Sxy);

		for (Locus l : Sx)
			clique.put(l,Sx);
		for (Locus l : Sy)
			clique.put(l,Sy);

		component.put(Sx,component.get(Sxy));
		component.put(Sy,component.get(Sxy));
		component.remove(Sxy);

		if (S.isEmpty())
		{
			if (resetcomps)
				setComponent(Sx,new ComponentMarker());
		}
		else
			connect(Sx,Sy,intersection(Sx,Sy));

		for (Set<Locus> n : getNeighbours(Sx))
			if (n.containsAll(Sx))
			{
				merge(n,Sx);
				break;
			}

		for (Set<Locus> n : getNeighbours(Sy))
			if (n.containsAll(Sy))
			{
				merge(n,Sy);
				break;
			}

		return S.isEmpty();
	}

	public LogLikelihood costConnect(Locus x, Locus y, LikelihoodCalculator calc)
	{
		return costConnect(x,y,calc,false);
	}

	public LogLikelihood costConnect(Locus x, Locus y, LikelihoodCalculator calc, boolean fullcheck)
	{
		Set<Locus> S = cliqueIntersection(x,y,fullcheck);
		if (S == null)
			return null;

		Set<Locus> Sy = new LinkedHashSet<Locus>(S);
		Sy.add(y);
		Set<Locus> Sx = new LinkedHashSet<Locus>(S);
		Sx.add(x);
		Set<Locus> Sxy = new LinkedHashSet<Locus>(S);
		Sxy.add(x);
		Sxy.add(y);

		LogLikelihood ll = new LogLikelihood(0,0);
		ll.add(calc.calc(Sxy));
		ll.add(calc.calc(S));
		ll.subtract(calc.calc(Sx));
		ll.subtract(calc.calc(Sy));
		
		return ll;
	}

	public void makeConnect(Locus x, Locus y)
	{
		makeConnect(x,y,false);
	}

	public void makeConnect(Locus x, Locus y, boolean fullcheck)
	{
		Set<Locus> S = cliqueIntersection(x,y,fullcheck);
		if (S == null)
			return;
		
		Set<Locus> Cx = clique.get(x);
		Set<Locus> Cy = clique.get(y);
		Set<Locus> Sxy = new HashSet<Locus>(S);
		Sxy.add(x);
		Sxy.add(y);

		if (component.get(Cx) != component.get(Cy))
			setComponent(Cy,component.get(Cx));
		component.put(Sxy,component.get(Cx));
		disconnect(Cx,Cy);
		connect(Cx,Sxy,intersection(Cx,Sxy));
		connect(Cy,Sxy,intersection(Cy,Sxy));
		
		if (Sxy.containsAll(Cx))
			merge(Sxy,Cx);
		if (Sxy.containsAll(Cy))
			merge(Sxy,Cy);
	}

// Private data.

	private Map<Locus,Set<Locus>> clique = null;
	private Map<Set<Locus>,ComponentMarker> component = null;

	private void setComponent(Set<Locus> a, ComponentMarker b)
	{
		LinkedList<Set<Locus>> l = new LinkedList<Set<Locus>>();
		if (component.get(a) != b)
		{
			component.put(a,b);
			l.add(a);
		}

		while (!l.isEmpty())
		{
			for (Set<Locus> n : getNeighbours(l.poll()))
				if (component.get(n) != b)
				{
					component.put(n,b);
					l.add(n);
				}
		}
	}

	private void merge(Set<Locus> a, Set<Locus> b)
	{
		for (Set<Locus> n : getNeighbours(b))
			if (n != a)
				connect(n,a,intersection(n,a));
		for (Locus l : a)
			clique.put(l,a);
		remove(b);
		component.remove(b);
	}

	private DirectedNetwork<Set<Locus>,Object> allCliques(Locus l)
	{
		DirectedNetwork<Set<Locus>,Object> d = new DirectedNetwork<Set<Locus>,Object>();
		d.add(clique.get(l));
		Vector<Set<Locus>>v = new Vector<Set<Locus>>();
		v.add(clique.get(l));
		for (int i=0; i<v.size(); i++)
			for (Set<Locus> n : getNeighbours(v.get(i)))
				if (n.contains(l) && !d.contains(n))
				{
					v.add(n);
					d.connect(n,v.get(i));
				}
		
		return d;
	}

	private DirectedNetwork<Set<Locus>,Object> allCliques(Locus x, Locus y)
	{
		DirectedNetwork<Set<Locus>,Object> d = new DirectedNetwork<Set<Locus>,Object>();
		Vector<Set<Locus>> v = new Vector<Set<Locus>>();
		v.add(clique.get(x));
		d.add(clique.get(x));

		for (int i=0 ; i<v.size(); i++)
			for (Set<Locus> n : getNeighbours(v.get(i)))
				if ((n.contains(x) || n.contains(y)) && !d.contains(n))
				{
					v.add(n);
					d.connect(n,v.get(i));
				}

		if (d.contains(clique.get(y)))
			return d;

		v.clear();
		v.add(clique.get(y));
		d.add(clique.get(y));
		for (int i=0; i<v.size(); i++)
			for (Set<Locus> n : getNeighbours(v.get(i)))
				if (n.contains(y) && !d.contains(n))
				{
					v.add(n);
					d.connect(n,v.get(i));
				}
		
		return d;
	}

	private boolean differentComponents(Set<Locus> a, Set<Locus> b)
	{
		if (component.get(a) != component.get(b))
			return true;

		Vector<Set<Locus>> v = new Vector<Set<Locus>>();
		Set<Set<Locus>> h = new LinkedHashSet<Set<Locus>>();
		v.add(a);
		h.add(b);
		for (int i=0; i<v.size(); i++)
			for (Set<Locus> c : getNeighbours(v.get(i)))
			{
				if (c == b)
					return false;
			
				if (!h.contains(c))
				{
					h.add(c);
					v.add(c);
				}
			}

		return true;
	}

	private Set<Locus> cliqueIntersection(Locus x, Locus y, boolean fullcheck)
	{
		Set<Locus> Cx = clique.get(x);
		Set<Locus> Cy = clique.get(y);
		Set<Locus> S = new LinkedHashSet<Locus>();

		if (fullcheck ? differentComponents(Cx,Cy) : component.get(Cx) != component.get(Cy))
			return S;

		if (connects(Cx,Cy))
		{
			S.addAll(Cx);
			S.retainAll(Cy);
			return S;
		}
		
		Vector<Set<Locus>> v = shortestPath(x,y);
		if (v == null)
			return null;

		Cx = v.get(0);
		Cy = v.get(v.size()-1);
		S.addAll(Cx);
		S.retainAll(Cy);

		for (int i=1 ; i < v.size(); i++)
		{
			Set<Locus> t = new LinkedHashSet<Locus>(v.get(i-1));
			t.retainAll(v.get(i));
			if (S.containsAll(t))
			{
				disconnect(v.get(i-1),v.get(i));
				connect(Cx,Cy,intersection(Cx,Cy));
				clique.put(x,Cx);
				clique.put(y,Cy);
				return S;
			}
		}
		return null;
	}

	private  Vector<Set<Locus>> shortestPath(Locus ly, Locus lx)
	{
		Set<Locus> y = null;
		Set<Locus> x = null;
		Set<Locus> s = new LinkedHashSet<Locus>();
		for (Set<Locus> cx : allCliques(lx).getVertices())
			for (Set<Locus> cy : allCliques(ly).getVertices())
			{
				Set<Locus> t = new HashSet<Locus>(cx);
				t.retainAll(cy);
				if (!s.containsAll(t))
				{
					s = t;
					x = cx;
					y = cy;
				}
			}
		
		if (s.isEmpty())
			return null;

		DirectedNetwork<Set<Locus>,Object> d = new DirectedNetwork<Set<Locus>,Object>();
		Vector<Set<Locus>> v = new Vector<Set<Locus>>();
		v.add(x);
		for (int i=0, j=0; i<v.size() && j == 0; i++)
			for (Set<Locus> w : getNeighbours(v.get(i)))
				if (!d.contains(w) && w.containsAll(s))
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
		for (Set<Locus> i = y ; i != x;)
		{
			i = d.inNeighbours(i).iterator().next();
			v.add(i);
		}

		while (v.get(1).contains(ly))
			v.removeElementAt(0);
		while (v.get(v.size()-2).contains(lx))
			v.removeElementAt(v.size()-1);

		return v;
	}

	private <T> Set<T> intersection(Set<T> a, Set<T> b)
	{
		Set<T> s = new LinkedHashSet<T>(a);
		s.retainAll(b);
		return s;
	} 
}
