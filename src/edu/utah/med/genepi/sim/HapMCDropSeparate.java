//******************************************************************************
// HapMCDropSeparate.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class HapMCDropSeparate extends HapMCDropSim 
{
  PedQuery.Predicate[] querySample; 
  
  //----------------------------------------------------------------------------
  public HapMCDropSeparate()
  {
    super();
    querySample = new PedQuery.Predicate[] {PedQuery.IS_CASE,
                                            PedQuery.IS_CONTROL};
  }

  //----------------------------------------------------------------------------
  public void simulateDescendantGenotypes(int index) 
         throws GEException
  {
    super.simulateDescendantGenotypes(0);
    setDataSource(querySample, index);
  }
   
  //----------------------------------------------------------------------------
  //Ryan 08-19-07 overloaded to pass in compressed Gtype datastructure
  public void simulateDescendantGenotypes(int index, compressGtype[] cGtypes, 
                                          int step) throws GEException
  {
    //Ryan changed input to super.simulateDescendantGenotypes index to 0.
    super.simulateDescendantGenotypes(0);
    setDataSource( querySample, index, cGtypes );
  }
}
