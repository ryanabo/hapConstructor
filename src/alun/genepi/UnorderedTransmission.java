package alun.genepi;


public class UnorderedTransmission extends MendelianTransmission
{
	public UnorderedTransmission(Genotype bod, Genotype father, Genotype mother)
	{
		super(bod,father,mother);
	}

	public double getValue()
	{
		double t = inherit(x.pat(),pa.pat(),pa.mat()) * inherit(x.mat(),ma.pat(),ma.mat());
		if (x.pat() != x.mat())
			t += inherit(x.mat(),pa.pat(),pa.mat()) * inherit(x.pat(),ma.pat(),ma.mat());
		return t;
	}
}
