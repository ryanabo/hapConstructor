package alun.genepi;

import java.util.LinkedHashSet;
import java.util.Set;

import alun.markov.GraphicalModel;

public class IndependentLocusSampler extends LinkageSampler
{
	public IndependentLocusSampler()
	{
	}

	public IndependentLocusSampler(LociVariables lv)
	{
		this(lv, 1);
	}

	public IndependentLocusSampler(LociVariables lv, int first)
	{
		setInitialBlocks(independentLocusBlocks(lv,first));
		setLocusBlocks(getInitialBlocks());
		setThrifty(true);
		//initialize();
	}

	public Set<GraphicalModel> independentLocusBlocks(LociVariables lv, int first)
	{
		Set<GraphicalModel> gms = new LinkedHashSet<GraphicalModel>();

		for (int j=0; j<lv.nLoci(); j++)
		{
			GraphicalModel h = new GenotypeModel(lv.unrelatedLocusProduct(j));

			h.allocateOutputTables();
			h.allocateInvolTables();
			h.reduceStates();
			h.clearOutputTables();
			h.clearInvolTables();

			h = new GenotypeModel(lv.locusProduct(j));

			h.allocateOutputTables();
			h.allocateInvolTables();
			h.reduceStates();
			h.clearOutputTables();
			h.clearInvolTables();

			h.allocateOutputTables();
			h.allocateInvolTables();
			h.collect();
			h.drop();
			h.clearOutputTables();
			h.clearInvolTables();

			if (j >= first)
				gms.add(h);
		}
		
		return gms;
	}

}
