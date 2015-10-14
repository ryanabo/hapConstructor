package alun.util;

public interface Interval
{
	public double getLength();
	public double getMiddle();
	public double getRight();
	public double getLeft();
	public boolean intersects(double p);
	public boolean intersects(Interval i);
	public double intersection(Interval i);
	public void setLength(double x);
	public void setMiddle(double x);
	public void setLeft(double l);
	public void setRight(double r);
}
