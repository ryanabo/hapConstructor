package alun.genepi;


public class MendelianTransmission extends GeneticFunction
{
	public MendelianTransmission(Genotype bod, Genotype father, Genotype mother)
	{
		x = bod;
		pa = father;
		ma = mother;
		s.add(x);
		s.add(pa);
		s.add(ma);
	}

/*
	public LinkedHashSet<Variable> getVariables()
	{
		LinkedHashSet<Variable> v = new LinkedHashSet<Variable>();
		v.add(x);
		v.add(pa);
		v.add(ma);
		return v;
	}
*/

	public double getValue()
	{
		return inherit(x.pat(),pa.pat(),pa.mat()) * inherit(x.mat(),ma.pat(),ma.mat());
	}

	public final double inherit(int a, int b, int c)
	{
		return a == b ? (a == c ? 1 : 0.5) : (a == c ? 0.5 : 0);
	}

	public String toString()
	{
		return "TRANS "+getVariables();
	}

// Protected data.

	protected Genotype x = null;
	protected Genotype pa = null;
	protected Genotype ma = null;
}
