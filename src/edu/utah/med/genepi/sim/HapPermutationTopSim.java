//******************************************************************************
// HapPermutationTopSim.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import java.lang.reflect.Array;
import java.util.Random;

import alun.genio.GeneticDataSource;
import edu.utah.med.genepi.gchapext.GeneticDataSourceImp;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.util.GEException;
//==============================================================================
public class HapPermutationTopSim extends HapMCTopSim 
{

  //----------------------------------------------------------------------------
  public HapPermutationTopSim()
  {
    super();
  }

  //----------------------------------------------------------------------------
  public void simulateFounderGenotypes() throws GEException
  {
    for ( int i = 0 ; i < nStudy; i++ )
    {
      PedData pd   = study[i].getPedData();
      // Random permute case / control status
      statusPermutation( pd.getIndividuals(PedQuery.IS_CASE),
                         pd.getIndividuals(PedQuery.IS_CONTROL) );
      Indiv[] caseIndiv    = pd.getIndividuals(PedQuery.IS_CASE);
      Indiv[] controlIndiv = pd.getIndividuals(PedQuery.IS_CONTROL);
      Indiv[] anyIndiv     = pd.getIndividuals(PedQuery.IS_ANY);
      Indiv.GtSelector selector = Indiv.GtSelector.SIM;

      if ( gdef == null )
        System.out.println("gdef is also null in Perm");

      for ( int j = 0; j < anyIndiv.length; j++ )
        anyIndiv[j].setGtype(anyIndiv[j].getGtype(Indiv.GtSelector.OBS),
                             Indiv.GtSelector.SIM ); 

      GeneticDataSource casegds    = new GeneticDataSourceImp(study[i],
                                                              caseIndiv,
                                                              selector,
                                                              -1, 
                                                              gdef );
      GeneticDataSource controlgds = new GeneticDataSourceImp(study[i],
                                                              caseIndiv,
                                                              selector,
                                                              -1,
                                                              gdef );

      setPhasedData ( casegds, caseIndiv, selector, -1, gdef ); 
      setPhasedData ( controlgds, controlIndiv, selector, -1, gdef ); 

    }
  }

  //----------------------------------------------------------------------------
  public void statusPermutation ( Indiv[] caseIndiv, Indiv[] controlIndiv )
  {
    int nCase        = caseIndiv.length;
    int nControl     = controlIndiv.length;
    int nTotal       = nCase + nControl;
    boolean[] all    = new boolean[nTotal];
    Indiv[] allIndiv = new Indiv[nTotal];
    int permCase     = 0;
   
    for ( int i = 0; i < nCase; i++ )
      allIndiv[i] = caseIndiv[i];

    for ( int i = nCase; i < nTotal; i++ )
      allIndiv[i] = controlIndiv[i - nCase];

    Random random = new Random();

    // for testing set position to a fix number. 
    // The first run getBoolean is true, and all other runs should be false
    for ( int i = 0 ; i < all.length; i++ )
      Array.setBoolean(all, i , false);

    while ( permCase< nCase )
    {
      //double r = random.nextDouble();
      //int position = Math.round(new Float(r * (nTotal - 1))) ;
      int position = new Random().nextInt(nTotal);
      if ( !Array.getBoolean(all, position)) 
      {
        permCase += 1;
        Array.setBoolean( all, position, true );
      }
    }
    
    for ( int i = 0; i < all.length; i++ )
    {
      if ( Array.getBoolean(all, i) )
        allIndiv[i].setPtype(Ptype.CASE);
      else 
        allIndiv[i].setPtype(Ptype.CONTROL);
    }
  }

}

