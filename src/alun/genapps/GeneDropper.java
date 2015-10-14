package alun.genapps;

import alun.genepi.Genotype;
import alun.genepi.GenotypeOnlyDropSampler;
import alun.genepi.LinkageVariables;
import alun.linkage.LinkageDataSet;
import alun.linkage.LinkageIndividual;
import alun.linkage.LinkageInterface;
import alun.linkage.LinkageLocus;
import alun.linkage.NumberedAlleleLocus;
import alun.linkage.NumberedAllelePhenotype;
import alun.mcld.LDModel;

public class GeneDropper
{
	public GeneDropper(LinkageDataSet d)
	{
	//	vars = new LinkageVariables(new LinkageInterface(d),0);
	//	mc = new GenotypeOnlyDropSampler(vars,0);
	//	loc = d.getParameterData().getLoci();
	//	ind = d.getPedigreeData().getIndividuals();

		this(d,new LDModel(new LinkageInterface(d)));
	}

	public GeneDropper(LinkageDataSet d, LDModel ld)
	{
		vars = new LinkageVariables(new LinkageInterface(d),0);
		mc = new GenotypeOnlyDropSampler(vars,0,ld);
		loc = d.getParameterData().getLoci();
		ind = d.getPedigreeData().getIndividuals();
	}

	public void geneDrop()
	{
		geneDrop(false);
	}

	public void geneDrop(boolean allout)
	{
		mc.sample();

		for (int i=0; i<loc.length; i++)
		{
			if (loc[i] instanceof NumberedAlleleLocus)
			{
				for (int j=0; j<ind.length; j++)
				{
					NumberedAllelePhenotype p = (NumberedAllelePhenotype) ind[j].getPhenotype(i);
					Genotype g = vars.getGenotype(j,i);
					if (p.a1 > 0 || allout)	p.a1 = 1+g.pat();
					if (p.a2 > 0 || allout)	p.a2 = 1+g.mat();
				}
			}
		}
	}

// Private data.

	private GenotypeOnlyDropSampler mc = null;
	private LinkageLocus[] loc = null;
	private LinkageIndividual[] ind = null;
	private LinkageVariables vars = null;
}
