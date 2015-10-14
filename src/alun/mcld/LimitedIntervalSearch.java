package alun.mcld;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import alun.graph.CarefulNetwork;
import alun.graph.Graph;

public class LimitedIntervalSearch implements GraphMHScheme
{
	public LimitedIntervalSearch(Collection<Locus> locs, double maxextent, double minoverlap)
	{
		max = maxextent;
		min = minoverlap;

		loc = locs.toArray(new Locus[0]);
		for (int i=0; i<loc.length; i++)
			for (int j=i; j>0 && loc[j].getPosition() < loc[j-1].getPosition(); j--)
			{
				Locus temp = loc[j];
				loc[j] = loc[j-1];
				loc[j-1] = temp;
			}
		
		g = new CarefulNetwork<Locus,Object>(loc.length);
		for (int i=0; i<loc.length; i++)
			g.add(loc[i]);

		initialize();
	}

	public Locus[] getLoci()
	{
		return loc;
	}

	public double maxExtent()
	{
		return max;
	}

	public void initialize()
	{
		for (int i=0; i<loc.length; i++)
		{
			g.disconnect(loc[i]);
			loc[i].setLeft(loc[i].getPosition());
			loc[i].setRight(loc[i].getPosition());
		}
	}

	public Graph<Locus,Object> getGraph()
	{
		return g;
	}

	private LogLikelihood calc(double left, double right, int i, JointScheme js)
	{
		Set<Locus> s = new TreeSet<Locus>();
		s.add(loc[i]);
		
		for (int j=i-1; j>=0 && loc[j].getPosition()+max >= left; j--)
			if (loc[j].getRight() >= left)
				s.add(loc[j]);

		for (int j=i+1; j<loc.length && loc[j].getPosition()-max <= right; j++)
			if (loc[j].getLeft() <= right)
				s.add(loc[j]);

		Locus[] a = s.toArray(new Locus[0]);

                for (int j=0; j<a.length; j++)
                {
                        a[j].reset();
                        s.remove(a[j]);
                        for (Locus u : s)
                                if (a[j].intersection(u) > min)
                                        a[j].getInvol().add(u);
                }

                return js.getCalculator().calc(a);
	}


	public LogLikelihood update(JointScheme js, double thresh)
	{
		int i = (int)(loc.length*Math.random());
		return update(i,js,thresh);
	}

	public LogLikelihood update(int i, JointScheme js, double thresh)
	{
		double oldL = loc[i].getLeft();
		double oldR = loc[i].getRight();
		double newL = loc[i].getPosition() - Math.random() * max;
		double newR = loc[i].getPosition() + Math.random() * max;
		double left = oldL < newL ? oldL : newL;
		double right = oldR > newR ? oldR : newR;


		LogLikelihood ll = new LogLikelihood(js.getCurrentLogLike());
		ll.subtract(calc(left,right,i,js));
		loc[i].setLeft(newL);
		loc[i].setRight(newR);
		ll.add(calc(left,right,i,js));

		if (js.value(ll) - js.getCurrentScore() >= thresh)
		{
			g.disconnect(loc[i]);

			for (int j=i-1; j>=0 && loc[j].getPosition()+max > loc[i].getLeft()+min; j--)
				if (loc[i].intersection(loc[j]) > min)
					g.connect(loc[i],loc[j]);

			for (int j=i+1; j<loc.length && loc[j].getPosition()-max < loc[i].getRight()-min; j++)
				if (loc[i].intersection(loc[j]) > min)
					g.connect(loc[i],loc[j]);
			
			return ll;
		}
		
		loc[i].setLeft(oldL);
		loc[i].setRight(oldR);
		return null;
	}

// Private data.

	private CarefulNetwork<Locus,Object> g = null;
	private Locus[] loc = null;
	private double max = 0;
	private double min = 0;
}
