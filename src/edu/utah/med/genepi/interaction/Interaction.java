package edu.utah.med.genepi.interaction;

import edu.utah.med.genepi.app.rgen.IOManager;
import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.util.GEException;

public class Interaction {

  private InteractionParams intx_params;
  
  public Interaction( IOManager iom ) throws GEException
  {   
	Specification spec = iom.getSpecification();
    Study[] study = iom.getStudy();	  
    intx_params = new InteractionParams(spec,study);
    String[] intx_stat = intx_params.get_statistics();
    for ( int i = 0; i < intx_stat.length; i++ ) call_stat(iom,intx_stat[i],study);
  }
  
  private void call_stat( IOManager iom, String intx_stat, Study[] study )
  {
    if (intx_stat.equals("OR"))
    {
      InteractionOddsRatios intx_or = new InteractionOddsRatios(study,intx_params);
      try 
      {
	    intx_or.run(iom);
	  } 
      catch (GEException e) 
	  {
		e.printStackTrace();
  	  }
    }
    else if ( intx_stat.equals("LD") )
    {
      InteractionLD intx_ld = new InteractionLD(study,intx_params);
      try {
		intx_ld.run();
	} catch (GEException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    else if ( intx_stat.equals("LD_composite") )
    {
    
    }
    else if ( intx_stat.equals("correlation") )
    {
      
    }    
  }

}
