package alun.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.PageAttributes;
import java.awt.PrintJob;
import java.awt.Toolkit;

public class PrintActiveCanvas extends ActiveCanvas
{
	public PrintActiveCanvas(Paintable p)
	{
		super(p);
		pagatt = new PageType(PageAttributes.MediaType.LETTER,PageAttributes.OrientationRequestedType.PORTRAIT);
	}

	public void paint(Graphics g)
	{
		if (showpage)
		{
			g.setColor(Color.white);
			g.fillRect(0,0,pagatt.getWidth(),pagatt.getHeight());
			g.setColor(Color.black);
			g.drawRect(0,0,pagatt.getWidth(),pagatt.getHeight());
		}
		super.paint(g);
	}

/**
 Output a screen dump of the current image.
*/
	public void screenDump()
	{
       		Toolkit t = Toolkit.getDefaultToolkit();
		if (t == null)
			return;

		Component c = this;
		while (!(c instanceof Frame))
			c = c.getParent();

               	PrintJob j = t.getPrintJob((Frame)c,"Canvas screen dump",pagatt.getJobAttributes(),pagatt.getAttributes());
		if (j == null)
			return;

		Graphics g = j.getGraphics();
		if (g == null)
			throw new RuntimeException("\n\t\tGraphics object returned form getGraphics() is null");

		super.paint(g);
		g.dispose();
               	j.end();
	}

/**
 Set the current page for output.
*/
	public void setPage(PageType t)
	{
		if (t.getAttributes() == null)
		{
			showpage = false;
		}
		else
		{
			showpage = true;
			pagatt = t;
		}
	}


// Private data, methods and classes.

	private boolean showpage = false; 
	private PageType pagatt = null;
}
