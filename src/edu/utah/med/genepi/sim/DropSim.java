//******************************************************************************
// DropSim.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import java.util.Iterator;
import java.util.List;

import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.gm.AllelePair;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.Gamete;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.GtypeBuilder;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Marriage;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.ped.Pedigree;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class DropSim implements GSimulator.Drop {

  protected Study[]      study;
  protected GDef         gdef;
  protected Indiv.GtSelector gtOBS, gtSIM;
  protected Pedigree[][] thePeds;
  protected Indiv[][]    descendInds, anyInds;
  public int           nStudy, nLoci;
  public int           nSim;
  protected int[]        nDescendants, nPeds;
  protected GtypeBuilder gtBuilder;
  public byte 	       missingData;

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
  //public void setNumSimulation( int num )
  //{
  //  nSim = num;
  //}

  //----------------------------------------------------------------------------
  public void simulateDescendantGenotypes(int index) 
         throws GEException
  {
    //gtORG = Indiv.GtSelector.ORG;
    gtOBS = Indiv.GtSelector.OBS;
    gtSIM = Indiv.GtSelector.SIM;

    for ( int i = 0; i < nStudy; i++ )
    {
      // reset descendant simulated genotypes
      for (int j = 0; j < nDescendants[i]; j++)
      {
        descendInds[i][j].setSimulatedGtype(null, index);
      }

      for (int iped = 0; iped < nPeds[i]; ++iped)
      {
        Marriage[] marrs = thePeds[i][iped].getFoundingMarriages();
        for (int imarr = 0, n = marrs.length; imarr < n; ++imarr)
          drop(marrs[imarr], index); // start recursive descend
      }

    // reset simulated data to match real original data - th respect to missing value 
      for (int ind = 0; ind < anyInds[i].length  ; ind++ )
      {
        //changed all org to obs
        //Gtype org = anyInds[i][ind].getGtype(gtORG);
        Gtype obs = anyInds[i][ind].getGtype(gtOBS);
        Gtype sim = anyInds[i][ind].getSimulatedGtype(index);
        //Gtype sim = anyInds[i][ind].getGtype(gtSIM);
        //int anyID  = anyInds[i][ind].getID(); 
        boolean indicator = false;

        for ( int l = 0; l < nLoci; l++ )
        {
          //nLociAllelePair orgpair = null;
          AllelePair obspair = null;

          if ( obs != null )
            obspair = obs.getAllelePairAt(l);

          if ( obs!= null && obspair != null )
          {
            AllelePair simpair = sim.getAllelePairAt(l);
            gtBuilder.addAllelePair(
	  	simpair.getAlleleCode(true), simpair.getAlleleCode(false) );
          }
          else
          { 
            gtBuilder.addAllelePair(missingData, missingData);
            indicator = true;
          }  
        }
                  
        if (indicator)
        {
          anyInds[i][ind].setSimulatedGtype(gtBuilder.buildNext(), index);
          //System.out.println("reset : ind " + anyInds[i][ind].getID() + "  gt : " + anyInds[i][ind].getSimulatedGtype(index));
        }
        else 
          gtBuilder.buildclean();
      }
    }
  }
   
  // overloaded for hapBuilder
  public void simulateDescendantGenotypes(int index, compressGtype[] cGtype,
                                          int step)
  throws GEException
  {
    System.out.println("WARNING : simulated descendant without using compressGtype");
  }

  //----------------------------------------------------------------------------
  private void drop(Marriage m, int index) throws GEException
  {
    System.out.println(" +++ inside wrong drop - DropSim");
    //Gtype his_gt = m.getHim().getGtype(Indiv.GtSelector.SIM);
    Gtype his_gt = m.getHim().getSimulatedGtype(index);
    if (his_gt == null)  // husband is somebody's kid not yet processed
      return;

    //Gtype her_gt = m.getHer().getGtype(Indiv.GtSelector.SIM);
    Gtype her_gt = m.getHer().getSimulatedGtype(index);
    if (her_gt == null)  // wife is somebody's kid not yet processed
      return;

    // for each kid...
    for (Iterator kidit = m.getKids().iterator(); kidit.hasNext(); )
    {
      Indiv  kid = (Indiv) kidit.next();

      // if we haven't yet reached this kid descending via spouse
      //if (kid.getGtype(Indiv.GtSelector.SIM) == null)
      if (kid.getSimulatedGtype(index) == null)
      {
        // the facts of life, geek-style:
        Gamete sperm = his_gt.doMeiosis();
        Gamete egg = her_gt.doMeiosis();
        for (int j = 0; j < nLoci; ++j)
          gtBuilder.addAllelePair(sperm.getAllele(j), egg.getAllele(j));

        kid.setSimulatedGtype(gtBuilder.buildNext(), index);
        //System.out.println("kid gt : " + kid.getID() + " " + kid.getSimulatedGtype(index).toString());
        gtBuilder.buildclean();
        // added buildclean to make sure the next simulation is clean

        // for each of this kid's marriages, if any...
        List marrs = kid.getMarriages();
        if (marrs != null)
        for (Iterator marrit = marrs.iterator(); marrit.hasNext(); )
            drop((Marriage) marrit.next(), index);   // recurse
      }
    }
  }
}
