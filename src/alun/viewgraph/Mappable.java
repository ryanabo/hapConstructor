package alun.viewgraph;

import java.awt.Graphics;

import alun.util.CartesianPoint;

public interface Mappable extends CartesianPoint
{
	public boolean isMobile();
	public void setMobile(boolean m);
	public boolean contains(double x, double y);
	public void paint(Graphics g);
	public void setHighLighted(boolean b);
}
