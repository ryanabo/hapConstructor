//******************************************************************************
// XTopSim.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import edu.utah.med.genepi.gm.AllelePair;
import edu.utah.med.genepi.gm.FreqDataSet;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.util.EmpiricalRandomizer;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Randy;

//==============================================================================
public class XTopSim extends AlleleFreqTopSim {

  public XTopSim()
  { super(); }

  //----------------------------------------------------------------------------
  public void setGDef(GDef gd)
  {
    gdef = gd;
    nLoci = gd.getLocusCount();
    gtBuilder = gd.getGtypeBuilder();
    randAlleleAt = new EmpiricalRandomizer[nStudy][nLoci];
    //pedGtype = new Gtype[nSampleInds];
    
    for ( int i = 0; i < nStudy; i++ )
    {
    //if ( gdef.getLocus(0).getFrequency() == null )
    // check if the study has a Frequency attached to it
      hasFrequency[i] = true;
      if ( (study[i].getFreqDataSet()) == null || "all".equals(samplemethod) ||
             "founder".equals(samplemethod) )
        hasFrequency[i] = false;

      for (int j = 0; j < nLoci; j++)
          randAlleleAt[i][j] = new EmpiricalRandomizer(Randy.getInstance());

      // add all sample individual's Gtype to EmpiricalRandomizer's map and
      // then calculate the freq inside EmpiricalRandomizer
      for (int iind = 0; iind < nSampleInds[i]; ++iind)
      {
        Gtype gt = sampleInds[i][iind].getGtype(Indiv.GtSelector.OBS);
        if (gt != null)
        {
          for (int iloc = 0; iloc < nLoci; ++iloc)
          {
            AllelePair pair = gt.getAllelePairAt(iloc);
            if (pair != null)
            {
              //d opt: eliminate duplicate object creation; see notes
              randAlleleAt[i][iloc].sampleNext(pair.getAlleleCode(true));
              // only sample both alleles for female
              char sex_id = sampleInds[i][iind].getSex_id();
              //System.out.println("ind : " + sampleInds[i][iind].getID() + "  sex is " + sex_id );
              if ( sex_id == '2' || sex_id == 'f' )
              { 
                //System.out.println(" I am a female, sample the allele code" );
                randAlleleAt[i][iloc].sampleNext(pair.getAlleleCode(false));
              }
            }
          }
        }
      }
      //System.out.println("allele freq map for study : " + i );
      for (int k = 0; k < nLoci; k++)
      {
        //System.out.print(" locus : " + k + " allele code : "); 
        randAlleleAt[i][k].closeSampling();
      }
      
      // attach nAllele to each study
      study[i].setAlleleMap( getAlleleMap(i) );
    }
   
  }

  //----------------------------------------------------------------------------
  public void simulateFounderGenotypes(int index) throws GEException
  {
    for (int i = 0; i < nStudy; i++ )
    {
      // make sure this simulation data set is set to null before simulation
      Indiv[] anyInds = study[i].getPedData().getIndividuals(PedQuery.IS_ANY);
      for ( int j = 0; j < anyInds.length; j++ )
        anyInds[j].setSimulatedGtype(null, index);
      for (int j = 0; j < nFounders[i]; j++)
      { 
        Gtype gg = randomGtype(i, founderInds[i][j].getSex_id());
        //founderInds[i][j].setSimulatedGtype(randomGtype(i), index);
        founderInds[i][j].setSimulatedGtype(gg, index);
        //System.out.println("asigning founder " + founderInds[i][j].getID() + " gtype : " + gg.toString());
        //System.out.println("who - " + founderInds[i][j].getID() + 
        //" gt : " + founderInds[i][j].getGtype(Indiv.GtSelector.SIM));
      }
    }
  }

  // overloaded for hapBuilder
  public void simulateFounderGenotypes( int index, 
                                        compressGtype cGtype,
                                        int step) throws GEException
  {
    System.out.println("WARNING: simulated founders without using compressGtype");
  }

  //----------------------------------------------------------------------------
  private Gtype randomGtype(int studyID, char sex_id) throws GEException
  {
    r = Randy.getInstance();
    for (int i = 0; i < nLoci; ++i)
    {
      //int allele1, allele2;
      byte allele1, allele2;
      if ( hasFrequency[studyID] )
      {
        //Frequency[] freq = gdef.getLocus(i).getFrequency();
        FreqDataSet[][] freq = study[studyID].getFreqDataSet();
        FreqDataSet[] cumfreq = new FreqDataSet[freq[i].length];
        cumfreq[0] = freq[i][0];
        for ( int j = 1; j < freq[i].length; j++ )
        {
	  cumfreq[j] = new FreqDataSet( cumfreq[j-1].getFrequency() + 
                                        freq[i][j].getFrequency(),
                                        freq[i][j].getCode(),
                                        "random" ); 
        }
        //allele1 = new Integer(getRandomCode(cumfreq)).intValue();
        //allele2 = new Integer(getRandomCode(cumfreq)).intValue();
        //allele1 = getRandomCode(cumfreq)[0];
        allele1 = getRandomCode(cumfreq);
        allele2 = getRandomCode(cumfreq);
      }
      else
      {
        //System.out.println("goto EmpiricalRandomizer to getNext -- " );
        allele1 = ((Byte) randAlleleAt[studyID][i].getNext()).byteValue();
        allele2 = ((Byte) randAlleleAt[studyID][i].getNext()).byteValue();
      }
      // male should only has one allele
      if ( sex_id == '1' || sex_id == 'm' )
        allele2 = allele1;
      gtBuilder.addAllelePair(allele1, allele2);
    }
    return gtBuilder.buildNext();
  }

  //----------------------------------------------------------------------------
  public byte getRandomCode(FreqDataSet[] freq)
  {
    double randomNum = r.nextDouble();
    //String randomCode = null;
    for ( int i = 0; i < freq.length; i++ )
    {
      if ( randomNum < freq[i].getFrequency() ) 
      {
        byte[] randomCode = freq[i].getCode();
        return randomCode[0];
        //randomCode = freq[i].getCode();
      }
    }
    return 0;
  }
  //----------------------------------------------------------------------------
  public EmpiricalRandomizer[] getAlleleMap(int studyID)
  {
    return randAlleleAt[studyID];
  }
  //----------------------------------------------------------------------------
  //public static Gtype[] getPedGtype()
  //{  return pedGtype; }
}

