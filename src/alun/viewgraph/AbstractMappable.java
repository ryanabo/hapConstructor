package alun.viewgraph;

import java.awt.Color;
import java.awt.Graphics;

/**
 An abstract class that defines what is needed of an object in order
 to be able to set coordinates and draw it.
*/
abstract public class AbstractMappable implements Mappable
{
/**
 Returns true iff the given point is contained in the drawn image of the object.
*/
	abstract public boolean contains(double x, double y);
/**
 Draws the object to the graphics object.
*/
	abstract public void paint(Graphics g);

	public final double getX()
	{
		return x;
	}

	public final double getY()
	{
		return y;
	}

	public final void setX(double a)
	{
		x = a;
	}

	public final void setY(double a)
	{
		y = a;
	}

	public final boolean isMobile()
	{
		return mobile;
	}
	
	public final void setMobile(boolean b)
	{
		mobile = b;
	}


	public void setHighLighted(boolean b)
	{
		if (b)
			co = Color.red;
		else
			co = Color.black;
	}

/**
 Sets the colour of the object.
*/
	public void setColor(Color cc)
	{
		c = cc;
	}

/**
 Gets the colour of the object.
*/
	public Color getColor()
	{
		return c;
	}

/**
 Sets the colour of the outline.
*/
	public void setOutline(Color cc)
	{
		co = cc;
	}

/**
 Gets the colour of the outline.
*/
	public Color getOutline()
	{
		return co;
	}

// Private data

	private Color c = Color.yellow;
	private Color co = Color.black;
	private boolean mobile = true;
	private double x = 0;
	private double y = 0;
}
