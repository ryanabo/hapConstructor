package edu.utah.med.genepi.hapconstructor;

public class hapCWrappedResult {

	private hapCResult[] results = null;
	private Long locusAddress;
	private String bufferKey = null;
	
	public hapCWrappedResult( String bKey, hapCResult[] storedResults )
	{
		results = storedResults;
		bufferKey = bKey;
		locusAddress = storedResults[0].getLocusAddress();
	}
	
	public Long getLocusAddress(){ return locusAddress; }
	
	public hapCResult[] getResults(){ return results; }
}
