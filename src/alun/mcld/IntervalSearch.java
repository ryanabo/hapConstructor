package alun.mcld;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import alun.graph.CarefulNetwork;
import alun.graph.Graph;
import alun.graph.Network;
import alun.util.BasicInterval;
import alun.util.Interval;
import alun.util.IntervalTree;
import alun.util.RandomBag;

public class IntervalSearch implements GraphMHScheme
{
public int gups = 0;

	public IntervalSearch(Collection<Locus> l, double minover)
	{
		gups = 0;

		minoverlap = minover;
		double bot = l.iterator().next().getPosition();
		double top = bot;
		for (Locus ll : l)
		{
			if (bot > ll.getPosition())
				bot = ll.getPosition();
			if (top < ll.getPosition())
				top = ll.getPosition();
			ll.setMiddle(ll.getPosition());
			ll.setLength(0.5);
		}

		g = new IntervalTree<Locus>(bot-1,top+1);
		gr = new CarefulNetwork<Locus,Object>();
		for (Locus ll : l)
		{
			g.add(ll);
			gr.add(ll);
		}

		loc = new RandomBag<Locus>();
		loc.addAll(l);

	}

	protected Interval pert(Locus l)
	{
		double opt = Math.random();

		double newL = l.getMiddle();
		if (opt < 0.75)
			newL = g.lowerBound() + Math.random() * (g.upperBound()-g.lowerBound());

		double newR = l.getLength();
		if (opt > 0.25) 
			newR += ( Math.random() < 0.5 ? Math.log(Math.random()) : -Math.log(Math.random()) );

		if (newR < 0)
			newR = -newR;

		newL -= newR/2;
		newR += newL;

		return new BasicInterval(newL,newR);
	}

	public LogLikelihood calc(double left, double right, JointScheme js, Set<Locus> out)
	{
		Set<Locus> s = g.intersectors(left,right);
		out.addAll(s);
		Locus[] a = new TreeSet<Locus>(s).toArray(new Locus[0]);
		
		for (int i=0; i<a.length; i++)
		{
			a[i].reset();
			s.remove(a[i]);
			for (Locus u : s)
				if (a[i].intersection(u) > minoverlap)
					a[i].getInvol().add(u);
		}

		return js.getCalculator().calc(a);
	}

	public LogLikelihood calc(double left, double right, JointScheme js)
	{
		Set<Locus> s = g.intersectors(left,right);
		Locus[] a = new TreeSet<Locus>(s).toArray(new Locus[0]);
		
		for (int i=0; i<a.length; i++)
		{
			a[i].reset();
			s.remove(a[i]);
			for (Locus u : s)
				if (a[i].intersection(u) > minoverlap)
					a[i].getInvol().add(u);
		}

		return js.getCalculator().calc(a);
	}

	public LogLikelihood update(JointScheme js, double thresh)
	{
		Locus l = loc.next();
		double oldL = l.getLeft();
		double oldR = l.getRight();

		Interval p = pert(l);
		double newL = p.getLeft();
		double newR = p.getRight();

		boolean intersect = Math.abs(newR-newL) + Math.abs(oldR-oldL) > Math.abs((newL+newR) - (oldL+oldR));
		double farR = oldL < newL ? oldL : newL ;
		double farL = oldR > newR ? oldR : newR ;

		LogLikelihood ll = new LogLikelihood(js.getCurrentLogLike());

		if (intersect)
		{
			ll.subtract(calc(farL,farR,js));
		}
		else
		{
			ll.subtract(calc(oldL,oldR,js));
			ll.subtract(calc(newL,newR,js));
		}

		g.remove(l);
		l.setRight(newR);
		l.setLeft(newL);
		g.add(l);
		
		Set<Locus> newneibs = new HashSet<Locus>();
		if (intersect)
		{
			ll.add(calc(farL,farR,js,newneibs));
		}
		else
		{
			ll.add(calc(oldL,oldR,js));
			ll.add(calc(newL,newR,js,newneibs));
		}

		if (js.value(ll) - js.getCurrentScore() >= thresh)
		{
			Collection<Locus> oldneibs = gr.getNeighbours(l);

			gr.remove(l);
			gr.add(l);
			newneibs.remove(l);
			for (Locus n : newneibs)
				if (l.intersection(n) > minoverlap)
					gr.connect(l,n);

			if (!oldneibs.equals(gr.getNeighbours(l)))
				gups++;

			return ll;
		}

		g.remove(l);
		l.setRight(oldR);
		l.setLeft(oldL);
		g.add(l);

		return null;
	}

	public Graph<Locus,Object> getGraph()
	{
		return gr;
	}

	public void initialize()
	{
		for (Locus l : loc)
			g.remove(l);
		gr.clear();
		for (Locus l : loc)
		{
			l.setMiddle(l.getPosition());
			l.setLength(0.5);
			g.add(l);
			gr.add(l);
		}
	}

// Private data.

	protected IntervalTree<Locus> g = null;
	protected RandomBag<Locus> loc = null;
	protected Network<Locus,Object> gr = null;
	protected double minoverlap = 0;
}
