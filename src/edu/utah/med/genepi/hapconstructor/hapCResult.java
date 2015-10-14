package edu.utah.med.genepi.hapconstructor;

import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.analysis.ComboSet;

public interface hapCResult {
	
	public boolean metSignificance( double threshold );
	public boolean hitMostSignificant( int nSims );
	public long getLocusAddress();
	public String getObservedReport();
	public String getInferentialReport();
	public long getHaplotype(boolean left2right);
	public String getBufferKey();
	public void newMarkerSet(int i, GenieDataSet gd, hapCParamManager params, HaplotypeResultStorage significantHaplotypes);
	public void reducedMarkerSet(int i, GenieDataSet gd, hapCParamManager params);
	public String getOutput();
	public boolean checkFailed();
	public ComboSet getComboSet();
}
