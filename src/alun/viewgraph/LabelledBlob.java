package alun.viewgraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class LabelledBlob extends AbstractMappable
{
	public final static int RECTANGLE = 0;
	public final static int OVAL = 1;
	public final static int DIAMOND = 2;

	public LabelledBlob()
	{
		this("");
	}

	public LabelledBlob(String s)
	{
		setLabel(s);
		setShowText(true);
		setSize(10,10);
		setColor(Color.yellow);
		setShape(RECTANGLE);
	}

	public boolean contains(double a, double b)
	{
		double d = getX()-a;
		if (d < -halfwidth || d > halfwidth)
			return false;
		d = getY()-b;
		if (d < -halfheight || d > halfheight)
			return false;
		return true;
	}
	
	public String toString()
	{
		return name;
	}
	
	public void setLabel(String nm) 
	{
		name = nm;	
	}

	public void setShowText(boolean x)
	{
		show = x;
	}

	public void setTextColor(Color c)
	{
		tc = c;
	}

	public void setSize(int x, int y)
	{
		width = x;
		halfwidth = x/2;
		height = y;
 		halfheight = (y+1)/2;
	}

	public void setShape(int i)
	{
		shape = i;
	}

	public void paint(Graphics g)
	{
		if (!sizeset && show)
		{ 
			int w = g.getFontMetrics().stringWidth(toString());
			int h = g.getFontMetrics().getHeight();
			setSize(w+2*xborder,h+2*yborder);
			sizeset = true;
		}

		int ix = (int)(getX()-halfwidth);
		int iy = (int)(getY()-halfheight);

		drawMyShape(g,ix,iy,width,height);

		if (show)
		{
			g.setColor(tc);
			g.drawString(toString(),ix+xborder,iy+height-yborder+ycorrection);
		}
	}

// Private data.

	protected int halfwidth = 8;
	protected int halfheight = 8;
	protected int width = 16;
	protected int height = 16;
	protected int shape = 0;
	protected boolean sizeset = false;
	protected int xborder = 2;
	protected int yborder = -1;
	protected int ycorrection = -3;
	protected String name = null; 
	protected boolean show = true;
	protected Color tc = Color.black;

	protected void drawMyShape(Graphics g, int x, int y, int w, int h)
	{
		switch(shape)
		{
		case DIAMOND:
			int ww = w/2+4;
			int hh = h/2+4;
			int xx = (2*x+w)/2;
			int yy = (2*y+h)/2;
			int[] xxx = {xx-ww,xx,xx+ww,xx};
			int[] yyy = {yy,yy+hh,yy,yy-hh};
			Polygon p = new Polygon(xxx,yyy,4);
			g.setColor(getColor());
			g.fillPolygon(p);
			g.setColor(getOutline());
			g.drawPolygon(p);
			break;
		case OVAL:
			g.setColor(getColor());
			g.fillOval(x-2,y-2,w+4,h+4);
			g.setColor(getOutline());
			g.drawOval(x-2,y-2,w+4,h+4);
			break;
		case RECTANGLE:
		default:
			g.setColor(getColor());
			g.fillRect(x-2,y-2,w+4,h+4);
			g.setColor(getOutline());
			g.drawRect(x-2,y-2,w+4,h+4);
			break;
		}
	}
}
