package alun.util;

public class BasicInterval implements Interval
{
	public BasicInterval(double lower, double upper)
	{
		if (lower < upper)
		{
			lo = lower;
			hi = upper;
		}
		else
		{
			hi = lower;
			lo = upper;
		}
	}

	private double lo = 0;
	private double hi = 0;
	
	public double getLength()
	{
		return hi - lo;
	}

	public double getMiddle()
	{
		return (hi+lo)/2;
	}

	public double getRight()
	{
		return hi;
	}

	public double getLeft()
	{
		return lo;
	}

	public boolean intersects(double p)
	{
		return p >= getLeft() && p <= getRight();
	}

	public boolean intersects(Interval i)
	{
		return Math.abs(i.getMiddle()-getMiddle()) < (i.getLength() + getLength())/2.0;
	}

	public double intersection(Interval i)
	{
		double x = (i.getLength() + getLength())/2.0 - Math.abs(i.getMiddle()-getMiddle());
		return x > 0 ? x : 0;
	}
		
	public void setLeft(double l)
	{
		if (l <= hi)
			lo = l;
	}

	public void setRight(double r)
	{
		if (r >= lo)
			hi = r;
	}

	public void setLength(double x)
	{
		double m = getMiddle();
		lo = m-x/2;
		hi = m+x/2;
	}

	public void setMiddle(double x)
	{
		double m = getMiddle();
		lo = lo - m + x;
		hi = hi - m + x;
	}
}
