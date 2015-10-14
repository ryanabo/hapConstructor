//******************************************************************************
// RepeatLoci.java
//******************************************************************************
package edu.utah.med.genepi.gm;

import edu.utah.med.genepi.util.GEException;
//==============================================================================
public class RepeatLoci implements Repeat
{
  public void RepeatLoci()
  {}

  public int[][] getGroup(int[] lociRange, int groupSize)
         throws GEException
  {
    int group = lociRange.length / groupSize; 
    if ( (group - Math.abs(group)) > 0 )
      throw new GEException(
      "Defined loci range can not be divided into group size of " + groupSize);
    int[][] returnGroup = new int[group][groupSize];
    int lociN = 0;

    for ( int i = 0; i < group; i++ )
    {
      for ( int j = 0; j < groupSize; j++ )
      {
        returnGroup[i][j] = lociRange[lociN];
        lociN++;
      }
    }
    return returnGroup;
  }
}
