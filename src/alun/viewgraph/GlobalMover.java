package alun.viewgraph;

/*
 	attraction
		d = x*x + y*y
	repulsion
		1/d
*/
public class GlobalMover extends LocalMover
{
	public GlobalMover()
	{
		setNParameters(1);
	}

	public void setParameter(int i, double d)
	{
		switch(i)
		{
		case 0: beta = 16*d*d*d*d;
			break;
		}		
	}

	public synchronized double move(MappableGraph map)
	{
		double x = 0;
		double xx = 0;
		double y = 0;
		double yy = 0;
		double r = 0;
		double s = 0;
		double t = 0;
		double dx = 0;
		double dy = 0;
		double d2x = 0;
		double d2y = 0;
		Mappable a = null;
		Mappable b = null;
		Mappable[] o = (Mappable[])map.getShownVertices().toArray(new Mappable[0]);
		Mappable[] q = null;

		double delta = 0;

		for (int i=0; i<o.length; i++)
		{
			if (!(a = o[i]).isMobile())
				continue;

			dx = 0;
			dy = 0;
			d2x = 0;
			d2y = 0;

			if (beta > Double.MIN_VALUE)
			{
				for (int j=0; j<o.length; j++)
				{
					if ((b = o[j]) == a)
						continue;
	
					x = a.getX() - b.getX();
					xx = x*x;
					y = a.getY() - b.getY();
					yy = y*y;

					r = xx + yy;
					if (r < Double.MIN_VALUE)
						continue;

					r = 1.0/r;
					s = -2*r*r;
					t = 4*s*r;

					dx += s*x;
					dy += s*y;
					d2x += s - t*xx;
					d2y += s - t*yy;
				}

				dx *= beta;
				d2x *= beta;
				dy *= beta;
				d2y *= beta;
			}

			x = 0;
			y = 0;
			r = 0;
			q = (Mappable[]) map.getShownNeighbours(a).toArray(new Mappable[0]);
			for (int j=0; j<q.length; j++)
			{	
				if ((b = q[j]) == a)
					continue;
				x += b.getX();
				y += b.getY();
				r += 1;
			}
			dx += 2*r*a.getX() - 2*x;
			dy += 2*r*a.getY() - 2*y;
			d2x += 2*r;	
			d2y += 2*r;

			double d2 = Math.abs(d2x+d2y);
			if (d2 > Double.MIN_VALUE)
			{
				a.setX( a.getX() - dx/d2 );
				a.setY( a.getY() - dy/d2 );

				delta += ( (dx*dx) + (dy*dy) ) /d2/d2;
			}
		}

		return delta;
	}

	private double beta = 16*50*50*50*50;
}
