//******************************************************************************
// HapMCTopTogether.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.util.GEException;
//==============================================================================
public class HapMCTopTogether extends HapMCTopSim 
{

  //----------------------------------------------------------------------------
  public HapMCTopTogether()
  {
  }

  //----------------------------------------------------------------------------
  public void preProcessor () throws GEException
  {
    PedQuery.Predicate[] querySample = new PedQuery.Predicate[] {
                                       PedQuery.IS_TOGETHER };
    setDataSource(querySample);
  }
}

