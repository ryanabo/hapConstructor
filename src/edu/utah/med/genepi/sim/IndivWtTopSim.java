//******************************************************************************
// IndivWtTopSim.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.GtypeBuilder;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.util.EmpiricalRandomizer;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Randy;

//==============================================================================
public class IndivWtTopSim implements GSimulator.Top {

  //private   String                  samplemethod;
  //protected PedQuery.Predicate	    pInSample = PedQuery.IS_ANY; 
                                    // default to all
  //protected GDef 	  	    gdef;
  protected Indiv[][]		    founderInds, sampleInds;
  protected int[]                   nFounders, nSampleInds;
  protected GtypeBuilder            gtBuilder;
  protected EmpiricalRandomizer[][] randAlleleAt;
  //protected static Gtype[] 	    pedGtype;
  //protected boolean[]		    hasFrequency;
  protected Randy 		    r;
  protected Study[]                 study;
  protected int 		    nStudy, nLoci;
  protected int 		    nSim = 0;

  //----------------------------------------------------------------------------
  public void preProcessor () throws GEException
  {
    //assignFounderGenotypes(0);
  }

  //----------------------------------------------------------------------------
  public void setUserParameters(Specification spec, Study[] std)
  {
    study  = std;
    nStudy = study.length;
  }

  //----------------------------------------------------------------------------
  public void setPedData()
  {
    founderInds = new Indiv[nStudy][];
    nFounders   = new int[nStudy];

    for ( int i = 0; i < nStudy; i++ )
    {
      PedData pd = study[i].getPedData();
      founderInds[i] = pd.getIndividuals(PedQuery.IS_FOUNDER);
      nFounders[i] = founderInds[i].length;
    }
  }

  //----------------------------------------------------------------------------
  public void setGDef(GDef gd)
  {
    gtBuilder = gd.getGtypeBuilder();
  }

  //----------------------------------------------------------------------------
  /*
  public void assignFounderGenotypes(int index) throws GEException
  {
    for (int i = 0; i < nStudy; i++ )
    {
      // make sure this simulation data set is set to null before simulation
      Indiv[] anyInds = study[i].getPedData().getIndividuals(PedQuery.IS_ANY);
      for ( int j = 0; j < anyInds.length; j++ )
        anyInds[j].setSimulatedGtype(null, index);

      Integer num = 1;
      for (int j = 0; j < nFounders[i]; j++)
      { 
        Integer allele1 = num++;
        Integer allele2 = num++;
        gtBuilder.addAllelePair(allele1.byteValue(), allele2.byteValue());
        Gtype gt =  gtBuilder.buildNext();
        founderInds[i][j].setSimulatedGtype(gt, index);
        System.out.println("founder : " + founderInds[i][j].getPedigree() + "-" + founderInds[i][j].getID() + ", a1leles : " + allele1 + " / " + allele2);
      }
    }
  }
  */

  //----------------------------------------------------------------------------
  public void simulateFounderGenotypes(int index) throws GEException
  {}

  //----------------------------------------------------------------------------
  // overloaded for hapBuilder
  public void simulateFounderGenotypes( int index, 
                                        compressGtype cGtype,
                                        int step) throws GEException
  {
    System.out.println("WARNING: simulated founders without using compressGtype");
  }
}

