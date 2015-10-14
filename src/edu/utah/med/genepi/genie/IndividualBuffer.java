package edu.utah.med.genepi.genie;

import edu.utah.med.genepi.gm.Qdata;
import edu.utah.med.genepi.ped.Individual;

public class IndividualBuffer {
	
	String[][] tempGtData = null;
	Phenotype phenObj = null;
	Individual indObj = null;
	Qdata quantData = null;
	
	//----------------------------------------------------------------------------	
	public IndividualBuffer( Individual ind, Phenotype phen )
	{
		indObj = ind;
		phenObj = phen;
	}
	
	//----------------------------------------------------------------------------
	public int getPedIndex(){ return indObj.getPedIndex(); }
	
	//----------------------------------------------------------------------------
	public int getIndIndex(){ return indObj.getIndIndex(); }
	
	//----------------------------------------------------------------------------	
	public void setPedIndex( int pedIndex ){ indObj.setPedIndex(pedIndex); }
	
	//----------------------------------------------------------------------------	
	public void setPaIndex( int paIndex ){ indObj.setPaIndex(paIndex); }
	
	//----------------------------------------------------------------------------	
	public void setMaIndex( int maIndex ){ indObj.setMaIndex(maIndex); }
	
	//----------------------------------------------------------------------------	
	public void addQuantData( Qdata qd ){ quantData = qd; }
	
	//----------------------------------------------------------------------------	
	public void addGtData( String[][] genotypeData ){ tempGtData = genotypeData; }
	
	//----------------------------------------------------------------------------	
	public String[][] getGtData(){ return tempGtData; }
	
	//----------------------------------------------------------------------------
	public Qdata getQdata(){ return quantData; }
	
	//----------------------------------------------------------------------------	
	public boolean isFounder(){	return indObj.isFounder(); }
	
	//----------------------------------------------------------------------------	
	public int getStorageIndex(){ return indObj.getStorageIndex(); }
	
	//----------------------------------------------------------------------------
	public String getPaId(){ return indObj.getPaId(); }
	
	//----------------------------------------------------------------------------
	public String getMaId(){ return indObj.getMaId(); }
	
	//----------------------------------------------------------------------------
	public Phenotype getPhenotype(){ return phenObj; }
	
	//----------------------------------------------------------------------------
	public Individual getIndividual(){ return indObj; }
	
	//----------------------------------------------------------------------------
	public boolean hasData(){ return tempGtData != null; }
	
	//----------------------------------------------------------------------------
	public String getPedId(){ return indObj.getPedId(); }
	
	//----------------------------------------------------------------------------
	public boolean isPhenoGenotyped() { 
		boolean phenogeno = false;
		if ( hasData() ){
			if ( quantData != null ){
				for ( int i=0; i < quantData.getNQuants(); i++ ){
					if ( quantData.getQdata(i) != 0.00 ){
						phenogeno = true;
						break;
					}
				}
			}
			else if ( phenObj.getPhenotype() != 0 ) phenogeno = true;
		}
		return phenogeno; 
	}
}
