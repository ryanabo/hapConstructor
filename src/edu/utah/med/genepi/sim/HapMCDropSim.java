//******************************************************************************
// HapMCDropSim.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.ped.Pedigree;
import edu.utah.med.genepi.ped.Marriage;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Indiv.GtSelector;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.GtypeBuilder;
import edu.utah.med.genepi.gm.AllelePair;
import edu.utah.med.genepi.gchapext.GeneticDataSourceImp;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.util.GEException;
import alun.genio.GeneticDataSource;
import java.util.Random;

//==============================================================================
public class HapMCDropSim implements GSimulator.Drop 
{
  protected Study[]      study;
  protected GDef         gdef;
  protected Indiv.GtSelector gtOBS, gtSIM;
  protected Pedigree[][] thePeds;
  protected Indiv[][]    descendInds, anyInds;
  public int           nStudy, nLoci;
  public int           nSim;
  protected int[]        nDescendants, nPeds;
  protected GtypeBuilder gtBuilder;
  public byte          missingData;

  //----------------------------------------------------------------------------
  public void preProcessor () throws GEException
  {}

  //----------------------------------------------------------------------------
  public void setUserParameters(Specification spec, Study[] std)
  {
    study = std;
    nStudy = std.length;
  }

  //----------------------------------------------------------------------------
  public void setPedData()
  {
    descendInds  = new Indiv[nStudy][];
    anyInds      = new Indiv[nStudy][];
    thePeds      = new Pedigree[nStudy][];
    nPeds        = new int[nStudy];
    nDescendants = new int[nStudy];

    for ( int i = 0; i < nStudy; i++ )
    {
      PedData pd = study[i].getPedData();
      thePeds[i] = pd.getPedigrees();
      nPeds[i] = thePeds[i].length;
      descendInds[i] = pd.getIndividuals(PedQuery.IS_DESCENDANT);
      anyInds[i] = pd.getIndividuals(PedQuery.IS_ANY);
      nDescendants[i] = descendInds[i].length;
    }
  }

  //----------------------------------------------------------------------------
  public void setGDef(GDef gd)
  {
    nLoci = gd.getLocusCount();
    gtBuilder = gd.getGtypeBuilder();
    gdef = gd;
    missingData = gd.getAlleleFormat().getMissingData();
  }

  //----------------------------------------------------------------------------
  public void simulateDescendantGenotypes(int inIndex) 
         throws GEException
  {
    int index = 0;
    gtOBS = Indiv.GtSelector.OBS;
    gtSIM = Indiv.GtSelector.SIM;

    for ( int i = 0; i < nStudy; i++ ) {
      // reset descendant simulated genotypes
      for (int j = 0; j < nDescendants[i]; j++)
        descendInds[i][j].setSimulatedGtype(null, index);

      for (int iped = 0; iped < nPeds[i]; ++iped) {
        Marriage[] marrs = thePeds[i][iped].getFoundingMarriages();
        System.out.println("this pedigree has " + marrs.length);
        for (int imarr = 0, n = marrs.length; imarr < n; ++imarr)
          drop(marrs[imarr], index); // start recursive descend
      }

    // reset simulated data to match real original data - th respect to missing value 
      for (int ind = 0; ind < anyInds[i].length  ; ind++ )
      {
        Gtype obs = anyInds[i][ind].getGtype(gtOBS);
        Gtype sim = anyInds[i][ind].getSimulatedGtype(index);
        boolean indicator = false;

        for ( int l = 0; l < nLoci; l++ ) {
          AllelePair obspair = null;

          if ( obs != null )
            obspair = obs.getAllelePairAt(l);

          if ( obs!= null && obspair != null ) {
            AllelePair simpair = sim.getAllelePairAt(l);
            gtBuilder.addAllelePair(
                simpair.getAlleleCode(true), simpair.getAlleleCode(false) );
          } else {
            gtBuilder.addAllelePair(missingData, missingData);
            indicator = true;
          }
        }

        if (indicator) {
          anyInds[i][ind].setSimulatedGtype(gtBuilder.buildNext(), index);
          //System.out.println("reset : ind " + anyInds[i][ind].getID() + "  gt : " + anyInds[i][ind].getSimulatedGtype(index));
        } else 
          gtBuilder.buildclean();
      }
    }
  }
   
