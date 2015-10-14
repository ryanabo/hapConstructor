package alun.mcld;

import java.io.PrintWriter;

import alun.graph.Graph;
import alun.graph.Network;

public class JointScheme
{
	public JointScheme(DataHaplotypeSource s, GraphMHScheme m)
	{
		calc = new DataCalculator(s);

		setCopyBest(false);
		setDistancePenalty(0.0);
		setParameterPenalty(0.5*Math.log(s.nHaplotypes()));
		//setParameterPenalty(0.25*Math.pow(s.nHaplotypes(),0.7));

		setMetro(m);
		setGibbs(s);
	}

	private void setGibbs(HaplotypeSource s)
	{
		gibbs = s;
		curll = calc.calc(current);
		saveCurrent();
		getCalculator().setCache(100);
	}

	private void setMetro(GraphMHScheme m)
	{
		metro = m;
		current = metro.getGraph();
		metro.initialize();
	}

	public GraphMHScheme getSearchScheme()
	{
		return metro;
	}

	public HaplotypeSource getHaplotypeSource()
	{
		return gibbs;
	}

	public LikelihoodCalculator getCalculator()
	{
		return calc;
	}

	public void reconstruct()
	{
		gibbs.maximize(calc.getLDModel(best,false));
	}

	public boolean gibbsUpdate()
	{
		return gibbsUpdate(1);
	}

	public boolean gibbsUpdate(double temp)
	{
		if (!(gibbs instanceof ImperfectHaplotypes))
			return false;

		boolean sample = temp > 0.00001;

		LDModel mod = calc.getLDModel(current,sample);

		if (sample)
			gibbs.update(mod);
		else
			gibbs.maximize(mod);

		calc.clear();

		curll = calc.calc(current);

		if (value(curll) > value(bestll))
		{
			saveCurrent();
			return true;
		}

		return false;
	}
	
	public boolean metropolisUpdate()
	{
		return metropolisUpdate(1);
	}

	public boolean metropolisUpdate(double temp)
	{
		LogLikelihood ll = metro.update(this,temp*Math.log(Math.random()));
		if (ll == null)
			return false;
		curll = ll;
		if (value(curll) > value(bestll))
			saveCurrent();
		return true;
	}

	public void setParameterPenalty(double x)
	{
		parampen = x;
	}

	public double getParameterPenalty()
	{
		return parampen;
	}

	public double getBestScore()
	{	
		return value(bestll);
	}

	public Graph<Locus,Object> getBestGraph()
	{
		return best;
	}

	public Graph<Locus,Object> getCurrentGraph()
	{
		return current;
	}

	public double getCurrentScore()
	{
		return value(curll);
	}

	public LogLikelihood getCurrentLogLike()
	{
		return curll;
	}

	public LogLikelihood getBestLogLike()
	{
		return bestll;
	}

	public void addRemoveAnalysis(PrintWriter w)
	{
		Network<Locus,Object> g = new Network<Locus,Object>(best);
		Locus[] c = (Locus []) g.getVertices().toArray(new Locus[0]);

		w.println("Scores for decomposable models  had by adding and removing single \nedges from final model.");
		w.println("\n");
		w.println("\t\t\t Score \t\t Log likelihood \t df");
		w.println("Final model");
		w.println("\t\t"+value(bestll)+"\t"+bestll.ll()+"\t"+bestll.df());
		w.println("Disconnecting edges");
		for (int i=0; i<c.length; i++)
			for (int j=0; j<i; j++)
				if (g.connects(c[i],c[j]))
				{
					g.disconnect(c[i],c[j]);
					LogLikelihood l = calc.calc(g);
					g.connect(c[i],c[j]);
					if (l != null)
						w.println("\t("+c[i]+","+c[j]+")\t"+value(l)+"\t"+l.ll()+"\t"+l.df());
				}
		w.println("Connecting edges");
		for (int i=0; i<c.length; i++)
			for (int j=0; j<i; j++)
				if (!g.connects(c[i],c[j]))
				{
					g.connect(c[i],c[j]);
					LogLikelihood l = calc.calc(g);
					g.disconnect(c[i],c[j]);
					if (l != null)
						w.println("\t("+c[i]+","+c[j]+")\t"+value(l)+"\t"+l.ll()+"\t"+l.df());
				}
	}

	public void setCopyBest(boolean b)
	{
		copybest = b;
	}

	public void setDistancePenalty(double d)
	{
		distpen = d;
	}

// Private data
	
	protected HaplotypeSource gibbs = null;
	protected GraphMHScheme metro = null;
	protected LikelihoodCalculator calc = null;
	protected Graph<Locus,Object> current = null;
	protected Graph<Locus,Object> best = null;
	protected LogLikelihood bestll = null;
	protected LogLikelihood curll = null;
	private double parampen = 1;
	private double distpen = 1.0;
	private boolean copybest = true;

	protected void saveCurrent()
	{
		if (copybest)
		{
			best = new Network<Locus,Object>(current);
			bestll = calc.calc(best);
		}
		else
		{
			best = current;
			bestll = new LogLikelihood(curll); 
		}
	}

	protected double value(LogLikelihood l)
	{
		return l == null ? 0 : l.ll() - parampen*l.df() - distpen*l.ss();
	}
}
