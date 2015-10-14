package edu.utah.med.genepi.gui2;

import edu.utah.med.genepi.util.GEException;

public class CCTable
{
  String type ;
  String loci ;
  String stats;
  String modelname;
 
  public CCTable ( String inType,
	           String inLoci,
	       	   String inStats,
		   String inModel ) throws GEException
  { 
    type  = inType;
    loci  = inLoci.trim();
    stats = inStats;
    modelname = inModel;
  }
 
  public String getType()
  { return type; }
 
  public String getLoci()
  { return loci; }
 
  public String getStats()
  { return stats; }
  
  public String getModelName()
  { return modelname; }
}
