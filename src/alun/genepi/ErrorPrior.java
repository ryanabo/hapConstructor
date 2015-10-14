package alun.genepi;

import java.util.LinkedHashSet;

import alun.markov.Function;
import alun.markov.Variable;

public class ErrorPrior implements Function //extends GeneticFunction
{
	public ErrorPrior(Error er, double pri)
	{
		e = er;
		p = pri;
//		s.add(e);
	}

	public LinkedHashSet<Variable> getVariables()
	{
		LinkedHashSet<Variable> s = new LinkedHashSet<Variable>();
		s.add(e);
		return s;
	}

	public double getValue()
	{
		return e.getState() == 0 ? 1-p : p;
	}

	public void set(double pri) 
	{
		p = pri;
	}

	public Error getError()
	{
		return e;
	}

	private Error e = null;
	private double p = 0;
}
