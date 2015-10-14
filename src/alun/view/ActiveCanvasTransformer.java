package alun.view;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

class ActiveCanvasTransformer extends MouseKey
{
	public ActiveCanvasTransformer(ActiveCanvas a)
	{
		ac = a;
		ac.addMouseMotionListener(this);
		ac.addMouseListener(this);
		ac.addKeyListener(this);
	}

	public void deConnect()
	{
		ac.removeMouseMotionListener(this);
		ac.removeMouseListener(this);
		ac.removeKeyListener(this);
	}
		
	public void keyPressed(KeyEvent e)
	{
		if (!isMine(e))
			return;

		switch(e.getKeyCode())
		{
		case KeyEvent.VK_HOME:
			ac.setTransform(new AffineTransform());
			break;
		case KeyEvent.VK_DOWN:
			ac.getTransform().scale(l,l);
			break;
		case KeyEvent.VK_UP:
			ac.getTransform().scale(li,li);
			break;
		case KeyEvent.VK_LEFT:
			ac.getTransform().rotate(-t);
			break;
		case KeyEvent.VK_RIGHT:
			ac.getTransform().rotate(t);
			break;
		}

		ac.repaint();
	}
	
	public void mouseEntered(MouseEvent e)
	{
		ac.requestFocusInWindow();
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		if (isMine(e) && button(e) == 3)
		{
			x = e.getX();
			y = e.getY();
			shift = true;
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (shift)
		{
			ac.getTransform().translate(e.getX()-x,e.getY()-y);
			ac.repaint();
			shift = false;
		}
	}

	public void mouseDragged(MouseEvent e)
	{
		if (shift)
		{
			ac.getTransform().translate(e.getX()-x,e.getY()-y);
			ac.repaint();
		}
	}

	// Protected methods

	protected  ActiveCanvas ac = null;

	protected boolean isMine(InputEvent e)
	{
		return !e.isShiftDown();
	}

	// Private data and methods.

	private int x = 0;
	private int y = 0;
	private boolean shift = false;
	private double l = 9.0/10;
	private double li = 1.0/l;
	private double t = Math.PI/12.0;
}
