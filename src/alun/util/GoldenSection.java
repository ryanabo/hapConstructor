package alun.util;

public class GoldenSection
{
	public static double gold = 0.61803390;

	public static XYPair maximum(Curve p, double lo, double hi, double tol)
	{
		return maximum(p,lo,hi,tol,false);
	}

	public static XYPair maximum(Curve p, double lo, double hi, double tol, boolean verb)
	{
		//return golden(p,lo,lo+(1-gold)*(hi-lo),hi,tol);

		double g = 1 - (Math.sqrt(5)-1) / 2.0;

		double h = hi;
		double fh = p.f(h);

		double l = lo;
		double fl = p.f(l);

		double a = l + g*(h-l);
		double fa = p.f(a);

		double b = a + g*(h-a);
		double fb = p.f(b);
		
		while (h-l > tol)
		{
			if (fa > fb)
			{
				h = b;
				fh = fb;
				b = a;
				fb = fa;
				a = b - g*(b-l);
				fa = p.f(a);
			}
			else
			{
				l = a;
				fl = fa;
				a = b;
				fa = fb;
				b = a + g*(h-a);
				fb = p.f(b);
			}
			
			if (verb)
			{
				System.out.print(l+"\t"+a+"\t"+b+"\t"+h+"\t\t");
				System.out.println(fl+"\t"+fa+"\t"+fb+"\t"+fh);
			}
		}

		double x = l;
		double y = fl;
		if (fa > y) { x = a; y = fa; }
		if (fb > y) { x = b; y = fb; }
		if (fh > y) { x = h; y = fh; }

		return new XYPair(x,y);
	}

	public static XYPair golden(Curve p, double lo, double mid, double hi, double tol)
	{	
		XYPair r = new XYPair(lo,p.f(lo));
		XYPair s = null;
		XYPair t = null;
		XYPair u = new XYPair(hi,p.f(hi));
		
		if (Math.abs(mid-lo) < Math.abs(hi-mid))
		{
			s = new XYPair(mid,p.f(mid));
			double x = mid + (hi-mid)*(1-gold);
			t = new XYPair(x,p.f(x));
		}
		else
		{
			double x = lo + (mid-lo)*(1-gold);
			s = new XYPair(x,p.f(x));
			t = new XYPair(mid,p.f(mid));
			
		}

		return golden(p,r,s,t,u,tol);
	}

	public static XYPair golden(Curve p, XYPair r, XYPair s, XYPair t, XYPair u, double tol)
	{
		while (Math.abs(r.x-u.x) > tol)
		{
			if (t.y > s.y)
			{
				r = s;
				s = t;
				double x = gold*s.x + (1-gold)*u.x;
				t = new XYPair(x,p.f(x));
			}
			else
			{
				u = t;
				t = s;
				double x = (1-gold)*r.x + gold*t.x;
				s = new XYPair(x,p.f(x));
			}
		}

		XYPair z = r;
		if (s.y > z.y)
			z = s;
		if (t.y > z.y)
			z = t;
		if (u.y > z.y)
			z = u;
		return z;
	}


	public static void main(String[] args)
	{
		try
		{
			System.out.println(GoldenSection.maximum(new TestCurve(),0,1,0.001));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

class TestCurve implements Curve
{
int count = 0;
	public double f(double x)
	{
System.err.println((++count)+" "+x);
		return x*x*(1-x)*(1-x)*(1-x)*(1-x)*(1-x)*(1-x)*(1-x)*(1-x);
		//return x*x;
	}
}
