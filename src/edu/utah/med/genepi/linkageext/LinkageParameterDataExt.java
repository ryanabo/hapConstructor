package edu.utah.med.genepi.linkageext;

import alun.linkage.LinkageParameterData;
import edu.utah.med.genepi.genie.GenieParameterData;
import edu.utah.med.genepi.gm.Locus;

public class LinkageParameterDataExt extends LinkageParameterData {

	public LinkageParameterDataExt( GenieParameterData gp, int studyIndex )
	{
		nloci = gp.getLociCount(-1);
		order = new int[nloci];
		for ( int i=0; i < nloci; i++) order[i] = i+1;
		locus = new LinkageLocusExt[nloci];
		malethetas = new double[nloci-1];
		for ( int i=0; i < locus.length; i++ )
		{
			Locus l = gp.getLocus(-1, i);
			locus[i] = new LinkageLocusExt();
			locus[i].freq = l.getFreqs(studyIndex);
			locus[i].type = 3;
			if ( i > 0) malethetas[i-1] = l.getTheta(); 
		}
		femalethetas = malethetas;
	}	
}
