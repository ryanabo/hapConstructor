package alun.viewgraph;

import java.awt.Color;
import java.awt.Graphics;

public class Line 
{
	public Line(Mappable x, Mappable y, Color c)
	{
		a = x;
		b = y;
		setColor(c);
	}

	public Line(Mappable x, Mappable y)
	{
		this(x,y,Color.black);
	}

	public void setColor(Color cc)
	{
		c = cc;
	}

	public Color getColor()
	{
		return c;
	}

	public void paint(Graphics g)
	{
		g.setColor(getColor());
		if (a == b)
		{
			g.drawOval((int)a.getX(),(int)a.getY()-selfy,selfx,selfy);
		}
		else
		{
			g.drawLine((int)a.getX(),(int)a.getY(),(int)b.getX(),(int)b.getY());
		}
	}

// Private data.

	protected Mappable a = null;
	protected Mappable b = null;
	private Color c = null;
	private int selfx = 50;
	private int selfy = 31;
}
