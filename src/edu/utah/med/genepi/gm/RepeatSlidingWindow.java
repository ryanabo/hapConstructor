//******************************************************************************
// RepeatSlidingWindow.java
//******************************************************************************
package edu.utah.med.genepi.gm;

import edu.utah.med.genepi.util.GEException;
//==============================================================================
public class RepeatSlidingWindow implements Repeat
{
  public void RepeatSlidingWindow()
  {}

  public int[][] getGroup(int[] lociRange, int groupSize)
         throws GEException
  {
    int group = lociRange.length - groupSize + 1; 
    if ( group <= 0 )
      throw new GEException(
      "Defined loci range can not be divided into group size of " + groupSize);
    int[][] returnGroup = new int[group][groupSize];
    int lociN = 0;

    for ( int i = 0; i < group; i++ )
    {
      for ( int j = 0; j < groupSize; j++ )
      {
        returnGroup[i][j] = lociRange[lociN + j];
      }
      lociN++;
    }
    return returnGroup;
  }
}
