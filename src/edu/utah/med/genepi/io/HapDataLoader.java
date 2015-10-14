//******************************************************************************
// HapDataLoader.java
//******************************************************************************
package edu.utah.med.genepi.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.FreqDataSet;

//==============================================================================
public class HapDataLoader
{
  FreqDataSet[] freqdata = null;

  //----------------------------------------------------------------------------
  public void parse ( File file, AlleleFormat af ) throws IOException
  {
    //if ( file == null )
    //  throw new IOException ("Missing Haplotype Frequency file, please specify");
    try 
    {
      BufferedReader     br = new BufferedReader(new FileReader(file));
      ArrayList<FreqDataSet> v = new ArrayList<FreqDataSet>();
      String s;
      
      while ( (s = br.readLine()) != null )
      {
        double freq = 0.0;
        String hap = null;
        StringTokenizer st = new StringTokenizer(s);
        
        while (st.hasMoreTokens() )
        {
          freq = (Double.valueOf(st.nextToken())).doubleValue();
          hap = st.nextToken();
        }
        
        v.add(new FreqDataSet(freq, hap, "HapFreqTopSim", af));
      }
      br.close();
      //freqdata = (FreqDataSet[]) v.toArray(new FreqDataSet[0]);
      freqdata = v.toArray(new FreqDataSet[0]);
    }
    catch ( Exception e )
    {
      System.out.println("Failed to load Haplotype Frequency file" );
      e.printStackTrace();
    }
  }
  
  //----------------------------------------------------------------------------
  public FreqDataSet[] getHapFreq()
  { return freqdata; }
   
}

