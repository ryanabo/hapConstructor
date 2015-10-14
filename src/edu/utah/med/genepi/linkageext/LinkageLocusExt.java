package edu.utah.med.genepi.linkageext;

import alun.linkage.LinkagePhenotype;
import alun.linkage.NumberedAlleleLocus;
import alun.linkage.NumberedAllelePhenotype;

public class LinkageLocusExt extends NumberedAlleleLocus {
	
	public LinkageLocusExt()
	{
		super();
	}

	public LinkagePhenotype readPhenotype ( int a, int b )
	{
		return new NumberedAllelePhenotype(this, a, b);
	}
}
