//******************************************************************************
// Indiv.java
//******************************************************************************
package edu.utah.med.genepi.ped;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.gm.Qdata;
import edu.utah.med.genepi.util.Enum;
import edu.utah.med.genepi.util.IntIdentified;

//==============================================================================
public class Indiv extends IntIdentified {

  public static final class GtSelector extends Enum {
    //public static final GtSelector OBS = new GtSelector(0, "measured");
    //public static final GtSelector SIM = new GtSelector(1, "simulated");
    //public static final GtSelector ORG = new GtSelector(2, "original");
    //Remove ORG to save memory 
    public static final GtSelector OBS = new GtSelector("0", "measured");
    public static final GtSelector SIM = new GtSelector("1", "simulated");
    private static final int       N = Enum.instanceCount(GtSelector.class);
    //private GtSelector(int id, String label) { super(id, label); }
    private GtSelector(String id, String label) { super(id, label); }
    //public static GtSelector valueOf(int id) {
    public static GtSelector valueOf(String id) {
      GtSelector gs = (GtSelector) Enum.valueOf(id, GtSelector.class);
      return (GtSelector) Enum.valueOf(id, GtSelector.class);
    }
  }

  //private final int     	myId, sex_id;
  private final String     	myId, myLiab;
  private final char 		sex_id;
  private Ptype   	        myPt;
  private final boolean 	amFounder;
  private Gtype[]       	simulatedGts;
  private Gtype 		observedGts;
  //private Gtype 		originalGts;
  //private Gtype[]       	selectableGts = new Gtype[GtSelector.N];
  private List<Marriage>	lMarriages;
  public  Qdata			quant_val;
  public  Marriage 		parentMarriage;
  public  Pedigree 		myPed;
  public  double 		weightedIndex;
  public  boolean 		gtPhased = false;
  public  boolean 		possible_explcontrol = false;

  //----------------------------------------------------------------------------
  //Indiv( Pedigree ped, int id, boolean is_founder, 
  //       Marriage parentMarr, int sex,
  //       Ptype pt, Gtype gt, Qdata quants )
  Indiv( Pedigree ped, String id, boolean is_founder, 
         Marriage parentMarr, char sex,
         Ptype pt, String liab, Gtype gt, Qdata quants, int numSimData )
  {
    super(id);
    myId = id;
    myPt = pt;
    myLiab = liab;
    observedGts = gt;
    amFounder = is_founder;
    quant_val = quants;
    sex_id = sex;
    parentMarriage = parentMarr;
    myPed = ped;
    simulatedGts = new Gtype[numSimData];
    possible_explcontrol = false;
  }

  //----------------------------------------------------------------------------
  public boolean getIsFounder() { return amFounder; }

  //----------------------------------------------------------------------------
  public List<Marriage>  getMarriages() { return lMarriages; }

  //----------------------------------------------------------------------------
  public Ptype getPtype() { return myPt; }

  //----------------------------------------------------------------------------
  public Pedigree getPedigree() { return myPed; }

  //----------------------------------------------------------------------------
  public void setNumSimulatedGtype ( int num )
  {
    if ( num != 0 )
      simulatedGts = new Gtype[num];
  }
    
  //----------------------------------------------------------------------------
  //public void setSimulatedGtype(Gtype gt)
  public void setSimulatedGtype(Gtype gt, int index)
  {
    simulatedGts[index] = gt;
    //selectableGts[Integer.parseInt(GtSelector.SIM.getID())] = gt;
  }

  //----------------------------------------------------------------------------
  public void setGtype(Gtype gt, GtSelector selector)
  {
    setGtype(gt, selector, false);
  }
  
  //----------------------------------------------------------------------------
  public void setGtype(Gtype gt, GtSelector selector, boolean phased )
  {
    //selectableGts[GtSelector.OBS.getID()] = gt;
    if ( selector == GtSelector.OBS )
      observedGts = gt;
    else if ( selector == GtSelector.SIM )
      setSimulatedGtype(gt, 0);
    gtPhased = phased;
  }

  //----------------------------------------------------------------------------
  public Gtype getGtype(GtSelector gs)
  {
    if ( gs == GtSelector.OBS )
      return observedGts;
    else 
      return getSimulatedGtype(0);
  }

  public Gtype getGtype(GtSelector gs, int index )
  {
    Gtype selectedGt = null;
    if ( gs == GtSelector.OBS )
       selectedGt = observedGts;
    //else if ( gs == GtSelector.ORG )
    //   selectedGt = originalGts;
    else if ( gs == GtSelector.SIM )
       selectedGt = getSimulatedGtype(index);
    return selectedGt;
  }
    //return selectableGts[Integer.parseInt(gs.getID())];

  //----------------------------------------------------------------------------
  public Gtype getSimulatedGtype(int index)
  {
    return simulatedGts[index];
  }

