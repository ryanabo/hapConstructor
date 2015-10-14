package alun.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FrameQuitter extends WindowAdapter
{
	public void windowClosing(WindowEvent e)
	{
		System.exit(0);
	} 

	public static void main(String[] args)
	{
		java.awt.Canvas c = new java.awt.Canvas();
		c.setSize(100,100);
		java.awt.Frame f = new java.awt.Frame();
		f.addWindowListener(new FrameQuitter());
		f.add(c);
		f.pack();
		f.setVisible(true);
	}
}
