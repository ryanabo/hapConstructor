package alun.util;

public class XYPair implements CartesianPoint
{
	public double x = 0;
	public double y = 0;

	public XYPair(double a, double b)
	{
		x = a;
		y = b;
	}

	public XYPair()
	{
	}

        public String toString()
        {
                return x+" "+y;
        }

	public void setX(double d)
	{
		x = d;
	}

	public void setY(double d)
	{
		y = d;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}
}
