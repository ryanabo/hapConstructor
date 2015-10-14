package alun.genepi;

import java.util.LinkedHashSet;

import alun.markov.Function;
import alun.markov.Variable;

public class AlleleErrorPenetrance implements Function //extends GeneticFunction
{
	public AlleleErrorPenetrance(Allele paternal, Allele maternal, Error e, double[][] penet)
	{
		pat = paternal;
		mat = maternal;
		err = e;
		fix(penet);
/*
		s.add(pat);
		s.add(mat);
		s.add(err);
*/
	}

	public LinkedHashSet<Variable> getVariables()
	{
		LinkedHashSet<Variable> s = new LinkedHashSet<Variable>();
		s.add(pat);
		s.add(mat);
		s.add(err);
		return s;
	}

	public double getValue()
	{
		if (p == null)
			return 1;
		return err.getState() == 0 ? p[pat.getState()][mat.getState()] : q; //1.0/p.length/p.length ;
	}

	public String toString()
	{
		return "APENET "+getVariables();
	}

	public void fix(double[][] penet)
	{
		p = penet;
		if (p != null)
			q = 1.0/(p.length*p.length);
	}

// Private data.

	private Allele pat = null;
	private Allele mat = null;
	private Error err = null;
	private double[][] p = null;
	private double q = 0;
}
