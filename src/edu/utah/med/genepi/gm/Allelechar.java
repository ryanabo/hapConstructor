//******************************************************************************
// Allelechar.java
//******************************************************************************
package edu.utah.med.genepi.gm;

//==============================================================================
public class Allelechar implements AlleleFormat
{
  public static byte missingData = (byte) 0;
 
  //----------------------------------------------------------------------------
  public byte convertAllele(String inString)
  {
    if ( inString.length() == 1 )
      return Integer.valueOf(inString.codePointAt(0)).byteValue();
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
    char[] ch = Character.toChars(intValue);
    if ( ch.length == 1 )
      return Character.toString(ch[0]);
    else 
    {
      System.out.println ("Failed to convert Allele from byte back to String, input byte convert to more than a single char!" );
      return null;
    }
  }

  //----------------------------------------------------------------------------
  public void setMissingData(String inString)
  {
    setMissingData( Integer.valueOf(inString.codePointAt(0)).byteValue() );
  }

  //----------------------------------------------------------------------------
  public void setMissingData(byte inbyte)
  {
    missingData = inbyte;
  }

  //----------------------------------------------------------------------------
  public byte getMissingData()
  {
    return missingData;
  }
}
