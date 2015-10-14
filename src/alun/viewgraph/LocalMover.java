package alun.viewgraph;

import alun.util.CartesianPoint;
import alun.util.RadixPlaneSorter;

/*
	Sets positions of vertices in a graph by minimuzing an energy
	funtion by the Newton Raphson iteration method.

	Let (x,y) be the differences in each direction between two vertices.
	The attracion is 
		d = x*x + y*y
	The repulsion is
		(gamm - d)^2 / d
*/
public class LocalMover extends MapMover
{
	public LocalMover()
	{
		setNParameters(1);
	}

	public void set(MappableGraph m)
	{
		super.set(m);
		double d = defvalue;
		gamma = 8*defvalue*defvalue;
		alpha = (gamma < Double.MIN_VALUE ? 0 : 1/3.0 );
		scaleToFit(m);
		double mx = 0; //Double.MAX_VALUE;
		double my = 0; //Double.MAX_VALUE;
		for (Mappable a : m.getShownVertices())
		{
			mx += a.getX();
			my += a.getY();
		}

		mx /= m.getShownVertices().size();
		my /= m.getShownVertices().size();

		for (Mappable a: m.getShownVertices())
		{
			a.setX(a.getX()-mx+200);
			a.setY(a.getY()-my+200);
		}
	}

/**
 However the variables are initially sets the scale to minimize
 the energy. It should avoid the time it takes for the vertices
 to move to the natural scale.
*/
	protected void scaleToFit(MappableGraph map)
	{
		Mappable[] o = (Mappable[]) map.getShownVertices().toArray(new Mappable[0]);
		RadixPlaneSorter<Mappable> p = null;
		boolean doitall = false;
		if (alpha > Double.MIN_VALUE)
		{
			p = new RadixPlaneSorter<Mappable>(Math.sqrt(gamma),Math.sqrt(gamma),40);
			for (int i=0; i<o.length; i++)
				p.add(o[i]);
			doitall = true;
		}

		double da = 0;
		double db = 0;
		double dc = 0;
		for (int i=0; i<o.length; i++)
		{
			Mappable a = o[i];

			if (doitall)
			{
				CartesianPoint[] n = p.getLocal(a,gamma);
				for (int j=0; j<n.length; j++)
				{
					Mappable b = (Mappable) n[j];
					double d = (a.getX()-b.getX())*(a.getX()-b.getX()) + (a.getY()-b.getY())*(a.getY()-b.getY());
					if (d < Double.MIN_VALUE)
						continue;
					da += 1/d;
					db += d;
				}
			}

			Mappable[] n = (Mappable[])map.getShownNeighbours(a).toArray(new Mappable[0]);
			for (int j=0; j<n.length; j++)
			{
				Mappable b = n[j];
				if (b == a)
					continue;
				dc += (a.getX()-b.getX())*(a.getX()-b.getX()) + (a.getY()-b.getY())*(a.getY()-b.getY());
			}
		}

		double s = alpha*gamma*gamma * da / (dc + alpha*db);
		s = Math.sqrt(s);
		s = Math.sqrt(s);
		for (int i=0; i<o.length; i++)
		{
			Mappable a = o[i];
			a.setX( a.getX() * s );
			a.setY( a.getY() * s );
		}
	}

	public void setParameter(int i, double dd)
	{
		switch(i)
		{
		case 0: double d = 4*dd*dd;
			gamma = 4*d;
			//alpha = (gamma < Double.MIN_VALUE ? 0 : 1/3.0 );
			alpha = d*d/(gamma-d)/(gamma-d);
			break;
		}
	}

	public double getParameterInit(int i)
	{
		switch(i)
		{
		case 0: return defvalue;
		default: return 0;
		}
	}

	public double getParameterScale(int i)
	{
		switch(i)
		{
		case 0: return 100;
		default: return 1;
		}
	}

	public String getParameterName(int i)
	{
		switch(i)
		{
		case 0: return "Repulsion";
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

					//s = df(r);
					//t = 2*d2f(r);		
					r = 1/r;
					s = gamma*r;
					s = s*s;
					t = 8*s*r;
					s = 2*(1-s);

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
			dx += r*a.getX() - 2*x;
			dy += r*a.getY() - 2*y;
			d2x += r;
			d2y += r;

			d2 = Math.abs(d2x+d2y);
			//d2 = Math.abs(d2x+d2y+2*d2xy);
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

	public static int defvalue = 50;
	private double alpha = 0;
	private double gamma = 0;
}
