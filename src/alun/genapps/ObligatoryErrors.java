package alun.genapps;

import alun.genepi.GenotypeModel;
import alun.genepi.LocusVariables;
import alun.genio.BasicGeneticData;
import alun.markov.GraphicalModel;

public class ObligatoryErrors
{
	public static boolean obligatoryError(BasicGeneticData d, int j)
	{
		LocusVariables g = new LocusVariables(d,j);
		GraphicalModel m = null;

		if (d.nAlleles(j) > 2)
		{
			m = new GraphicalModel(g.makeUnrelatedProduct()); 
			m.allocateOutputTables();
			m.allocateInvolTables();
			m.reduceStates();
		}

		m = new GenotypeModel(g.makeLocusProduct());
		m.allocateOutputTables();
		//System.out.println(m.peel());
		return m.peel() <= 0;
	}
}
