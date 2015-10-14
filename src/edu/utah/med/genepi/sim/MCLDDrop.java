//******************************************************************************
// MCLDDrop.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import alun.genio.GeneticDataSource;
import edu.utah.med.genepi.gchapext.GeneticDataSourceImp;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class MCLDDrop extends HapMCDropSim 
{
  PedQuery.Predicate[] querySample; 
  
  //----------------------------------------------------------------------------
  public MCLDDrop()
  {
    super();
    querySample = new PedQuery.Predicate[] {PedQuery.IS_TOGETHER};
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
  
  //----------------------------------------------------------------------------
  public void setDataSource( PedQuery.Predicate[] querySample, int index )
  {
    MCLDTop top = new MCLDTop();
    if ( nLoci > 1 )
    {
      for ( int i = 0 ; i < study.length; i++ )
      {
        Indiv.GtSelector selector = Indiv.GtSelector.SIM;
        PedData peddata = study[i].getPedData();
        for ( int j = 0 ; j < querySample.length; j++ )
        {
          Indiv[] sampleIndiv = peddata.getIndividuals( querySample[j], true);
          GeneticDataSource sampleGds = new GeneticDataSourceImp( study[i],
                                                                  sampleIndiv,
                                                                  selector,
                                                                  index,
                                                                  gdef );

          top.setPhasedData(sampleGds, sampleIndiv, selector, index, gdef);
        }
      }
    }
  }
  
}
