package alun.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class ActiveCanvas extends BufferedCanvas
{
	public ActiveCanvas(Paintable p)
	{
		super(p);
		setTransform(new AffineTransform());
		//setTransformer(new ActiveCanvasTransformer(this));
		new ActiveCanvasTransformer(this);
	}

	public Graphics getGraphics() 
	{
		Graphics2D g = (Graphics2D) super.getGraphics();
		g.transform(getTransform());
		return g;
	}

	public void update(Graphics g)
	{
		// Create a buffer image.
		Dimension d = getSize();
		if (ima == null || ima.getHeight(null)!=d.height || ima.getWidth(null)!=d.width)
			ima = createImage(d.width,d.height);

		// Draw a background to the image.
		Graphics gg = ima.getGraphics();
		gg.setColor(getBackground());
		gg.fillRect(0,0,d.width,d.height);

		// Transform the coordinates.
		((Graphics2D)gg).transform(getTransform());

		// Paint the background image.
		paint(gg);
			
		// Flash the image to the screen.
		super.getGraphics().drawImage(ima,0,0,null);
		//g.drawImage(ima,0,0,null);

		// Clean up.
		gg.dispose();
	}

// Protected methods.

	protected void processMouseEvent(MouseEvent e)
	{
		super.processMouseEvent(transformed(e));
	}

	protected void processMouseMotionEvent(MouseEvent e)
	{
		super.processMouseMotionEvent(transformed(e));
	}

	protected AffineTransform getTransform()
	{
		return af;
	}

	protected void setTransform(AffineTransform a)
	{
		af = a;
	}

	protected void setTransformer(ActiveCanvasTransformer t)
	{
		tran = t;
	}

	protected ActiveCanvasTransformer getTransformer()
	{
		return tran;
	}

// Private data, methods and classes.

	private ActiveCanvasTransformer tran = null;
	private AffineTransform af = null;
	private Image ima = null;

	private MouseEvent transformed(MouseEvent e)
	{
		try
		{ 
			Point2D p = new Point2D.Double(e.getX(),e.getY());
			af.inverseTransform(p,p);
			return new MouseEvent
			(
				(Component)e.getSource(),
				e.getID(),
				e.getWhen(),
				e.getModifiers(),
				(int)p.getX(),
				(int)p.getY(),
				e.getClickCount(),
				e.isPopupTrigger()
			);
		}
		catch (NoninvertibleTransformException f)
		{
			System.err.println("Caught in ActiveCanvas:transformed()");
			f.printStackTrace();
		}
		return e;
	}
}
