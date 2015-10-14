package edu.utah.med.genepi.ped;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import alun.genio.Family;
import alun.graph.Network;
import edu.utah.med.genepi.util.Trio;

public class Pedigree2 {

	private Network<Object,Set<Object>> g = null;
	private Map<Object,Trio> h = null;
	private Set<Object> males = null;
	private Set<Object> females = null;
	private String pedid = null;
	private int pedIndex = -1;
	
	//----------------------------------------------------------------------------
	public Pedigree2( String pid, int pIndex )
	{
		pedid = pid;
		g = new Network<Object,Set<Object>>();
		h = new LinkedHashMap<Object,Trio>();
		males = new LinkedHashSet<Object>();
		females = new LinkedHashSet<Object>();
		pedIndex = pIndex;
	}
	
	//----------------------------------------------------------------------------
	public int getPedIndex(){ return pedIndex; }

	//----------------------------------------------------------------------------
	public void addTrio(Trio t)
	{
		Set<Object> s = g.connection(t.y,t.z);
		if (t.y != null && t.z != null)
		{
			if (s == null)
			{
				s = new LinkedHashSet<Object>();
				g.connect(t.y,t.z,s);
			}
			s.add(t.x);
		}
		h.put(t.x,t);
		males.add(t.y);
		females.add(t.z);
	}

	//----------------------------------------------------------------------------
	public Object pa(Object x)
	{
		Trio t = h.get(x);
		return t == null ? null : t.y;
	}
	
	//----------------------------------------------------------------------------
	public Object ma(Object x)
	{
		Trio t = h.get(x);
		return t == null ? null : t.z;
	}
	
/*
	public void addTrio(Trio[] t)
	{
		for (int i=0; i<t.length; i++)
			addTrio(t[i]);
	}
*/

	//----------------------------------------------------------------------------
	public void addTrios(Collection<Trio> t)
	{
		for (Trio x : t)
			addTrio(x);
	}

	//----------------------------------------------------------------------------
	public Trio getTrio(Object x)
	{
		return h.get(x);
	}

	//----------------------------------------------------------------------------
	public Object[] kids(Object x)
	{
		Collection ss = g.getNeighbours(x);
		if (ss == null)
		{
			Object[] result = {};
			return result;
		}

		Set<Object> k = new LinkedHashSet<Object>();
		for (Object i : ss)
			k.addAll(g.connection(x,i));
		return k.toArray();
	}

	//----------------------------------------------------------------------------
	public Family[] nuclearFamilies()
	{
		Network<Object,Set<Object>> n = new Network<Object,Set<Object>>(g);
		Set<Family> f = new LinkedHashSet<Family>();
		Set<Object> vertices = new LinkedHashSet<Object>(n.getVertices());
		for (Object u : vertices) //n.getVertices())
		{
			if (males.contains(u))
			{
				for (Object v : n.getNeighbours(u))
				{
					Family ff = new Family();
					ff.setPa(u);
					ff.setMa(v);
					for (Object w : n.connection(u,v))
						ff.addKid(w);
					f.add(ff);
				}
				n.remove(u);
			}
		}
		return (Family[]) f.toArray(new Family[0]);
	}

	//----------------------------------------------------------------------------
	public Collection<Object> individuals()
	{
		return h.keySet();
	}	
}
