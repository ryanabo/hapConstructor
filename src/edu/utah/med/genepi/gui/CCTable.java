package edu.utah.med.genepi.gui;

import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Ut;

public class CCTable
{
  String type ;
  String loci ;
  String stats;
  String modelname;
  Model model;
 
  public CCTable ( String inType,
	           String inLoci,
	       	   String inStats,
		   String inModel ) throws GEException
  { 
    type  = inType;
    loci  = inLoci.trim();
    stats = inStats;
    modelname = inModel;
    model = (Model) Specification.newModule(Ut.pkgOf(Model.class), 
                                            modelname + "Model"   );
  }
 
  public String getType()
  { return type; }
 
  public String getLoci()
  { return loci; }
 
  public String getStats()
  { return stats; }
  
  public Model getModel()
  { return model; }

  public String getModelName()
  { return modelname; }
}
