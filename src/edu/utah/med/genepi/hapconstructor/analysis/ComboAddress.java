package edu.utah.med.genepi.hapconstructor.analysis;

public class ComboAddress {

	int geneunit = 0;
	int markerunit = 0;
	int markerIndex = 0;
	boolean createGeneUnit = false;
	boolean createMarkerUnit = false;
	private String type = null;
	private String action = null;
	
	public ComboAddress( int[] address, boolean[] creations, String itype, String iaction )
	{
		geneunit = address[1];
		markerunit = address[2];
		markerIndex = address[0];
		createGeneUnit = creations[0];
		createMarkerUnit = creations[1];
		type = itype;
		action = iaction;
	}
	
	//---------------------------------------------------------------------------	
	public boolean[] getCreations(){ return new boolean[]{createGeneUnit,createMarkerUnit}; }
	
	//---------------------------------------------------------------------------	
	public int getIndex(){ return markerIndex; }
	
	//---------------------------------------------------------------------------	
	public int getGeneUnitIndex(){ return geneunit; }
	
	//---------------------------------------------------------------------------	
	public int getMarkerUnitIndex(){ return markerunit; }

	//---------------------------------------------------------------------------	
	public String getType() { return type; }
	
	//---------------------------------------------------------------------------	
	public String getAction() { return action; }
}
