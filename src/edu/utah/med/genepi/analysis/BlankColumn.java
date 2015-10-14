package edu.utah.med.genepi.analysis;

import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.GtypeMatcher;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

public class BlankColumn implements AnalysisTable.Column {
	
	private final int theWt;
	public BlankColumn( int wt ){ theWt = wt; }
	public int subsumesAtype(Gtype gt, boolean first){ return 0; }
	public int subsumesGtype(Gtype gt){ return 0; }
	public int getWeight() { return 0; }
	public GtypeMatcher[] getGtypeMatcher() { return null; }
}
