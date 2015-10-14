package edu.utah.med.genepi.analysis;

import java.util.List;

import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.GtypeMatcher;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

public class RgenColumn implements AnalysisTable.Column {

	private final GtypeMatcher[] gtMatchers;
	private final int            theWt;
	
	public RgenColumn( List<GtypeMatcher> gt_matchers,  int weight )
	{
		gtMatchers = gt_matchers.toArray(new GtypeMatcher[0]);
		theWt = weight;
	}
	// pass each allele separately for type = Allele
	public int subsumesAtype(Gtype gt, boolean first) 
	{
		for ( int j = 0; j < gtMatchers.length; j++ )
		{
			if ( gtMatchers[j].matchesGtype(gt, first) )
				return 1;
		}
		return 0;
	}

	public int subsumesGtype(Gtype gt)
	{
		for (int j = 0; j < gtMatchers.length; ++j)
		{
			if ( gtMatchers[j].matchesGtype(gt) )
				return 1;
		}
		return 0;
	}

	public int getWeight() { return theWt; }
	public GtypeMatcher[] getGtypeMatcher() { return gtMatchers; }
}