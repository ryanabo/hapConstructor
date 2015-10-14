package alun.genepi;

import java.util.LinkedHashSet;
import java.util.Set;

import alun.markov.GraphicalModel;

public class LinkedLocusSampler extends IndependentLocusSampler
{
	public LinkedLocusSampler()
	{
	}

	public LinkedLocusSampler(LinkageVariables vars)
	{
		this(vars,1);
	}

	public LinkedLocusSampler(LinkageVariables vars, int first)
	{
		setInitialBlocks(independentLocusBlocks(vars,first));
		setLocusBlocks(linkedLocusBlocks(vars,first));
		setThrifty(true);
		//initialize();
	}

	public Set<GraphicalModel> linkedLocusBlocks(LinkageVariables vars, int first)
	{
		LinkedHashSet<GraphicalModel> g = new LinkedHashSet<GraphicalModel>();

		g.add(new GenotypeModel(vars.connectedLocusProduct(first,false,true)));

		for (int j=first+1; j<vars.nLoci(); j++)
			g.add(new GenotypeModel(vars.connectedLocusProduct(j,true,true)));

		return g;
	}
}
