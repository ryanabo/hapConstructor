//******************************************************************************
// Randy.java
//******************************************************************************
package edu.utah.med.genepi.util;

import java.util.Random;

//==============================================================================
public final class Randy {

  private static final int RESOLUTION = Integer.MAX_VALUE;

  private static Randy theInstance;
  private final Random myRNG;

  //----------------------------------------------------------------------------
  private Randy(Random r)
  {
    myRNG = r;
  }

  //----------------------------------------------------------------------------
  public static synchronized void create(long seed) throws GEException
  {
    create(new Random(seed));
  }

  //----------------------------------------------------------------------------
  public static synchronized void create(Random r) throws GEException
  {
    if (theInstance != null)
      throw new GEException(
        Randy.class.getName() + " can be created only once"
      );
    theInstance = new Randy(r);
  }

  //----------------------------------------------------------------------------
  public static synchronized Randy getInstance()
  {
    return theInstance;
  }

  //----------------------------------------------------------------------------
  public boolean nextBoolean(double p_true)
  {
    return ((double) myRNG.nextInt(RESOLUTION)) < RESOLUTION * p_true;
  }

  //----------------------------------------------------------------------------
  public int nextInt(int n)
  {
    return myRNG.nextInt(n);
  }
 
 
  //----------------------------------------------------------------------------
  public double nextDouble()
  { 
    return myRNG.nextDouble();
  }

}
