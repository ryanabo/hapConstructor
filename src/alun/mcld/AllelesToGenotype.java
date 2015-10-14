package alun.mcld;

import java.util.LinkedHashSet;

import alun.genepi.Allele;
import alun.genepi.Genotype;
import alun.markov.Function;
import alun.markov.Variable;

public class AllelesToGenotype implements Function
{
	public AllelesToGenotype(Allele pat, Allele mat, Genotype gt)
	{
		p = pat;
		m = mat;
		g = gt;
	}

	public LinkedHashSet<Variable> getVariables()
	{
		LinkedHashSet<Variable> s = new LinkedHashSet<Variable>();
		s.add(p);
		s.add(m);
		s.add(g);
		return s;
	}

	public double getValue()
	{
		return g.pat() == p.getState() && g.mat() == m.getState() ? 1 : 0;
	}

	public String toString()
	{
		return "A2G "+getVariables();
	}

// Private data.

	private Allele p = null;
	private Allele m = null;
	private Genotype g = null;
}
