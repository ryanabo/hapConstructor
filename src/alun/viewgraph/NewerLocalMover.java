package alun.viewgraph;

import alun.util.CartesianPoint;
import alun.util.RadixPlaneSorter;

/*
	The same as LocalMover except that the
	attraction is 
		d = x*x + y*y
	and the repulsion is
		(r - gamma)^2 / r
	where r*r = d.
*/
public class NewerLocalMover extends LocalMover
{
	public NewerLocalMover()
	{
		setNParameters(1);
	}

	public void setParameter(int i, double dd)
	{
		switch(i)
		{
		case 0: double d =  2*dd;
			gamma = 2*d;
			if (d == 0)
				alpha = 0;
			else
				alpha = d*d*d/(d-gamma)/(d-gamma);
			gamma = gamma*gamma;
			break;
		}
	}

	public synchronized double move(MappableGraph map)
	{
		boolean doitall = false;
		double x = 0;
		double y = 0;
		double xx = 0;
		double yy = 0;
		double r = 0;
		double s = 0;
		double t = 0;

		double dx = 0;
		double dy = 0;
		double d2x = 0;
		double d2y = 0;
		double d2 = 0;
		double d2xy = 0;

		Mappable a = null;
		Mappable b = null;
		CartesianPoint[] n = null;
		Mappable[] o = (Mappable[]) map.getShownVertices().toArray(new Mappable[0]);
		RadixPlaneSorter<Mappable> p = null;

		double delta = 0;

		if (alpha > Double.MIN_VALUE)
		{
			p = new RadixPlaneSorter<Mappable>(Math.sqrt(gamma),Math.sqrt(gamma),40);
			for (int i=0; i<o.length; i++)
				p.add(o[i]);
			doitall = true;
		}

		for (int i=0; i<o.length; i++)
		{
			if(!(a = o[i]).isMobile())
				continue;

			dx = dy = d2x = d2y = d2xy = 0;
			if (doitall)
			{

				n = p.getLocal(a,gamma);
				p.remove(a);

				for (int j=0; j<n.length; j++)
				{
					b = (Mappable) n[j];
	
					x = a.getX() - b.getX();
					xx = x*x;
					y = a.getY() - b.getY();
					yy = y*y;

					r = xx + yy;
					if (r < Double.MIN_VALUE)
						continue;

					s = r - gamma;
					t = 3*gamma - r;
					r = 1/Math.sqrt(r);
					s *= r*r*r;
					t *= r*r*r*r*r;
/*

					r = Math.sqrt(r);
					s = (r*r -gamma) /r/r/r;
					t = (3*gamma - r*r)/r/r/r/r/r;
*/

					dx += x * s;
					dy += y * s;
					d2x += s + xx * t;
					d2y += s + yy * t;

				}
	
				dx *= alpha;
				dy *= alpha;
				d2x *= alpha;
				d2y *= alpha;
			}

			x = y = r = 0;
			n = (Mappable[]) map.getShownNeighbours(a).toArray(new Mappable[0]);
			for (int j=0; j<n.length; j++)
			{
				if ((b = (Mappable)n[j]) == a) 
					continue;
				x += b.getX();
				y += b.getY();
				r += 1;
			}
			r *= 2;
			dx +=  r*a.getX() - 2*x;
			dy +=  r*a.getY() - 2*y;
			d2x += r;
			d2y += r;

			d2 = Math.abs(d2x+d2y);
			if (d2 > Double.MIN_VALUE)
			{
				a.setX( a.getX() - dx/d2 );
				a.setY( a.getY() - dy/d2 );

				delta += ( (dx*dx) + (dy*dy) )/ d2/d2;
			}
	
			if (doitall)
				p.add(a);
		}

		return delta;
	}

// Private data.

	private double alpha = 0;
	private double gamma = 0;
}
