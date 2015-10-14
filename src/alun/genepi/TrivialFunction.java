package alun.genepi;

import alun.markov.Variable;

public class TrivialFunction extends GeneticFunction
{
	public TrivialFunction(Variable vv)
	{
		v = vv;
		s.add(v);
	}

/*
	public LinkedHashSet<Variable> getVariables()
	{
		return new LinkedHashSet<Variable>(Collections.singleton(v));
	}
*/

	public double getValue()
	{
		return 1;
	}

	public String toString()
	{
		return "TRIV "+getVariables();
	}

// Private data.

	private Variable v = null;
}
