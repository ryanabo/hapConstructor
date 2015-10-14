package alun.mcld;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Vector;

import alun.graph.GraphFunction;
import alun.graph.Network;

public class WindowingScheme extends JointScheme
{
	public WindowingScheme(DataHaplotypeSource s, LimitedIntervalSearch m)
	{
		super(s,m);

		lim = m;

		wind = new Vector<Locus>();
		need = new LinkedHashSet<Locus>();
		edge = new LinkedHashSet<Locus>();

		subgraph = new Network<Locus,Object>();
	}

	public void setWindow(int first, int width)
	{
		Locus[] loc = lim.getLoci();
		need.clear();
		edge.clear();

		int hi = first+width;
		if (hi > loc.length)
			hi = loc.length;

		low = first;
		if (low < 0 || low >=loc.length)
			low = 0;

		wid = hi - low;

		for (int i=low; i<low+wid; i++)
			need.add(loc[i]);

		for (int i=low-1; i>=0; i--)
		{
			if (loc[i].getPosition() >= loc[low].getPosition() - 2*lim.maxExtent())
			{
				need.add(loc[i]);
				edge.add(loc[i]);
			}
			else
				break;
		}

		for (int i=low+wid; i<loc.length; i++)
		{
			if (loc[i].getPosition() <= loc[low+wid-1].getPosition() + 2*lim.maxExtent())
			{
				need.add(loc[i]);
				edge.add(loc[i]);
			}
			else
				break;
		}
	}

	public boolean gibbsUpdate(double temp)
	{
		if (!(gibbs instanceof ImperfectHaplotypes))
			return false;

		subgraph.clear();
		GraphFunction.addInducedSubgraph(subgraph,need,current);
		LogLikelihood oldscore = calc.calc(subgraph);

		boolean sample = temp > 0.00001;

		LDModel mod = calc.getLDModel(subgraph,sample);
		mod.removeVariables(edge);

		if (sample)
			gibbs.update(mod);
		else
			gibbs.maximize(mod);

		calc.clear();
		LogLikelihood newscore = calc.calc(subgraph);

		//curll = calc.calc(current);
		curll.subtract(oldscore);
		curll.add(newscore);

		if (value(curll) > value(bestll))
		{
			saveCurrent();
			return true;
		}

		return false;
	}
	
	public boolean metropolisUpdate(double temp)
	{
		int i = low + (int)(Math.random()*wid);
		return metropolisUpdate(i,temp);
	}

	public boolean metropolisUpdate(int i, double temp)
	{
		if (i < low)
			throw new RuntimeException("Index supplied is below lower end of window");
		if (i >= low + wid)
			throw new RuntimeException("Index supplied is above upper end of window");

		LogLikelihood ll = lim.update(i,this,temp*Math.log(Math.random()));

		if (ll == null)
			return false;
		curll = ll;

		if (value(curll) > value(bestll))
			saveCurrent();
		return true;
	}

	public int first()
	{
		return low;
	}
	
	public int width()
	{
		return wid;
	}
	
	private int low = 0;
	private int wid = 0;
	private LimitedIntervalSearch lim = null;
	private Vector<Locus> wind = null;
	private Collection<Locus> need = null;
	private Collection<Locus> edge = null;
	private Network<Locus,Object> subgraph = null;
}
