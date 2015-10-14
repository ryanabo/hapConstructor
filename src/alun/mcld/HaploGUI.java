package alun.mcld;

import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import alun.util.StringFormatter;
import alun.viewgraph.AnnealMover;
import alun.viewgraph.MapEditor;

public class HaploGUI extends MapEditor 
{
	public HaploGUI(boolean generic)
	{
		super();
		init();
		if (generic)
			am = new AnnealMover(10);
		else
			am = new DistanceAnnealMover(10);
		setMover(am);
	}

	public boolean getDone()
	{
		return done;
	}

	public double getDistancePenalty()
	{
		return ((DistanceAnnealMover)am).getDistancePenalty();
	}

	public double getAnneal()
	{
		return am.getAnneal();
	}

	public void showBest(double d)
	{
		scrText.setText(StringFormatter.format(d,2));
	}

	public void showIterations(int i)
	{
		itText.setText(" "+i);
	}

	public void showCurrent(double d)
	{
		curText.setText(StringFormatter.format(d,2));
	}
	
	public void showTime(long t)
	{	
		int sec = (int) (t/1000);
		int min = sec / 60;
		sec -= 60*min;
/*
		timText.setText(min+":"+(sec<10?"0":"")+sec);
*/
		int hrs = min / 60;
		min -= 60*hrs;
		timText.setText(hrs+":"+(min<10?"0":"")+min+":"+(sec<10?"0":"")+sec);
	}

// Private data.

	protected Panel makeButtonPanel()
	{
		Panel b = super.makeButtonPanel();
		GridLayout l = (GridLayout)b.getLayout();
		l.setRows(l.getRows()+6);

		Label lab = new Label("Annealing",Label.CENTER);
		lab.setBackground(Color.white);
		b.add(lab);

		Panel its = new Panel();
		its.setLayout(new GridLayout(1,2));
		its.add(new Label("Iteration"));
		itText = new TextField("0");
		itText.setEditable(false);
		its.add(itText);
		b.add(its);

		Panel cur = new Panel();
		cur.setLayout(new GridLayout(1,2));
		cur.add(new Label("Current score"));
		curText = new TextField("");
		curText.setEditable(false);
		cur.add(curText);
		b.add(cur);

		Panel scr = new Panel();
		scr.setLayout(new GridLayout(1,2));
		scr.add(new Label("Best score"));
		scrText = new TextField("");
		scrText.setEditable(false);
		scr.add(scrText);
		b.add(scr);

		Panel tim = new Panel();
		tim.setLayout(new GridLayout(1,2));
		tim.add(new Label("Time"));
		timText = new TextField("");
		timText.setEditable(false);
		tim.add(timText);
		b.add(tim);

		Button but = new Button("Stop searching");
		but.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setDone();
					((Button)e.getSource()).setLabel("Done");
				}
			}
		);
		b.add(but);

		return b;
	}

	protected void setDone()
	{
		done = true;
	}

// Private data 

	private AnnealMover am = null;
	private boolean done = false;
	private TextField itText = null;
	private TextField scrText = null;
	private TextField curText = null;
	private TextField timText = null;
}