  //----------------------------------------------------------------------------
  //public Gtype[] getSimulatedGtype(GtSelector gs)
  public Gtype[] getSimulatedGtype()
  {
    return simulatedGts; 
  }

  //----------------------------------------------------------------------------
  void addMarriage(Marriage m)
  {
    if (lMarriages == null)
      lMarriages = new ArrayList<Marriage>();

    lMarriages.add(m);
  }

  //----------------------------------------------------------------------------
  public Qdata getQuantVal() { return quant_val; }

  //----------------------------------------------------------------------------
  //public String getIndividual_id() { return myId; }
  
  //----------------------------------------------------------------------------
  public char getSex_id() { return sex_id; }

  //----------------------------------------------------------------------------
  //public Indiv getParent_Indiv(int sex_id) 
  public Indiv getParent_Indiv(char sex_id) 
  { 
    //if ( sex_id == 2 )
    //if ( sex_id.equals("2") || sex_id.equalsIgnoreCase("F") )
    if ( sex_id == '2' || sex_id == 'f')
      return parentMarriage.getHer();
    else 
      return parentMarriage.getHim();
  }

  //----------------------------------------------------------------------------
  public Marriage getParentMarr ()
  {  return parentMarriage; }

  //----------------------------------------------------------------------------
  public void setPtype(Ptype ptype)
  { myPt = ptype; }

  //----------------------------------------------------------------------------
  public String getLiab_id()
  { return myLiab; }

  //----------------------------------------------------------------------------
  public boolean getGtPhased()
  { return gtPhased; }
 
  //----------------------------------------------------------------------------
  public void setWeightedIndex(double inWt)
  { weightedIndex = inWt; }

  //----------------------------------------------------------------------------
  public double getWeightedIndex()
  { return weightedIndex; }

  //----------------------------------------------------------------------------
  public final boolean equals(Object inIndiv)
  {  
    Indiv inInd = (Indiv) inIndiv;
    Pedigree inPed = inInd.getPedigree();
    //String newIn = inInd.myId + "-" + inPed.myPed;
    //String newMy = myId + "-" + myPed.myPed;
    int pedValue = myPed.hashCode() - inPed.hashCode();
    int indValue = this.hashCode() - inInd.hashCode();
    if ( pedValue == 0 && indValue == 0 )
      return true;
    else
      return false;
    //System.out.println("myId - " + myId + " myPed : " + myPed + " in indiv - " + inInd.myId + "  inPed : " + inPed.myPed);
    //if ( myId == inInd.myId && myPed.myPed == inPed.myPed)
    //  System.out.print(" matched okay");
    //return ( newMy.hashCode() == newIn.hashCode());
  }

  //----------------------------------------------------------------------------
  public int compareTo(Object inIndiv)
  {
    //System.out.println(" ***** compareTo in Indiv ********");
    Indiv inInd = (Indiv) inIndiv;
    Pedigree inPed = inInd.getPedigree();
    //String newIn = inInd.myId + "-" + inPed.myPed;
    //String newMy = myId + "-" + myPed.myPed;
    //return newMy.hashCode() - newIn.hashCode();
    int pedValue = myPed.hashCode() - inPed.hashCode(); 
    if ( pedValue == 0 ) 
      return (this.hashCode() - inInd.hashCode()); 
    else 
      return pedValue;
  }

  //----------------------------------------------------------------------------
  public List<Indiv> getSibs()
  {
    List<Indiv> sibs = new ArrayList<Indiv>();
    if ( getIsFounder() )
    {
      return null;
    }
    else
    {
      Indiv mum = getParent_Indiv('f');
      Indiv dad = getParent_Indiv('m');
      List<Marriage> lmarriages = mum.getMarriages();
      for ( Iterator mit = lmarriages.iterator(); mit.hasNext(); )
      {
        Marriage m = (Marriage) mit.next();
        if ( dad == m.getHim() )
        {
          List<Indiv> lkids = m.getKids();
          for ( Iterator kit = lkids.iterator(); kit.hasNext(); )
          {
            Indiv kid = (Indiv) kit.next();
            if ( kid != this )
            {
              sibs.add(kid);
            } 
          }
        }
      }
    }
    return sibs;
  }

  //----------------------------------------------------------------------------
  public List<Indiv> getOffspring()
  {
    List<Indiv> offspring = new ArrayList<Indiv>();
    List<Marriage> lmarriages = getMarriages();
    for ( Iterator mit = lmarriages.iterator(); mit.hasNext(); )
    {
      Marriage m = (Marriage) mit.next();
      offspring.addAll(m.getKids());
    }
    return offspring;
  }

  //----------------------------------------------------------------------------
  public boolean isIndependent()
  {
    boolean independent = false;
    if ( getIsFounder() )
      if ( getMarriages() == null )
        independent = true;
    return independent;
  }
}
