//******************************************************************************
// Repeat.java
//******************************************************************************
package edu.utah.med.genepi.gm;

import edu.utah.med.genepi.util.GEException;
//==============================================================================
public interface Repeat
{
   public int[][] getGroup(int[] lociRange, int groupSize) 
          throws GEException;
}
