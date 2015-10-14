//******************************************************************************
// QuantIndiv.java
//******************************************************************************
package edu.utah.med.genepi.ped;

import edu.utah.med.genepi.gm.Qdata;

//==============================================================================
public class QuantIndiv {

  //public int 		Ind, Ped_id;
  public String 	ind, ped_id;
  public Qdata 		quants;

  //----------------------------------------------------------------------------
  //QuantIndiv(int ped_id, int id, Qdata quant_val)
  QuantIndiv(String pedid, String id, Qdata quant_val)
  {
    ped_id = pedid;
    ind = id;
    quants = quant_val;
  }
 
}
