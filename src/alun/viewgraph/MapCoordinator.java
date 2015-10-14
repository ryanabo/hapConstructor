package alun.viewgraph;

import java.util.Iterator;

/**
 This class coordinates changes made to a map by mouse clicks etc.
 Subclasses define a set() method that sets initial coordinates for
 vertices, and a move() method that iterates in a loop to optimise
 the coordinates. 
*/
public class MapCoordinator
{
	public MapCoordinator(MapViewer v)
	{
		gv = v;
	}

	public Mappable get(double x, double y)
	{
		if (getViewer().getMap() == null)
			return null;
		else
			return getViewer().getMap().getShowing(x,y);
	}

	public void flip()
	{
		getViewer().getThread().safeFlip();
	}

	public void flash()
	{
		getViewer().setChanged();
	}

	public void reset()
	{
		getViewer().reset();
		flash();
	}

/**
 Set the mobility state of the mappable object to the given value.
*/
	public void set(Mappable v, boolean mob)
	{
		v.setMobile(mob);
		flash();
	}

	public void setComponent(Mappable v, boolean mob)
	{
		for (Iterator<Mappable> i = getViewer().getMap().getShownComponent(v).iterator(); i.hasNext(); )
			set(i.next(),mob);
		flash();
	}

	public void setAll(boolean mob)
	{
		for (Iterator<Mappable> i = getViewer().getMap().getShownVertices().iterator(); i.hasNext(); )
			set(i.next(),mob);
		flash();
	}

/**
 Sets both the coordinates and the mobility of the given mappable object.
*/
	public void set(Mappable v, double x, double y, boolean mob)
	{
		v.setX(x);
		v.setY(y);
		v.setMobile(mob);
		flash();
	}

	public void shift(Mappable v, double x, double y)
	{
		v.setX(v.getX()+x);
		v.setY(v.getY()+y);
		flash();
	}

	public void shiftComponent(Mappable v, double x, double y)
	{
		for (Iterator<Mappable> i = getViewer().getMap().getShownComponent(v).iterator(); i.hasNext(); )
			shift(i.next(),x,y);
		flash();
	}

	public void shiftAll(double x, double y)
	{
		for (Iterator<Mappable> i = getViewer().getMap().getShownVertices().iterator(); i.hasNext(); )
			shift(i.next(),x,y);
		flash();
	}

	public void rotateAll(double s)
	{
		for (Iterator<Mappable> i = getViewer().getMap().getShownVertices().iterator(); i.hasNext(); )
		{
			Mappable m = i.next();
			double r = Math.sqrt(m.getX()*m.getX() + m.getY()*m.getY());
			double t = Math.atan2(m.getY(),m.getX());
			t += s;
			m.setX(r*Math.cos(t));
			m.setY(r*Math.sin(t));
		}
		flash();
	}

	public void scaleAll(double x, double y)
	{
		for (Iterator<Mappable> i = getViewer().getMap().getShownVertices().iterator(); i.hasNext(); )
		{
			Mappable m = i.next();
			m.setX(m.getX()*x);
			m.setY(m.getY()*y);
		}
		flash();
	}

// Protected methods.

	protected final MapViewer getViewer()
	{
		return gv;
	}

// Private data.

	private MapViewer gv = null;
}
