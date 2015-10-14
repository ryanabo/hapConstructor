package alun.mcld;

import java.awt.Frame;
import java.util.LinkedHashSet;
import java.util.Set;

import alun.graph.Network;
import alun.markov.Function;
import alun.markov.Variable;
import alun.util.Interval;
import alun.viewgraph.DynamicMappableMap;
import alun.viewgraph.LabelledBlob;
import alun.viewgraph.MapViewer;
import alun.viewgraph.Mappable;

public class Locus extends LabelledBlob implements Mappable, Variable, Interval, Comparable<Locus>
{
	public Locus(int l, int na)
	{
		this(l,na,0);
	}

	public int compareTo(Locus x)
	{
		if (getRight() < x.getRight())
			return -1;
		if (getRight() > x.getRight())
			return 1;
		return 0;
	}

	public Locus(int l, int na, double p)
	{
		setShowText(true);
		//setColor(java.awt.Color.cyan);
		setColor(java.awt.Color.yellow);
		index = l;
		invol = new LinkedHashSet<Locus>();
		nalleles = na;
		position = p;
		located = true;
	}

// These first methods are to do with using a Locus to
// computute likelihoods and degrees of freedom for graphical models.

	public final void reset()
	{
		count = 0;
		done = false;
		invol.clear();
	}

	public final int getCount()
	{
		return count;
	}

	public final void setCount(int i)
	{
		count = i;
	}
	
	public final void setDone(boolean b)
	{
		done = b;
	}

	public final boolean getDone()
	{
		return done;
	}

	public final int  getIndex()
	{
		return index;
	}

	public final Set<Locus> getInvol()
	{
		return invol;
	}

	public double getPosition()
	{
		return position;
	}

	public void setName(String s)
	{
		name = s;
	}
	
	public boolean isLocated()
	{
		return located;
	}

	public void setLocated(boolean b)
	{
		located = b;
	}

	public String toString()
	{
		//return name == null ? index+"" : index+"="+name;
		//return "("+StringFormatter.format(getLeft(),2,3)+","+StringFormatter.format(getRight(),2,3)+")";
		//return index +" "+getLeft()+" "+getRight();
		 return name == null ? index+"" : name;
	}

// These next functions are to make a locus a Variable
// so that they can be used to defined products for 
// other graphical models.

	public void init()
	{
		state = -1;
	}

	public boolean next()
	{
		if (++state == nalleles)
		{
			state = -1;
			return false;
		}
		return true;
	}

	public int getState()
	{
		return state;
	}

	public boolean setState(int i)
	{
		if (i < 0 || i >= nalleles)
			return false;
		state = i;
		return true;
	}

	public int getNStates()
	{
		return nalleles;
	}

	public int[] getStates()
	{
		int[] x = new int[nalleles];
		for (int i=0; i<x.length; i++)
			x[i] = i;
		return x;
	}

	public void setStates(int[] x)
	{
	}

	public void setStates(Function f)
	{
	}

// These next functions are to make a Locus an Interval so that
// it can be sorted easily in an interval graph.

	public void setMiddle(double d)
	{
		mid = d;
	}

	public void setLength(double d)
	{
		len = Math.abs(d);
	}

	public void setRight(double d)
	{
		double l = getLeft();
		len = d - l;
		mid = (d + l) / 2;
	}

	public void setLeft(double d)
	{
		double r = getRight();
		len = r - d;
		mid = (r + d) / 2;
	}

	public double getMiddle()
	{
		return mid;
	}

	public double getLength()
	{
		return len;
	}

	public double getLeft()
	{
		return mid - len/2;
	}

	public double getRight()
	{
		return mid + len/2;
	}

	public boolean intersects(Interval i)
	{
		return Math.abs(getMiddle() - i.getMiddle()) < (Math.abs(getLength()) + Math.abs(i.getLength()))/2.0;
	}

	public boolean intersects(double p)
	{
		return Math.abs(getMiddle() - p) < Math.abs(getLength()/2.0);
	}	

	public double intersection(Interval i)
	{
		double top = getRight();
		if (top > i.getRight())
			top = i.getRight();
		double bot = getLeft();
		if (bot < i.getLeft())
			bot = i.getLeft();

		return bot > top ? 0 : top - bot;
	}

	public double intersection(double a, double b)
	{
		double top = getRight();
		if (top > b)
			top = b;
		double bot = getLeft();
		if (bot < a)
			bot = a;

		return bot > top ? 0 : top - bot;
	}

// Private data.
	private int index = 0;
	private int count = 0;
	private boolean done = false;
	private Set<Locus> invol = null;
	private int nalleles = 0;
	private double position = 0;
	private int state = 0;
	private double mid = 0;
	private double len = 0;
	private String name = null;
	private boolean located = false;

	public static void main(String[] args)
	{
		try
		{
			Network<Locus,Object> net = new Network<Locus,Object>();
			Locus[] b = new Locus[500];
			for (int i=0; i<b.length; i++)
				b[i] = new Locus(i,10,1+i);
			
			for (int i=0; i<b.length; i++)
			{
				net.add(b[i]);
				for (int j=i-20; j<i; j+=2)
					if (j >= 0)
						net.connect(b[j],b[i]);
			}
			
			MapViewer r = new MapViewer();
			r.init();
			r.setMap(new DynamicMappableMap<Locus>(net));
			Frame f = new Frame();
			f.add(r);
			f.pack();
			f.setVisible(true);
			r.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
