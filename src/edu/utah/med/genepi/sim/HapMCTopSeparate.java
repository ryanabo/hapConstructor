//******************************************************************************
// HapMCTopSeparate.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.util.GEException;
//Ryan 08-19-07
//import alun.genio.LinkageParameterData;
//==============================================================================
public class HapMCTopSeparate extends HapMCTopSim
{

  //----------------------------------------------------------------------------
  public HapMCTopSeparate()
  {
    super();
  }

  //----------------------------------------------------------------------------
  public void preProcessor () throws GEException
  {
    PedQuery.Predicate[] querySample = new PedQuery.Predicate[] {
                                       PedQuery.IS_CASE,
                                       PedQuery.IS_CONTROL};
    setDataSource(querySample);
  }

}

