//******************************************************************************
// DataLoaderImp.java
//******************************************************************************
package edu.utah.med.genepi.ped;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.gm.Qdata;

//==============================================================================
class DataLoaderImp implements PedData.Loader {

  private final Map<String, Pedigree> mID2Ped = new HashMap<String, Pedigree>();

  //----------------------------------------------------------------------------
  DataLoaderImp()
  {
  }

  //----------------------------------------------------------------------------
  //public void loadIndividual(int ped_id, int ind_id, int dad_id, int mom_id,
  //                           int sex_id, int ptype_id, Gtype gt, Qdata qt )
  public void loadIndividual( String ped_id, String ind_id,
                              String dad_id, String mom_id,
                              char sex_id, String ptype_id,
                              String liab_id, Gtype gt,
                              Qdata qt, int numSimData )
  {
    Pedigree ped = getPedigree(ped_id);
    if (ped == null)
    {
      ped = new Pedigree(ped_id);
      addPedigree(ped);
    }

    Marriage marr = ped.getMarriage(dad_id, mom_id);
    if (marr == null)
    {
      marr = new Marriage( dad_id, mom_id);
      ped.addMarriage(marr);
      
    }

    Indiv ind = new Indiv( ped, ind_id, marr.equals(Marriage.ROOT),
                           marr, sex_id,
                           Ptype.valueOf(ptype_id), liab_id, 
                           gt, qt, numSimData );
    ped.addMember(ind);
    marr.addKid(ind);
  }

  //----------------------------------------------------------------------------
  Set<Pedigree> buildPedigreeSet()
  {
    TreeSet<Pedigree> peds = new TreeSet<Pedigree>();
    for (Iterator i = mID2Ped.values().iterator(); i.hasNext(); )
    {
      Pedigree p = (Pedigree) i.next();
      p.buildMarriageInfo();
      peds.add(p);
    }
    return peds;
  }

  //----------------------------------------------------------------------------
  private void addPedigree(Pedigree p)
  {
    //mID2Ped.put(new Integer(p.getID()), p);
    mID2Ped.put(p.getID(), p);
  }

  //----------------------------------------------------------------------------
  //private Pedigree getPedigree(int id)
  private Pedigree getPedigree(String id)
  {
    return (Pedigree) mID2Ped.get(id);
  }
}

