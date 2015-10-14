package edu.utah.med.genepi.gchapext;

import alun.gchap.Marker;
import alun.gchap.MarkerObservation;
import alun.gchap.Phenotype;
import alun.gchap.SNP;

/**
 Represents an observation for a marker locus.
*/
public class MarkerObservationExt extends MarkerObservation
{
/** 
 Creates a new marker observation by reading the data from the given
 linkage data.
 The data for the ith locus in the list is read.
*/
	//Ryan 06-20-07 overloaded to include index for saved simulated data.
	public MarkerObservationExt(GeneticDataSourceImp gds, int i, int index)
	{
          	super(gds, i);
		if (gds.nAlleles(i) == 4 && gds.locName(i).toUpperCase().indexOf("SNP") > -1)
			m = new SNP();
		else
			m = new Marker(gds.nAlleles(i));

		d = new Phenotype[gds.nIndividuals()];
		for (int j=0; j<d.length; j++)
		{
			d[j] = ((Marker)m).findPhenotype(gds.indAllele(j,i,0,index),gds.indAllele(j,i,1,index));
			d[j].incFrequency(1);
		}

		m.downCode();
		m.geneCount(1000);
		m.downCode(10E-10);
	}
}
