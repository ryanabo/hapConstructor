package alun.viewgraph;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import alun.view.OutputFileListener;
import alun.view.PrintingApplet;
import alun.view.ScrollWidget;

public class MapViewer extends PrintingApplet
{
	static { className = "alun.graph.MapViewer" ; }

/**
 Initialises the status of the viewer. Replaces constructor in
 an Applet.
*/
	public void init()
	{
		super.init();
		setMouser(new MovingMouser(this));
		//mo = new LocalMover();
		mo = new NewerLocalMover();
		co = new MapCoordinator(this);
		makeLayout();
		makePanel();
		reset();
	}

	public void reset()
	{
		if (getMap() != null && getMover() != null)
		{
			getMover().set(getMap());
			setChanged();
		}
	}

/**
 Runs the coordinator's move() method in an infinite loop.
*/
	public void loop()
	{
		try
		{
			if (getMap() == null)
			{
				Thread.sleep(100);
				return;
			}
			//Thread.sleep(getMap().getShownVertices().size() < 50 ? 80 : 20);
			//Thread.sleep(100);
			Thread.sleep(winks);
			getMover().move(getMap());
			setChanged();
		}
		catch(Exception e)
		{
			// e.printStackTrace();
		}
	}

	public synchronized void setMap(MappableGraph m)
	{
		setMap(m,true);
	}

	public synchronized void setMap(MappableGraph m, boolean reset)
	{
		g = m;
		if (reset)
			reset();
	}

	public final synchronized MappableGraph getMap()
	{	
		return g;
	}

	public synchronized void setCoordinator(MapCoordinator x)
	{
		co = x;
		//makePanel();
	}

	public final MapCoordinator getCoordinator()
	{
		return co;
	}

	public synchronized void setMover(MapMover x)
	{
		mo = x;
		makePanel();
	}

	public synchronized MapMover getMover()
	{
		return mo;
	}

/*
 Paints the map to the given graphics object.
 Applies the transform in the process.	
*/
	public void paint(Graphics r)
	{
		try
		{
		if (getMap() != null)
			getMap().paint(r);
		}
		catch (Exception e)
		{
			//System.err.println("Caught here");
		}
	}

	public void makeLayout()
	{
		scrollpanel = new Panel();
		getCanvas().setBackground(new Color(255,255,210));
		//getCanvas().setBackground(new Color(150,150,100));
		getCanvas().setSize(800,800);
		add(scrollpanel,BorderLayout.SOUTH);
	
		Panel ppp = new Panel();
		ppp.setLayout(new BorderLayout());
		setButtonPanel(makeButtonPanel());
		ppp.add(getButtonPanel(),BorderLayout.NORTH);
		add(ppp,BorderLayout.EAST);
	}

	public void makePanel()
	{
		AdjustmentListener al = new AdjustmentListener()
		{
			public void adjustmentValueChanged(AdjustmentEvent e)
			{
				ScrollWidget s = (ScrollWidget)(e.getSource());
				int j = -1;		
				for (int i=0; i<scrolls.length && j == -1; i++)
					if (s == scrolls[i])
						j = i;
				getMover().setParameter(j,s.getRealValue());
			} 
		};

		scrollpanel.removeAll();
		if (getCoordinator() != null)
		{
			scrollpanel.setLayout(new GridLayout(getMover().getNParameters(),1));
			scrolls = new Scrollbar[getMover().getNParameters()];
			for (int i=0; i<scrolls.length; i++)
			{
				scrolls[i] = new ScrollWidget(getMover().getParameterName(i),getMover().getParameterScale(i),getMover().getParameterInit(i));
				scrolls[i].addAdjustmentListener(al);
				scrollpanel.add(((ScrollWidget)scrolls[i]).getPanel());
			}
		}
	}

	protected Panel makeButtonPanel()
	{
		Panel b = super.makeButtonPanel();
		b.setLayout(new GridLayout(5,1));
	
		Label lab = new Label("Graph controls",Label.CENTER);
		lab.setBackground(Color.white);
		b.add(lab,0);

		Button but = null;
		but = new Button("Output graph");
		but.addActionListener
		(
			new OutputFileListener("Outputing graph adjacencies")
			{
				public void writeTo(java.io.PrintWriter w)
				{
					w.print(getMap());
				}
			}
		);
		b.add(but);

		but = new Button("Stop positioning");
                but.addActionListener
                (
                        new ActionListener()
                        {
                                public void actionPerformed(ActionEvent e)
                                {
					getThread().safeFlip();
					((Button)e.getSource()).setLabel(getThread().isRunning() ? "Stop positioning" : "Run positioning");
                                }
                        }
                );
                b.add(but);
		return b;
	}

	public void setWinks(int w)
	{
		winks = w;
	}

// Private data. 

	private MappableGraph g = null;
	private MapCoordinator co = null;
	private MapMover mo = null;
	private int winks = 40;

	private Panel scrollpanel = null;
	private Scrollbar[] scrolls = null;
}
