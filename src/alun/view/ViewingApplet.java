package alun.view; 

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;

import alun.util.SafeRunnable;
import alun.util.SafeThread;

public class ViewingApplet extends Applet implements Paintable, SafeRunnable
{
	public static String className = "alun.view.ViewingApplet";

	public void init()
	{
		setLayout(new BorderLayout());
		//setCanvas(new BufferedCanvas(this));
		setCanvas(new ActiveCanvas(this));
		getCanvas().setSize(500,500);
		getCanvas().setBackground(Color.red);

		t = new SafeThread(this);
		t.safeStart();
		t.safeSuspend();
	}

	public void loop()
	{
		try 
		{ 
			Thread.sleep(250); 
		} 
		catch (Exception e)
		{
			System.err.println("Caught in ViewingApplet:loop()");
			e.printStackTrace();
		}
	}

	public void start()
	{
		t.safeResume();
	}

	public void stop()
	{
		t.safeSuspend();
	}

	public void destroy()
	{
		t.safeStop();
	}

	public void paint(Graphics g)
	{
	}

	public Canvas getCanvas()
	{
		return ac;
	}

	public void setCanvas(Canvas c)
	{
		if (getCanvas() != null)
		{
			remove(getCanvas());
			if (getMouser() != null)
			{
				getCanvas().removeMouseListener(getMouser());
				getCanvas().removeMouseMotionListener(getMouser());
				getCanvas().removeKeyListener(getMouser());
			}
		}

		ac = c;

		if (getMouser() != null)
		{
			getCanvas().addMouseListener(getMouser());
			getCanvas().addMouseMotionListener(getMouser());
			getCanvas().addKeyListener(getMouser());
		}

		add(getCanvas(),BorderLayout.CENTER);
	}

	public synchronized final void setMouser(Mouser mos)
	{
		if (getCanvas() != null)
		{
			getCanvas().removeMouseListener(m);
			getCanvas().removeMouseMotionListener(m);
			getCanvas().removeKeyListener(m);
			getCanvas().addMouseListener(mos);
			getCanvas().addMouseMotionListener(mos);
			getCanvas().addKeyListener(mos);
		}
		m = mos;
	}

	public Mouser getMouser()
	{
		return m;
	}

	public void setChanged()
	{
		getCanvas().repaint();
		//getCanvas().update(getCanvas().getGraphics());
	}

	public SafeThread getThread()
	{
		return t;
	}

// Private data and methods and classes.

	private SafeThread t = null;
	private Canvas ac = null;
	private Mouser m = null;

/**
 Test main.
*/
 	public static void main(String[] args) 
	{
		try
		{
			System.out.println("Running main for "+className);
			ViewingApplet a = (ViewingApplet)(Class.forName(className).newInstance());
			a.init();

			Frame f = new Frame();
			f.addWindowListener(new FrameQuitter());
			f.add(a);
			f.pack();
			f.setVisible(true);
			a.start();
		}
		catch (Exception e)
		{
			System.err.println("Caught in ViewingApplet:main()");
			e.printStackTrace();
		}
	}
}
