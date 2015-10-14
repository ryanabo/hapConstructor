//******************************************************************************
// XDropSim.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import java.util.Iterator;
import java.util.List;

import edu.utah.med.genepi.gm.AllelePair;
import edu.utah.med.genepi.gm.Gamete;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Marriage;
import edu.utah.med.genepi.util.GEException;
//import edu.utah.med.genepi.util.Ut;

//==============================================================================
public class XDropSim extends DropSim
{
  public XDropSim ()
  {
    super();
  }

  //----------------------------------------------------------------------------
  public void simulateDescendantGenotypes( int index )
         throws GEException
  {
    gtOBS = Indiv.GtSelector.OBS;
    gtSIM = Indiv.GtSelector.SIM;

    for ( int i = 0; i < nStudy; i++ )
    {
      // reset descendant simulated genotypes
      for (int j = 0; j < nDescendants[i]; j++)
        descendInds[i][j].setSimulatedGtype(null, index);

      for (int iped = 0; iped < nPeds[i]; ++iped)
      {
        Marriage[] marrs = thePeds[i][iped].getFoundingMarriages();
        for (int imarr = 0, n = marrs.length; imarr < n; ++imarr)
          drop(marrs[imarr], index); // start recursive descend
      }
      for (int ind = 0; ind < anyInds[i].length  ; ind++ )
      {
        Gtype obs = anyInds[i][ind].getGtype(gtOBS);
        Gtype sim = anyInds[i][ind].getSimulatedGtype(index);
        boolean indicator = false;

        for ( int l = 0; l < nLoci; l++ )
        {
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

  //----------------------------------------------------------------------------
  private void drop(Marriage m, int index) throws GEException
  {
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
        Gamete egg = her_gt.doMeiosis();
        Gamete sperm = his_gt.doMeiosis();
        char sex_id = kid.getSex_id();
        // son get both alleles from mother
        if ( sex_id == '1' || sex_id == 'm' )
          sperm = egg;
        for (int j = 0; j < nLoci; ++j)
        {
          gtBuilder.addAllelePair(sperm.getAllele(j), egg.getAllele(j));
          //System.out.println(" -- ind " + kid.getID() + " sperm : " + sperm.getAllele(j) + " egg : " + egg.getAllele(j) );
        }

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
