package alun.viewgraph;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class MovingMouser extends MapMouser
{
	public MovingMouser(MapViewer g)
	{
		super(g);
	}

	public void keyPressed(KeyEvent e)
	{
		if (!e.isShiftDown())
			return;

 		switch(e.getKeyCode())
		{
		case KeyEvent.VK_DOWN:
			co().scaleAll(9.0/10.0,9.0/10.0);
			break;
		case KeyEvent.VK_UP:
			co().scaleAll(10.0/9.0,10.0/9.0);
			break;
		case KeyEvent.VK_LEFT:
			co().rotateAll(-Math.PI/12.0);
			break;
		case KeyEvent.VK_RIGHT:
			co().rotateAll(Math.PI/12.0);
			break;
		case KeyEvent.VK_DELETE:
			co().flip();
			break;
		}
		co().flash();
	}

	public void mousePressed(MouseEvent e)
	{
		x = e.getX();
		y = e.getY();
		switch(button(e))
		{
		case 3:
			if (e.isShiftDown())
				shift = true;
			break;
		case 2:
		case 1:
			if ((v=co().get(e.getX(),e.getY())) != null)
			{
				if (e.isShiftDown())
				{
					co().setComponent(v,false);
					shiftComp = true;
				}
				else
				{
					co().set(v,false);
					shiftComp = false;
				}
			}
			break;
		}
	}

	public synchronized void mouseDragged(MouseEvent e)
	{
		switch(button(e))
		{
		case 3:
			if (shift)
			{
				co().shiftAll(e.getX()-x,e.getY()-y);
				co().flash();
				x = e.getX();
				y = e.getY();
			}
			break;
		case 2:
		case 1:
			if (v != null)
			{
				if (shiftComp)
					co().shiftComponent(v,e.getX()-x,e.getY()-y);
				else
					co().shift(v,e.getX()-x,e.getY()-y);
				co().flash();
				x = e.getX();
				y = e.getY();
			}
			break;
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		switch(button(e))
		{
		case 3:
			if (shift)
			{
				co().shiftAll(e.getX()-x,e.getY()-y);
				co().flash();
				shift = false;
			}
			break;
		case 2:
			if (v != null)
			{
				if (shiftComp)
				{
					co().shiftComponent(v,e.getX()-x,e.getY()-y);
					co().setComponent(v,false);
				}
				else
				{
					co().shift(v,e.getX()-x,e.getY()-y);
					co().set(v,false);
				}
				co().flash();
				v = null;
				shiftComp = false;
			}
			break;
		case 1:
			if (v != null)
			{
				if (shiftComp)
				{
					co().shiftComponent(v,e.getX()-x,e.getY()-y);
					co().setComponent(v,true);
				}
				else
				{
					co().shift(v,e.getX()-x,e.getY()-y);
					co().set(v,true);
				}
				co().flash();
				v = null;
				shiftComp = false;
			}
			break;
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2)
		{
			if (e.isShiftDown() && e.isControlDown())
			{
				co().flip();
			}
			else
			{
				co().reset();
			}
			return;
		}
		co().flash();
 	}

// Private data.

	private Mappable v = null;
	private double x = 0;
	private double y = 0;
	private boolean shift = false;
	private boolean shiftComp = false;
}
