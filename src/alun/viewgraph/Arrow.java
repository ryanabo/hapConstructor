package alun.viewgraph;

import java.awt.Color;
import java.awt.Graphics;

public class Arrow extends Line
{
	public Arrow(Mappable x, Mappable y)
	{
		super(x,y);
	}

	public Arrow(Mappable x, Mappable y, Color c)
	{
		super(x,y,c);
	}

	public void paint(Graphics g, boolean arrows)
	{
		if (arrows)
			paint(g);
		else
			super.paint(g);
	}

	public void paint(Graphics g)
	{
		double x1 = a.getX();
		double y1 = a.getY();
		double x2 = b.getX();
		double y2 = b.getY();
		double th = Math.atan((y2-y1)/(x2-x1));
		int sg = x1 < x2 ? 1 : -1;
		double s = dist*Math.cos(th);
		double t = dist*Math.sin(th);
		double x = (10*x1+15*x2)/25.0;
		double y = (10*y1+15*y2)/25.0;
		int[] xx = {(int)(x-t), (int)(x+t), (int)(x+prop*s*sg)};
		int[] yy = {(int)(y+s), (int)(y-s), (int)(y+prop*t*sg)};

		g.setColor(getColor());
		g.drawLine((int)x1,(int)y1,(int)x2,(int)y2);
	//	g.setColor(getFill());
	//	g.fillPolygon(xx,yy,3);
	//	g.setColor(getColor());
	//	g.drawPolygon(xx,yy,3);
	}

	public void paintArrow(Graphics g)
	{
		double x1 = a.getX();
		double y1 = a.getY();
		double x2 = b.getX();
		double y2 = b.getY();
		double th = Math.atan((y2-y1)/(x2-x1));
		int sg = x1 < x2 ? 1 : -1;
		double s = dist*Math.cos(th);
		double t = dist*Math.sin(th);
		double x = (10*x1+15*x2)/25.0;
		double y = (10*y1+15*y2)/25.0;
		int[] xx = {(int)(x-t), (int)(x+t), (int)(x+prop*s*sg)};
		int[] yy = {(int)(y+s), (int)(y-s), (int)(y+prop*t*sg)};

	//	g.setColor(getColor());
	//	g.drawLine((int)x1,(int)y1,(int)x2,(int)y2);
		g.setColor(getFill());
		g.fillPolygon(xx,yy,3);
		g.setColor(getColor());
		g.drawPolygon(xx,yy,3);
	}

	public void setFill(Color x)
	{
		fill = x;
	}

	public Color getFill()
	{
		return fill;
	}

// Private data.

	private double dist = 4.0;
	private double prop = 2.0;
	private Color fill = Color.white;
}
