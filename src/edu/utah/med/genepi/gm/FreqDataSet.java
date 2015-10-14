//******************************************************************************
// FreqDataSet.java						
//******************************************************************************
package edu.utah.med.genepi.gm;

import java.util.StringTokenizer;
//------------------------------------------------------------------------------
public class FreqDataSet 
{
  double frequency = 0.0;
  byte[] code;
  String createdFor = null;

  //----------------------------------------------------------------------------
  public FreqDataSet (double freq, byte[] c, String s )
  { 
    frequency = freq;
    code  = c;
    createdFor = s;
  }
  
  //----------------------------------------------------------------------------
  public FreqDataSet (double freq, byte i, String s ) 
  {
    frequency = freq;
    code = new byte[1];
    code[0] = i;
    createdFor = s;
  }
  
  //----------------------------------------------------------------------------
  public FreqDataSet (double freq, String inHaplotype, String s,
                      AlleleFormat af )
  {
    frequency = freq;
    createdFor = s;
    StringTokenizer st = new StringTokenizer(
                             inHaplotype.replaceAll("[-/.]", " "), " ");
    int numHap = st.countTokens();
    int i = 0;
    code = new byte[numHap];
    while (st.hasMoreTokens())
    {
      //code[i] = (new Byte(st.nextToken())).byteValue();
      code[i] = af.convertAllele(st.nextToken());
      i++;
    }
  }

  //----------------------------------------------------------------------------
  public double getFrequency()
  { return frequency; }
  
  //----------------------------------------------------------------------------
  public byte[] getCode()
  { return code; }

  //----------------------------------------------------------------------------
  public Object getCreatedFor()
  { return createdFor; }
}
