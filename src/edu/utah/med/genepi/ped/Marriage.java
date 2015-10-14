//******************************************************************************
// Marriage.java
//******************************************************************************
package edu.utah.med.genepi.ped;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.util.IntIdentified;

//==============================================================================
public class Marriage extends IntIdentified {

  static final Marriage ROOT = new Marriage("0", "0");

  private final String hisID, herID;
  private Indiv     indHim, indHer;
  private List<Indiv>      lKids = new ArrayList<Indiv>();

  //----------------------------------------------------------------------------
  Marriage(String his_id, String her_id)
  {
    super(computeID(his_id, her_id));
    hisID = his_id;
    herID = her_id;
  }

  //----------------------------------------------------------------------------
  public Indiv getHim() { return indHim; }

  //----------------------------------------------------------------------------
  public Indiv getHer() { return indHer; }

  //----------------------------------------------------------------------------
  public List<Indiv> getKids() { return lKids; }

  //----------------------------------------------------------------------------
  //public int getHisID() { return hisID; }
  public String getHisID() { return hisID; }

  //----------------------------------------------------------------------------
  //public int getHerID() { return herID; }
  public String getHerID() { return herID; }

  //----------------------------------------------------------------------------
  void addKid(Indiv kid) { lKids.add(kid); }

  //----------------------------------------------------------------------------
  void completeTheCeremony(Indiv him, Indiv her)
  {
    indHim = him;
    indHim.addMarriage(this);
    indHer = her;
    indHer.addMarriage(this);
  }

  //----------------------------------------------------------------------------
  //static int computeID(int his_id, int her_id)
  static String computeID(String his_id, String her_id)
  {
    // no longer compute ID for the marriage, just create unique string
    //int his = Integer.parseInt(his_id);
    //int her = Integer.parseInt(her_id);
    //return (his_id << 16) | her_id;
    //return String.valueOf((his << 16) | her);
    return new String ( his_id + "-" + her_id );
  }
}

