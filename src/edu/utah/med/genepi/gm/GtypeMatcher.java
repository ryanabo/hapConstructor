//******************************************************************************
// GtypeMatcher.java
//******************************************************************************
package edu.utah.med.genepi.gm;

import java.util.StringTokenizer;

import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class GtypeMatcher {

  public int[]     iLoci;
  //private final Pattern[] rxPatterns;
  private final byte[][] rxPatterns;
  private byte missingData;

  //----------------------------------------------------------------------------
  public GtypeMatcher(int[] selected_locus_indices)
  {
    iLoci = selected_locus_indices;
    //rxPatterns = new Pattern[iLoci.length];
    rxPatterns = new byte[iLoci.length][];
  }
  
  //----------------------------------------------------------------------------
  public int[] getLoci(){ return iLoci; }

  //----------------------------------------------------------------------------
  public void updateSelectedLocus( int locus_index )
  {
    iLoci = new int[1];
    iLoci[0] = locus_index;
  }

  //----------------------------------------------------------------------------
  public void updateSelectedLocus( int[] locus_index )
  {
    iLoci = locus_index;
  }

  //----------------------------------------------------------------------------
  public boolean matchesGtype(Gtype gt)
  {
    boolean matched = true;
    
    //System.out.println("missing Data : " + missingData);
    if (gt == null)
      return false;

    for (int i = 0; i < iLoci.length; ++i)
    {
      if ( matched )
      {
        AllelePair ap = gt.getAllelePairAt(iLoci[i]);
        matched = false;
      
        if (ap != null)
        {
          byte a1 = ap.getAlleleCode(true);
          byte a2 = ap.getAlleleCode(false);
          int j = 0;
          int nPatterns = rxPatterns[i].length;
         
          while ( j < nPatterns )
          {
            if ((rxPatterns[i][j] == missingData || rxPatterns[i][j] == a1) &&
                (rxPatterns[i][j+1] == missingData || rxPatterns[i][j+1] == a2) )
            { 
              j = nPatterns;
              matched = true;
            }
            else 
              j += 2;
          }
        }
      }
      else 
        return matched;
    }
    return matched;
  }

  //----------------------------------------------------------------------------
  public boolean matchesGtype(Gtype gt, int allelePosition)
  {
    boolean first = true; 
    if (allelePosition == 1)
      first = false;
    return matchesGtype(gt, first);
  }

  //----------------------------------------------------------------------------
  public boolean matchesGtype(Gtype gt, boolean allelePosition)
  {
    boolean matched = true;
  
    if ( gt == null )
      return false;

    for (int i = 0; i < iLoci.length; ++i)
    {
      if ( matched == true )
      {
        AllelePair ap = gt.getAllelePairAt(iLoci[i]);
        matched = false;
   
        if ( ap != null )
        { 
          byte a1 = ap.getAlleleCode(allelePosition);
          int j = 0;
          int nPatterns = rxPatterns[i].length;
          
          while ( j < nPatterns )
          {
            if ( rxPatterns[i][j] == missingData || rxPatterns[i][j] == a1 )
            {
              j = nPatterns;
              matched = true;
            }
            else
              j++;
          }
        }
        else 
        {
          return false;
        }
      }
      else 
      {
        //System.out.println("failed to match pattern");
        return false;
      }
    }
    return matched;
  }
  
  //----------------------------------------------------------------------------
  public void setRegex( int i, String regex, AlleleFormat af ) throws GEException
  {
    missingData = af.getMissingData();
    String ss = regex.replaceAll("[ \t\n\f\r]", "");
    StringTokenizer st = new StringTokenizer (ss, "/|()");
    rxPatterns[i] = new byte[st.countTokens()];
    int j = 0;
    while ( st.hasMoreTokens() )
    {
      String token = st.nextToken();
      if ( token.equals("."))
        rxPatterns[i][j] = missingData;
      else 
      {
        rxPatterns[i][j] = af.convertAllele(token); 
      }
      j++;
    }
  }
  
  //----------------------------------------------------------------------------
  public byte[][] getPatterns(){ return rxPatterns; }

}

