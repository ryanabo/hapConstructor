package alun.view; 

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class PrintingApplet extends ViewingApplet
{
	static { className = "alun.view.PrintingApplet";}

	public void init()
	{
		super.init();
		setCanvas(new PrintActiveCanvas(this));
		setButtonPanel(makeButtonPanel());
		add(getButtonPanel(),BorderLayout.SOUTH);
		getCanvas().setBackground(Color.cyan);
		getCanvas().setSize(600,600);
	}

// Protected methods.

	protected Panel getButtonPanel()
	{
		return buttpan;
	}
	
	protected void setButtonPanel(Panel p)
	{
		buttpan = p;
	}

	protected Panel makeButtonPanel()
	{
		Panel buttonpanel = new Panel();
		buttonpanel.setLayout(new GridLayout(1,2));

		Choice cc = new Choice();
		cc.addItemListener
		(
			new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					String s = (String) e.getItem();
					for (int i=0; i<PageType.list.length; i++)
						if (s.equals(PageType.list[i].toString()))
							if (getCanvas() instanceof PrintActiveCanvas)
								((PrintActiveCanvas)getCanvas()).setPage(PageType.list[i]);
				}
			}
		);
		
		for (int i=0; i<PageType.list.length; i++)
			cc.add(PageType.list[i].toString());
		buttonpanel.add(cc);

		Button but = new Button("Print");
		but.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (getCanvas() instanceof PrintActiveCanvas)
						((PrintActiveCanvas)getCanvas()).screenDump();
				}

			}
		);
		buttonpanel.add(but);

		return buttonpanel;
	}

// Private data

	private Panel buttpan;
}
