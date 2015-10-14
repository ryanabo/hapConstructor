package alun.viewgraph;

import java.util.Iterator;

abstract public class MapMover
{
	abstract public double move(MappableGraph m);

	abstract public void setParameter(int i, double d);
	abstract public String getParameterName(int i);
	abstract public double getParameterScale(int i);
	abstract public double getParameterInit(int i);

	public int getNParameters()
	{
		return npar;
	}

	public void setNParameters(int k)
	{
		npar = k;
		for (int i=0; i<npar; i++)
			setParameter(i,getParameterInit(i));
	}

	public void set(MappableGraph m)
	{
		for (Iterator<Mappable> i = m.getShownVertices().iterator(); i.hasNext(); )
		{
			Mappable u = i.next();
			if (u.isMobile())
			{
				u.setX(800*Math.random());
				u.setY(800*Math.random());
				//u.z = 0;
			}
		}
	}

// Private data.

	private int npar = 0;
}
