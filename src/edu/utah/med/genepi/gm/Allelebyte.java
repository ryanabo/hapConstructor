//******************************************************************************
// Allelebyte.java
//******************************************************************************
package edu.utah.med.genepi.gm;

//==============================================================================
public class Allelebyte implements AlleleFormat
{
  public static byte missingData = (byte) 0;

  //----------------------------------------------------------------------------
  public byte convertAllele(String inString)
  {
    if ( inString.length() == 1 )
      return Integer.valueOf(inString).byteValue();
    else
    {
      System.out.println ("Failed to read allele. Allele is more than a single character.");
      return missingData;
    }
  }

  //----------------------------------------------------------------------------
  public String toString(byte inbyte)
  {
    int intValue = inbyte;
    return String.valueOf(intValue);
  }

  //----------------------------------------------------------------------------
  public void setMissingData(String inString)
  {
    setMissingData( Integer.valueOf(inString).byteValue() );
  }
 
  //----------------------------------------------------------------------------
  public void setMissingData(byte inbyte)
  {
    missingData = inbyte;
  }

  //----------------------------------------------------------------------------
  public byte getMissingData()
  {
    return missingData ;
  }
}
