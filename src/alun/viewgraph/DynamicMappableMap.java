package alun.viewgraph;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import alun.graph.Graph;
import alun.graph.GraphFunction;

public class DynamicMappableMap<V extends Mappable> extends Map implements MappableGraph
{
	public DynamicMappableMap(Graph<V,Object> gg, boolean edges)
	{
		g = (Graph<Mappable,Object>)gg;
		individualedges = edges;
	}
		
	public DynamicMappableMap(Graph<V,Object> gg)
	{
		this(gg,false);
	}

	public Mappable getShowing(double x, double y)
	{
		for (Mappable m : g.getVertices())
			if (m.contains(x,y))
				return m;
		return null;
	}

	public void paint(Graphics gr)
	{
		if (font != null)
			gr.setFont(font);
		gr.setColor(Color.black);


		if (individualedges)
		{
			for (Object x : g.getEdges())
			{
				if (x instanceof Line)
					((Line)x).paint(gr);
			}
		}
		else
		{ 
			for (Iterator<Mappable> i = getShownVertices().iterator(); i.hasNext(); )
			{
				Mappable a = i.next();
				for (Iterator<Mappable> j = getShownNeighbours(a).iterator(); j.hasNext(); )
				{
					Mappable b = j.next();
					if (a != b)
						gr.drawLine((int)a.getX(),(int)a.getY(),(int)b.getX(),(int)b.getY());
				}
			}
		}

		for (Iterator<Mappable> i = getAllVertices().iterator(); i.hasNext(); )
			i.next().paint(gr);
	}

	public void show(Mappable x){}
	public void hide(Mappable x){}
	public void show(Collection<? extends Mappable> x){}
	public void hide(Collection<? extends Mappable> x){}

	public Set<Mappable> getAllVertices()
	{
		return g.getVertices();
	}

	public Set<Mappable> getShownVertices()
	{
		return g.getVertices();
	}

	public Set<Mappable> getAllComponent(Mappable x)
	{
		return GraphFunction.reachables(g,x);
	}

	public Set<Mappable> getShownComponent(Mappable x)
	{
		return  GraphFunction.reachables(g,x);
	}

	public Collection<Mappable> getAllNeighbours(Mappable x)
	{
		return g.getNeighbours(x);
	}

	public Collection<Mappable> getShownNeighbours(Mappable x)
	{
		return  g.getNeighbours(x);
	}

	public Collection<Mappable> getShownOutNeighbours(Mappable x)
	{
		Set<Mappable> s = new LinkedHashSet<Mappable>(getShownNeighbours(x));
		for (Iterator<Mappable> i = getShownNeighbours(x).iterator(); i.hasNext(); )
		{
			Mappable y = i.next();
			if (!g.connects(x,y))
				s.remove(y);
		}
		return s;
	}

	public Collection<Mappable> getShownInNeighbours(Mappable x)
	{
		Set<Mappable> s = new LinkedHashSet<Mappable>(getShownNeighbours(x));
		for (Iterator<Mappable> i = getShownNeighbours(x).iterator(); i.hasNext(); )
		{
			Mappable y = i.next();
			if (!g.connects(y,x))
				s.remove(y);
		}
		return s;
	}

	public String toString()
	{
		return g.toString();
	}

// Private data.

	private Graph<Mappable,Object> g = null;
	private boolean individualedges = false; //true; // May be a problem!!!!!!!
}
