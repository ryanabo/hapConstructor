package alun.viewgraph;

import java.util.Iterator;

public class EditingCoordinator extends MapCoordinator
{
	public EditingCoordinator(MapEditor v)
	{
		super(v);
	}

	public synchronized void remove(Mappable v)
	{
		getViewer().getMap().hide(v);
		set(v,true);
		checkDegs();
		flash();
	}

	public synchronized void removeAll()
	{
		getViewer().getMap().hide(getViewer().getMap().getShownVertices());
		checkDegs();
		flash();
	}

	public synchronized void addAll()
	{
		getViewer().getMap().show(getViewer().getMap().getAllVertices());
		checkDegs();
		flash();
	}

	public synchronized void removeComponent(Mappable v)
	{
		getViewer().getMap().hide(getViewer().getMap().getShownComponent(v));
		checkDegs();
		flash();
	}

	public synchronized void find(String s)
	{
		Mappable m = null;
		for (Iterator<Mappable> i = getViewer().getMap().getAllVertices().iterator(); i.hasNext() && m == null; )
		{
			Mappable mm = i.next();
			if (s.equals(mm.toString()))
				m = mm;
		}

		if (m != null)
		{
			getViewer().getMap().show(m);
			set(m,true);
			checkDegs();
		}
	}

	public synchronized void expand(Mappable v)
	{
		getViewer().getMap().show(getViewer().getMap().getAllNeighbours(v));
		checkDegs();
		flash();
	}

	public synchronized void expandComponent(Mappable v)
	{
		getViewer().getMap().show(getViewer().getMap().getAllComponent(v));
		checkDegs();
		flash();
	}

	public synchronized void peel(Mappable v,boolean force)
	{
// Where does this get done? Here or in the MappableGraph?
		if (getViewer().getMap() instanceof StaticMap)
		{
			((StaticMap)getViewer().getMap()).peel(v);
			checkDegs();
			flash();
		}
/*
*/
	}

	public synchronized void peelAll()
	{
		if (getViewer().getMap() instanceof StaticMap)
		{
			for (int k=0; k<100; k++)
			{
				for (Mappable v : getViewer().getMap().getShownVertices())
					((StaticMap)getViewer().getMap()).peel(v);
			}
			checkDegs();
			flash();
		}
/*
*/
	}
	
// Protected methods.

	protected void checkDegs()
	{
		for (Iterator<Mappable> i = getViewer().getMap().getShownVertices().iterator(); i.hasNext(); )
		{
			Mappable m = i.next();
			if (getViewer().getMap().getShownVertices().containsAll(getViewer().getMap().getAllNeighbours(m)))
				m.setHighLighted(false);
			else
				m.setHighLighted(true);
		 }
	}

// Private data and methods.

	private double noise()
	{
		return Math.random()*5;
	}
}
