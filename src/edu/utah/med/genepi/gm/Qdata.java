//******************************************************************************
// Qdata.java
//******************************************************************************
package edu.utah.med.genepi.gm;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.util.Ut;

//==============================================================================
public class Qdata {

  private double[] qvals;

  //----------------------------------------------------------------------------
  public Qdata(double[] quants) 
  { qvals = quants; }

  //----------------------------------------------------------------------------
  public String toString()
  {
    List l = new ArrayList();
    for (int i = 0; i < qvals.length; ++i)
    {
      String qv = Double.toString(qvals[i]);
      l.add(qv);
    }
    return Ut.join(l, " ");
  }
  
  //----------------------------------------------------------------------------
  public int getNQuants(){ return qvals.length; }

  //----------------------------------------------------------------------------
  public double getQdata(int i){ return qvals[i]; }

}

