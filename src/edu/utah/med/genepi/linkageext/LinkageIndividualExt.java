package edu.utah.med.genepi.linkageext;

import alun.linkage.LinkageIndividual;
import alun.linkage.LinkageLocus;
import alun.linkage.LinkageParameterData;
import alun.linkage.LinkagePhenotype;
import edu.utah.med.genepi.gm.GenotypeDataInterface;
import edu.utah.med.genepi.ped.Individual;

public class LinkageIndividualExt extends LinkageIndividual {

	private boolean missing = false;	

	public LinkageIndividualExt( GenotypeDataInterface gtDataInterface, LinkageParameterData par, Individual individual, int simIndex ) 
	{
		pedid = individual.getPedIndex();
		id = individual.getIndIndex();
		paid = individual.getPaIndex();
		maid = individual.getMaIndex();
		sex = individual.getSex();
		missing = (individual.getDataIndex() == -1);
		
		LinkageLocus[] ll = (LinkageLocus[]) par.getLoci();
		LinkageLocusExt[] l = new LinkageLocusExt[ll.length];
		for ( int i = 0 ; i < ll.length; i++ )
		{
			try 
			{
				l[i] = (LinkageLocusExt) ll[i];
			}
			catch ( Exception e )
			{
				System.out.println(e.getMessage());
			}
		}
		pheno = new LinkagePhenotype[l.length];
		for ( int i = 0; i < pheno.length; i++ )
		{
			if ( missing ) pheno[i] = l[i].readPhenotype(0,0);
			else
			{
				int[] a = gtDataInterface.getGt(individual.getDataIndex(),i,simIndex);				
				pheno[i] = l[i].readPhenotype(a[0],a[1]);
			}
			
		}
		comment = null;
	}
	
	public boolean isMissing(){ return missing; }
}
