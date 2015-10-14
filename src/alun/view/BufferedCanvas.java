package alun.view;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

public class BufferedCanvas extends Canvas
{
	public BufferedCanvas(Paintable p)
	{
		painter = p;
	}

	public void paint(Graphics g)
	{
		if (painter != null)
			painter.paint(g);
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

		// Paint the background image.
		paint(gg);
			
		// Flash the image to the screen.
		g.drawImage(ima,0,0,null);

		// Clean up.
		gg.dispose();
	}

// Private data.

	private Image ima = null;
	private Paintable painter = null;
}
