package alun.view;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

abstract public class OutputFileListener implements ActionListener
{
	public OutputFileListener(String t)
	{
		title = t;
	}

	abstract public void writeTo(PrintWriter w);

	public void actionPerformed(ActionEvent e)
	{
		try
		{
			Component c = (Component)e.getSource();
			while (!(c instanceof Frame))
				c = c.getParent();

			FileDialog f = new FileDialog((Frame)c,title,FileDialog.SAVE);
			f.setVisible(true);
			String s = f.getFile();
			if (s == null)
			{
				f.dispose();
				return;
			}
			PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(s)));
			writeTo(w);
			w.flush();
			w.close();
			f.dispose();
		}
		catch (IOException x)
		{
			System.err.println("Caught in GraphViewer:ActionListener:actionPerformed()");
			x.printStackTrace();
		}
	}

// Private data.

	private String title = null;
}
