package alun.genepi;


public class ErrorPenetrance extends GeneticFunction
{
	public ErrorPenetrance(Genotype gt, Error er, double[][] penet)
	{
		g = gt;
		e = er;
		p = penet;
		q = 1.0/(p.length * p.length);
		s.add(g);
		s.add(e);
	}

/*
	public LinkedHashSet<Variable> getVariables()
	{
		LinkedHashSet<Variable> s = new LinkedHashSet<Variable>();
		s.add(g);
		s.add(e);
		return s;
	}
*/

	public double getValue()
	{
		return e.getState() == 0 ? p[g.pat()][g.mat()] : q ;
	}

	public String toString()
	{
		return "ERPEN "+getVariables();
	}

// Private data.

	private Genotype g = null;
	private Error e = null;
	private double[][] p = null;
	private double q = 0;
}
