package alun.viewgraph;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import alun.graph.Graph;
import alun.graph.GraphFunction;

public class DynamicMap<V> extends Map<V>
{
	public DynamicMap(Graph<V,? extends Object> gg)
	{
		super(gg);
		showing = new LinkedHashSet<Mappable>();
		for (Iterator i = g.getVertices().iterator(); i.hasNext(); )
			showing.add(om.get(i.next()));
	}

	public void show(Mappable x)
	{
		showing.add(x);
	}

	public void hide(Mappable x)
	{
		showing.remove(x);
	}

	public void show(Collection<? extends Mappable> x)
	{
		showing.addAll(x);
	}

	public void hide(Collection<? extends Mappable> x)
	{
		showing.removeAll(x);
	}

	public Set<Mappable> getAllVertices()
	{
		return mo.keySet();
	}

	public Set<Mappable> getShownVertices()
	{
		return showing;
	}

	public Collection<Mappable> getAllComponent(Mappable x)
	{
		return reps(GraphFunction.reachables(g,mo.get(x)));
	}

	public Collection<Mappable> getShownComponent(Mappable x)
	{
		Collection<Mappable> s = getAllComponent(x);
		s.retainAll(showing);
		return s;
	}

	public Collection<Mappable> getAllNeighbours(Mappable x)
	{
		return reps(g.getNeighbours(mo.get(x)));
	}

	public Collection<Mappable> getShownNeighbours(Mappable x)
	{
		Collection<Mappable> s = getAllNeighbours(x);
		s.retainAll(showing);
		return s;
	}

	public Collection<Mappable> getShownOutNeighbours(Mappable x)
	{
		Collection<Mappable> s = new LinkedHashSet<Mappable>(getShownNeighbours(x));
		for (Iterator<Mappable> i = getShownNeighbours(x).iterator(); i.hasNext(); )
		{
			Mappable y = i.next();
			if (!g.connects(mo.get(x),mo.get(y)))
				s.remove(y);
		}
		return s;
	}

	public Collection<Mappable> getShownInNeighbours(Mappable x)
	{
		Collection<Mappable> s = new LinkedHashSet<Mappable>(getShownNeighbours(x));
		for (Iterator<Mappable> i = getShownNeighbours(x).iterator(); i.hasNext(); )
		{
			Mappable y = i.next();
			if (!g.connects(mo.get(y),mo.get(x)))
				s.remove(y);
		}
		return s;
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		g.setColor(Color.black);

		for (Iterator<Mappable> i = getShownVertices().iterator(); i.hasNext(); )
		{
			Mappable a = i.next();
			for (Iterator<Mappable> j = getShownNeighbours(a).iterator(); j.hasNext(); )
			{
				Mappable b = j.next();
				if (a != b)
					g.drawLine((int)a.getX(),(int)a.getY(),(int)b.getX(),(int)b.getY());
			}
		}

		for (Iterator<Mappable> i = getShownVertices().iterator(); i.hasNext(); )
			i.next().paint(g);
	}

	public Mappable getShowing(double x, double y)
	{
		for (Iterator<Mappable> i = getShownVertices().iterator(); i.hasNext(); )
		{
			Mappable m = i.next();
			if (m.contains(x,y))
				return m;
		}
		return null;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		for (Iterator<Mappable> i = getShownVertices().iterator(); i.hasNext(); )
		{
			Mappable a = i.next();
			s.append(mo.get(a)+"\t");
			for (Iterator<Mappable> j = getShownNeighbours(a).iterator(); j.hasNext(); )
			{
				Mappable b= j.next();
				s.append(mo.get(b)+" ");
			}
			if (getShownNeighbours(a).size() == 0)
				s.deleteCharAt(s.length()-1);
			s.append("\n");
		}

		if (getShownVertices().size() == 0)
			s.deleteCharAt(s.length()-1);

		return s.toString();
	}

// Private data.

	private LinkedHashSet<Mappable> showing = null;
}
