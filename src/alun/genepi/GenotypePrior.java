package alun.genepi;


/**
 A function that returns the prior probability of the 
 genotype as the product of the priors for the paternal 
 and maternal alleles.
*/
public class GenotypePrior extends GeneticFunction
{
	public GenotypePrior(Genotype gt, double[] allelefreqs)
	{
		f = allelefreqs;
		g =gt;
		s.add(g);
	}

/*
	public LinkedHashSet<Variable> getVariables()
	{
		return new LinkedHashSet<Variable>(Collections.singleton(g));
	}
*/

	public double getValue()
	{
		return f[g.pat()]*f[g.mat()];
	}

	public String toString()
	{
		return "PRIOR "+getVariables();
	}

// Private data.

	protected Genotype g = null;
	protected double[] f = null;
}
