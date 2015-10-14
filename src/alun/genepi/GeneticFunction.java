package alun.genepi;

import java.util.LinkedHashSet;
import java.util.Set;

import alun.markov.Function;
import alun.markov.Variable;

abstract public class GeneticFunction implements Function
{
	public GeneticFunction()
	{
		s = new LinkedHashSet<Variable>();
	}

	public Set<Variable> getVariables()
	{
		return s;
	}

	protected Set<Variable> s = null;
}
