package alun.mcld;

import java.util.Collection;

import alun.graph.CarefulNetwork;
import alun.graph.Graph;
import alun.graph.Network;
import alun.util.RandomBag;

public class DecomposableSearch implements GraphMHScheme
{
public int nups = 0;

	public DecomposableSearch(Collection<Locus> l)
	{
		this(l,0.5,0.5);
	}

	public DecomposableSearch(Collection<Locus> l, double o0, double o1)
	{
		opt0 = o0;
		opt1 = o1;
nups = 0;
		b = new RandomBag<Locus>(l);
		g = new CarefulNetwork<Locus,Object>();
		initialize();
		careful = false;
	}

	public Graph<Locus,Object> getGraph()
	{
		return g;
	}

	public void initialize()
	{
		g.clear();
		for (Locus l : b)
			g.add(l);
		jt = new JunctionTree(g);
	}

	public LogLikelihood update(JointScheme js, double thresh)
	{
		if (b.size() < 2)
			return null;
		
		int option = Math.random() < opt0 ? 0 : (Math.random() < opt1 ? 1 : 2);

		switch(option)
		{
		case 2:  return switchVertices(js,thresh);
		case 1:  return moveEdge(js,thresh);
		case 0:
		default: 
			Locus x = b.next();
			Locus y = b.next();
			while (y == x) 
				y = b.next();

			return toggleEdge(x,y,js,thresh);
		}
	}

	protected LogLikelihood toggleEdge(Locus x, Locus y, JointScheme js, double thresh)
	{
		LogLikelihood ll = null;

		if (g.connects(x,y))
		{
			ll = jt.costDisconnect(x,y,js.getCalculator());
			if (careful)
			{
				LogLikelihood oll = oldtryDisconnect(js,thresh,x,y);
				if (oll != null || ll != null)
					if (Math.abs(js.value(oll)-js.getCurrentScore()-js.value(ll)) > 0.000001)
						System.err.println("Disconnect "+oll+" "+ll);
			}
if (ll != null) nups++;
			if (ll == null || js.value(ll) < thresh)
				return null;
			jt.makeDisconnect(x,y);
			g.disconnect(x,y);
		}
		else
		{
			ll = jt.costConnect(x,y,js.getCalculator());
			if (careful)
			{
				LogLikelihood oll = oldtryConnect(js,thresh,x,y);
				if (oll != null || ll != null)
					if (Math.abs(js.value(oll)-js.getCurrentScore()-js.value(ll)) > 0.000001)
						System.err.println("Connect "+oll+" "+ll);
			}
if (ll != null) nups++;
			if (ll == null || js.value(ll) < thresh)
				return null;

			jt.makeConnect(x,y);
			g.connect(x,y);
		}

		ll.add(js.getCurrentLogLike());
		return ll;
	}

// Private data.

	private Network<Locus,Object> g = null;
	private RandomBag<Locus> b = null;
	private JunctionTree jt = null;
	private int its = 0;
	private int maxtries = 100;
	private boolean careful = false;
	private double opt0 = 0.5;
	private double opt1 = 0.5;

	private LogLikelihood switchVertices(JointScheme js, double thresh)
	{
		Locus x = b.next();
		Locus y = b.next();
		int i = 0;
		for ( ; g.getNeighbours(y).equals(g.getNeighbours(x)) && i < maxtries; i++)
			y = b.next();
		if (i == maxtries)
			return null;
		
		LogLikelihood ll = jt.costSwitch(x,y,js.getCalculator());
		if (careful)
		{
			LogLikelihood oll = oldtrySwitch(js,thresh,x,y);
			if (oll != null || ll != null)
				if (Math.abs(js.value(oll)-js.getCurrentScore()-js.value(ll)) > 0.000001)
					System.err.println("Switch "+oll+" "+ll);
		}

		if (js.value(ll) < thresh)
			return null;

		jt.makeSwitch(x,y);
		boolean join = false;
		if (g.connects(x,y))
		{
			join = true;
			g.disconnect(x,y);
		}
		Collection<Locus> nx = g.getNeighbours(x);
		Collection<Locus> ny = g.getNeighbours(y);
		g.remove(x);
		g.remove(y);
		g.add(x);
		g.add(y);

		for (Locus n : nx)
			g.connect(n,y);
		for (Locus n : ny)
			g.connect(n,x);

		if (join)
			g.connect(x,y);

		ll.add(js.getCurrentLogLike());
		return ll;
	}

	private LogLikelihood moveEdge(JointScheme js, double thresh)
	{
		int i = 0;
		Locus x = b.next();
		for (i=0; (g.getNeighbours(x) == null || g.getNeighbours(x).size() == 0) && i < maxtries; i++)
			x = b.next();
		if (i == maxtries)
			return null;

		RandomBag<Locus> d = new RandomBag<Locus>(g.getNeighbours(x));
		Locus y = d.next();

		Locus z = b.next();
		for (i=0; (z == x || g.connects(x,z)) && i < maxtries; i++)
			z = b.next();
		if (i == maxtries)
			return null;
		
		LogLikelihood ll = jt.costMove(x,y,z,js.getCalculator());
/*
		LogLikelihood ll = oldtryMove(js,thresh,x,y,z);
		if (ll != null)
			ll.subtract(js.getCurrentLogLike());
*/
		if (careful)
		{
			LogLikelihood oll = oldtryMove(js,thresh,x,y,z);
			if (oll != null || ll != null)
				if (Math.abs(js.value(oll)-js.getCurrentScore()-js.value(ll)) > 0.000001)
					System.err.println("Move "+oll+" "+ll);
		}

if (ll != null) nups++;

		if (ll == null || js.value(ll) < thresh)
			return null;

		jt.makeMove(x,y,z);
		g.disconnect(x,y);
		g.connect(x,z);
		ll.add(js.getCurrentLogLike());
		return ll;
	}

	private LogLikelihood oldtrySwitch(JointScheme js, double thresh, Locus x, Locus y)
	{
		moveem(x,y);
		LogLikelihood ll = js.getCalculator().calc(getGraph());
		moveem(x,y);
		return ll;
	}

	private void moveem(Locus x, Locus y)
	{
		boolean join = g.connects(x,y);
		if (join)
			g.disconnect(x,y);
		Collection<Locus> nx = g.getNeighbours(x);
		Collection<Locus> ny = g.getNeighbours(y);
		g.remove(x);
		g.remove(y);
		g.add(x);
		g.add(y);
		for (Locus n : nx)
			g.connect(n,y);
		for (Locus n : ny)
			g.connect(n,x);
		if (join)
			g.connect(x,y);
	}

	private LogLikelihood oldtryMove(JointScheme js, double thresh, Locus x, Locus y, Locus z)
	{
		g.disconnect(x,y);
		g.connect(x,z);
		LogLikelihood ll = js.getCalculator().calc(getGraph());
		g.disconnect(x,z);
		g.connect(x,y);
		return ll;
	}

	private LogLikelihood oldtryDisconnect(JointScheme js, double thresh, Locus x, Locus y)
	{
		g.disconnect(x,y);
		LogLikelihood ll = js.getCalculator().calc(getGraph());
		g.connect(x,y);
		return ll;
	}
	
	private LogLikelihood oldtryConnect(JointScheme js, double thresh, Locus x, Locus y)
	{
		g.connect(x,y);
		LogLikelihood ll = js.getCalculator().calc(getGraph());
		g.disconnect(x,y);
		return ll;
	}
}