  //----------------------------------------------------------------------------
  private void drop(Marriage m, int index) throws GEException
  {
    Gtype his_gt = m.getHim().getSimulatedGtype(index);
    Gtype her_gt = m.getHer().getSimulatedGtype(index);
    System.out.println("father " + m.getHim().getID()+" sim gtype : " + his_gt.toString());
    System.out.println("mother " + m.getHer().getID()+" sim gtype : " + her_gt.toString());

    // husband or wife is somebody's kid not yet processed
    if (his_gt == null || her_gt == null )
      return;

    //for (Iterator kidit = m.getKids().iterator(); kidit.hasNext(); )
    for ( Indiv kid : m.getKids() )
    {
      //Indiv  kid = (Indiv) kidit.next();
      if (kid.getSimulatedGtype(index) == null)
      {
        byte[][] his_hap = his_gt.0();
        byte[][] her_hap = her_gt.getHaplotype();
        int nPairs = his_hap[0].length;
        byte[][] kid_hap = new byte[2][nPairs];
        Random random = new Random();
        if ( random.nextBoolean() )
          kid_hap[0] = his_hap[0];
        else
          kid_hap[0] = his_hap[1];

        if ( random.nextBoolean() )
          kid_hap[1] = her_hap[0];
        else
          kid_hap[1] = her_hap[1];

        for ( int i =0; i < nPairs; i++ )
          gtBuilder.addAllelePair(kid_hap[0][i], kid_hap[1][i]);
        kid.setSimulatedGtype (gtBuilder.buildNext(), index);
        gtBuilder.buildclean();

        // for each of this kid's marriages, if any...
        List<Marriage> marrs = kid.getMarriages();
        if (marrs != null)
        //for (Iterator marrit = marrs.iterator(); marrit.hasNext(); )
	for ( Marriage kidm : marrs )
            drop(kidm, index);   // recurse
      }
      System.out.println("kid " + kid.getID()+ " sim gtype is : " + kid.getSimulatedGtype(index).toString());
    }
  }

  //----------------------------------------------------------------------------
  public void setDataSource( PedQuery.Predicate[] querySample, int index )
  {
    HapMCTopSim top = new HapMCTopSim();
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
  
  //----------------------------------------------------------------------------
  public void setDataSource( PedQuery.Predicate[] querySample,
                             int index, compressGtype[] cGtypes )
  {
    HapMCTopSim top = new HapMCTopSim();
    if ( nLoci > 1 )
    {
      for ( int i = 0 ; i < study.length; i++ )
      {
        Indiv.GtSelector selector = Indiv.GtSelector.SIM;
        PedData peddata = study[i].getPedData();

        int[] loci = new int[gdef.getLocusCount()];
        for(int j =0; j < gdef.getLocusCount(); j ++ )
        {
          loci[j] = j;
        }

        for ( int j = 0 ; j < querySample.length; j++ )
        {
          Indiv[] sampleIndiv = peddata.getIndividuals( querySample[j], true);
          GeneticDataSource sampleGds = new GeneticDataSourceImp( study[i],
                                                                  sampleIndiv,
                                                                  selector,
                                                                  index,
                                                                  gdef );
          if ( querySample[j] == PedQuery.IS_ANY )
          {
            Indiv[] caseIndiv = peddata.getIndividuals( PedQuery.IS_CASE, true, loci);
            Indiv[] controlIndiv = peddata.getIndividuals( PedQuery.IS_CONTROL, true, loci);
            top.setPhasedData(sampleGds, caseIndiv, selector, index,
                        gdef, cGtypes[i], PedQuery.IS_CASE );
            top.setPhasedData(sampleGds, controlIndiv, selector,
                        index, gdef, cGtypes[i], PedQuery.IS_CONTROL);
          }
          else 
          {
            top.setPhasedData(sampleGds, sampleIndiv, selector, index, 
                              gdef, cGtypes[i], querySample[j] );
          }
        }
      }
    }
  }

  //----------------------------------------------------------------------------
  //Ryan 08-19-07 overloaded to pass in compressed Gtype datastructure
  public void simulateDescendantGenotypes(int index, compressGtype[] cGtypes, 
                                          int step) throws GEException
  {
    //Ryan changed input to super.simulateDescendantGenotypes index to 0.
    this.simulateDescendantGenotypes(0);
  }

}
