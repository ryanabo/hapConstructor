//******************************************************************************
// RepeatAllSingle.java
//******************************************************************************
package edu.utah.med.genepi.gm;

//==============================================================================
public class RepeatAllSingle
{
  public void RepeatAllSingle()
  {}

  public int[][] getGroup(int[] lociRange, int groupSize)
  {
    //int group = gdef.getLocusCount();
    int group = lociRange.length;
    int[][] returnGroup = new int[group][1];
    for ( int i = 0; i < group; i++ )
    {
      //returnGroup[i] = new int[1];
      returnGroup[i][0] = i;
    }
    return returnGroup;
  }
}
