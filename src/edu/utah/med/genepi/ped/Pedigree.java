//******************************************************************************
// Pedigree.java
//******************************************************************************
package edu.utah.med.genepi.ped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.utah.med.genepi.util.IntIdentified;

//==============================================================================
public class Pedigree extends IntIdentified {

  //private final Map  mID2Ind = new TreeMap();
  //private final Map  mID2Marr = new HashMap();
  private final Map<String, Indiv>  mID2Ind;
  private final Map  mID2Marr;
  private Marriage[] foundingMarriages;
  //protected int myPed;
  protected String myPed;

  //----------------------------------------------------------------------------
  //Pedigree(int ped_id)
  Pedigree(String ped_id)
  {
    super(ped_id);
    myPed = ped_id;
    mID2Ind = new TreeMap<String, Indiv>();
    mID2Marr = new HashMap();
  }

  //----------------------------------------------------------------------------
  //public int getPed_id() { return myPed; }
  public String getPed_id() { return myPed; }

  //----------------------------------------------------------------------------
  public int getMemberCount() { return mID2Ind.size(); }

  //----------------------------------------------------------------------------
  public Collection<Indiv> getMembers() { return  mID2Ind.values(); }

  //----------------------------------------------------------------------------
  public Marriage[] getFoundingMarriages() { return foundingMarriages; }

  //----------------------------------------------------------------------------
  void addMember(Indiv ind)
  {
    //mID2Ind.put(new Integer(ind.getID()), ind);
    mID2Ind.put(ind.getID(), ind);
  }

  //----------------------------------------------------------------------------
  void addMarriage(Marriage m)
  {
    //mID2Marr.put(new Integer(m.getID()), m);
    mID2Marr.put(m.getID(), m);
  }

  //----------------------------------------------------------------------------
  //public Marriage getMarriage(int dad_id, int mom_id)
  public Marriage getMarriage(String dad_id, String mom_id)
  {
    return (Marriage) mID2Marr.get(Marriage.computeID(dad_id, mom_id));
      //new Integer(Marriage.computeID(dad_id, mom_id))
  }

  //----------------------------------------------------------------------------
  void buildMarriageInfo()
  {
    List fmarrs = new ArrayList();
    for (Iterator i = mID2Marr.values().iterator(); i.hasNext();)
    {
      Marriage marr = (Marriage) i.next();

      if (marr.equals(Marriage.ROOT))
        continue;

      Indiv dad = getMember(marr.getHisID());
      Indiv mom = getMember(marr.getHerID());

      marr.completeTheCeremony(dad, mom);
      if (dad.getIsFounder() && mom.getIsFounder())
        fmarrs.add(marr);
    }
    foundingMarriages = (Marriage[]) fmarrs.toArray(new Marriage[0]);
  }

  //----------------------------------------------------------------------------
  //public Indiv getMember(int id)
  public Indiv getMember(String id)
  {
    //return (Indiv) mID2Ind.get(new Integer(id));
    return (Indiv) mID2Ind.get(id);
  }
  //----------------------------------------------------------------------------
  public int getNumMarriage() 
  { return mID2Marr.size(); }

  //----------------------------------------------------------------------------
  public Map getMap()
  { return mID2Marr; }
 
}

