package alun.mcld;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.io.PrintWriter;

import alun.view.OutputFileListener;
import alun.viewgraph.LabelledBlob;
import alun.viewgraph.Mappable;

public class DiploGUI extends HaploGUI
{
	public DiploGUI(JointScheme s)
	{
		this(s,false);
	}

	public DiploGUI(JointScheme s, boolean generic)
	{
		super(generic);
		search = s;
	}

	public void flash(Color c)
	{
		Mappable[] v = (Mappable[])getMap().getAllVertices().toArray(new Mappable[0]);
		for (int i=0; i<v.length; i++)
			((LabelledBlob)v[i]).setColor(c);
	}

	public boolean getMaximizing()
	{
		return maximizing;
	}

// Private data 

	private JointScheme search = null;
	private boolean maximizing = false;

	protected Panel makeButtonPanel()
	{
		Panel b = super.makeButtonPanel();
		GridLayout l = (GridLayout)b.getLayout();
		int k = l.getRows();
		l.setRows(k+1);

/*
		Button but = new Button("Sampling complete haplotypes");
		but = new Button("Sampling complete haplotypes");
		but.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					maximizing = !maximizing;
					((Button)e.getSource()).setLabel(maximizing ? "Maximized reconstruction" : "Sampled reconstruction");
				}
			}
		);
		b.add(but,k-1);
*/

		return b;
	}

	protected void setDone()
	{
		if (getDone())
			return;

		super.setDone();
	
		Panel mybuttonpanel = getButtonPanel();
		GridLayout l = (GridLayout)mybuttonpanel.getLayout();
		l.setRows(l.getRows()+3);
		
		Label outlab = new Label("Output",Label.CENTER);
		outlab.setBackground(Color.white);
		mybuttonpanel.add(outlab);

		Button butt = null;
/*
		butt = new Button("Sampled haplotype freqs");
		butt.addActionListener
		(
			new OutputFileListener("Outputing estimated haplotype frequencies")
			{
				public void writeTo(PrintWriter w)
				{
					w.write("ESTIMATED HAPLOTYPE FREQUENCIES");
					w.flush();
				}
			}
		);
		mybuttonpanel.add(butt);
*/

		butt = new Button("Reconstructed haplotypes");
		butt.addActionListener
		(
			new OutputFileListener("Outputing reconstructed phase know haplotypes")
			{
				public void writeTo(PrintWriter w)
				{
					search.reconstruct();
					DataHaplotypeSource h = (DataHaplotypeSource) search.getHaplotypeSource();
					for (int i=0; i<h.nHaplotypes(); i++)
					{
						int[] t = h.getHaplotype(i);
						for (int j=0; j<t.length; j++)
							w.print(" "+(1+t[j]));
						w.println();
					}
				}
			}
		);
		mybuttonpanel.add(butt);

		butt = new Button("Output model");
		butt.addActionListener
		(
			new OutputFileListener("Outputing graphical model tables")
			{
				public void writeTo(PrintWriter w)
				{
					//w.println(search.getCalculator().getLDModel(search.getBestGraph(),true));
					w.println(search.getCalculator().getLDModel(search.getBestGraph(),false));
				}
			}
		);
		mybuttonpanel.add(butt);

		butt = new Button("Add/remove analysis");
		butt.addActionListener
		(
			new OutputFileListener("Outputting added/removed edge analysis")
			{
				public void writeTo(PrintWriter w)
				{
					search.addRemoveAnalysis(w);
				}
			}
		);
		mybuttonpanel.add(butt);

		Component f = this;
		while (! (f instanceof Frame))
			f = f.getParent();
		((Frame)f).pack();
		f.paintAll(f.getGraphics());
	}
}
