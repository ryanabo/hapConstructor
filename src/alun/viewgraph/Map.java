package alun.viewgraph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import alun.graph.Graph;

abstract public class Map<V> implements MappableGraph
{
	public Mappable makeRep(V a)
	{
		LabelledBlob b = new LabelledBlob(a.toString());
		b.setSize(16,16);
		b.setColor(Color.yellow);
		b.setTextColor(Color.black);
		b.setShowText(true);
		b.setShape(LabelledBlob.RECTANGLE);
		return b;
	}

	public Map()
	{
		om = new LinkedHashMap<V,Mappable> ();
		mo = new LinkedHashMap<Mappable, V> ();
	}

	public Map(Graph<V,? extends Object> gg)
	{
		this();
		g = gg;

		if (g != null)
		{
			for (V o : g.getVertices())
			{
				Mappable m = makeRep(o);
				om.put(o,m);
				mo.put(m,o);
			}
		}
	}

	public void setFontSize(int k)
	{
		font = new Font(null,Font.BOLD,k);
	}

	public void paint(Graphics g)
	{
		if (font != null)
			g.setFont(font);
	}

	public String toString()
	{
		return g.toString();
	}

// Private data.

	protected Graph<V,? extends Object> g = null;
	protected LinkedHashMap<V, Mappable> om = null;
	protected LinkedHashMap<Mappable, V> mo = null;
	protected Font font = null;

	protected Collection<Mappable> reps(Collection<V> o)
	{
		LinkedHashSet<Mappable> s = new LinkedHashSet<Mappable>();
		for (Iterator i = o.iterator(); i.hasNext(); )
			s.add(om.get(i.next()));
		s.remove(null);
		return s;
	}

	protected Set<V> obs(Set<Mappable> m)
	{
		LinkedHashSet<V> s = new LinkedHashSet<V>();
		for (Iterator i = m.iterator(); i.hasNext(); )
			s.add(mo.get(i.next()));
		s.remove(null);
		return s;
	}
}
