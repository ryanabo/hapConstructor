package alun.genepi;


public class UnorderedPrior extends GenotypePrior
{
	public UnorderedPrior(Genotype gt, double[] allelefreqs)
	{
		super(gt,allelefreqs);
	}

	public double getValue()
	{
		int pa = g.pat();
		int ma = g.mat();
		double res = f[pa] * f[ma];
		if (pa != ma)
			res *= 2;
		return res;
	}
}
