//******************************************************************************
// Ptype.java
//******************************************************************************
package edu.utah.med.genepi.gm;

import edu.utah.med.genepi.util.Enum;

//==============================================================================
public final class Ptype extends Enum {
  
  public static final Ptype UNKNOWN = new Ptype("0", "Unknown");
  public static final Ptype CONTROL = new Ptype("1", "Control");
  public static final Ptype CASE    = new Ptype("2", "Case");
    
  //----------------------------------------------------------------------------
  //private Ptype(int id, String label) { super(id, label); }
  private Ptype(String id, String label) { super(id, label); }

  //----------------------------------------------------------------------------
  //public static Ptype valueOf(int id)
  public static Ptype valueOf(String id)
  {
    return (Ptype) Enum.valueOf(id, Ptype.class);
  }

  //----------------------------------------------------------------------------
  public static int instanceCount() { return Enum.instanceCount(Ptype.class); }

  //----------------------------------------------------------------------------
  //public int getPtype_ID()  { return getID(); }
  public String getPtype_ID()  { return getID(); }

}

