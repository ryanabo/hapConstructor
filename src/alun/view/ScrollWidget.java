package alun.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import alun.util.StringFormatter;

public class ScrollWidget extends Scrollbar implements AdjustmentListener
{
	public ScrollWidget(String label, double scl, double init)
	{
		super(Scrollbar.HORIZONTAL);
		setValues(500,100,0,1100);
		setBlockIncrement(100);
		setUnitIncrement(1);
		addAdjustmentListener(this);

		lab = new TextField("",20);
		lab.setEditable(false);
		setLabel(label);

		dp = 3;
		box = new TextField("",5);
		box.setEditable(false);
		scaleBy(scl);
		setValue((int)(init/scale));
		adjustmentValueChanged(null);

		pan = new Panel();
		pan.setLayout(new BorderLayout());
		pan.add(lab,BorderLayout.WEST);
		pan.add(this,BorderLayout.CENTER);
		pan.add(box,BorderLayout.EAST);
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		box.setText(StringFormatter.format(getRealValue(),dp));
	}

	public void scaleBy(double s)
	{
		scale *= s;
		adjustmentValueChanged(null);
	}

	public void setLabel(String s)
	{
		lab.setText(s);
	}

	public Panel getPanel()
	{
		return pan;
	}

	public double getRealValue()
	{
		return getValue()*scale;
	}

	private double scale = 0.001;
	private TextField lab = null;
	private Panel pan = null;
	private TextField box = null;
	private int dp = 3;

	public static void main(String[] args)
	{
		try
		{
			Frame f = new Frame();
			ScrollWidget sw = new ScrollWidget("Widget",10,2);
			f.add(sw.getPanel());
			sw.setLabel("New widget");
			sw.scaleBy(0.1);
			
			f.pack();
			f.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
