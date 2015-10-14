package alun.gchap;

import alun.genio.GeneticDataSource;

/**
 Represents an observation for a marker locus.
*/
public class MarkerObservation extends Observation
{
/** 
 Creates a new marker observation by reading the data from the given
 linkage data.
 The data for the ith locus in the list is read.
*/
	public MarkerObservation(GeneticDataSource gds, int i)
	{
		if (gds.nAlleles(i) == 4 && gds.locName(i).toUpperCase().indexOf("SNP") > -1)
			m = new SNP();
		else
			m = new Marker(gds.nAlleles(i));

		d = new Phenotype[gds.nIndividuals()];
		for (int j=0; j<d.length; j++)
		{
			d[j] = ((Marker)m).findPhenotype(gds.indAllele(j,i,0),gds.indAllele(j,i,1));
			d[j].incFrequency(1);
		}

		m.downCode();
		m.geneCount(1000);
		m.downCode(10E-10);
	}
}
