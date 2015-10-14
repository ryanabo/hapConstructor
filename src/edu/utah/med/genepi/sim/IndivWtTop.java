//******************************************************************************
// IndivWtTop.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Pedigree;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class IndivWtTop implements GSimulator.Top {

  protected Study[]      study;
  private Pedigree[][] thePeds;
  private Indiv[][]    descendInds, anyInds, caseInds, controlInds;
  private Indiv[][][]  inds;
  public int           nStudy;

  //----------------------------------------------------------------------------
  public void preProcessor () throws GEException
  {
  } 

  //----------------------------------------------------------------------------
  public void setUserParameters(Specification spec, Study[] std) 
  {
    study = std;
    nStudy = study.length;
  }

  //----------------------------------------------------------------------------
  public void setPedData()
  {
  }

  //----------------------------------------------------------------------------
  public void setGDef(GDef gd)
  {
  }

  //----------------------------------------------------------------------------
  public void simulateFounderGenotypes(int index) throws GEException
  {
  }
   
  // overloaded for hapBuilder
  public void simulateFounderGenotypes(int index, compressGtype cGtype,
                                          int step)
  throws GEException
  {
    System.out.println("WARNING : simulated descendant without using compressGtype");
  }

  //----------------------------------------------------------------------------
  /*
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
        Gamete sperm = his_gt.doMeiosis();
        Gamete egg = her_gt.doMeiosis();
        byte allele1 = sperm.getAllele(0);
        byte allele2 = egg.getAllele(0);
        gtBuilder.addAllelePair(allele1, allele2);
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
  */
    
}
