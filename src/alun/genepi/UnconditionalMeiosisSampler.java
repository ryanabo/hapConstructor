package alun.genepi;


public class UnconditionalMeiosisSampler extends LinkageSampler
{
	public UnconditionalMeiosisSampler(LinkageVariables lv)
	{
		this(lv, 1);
	}

	public UnconditionalMeiosisSampler(LinkageVariables v, int f)
	{
		vars = v;
		first = f;
		vars.makeInheritances(first);

		mrec = new double[vars.nLoci()];
		frec = new double[vars.nLoci()];
		for (int j=first+1; j<vars.nLoci(); j++)
		{
			mrec[j] = vars.getDataSource().getMaleRecomFrac(j,j-1);
			frec[j] = vars.getDataSource().getFemaleRecomFrac(j,j-1);
		}
	}

	public void sample()
	{
		for (int i=0; i<vars.nIndividuals(); i++)
			if (!vars.isFounder(i))
			{
				vars.getInheritance(i,first,0).setState( Math.random() <= 0.5 ? 0 : 1);
				vars.getInheritance(i,first,1).setState( Math.random() <= 0.5 ? 0 : 1);

				for (int j=first+1; j<vars.nLoci(); j++)
				{
					vars.getInheritance(i,j,0).setState(Math.random() <= mrec[j] ? 1-vars.getInheritance(i,j-1,0).getState() : vars.getInheritance(i,j-1,0).getState());
					vars.getInheritance(i,j,1).setState(Math.random() <= frec[j] ? 1-vars.getInheritance(i,j-1,1).getState() : vars.getInheritance(i,j-1,1).getState());
				}
			}
	}

	public void initialize()
	{
		sample();
	}

// Private data.

	protected int first = 0;
	protected LinkageVariables vars = null;
	double[] mrec = null;
	double[] frec = null;
}
