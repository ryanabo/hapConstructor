//******************************************************************************
// HapFreqTopSim.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import java.io.IOException;

import edu.utah.med.genepi.gm.FreqDataSet;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.io.HapDataLoader;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Randy;

//==============================================================================
public class HapFreqTopSim extends AlleleFreqTopSim 
{
  protected String[] 		code;
  protected FreqDataSet[] 	newcumfreq; 
  public static String[] defaultnames;
  public static double[] defaultcumfreq;

  //----------------------------------------------------------------------------
  public HapFreqTopSim ()
  { 
    super();
  }
  //----------------------------------------------------------------------------
  public void preProcessor () throws GEException
  {
    for ( int i = 0; i < study.length; i++ )
    {
      HapDataLoader loader = new HapDataLoader();
      try 
      { loader.parse( study[i].getHaplotypeFile(), gdef.getAlleleFormat() ); }
      catch ( IOException e )
      { throw new GEException (e.getMessage()); }

      FreqDataSet[] freqdata = loader.getHapFreq();
      study[i].setFreqDataSet(freqdata);
    }
  }

  //----------------------------------------------------------------------------
  public void simulateFounderGenotypes(int index) throws GEException
  {
    for ( int i = 0 ; i < nStudy; i++ )
    {
      for (int j = 0; j < nFounders[i]; j++ )
      {
        Gtype gt = newrandomGtype(i);
        founderInds[i][j].setSimulatedGtype(gt, index);
      }
    }
  }
  
  //----------------------------------------------------------------------------
  public Gtype newrandomGtype(int studyID) throws GEException
  {
    byte[]    Hap1         = null;
    byte[]    Hap2         = null;

    gtBuilder.buildclean();
    r = Randy.getInstance();
    newcumfreq = getcumfreq(studyID);
    //newnames = getnames();
    Hap1 = randomHaptype();
    Hap2 = randomHaptype();

    for (int i = 0; i < nLoci; ++i)
      gtBuilder.addAllelePair( Hap1[i], Hap2[i] );
      
    return gtBuilder.buildNext();
  }
  
  //----------------------------------------------------------------------------
  //public String[] randomHaptype ()
  public byte[] randomHaptype ()
  {
    double randomNum = 0.0;
    randomNum = r.nextDouble();

    //int[] h = new int[nLoci];
    //byte[] h = new byte[nLoci];
    byte[] haplotype = new byte[nLoci];

    for ( int j = 0; j < newcumfreq.length; j++ )
    {
      if ( randomNum < newcumfreq[j].getFrequency() )
        {
          haplotype = newcumfreq[j].getCode();
          break;
        }
    }
    return haplotype;
    
    //String ss = hapString.replaceAll("[^0-9]", " ");
    //StringTokenizer st = new StringTokenizer(ss, " " );
    //int i = 0;
    
    //while ( st.hasMoreTokens())
    //{
    //   h[i] = st.nextToken();
    //   i++;
    //}

    //return h;
  }

  //---------------------------------------------------------------------
  //public static void setcumfreq(double[] cf)
  //{ defaultcumfreq = cf; }

  //---------------------------------------------------------------------
  //public static void setnames(String[] n)
  //{ defaultnames = n; }

  //---------------------------------------------------------------------
  public FreqDataSet[] getcumfreq (int studyID)
    {	
      FreqDataSet[][] freq = study[studyID].getFreqDataSet();
      FreqDataSet[] cumfreq = new FreqDataSet[freq[0].length];
      cumfreq[0] = freq[0][0];
      for ( int i = 1; i < cumfreq.length; i++ )
        cumfreq[i] = new FreqDataSet( cumfreq[i - 1].getFrequency() +
                                       freq[0][i].getFrequency(), 
                                      freq[0][i].getCode(),
                                      "HapFreqTopSim" );
        
      return cumfreq; 
    }

  //---------------------------------------------------------------------
/**  public void getdbl()
  {
    Randy a = Randy.getInstance();
    double randomNum = a.nextDouble();
  }
*/
}

