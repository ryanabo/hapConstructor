package alun.gchap;

/**
 A base class from which all observation, single or multi-locus are
 derived.
 An observation is a collection of phenotypes.
*/
abstract public class Observation
{
/**
 Returns the trait for which this is an observation.
*/
	public GeneticTrait getTrait()
	{
		return m;
	}

/**
 Returns the log likelihood for the given observation.
*/
	public double logLikelihood()
	{
		double ll = 0;
		for (int i=0; i<d.length; i++)
			ll += d[i].logLikelihood();
	
		return ll;
	}

/**
 Performs gene counting on this observation until 
 we get some sort of convergence.
*/
	public int geneCountToConvergence()
	{
		int jump = 10;
		int its = 0;
		int count = 0;
		double ll = logLikelihood();
		double t = 0;
		do
		{
			its += jump;
			getTrait().geneCount(jump);
			getTrait().downCode(0);
			t = ll;
			ll = logLikelihood();
			if (Math.abs(ll-t) < Double.MIN_VALUE)
				count++;
			else
				count = 0;
//System.err.println("Log likelihood = "+logLikelihood());
		}
		while (count < 10);
		return its;
	}

	public Phenotype[] getData()
	{
		return d;
	}

	protected GeneticTrait m = null;
	protected Phenotype[] d = null;
}
