package alun.genepi;


public class Penetrance extends GeneticFunction
{
	public Penetrance(Genotype gt, double[][] penet)
	{
		p = penet;
		g = gt;
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
		return p[g.pat()][g.mat()];
	}

	public String toString()
	{
		return "PENET "+getVariables();
	}

// Private data.

	private Genotype g = null;
	private double[][] p = null;
}
