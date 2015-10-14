package alun.viewgraph;

import alun.util.CartesianPoint;
import alun.util.RadixPlaneSorter;

public class DagMover extends MapMover
{
	public DagMover()
	{
		setNParameters(3);
	}

	public void setParameter(int i, double d)
	{
		switch(i)
		{
		case 0: gamma = 2*2*d*2*d;
			alpha = (gamma < Double.MIN_VALUE ? 0 : 1/3.0 );
			break;

		case 1: beta = d*d*d;
			break;

		case 2: delta = d;
			break;
		}
	}

	public double getParameterInit(int i)
	{
		switch(i)
		{
		case 0: return 50;
		case 1: return 0;
		case 2: return 50;
		default: return 0;
		}
	}

	public double getParameterScale(int i)
	{
		switch(i)
		{
		case 0: return 100;
		case 1: return 10;
		case 2: return 200;
		default: return 1;
		}
	}

	public String getParameterName(int i)
	{
		switch(i)
		{
		case 0: return "X-Repulsion";
		case 1: return "Gravity";
		case 2: return "Y-Repulsion";
		default: return "";
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
		double d2xy = 0;
		double d2 = 0;

		Mappable a = null;
		Mappable b = null;
		CartesianPoint[] n = null;
		Mappable[] o = (Mappable[])map.getShownVertices().toArray(new Mappable[0]);
		RadixPlaneSorter<Mappable> p = null;

		double change = 0;

		if (gamma > Double.MIN_VALUE && alpha > Double.MIN_VALUE)
		{
			//p = new RadixPlaneSorter(Math.sqrt(gamma)/4,Math.sqrt(gamma)/4,40);
			p = new RadixPlaneSorter<Mappable>(Math.sqrt(gamma),Math.sqrt(gamma),40);
			for (int i=0; i<o.length; i++)
				p.add(o[i]);
			doitall = true;
		}

		for (int i=0; i<o.length; i++)
		{
			if(!(a = o[i]).isMobile())
				continue;

			dx = dy = d2x = d2y = 0;
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

					//s = df(r);
					//t = 2*d2f(r);		
					r = 1/r;
					s = gamma*r;
					s = s*s;
					t = 4*s*r;
					s = 1-s;

					dx += s * x;
					dy += s * y;
					d2x += s + t*xx;
					d2y += s + t*yy;
					//d2xy += t*x*y;
				}
	
				dx *= alpha;
				dy *= alpha;
				d2x *= alpha;
				d2y *= alpha;
				//d2xy *= alpha;
			}

			x = y = r = 0;
			n = (Mappable[])map.getShownNeighbours(a).toArray(new Mappable[0]);
			for (int j=0; j<n.length; j++)
			{
				if ((b = (Mappable)n[j]) == a) 
					continue;
				x += b.getX();
				y += b.getY();
				r += 1;
			}
			dx += r*a.getX() - x;
			dy += r*a.getY() - y;
			d2x += r;
			d2y += r;


			n =  (CartesianPoint[])map.getShownOutNeighbours(a).toArray(new Mappable[0]);
			s = 0;
			t = 0;
			for (int j=0; j<n.length; j++)
			{
				if ((b = (Mappable) n[j]) == a)
					continue;
				s += a.getY() - b.getY() + delta;
				t += 1;
			}

			n =  (CartesianPoint[])map.getShownInNeighbours(a).toArray(new Mappable[0]);
			for (int j=0; j<n.length; j++)
			{
				if ((b = (Mappable) n[j]) == a)
					continue;
				s += a.getY() - b.getY() - delta;
				t += 1;
			}

			dy += beta * s;
			d2y += beta * t;


			d2 = Math.abs(d2x+d2y);
			//d2 = Math.abs(d2x+d2y+2*d2xy);
			if (d2 > Double.MIN_VALUE)
			{
		
				a.setX(a.getX() - dx/d2);
				a.setY(a.getY() - dy/d2);
				change += ( (dx*dx) + (dy*dy) )/d2/d2;
			}
	
			if (doitall)
				p.add(a);
		}

		return change;
	}

// Private data.

	protected double alpha = 0;
	protected double gamma = 0;
	protected double delta = 0;
	protected double beta = 0;
}
