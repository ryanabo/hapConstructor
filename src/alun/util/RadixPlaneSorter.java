package alun.util;

import java.util.Vector;

public class RadixPlaneSorter<E extends CartesianPoint>
{
	public RadixPlaneSorter(double x, double y, int k)
	{
		l = new Lattice<Vector<E>>(k);
		xgap = x;
		ygap = y;
	}

	public void add(E m)
	{
		tile(m.getX(),m.getY()).add(m);
	}

	public void remove(E m)
	{
		tile(m.getX(),m.getY()).remove(m);
	}

	public CartesianPoint[] getLocal(E a, double rr)
	{
		double r = Math.sqrt(rr);
		int xl = (int)( (a.getX()-r)/xgap );
		int xh = (int)( (a.getX()+r)/xgap );
		int yl = (int)( (a.getY()-r)/ygap );
		int yh = (int)( (a.getY()+r)/ygap );
		Vector<E> s = null;
		Vector<E> v = new Vector<E>();

		for (int i=xl; i<=xh; i++)
			for (int j=yl; j<=yh; j++)
			{
				s = l.get(i,j);
				if (s != null)
				{
					for (int k=0; k<s.size(); k++)
					{
						E m = s.elementAt(k);
						double xx = m.getX() - a.getX();
						xx = xx*xx;
						double yy = m.getY() - a.getY();
						yy = yy*yy;
						if (xx+yy < rr)
							v.add((E)m);
					}
				}
			}

		v.removeElement(a);

		return v.toArray(new CartesianPoint[0]);
	}

	public CartesianPoint[] getLocalX(E a, double rr)
	{
		double r = Math.sqrt(rr);
		int xl = (int)( (a.getX()-r)/xgap );
		int xh = (int)( (a.getX()+r)/xgap );
		int yl = (int)( (a.getY()-r)/ygap );
		int yh = (int)( (a.getY()+r)/ygap );
		Vector<E> s = null;
		Vector<E> v = new Vector<E>();

		for (int i=xl; i<=xh; i++)
			for (int j=yl; j<=yh; j++)
			{
				s = l.get(i,j);
				if (s != null)
				{
					for (int k=0; k<s.size(); k++)
					{
						E m = s.elementAt(k);
						double xx = m.getX() - a.getX();
						xx = xx*xx;
						if (xx < rr)
							v.add(m);
					}
				}
			}
		v.removeElement(a);

		return v.toArray(new CartesianPoint[0]);
	}

// Private data.

	private Lattice<Vector<E>> l = null;
	private double xgap = 0;
	private double ygap = 0;

	private Vector<E> tile(double a, double b)
	{
		int i = (int)(a/xgap);
		int j = (int)(b/ygap);
		Vector<E> s = l.get(i,j);
		if (s == null)
		{
			s = new Vector<E>();
			l.put(i,j,s);
		}
		return s;
	}
}
