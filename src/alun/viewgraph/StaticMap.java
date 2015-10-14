package alun.viewgraph;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import alun.graph.DirectedNetwork;
import alun.graph.Graph;
import alun.graph.GraphFunction;
import alun.graph.MaskedNetwork;
import alun.graph.MutableGraph;
import alun.graph.Network;

public class StaticMap<V> extends Map<V>
{
	public StaticMap(MutableGraph<Mappable,Line> h)
	{
		super(null);
		hide = h;
		show = new MaskedNetwork<Mappable,Line>(hide);
	}

	public StaticMap(Graph<V,Object> gg)
	{
		super(gg);

		if (gg instanceof DirectedNetwork)
			hide = new DirectedNetwork<Mappable,Line>();
		else
			hide = new Network<Mappable,Line>();

		for (Iterator<V> i = gg.getVertices().iterator(); i.hasNext(); )
			hide.add(om.get(i.next()));
			
		for (Iterator<V> i = gg.getVertices().iterator(); i.hasNext(); )
		{
			V x = i.next();
			Mappable mx = om.get(x);
			for (Iterator<V> j = gg.getNeighbours(x).iterator(); j.hasNext(); )
			{
				V y = j.next();
				Mappable my = om.get(y);
				if (gg.connects(x,y) && !hide.connects(mx,my))
				{
					if (gg.connection(x,y) instanceof Arrow)
						hide.connect(mx,my,new Arrow(mx,my,edgecol));
					else
						hide.connect(mx,my,new Line(mx,my,edgecol));
				}
			}
		}

		show = new MaskedNetwork<Mappable,Line>(hide);
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		for (Line i : show.getEdges())
		{
			i.paint(g);
		}
		for (Line i : show.getEdges())
		{
			if (i instanceof Arrow)
				((Arrow)i).paintArrow(g);
			//i.paint(g);
		}
		for (Mappable i : show.getVertices())
			i.paint(g);
	}
	
	public Mappable getShowing(double x, double y)
	{
		ArrayList<Mappable> a = new ArrayList<Mappable>(show.getVertices());
		for (int i=a.size()-1; i>=0; i--)
			if (a.get(i).contains(x,y))
				return a.get(i);
		return null;
	}

	public void show(Mappable x)
	{
		show.show(x);
	}

	public void show(Collection<? extends Mappable> x)
	{
		show.show(x);
	}

	public void hide(Mappable x)
	{
		show.hide(x);
	}

	public void peel(Mappable x)
	{
		if (clique(show.getNeighbours(x)))
			show.hide(x);
	}

	private boolean clique(Collection<Mappable> n)
	{
		for (Mappable x : n)
			for (Mappable y : n)
				if (x != y && !show.connects(x,y))
					return false;
		return true;
	}

	public void hide(Collection<? extends Mappable> x)
	{
		show.hide(x);
	}

	public Set<Mappable> getShownVertices()
	{
		return show.getVertices();
	}

	public Set<Mappable> getShownNeighbours(Mappable x)
	{
		return show.getNeighbours(x);
	}

	public Set<Mappable> getShownOutNeighbours(Mappable x)
	{
		Set<Mappable> s = new LinkedHashSet<Mappable>(getShownNeighbours(x));
		for (Iterator<Mappable> i = getShownNeighbours(x).iterator(); i.hasNext(); )
		{
			Mappable y = i.next();
			if (!show.connects(x,y))
				s.remove(y);
		}
		return s;
	}

	public Set<Mappable> getShownInNeighbours(Mappable x)
	{
		Set<Mappable> s = new LinkedHashSet<Mappable>(getShownNeighbours(x));
		for (Iterator<Mappable> i = getShownNeighbours(x).iterator(); i.hasNext(); )
		{
			Mappable y = i.next();
			if (!show.connects(y,x))
				s.remove(y);
		}
		return s;
	}

	public Set<Mappable> getShownComponent(Mappable x)
	{
		return GraphFunction.reachables(show,x);
	}

	public Set<Mappable> getAllVertices()
	{
		return hide.getVertices();
	}

	public Collection<Mappable> getAllNeighbours(Mappable x)
	{
		return hide.getNeighbours(x);
	}

	public Set<Mappable> getAllComponent(Mappable x)
	{
		return  GraphFunction.reachables(hide,x);
	}


	private MutableGraph<Mappable,Line> hide = null;
	private MaskedNetwork<Mappable,Line> show = null;
	private Color edgecol = Color.black;
}
