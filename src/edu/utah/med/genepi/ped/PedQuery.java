//******************************************************************************
// PedQuery.java
//******************************************************************************
package edu.utah.med.genepi.ped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.utah.med.genepi.gm.Ptype;

//==============================================================================
public class PedQuery {

  private static int percentage = 0;
  private static int[] markers = null;

  public interface Predicate {
    public boolean indivSatisfies(Indiv ind);
    public String toString();
  }

  public static final Predicate IS_ANY = new Predicate() {
    public boolean indivSatisfies(Indiv ind) { return true; }
    public String toString() 
    { return "any"; }
  };

  public static final Predicate IS_FOUNDER = new Predicate() {
    public boolean indivSatisfies(Indiv ind) { return ind.getIsFounder(); }
    public String toString() 
    { return "founder"; }
  };

  public static final Predicate IS_DESCENDANT = new Predicate() {
    public boolean indivSatisfies(Indiv ind) { return !ind.getIsFounder(); }
    public String toString() 
    { return "descendant"; }
  };

  public static final Predicate IS_UNKNOWN = new Predicate() {
    public boolean indivSatisfies(Indiv ind) {
      return HAS_GTDATA.indivSatisfies(ind) && ind.getPtype() == Ptype.UNKNOWN;
    }
    public String toString() 
    { return "unknown"; }
  };

  public static final Predicate IS_CASE = new Predicate() {
    public boolean indivSatisfies(Indiv ind) {
      return HAS_GTDATA.indivSatisfies(ind) && ind.getPtype() == Ptype.CASE;
    }
    public String toString() 
    { return "case"; }
  };

  public static final Predicate IS_CONTROL = new Predicate() {
    public boolean indivSatisfies(Indiv ind) {
      return HAS_GTDATA.indivSatisfies(ind) && ind.getPtype() == Ptype.CONTROL;
    }
    public String toString() 
    { return "control"; }
  };

  public static final Predicate IS_TOGETHER = new Predicate() {
    public boolean indivSatisfies(Indiv ind) {
      return HAS_GTDATA.indivSatisfies(ind) && 
             (ind.getPtype() == Ptype.CASE || ind.getPtype() == Ptype.CONTROL);
    }
    public String toString()
    { return "case and control"; }
  };

  public static final Predicate IS_MCLD = new Predicate() {
    public boolean indivSatisfies(Indiv ind) {
      return HAS_GTDATA.indivSatisfies(ind) || HAS_FIRSTRELATIVES.indivSatisfies(ind);   
    }
    public String toString()
    { return "MCLD indiv"; }
  };
  /*
  public static final Predicate IS_CASE_50 = new Predicate() 
  {
    public boolean indivSatisfies(Indiv ind) 
    {
      return GTPercent_50.indivSatisfies(ind) && ind.getPtype() == Ptype.CASE;
    }
  };

  public static final Predicate IS_CONTROL_50 = new Predicate()
  {
    public boolean indivSatisfies(Indiv ind)
    {
      return GTPercent_50.indivSatisfies(ind) && ind.getPtype() == Ptype.CONTROL;
    }
  };

  public static final Predicate HAS_GTDATA = new Predicate() {
    public boolean indivSatisfies(Indiv ind) 
    {
      return ind.getGtype(Indiv.GtSelector.OBS) != null;
    }
  };

  public static final Predicate GTPercent_50 = new Predicate() 
  {
    public boolean indivSatisfies(Indiv ind) 
    {
      return ind.getGtype(Indiv.GtSelector.OBS).getGtPercent() > 0.5;
    }
  };
  **/

  public static final Predicate HAS_GTDATA = new Predicate() 
  {
    public boolean indivSatisfies(Indiv ind) 
    {
      boolean returnVal = false;
      //percentage should not be set here, 
      //should depend on the indiv's gtPhased set to true
      if (percentage == 0 && markers == null)
        return ind.getGtype(Indiv.GtSelector.OBS) != null;
      else if ( markers == null )
      {
        //if (ind.getGtype(Indiv.GtSelector.ORG) != null)
        if (ind.getGtype(Indiv.GtSelector.OBS) != null)
          return ind.getGtype(Indiv.GtSelector.OBS).getGtPercent() >= 
                 (double) percentage;
        return returnVal;
      }
      else 
      {
        //return ind.getGtype(Indiv.GtSelector.ORG).getGtPercent(markers) >= 
                 //(double) percentage / 100;
        //if ( ind.getGtype(Indiv.GtSelector.ORG) != null )
        if ( ind.getGtype(Indiv.GtSelector.OBS) != null )
        {
        double aa = ind.getGtype(Indiv.GtSelector.OBS).getGtPercent(markers); 
        //System.out.println("id " + ind.getID() + " percent : " + aa );
        return aa >=  (double) percentage / 100;
        }
        return returnVal;
      }
    }
    
    public String toString() 
    { return "has_GTdata"; }
  };

  //----------------------------------------------------------------------------
  public static final Predicate HAS_FIRSTRELATIVES = new Predicate()
  {   
    public boolean indivSatisfies(Indiv ind)
    {
      boolean returnVal = false;
      int count = 0;
      List sibs = ind.getSibs();
      if ( sibs.size() > 0 )
      {
        for ( int i =0; i < sibs.size(); i++ )
        {
          Indiv sib = (Indiv) sibs.get(i);
          if ( HAS_GTDATA.indivSatisfies(sib) )
          {
            count += 1;
          }
        }
      }
      List offspring = ind.getOffspring();
      if ( offspring.size() > 0 )
      {
        for ( int i =0; i < offspring.size(); i++ )
        {
          Indiv os = (Indiv) offspring.get(i);
          if ( HAS_GTDATA.indivSatisfies(os) )
          {
            count += 1;
          }
        }
      }
     
      if ( count > 1 )
      {
        return true;
      }
      else
      {
        return false;
      }
    }
  };
 
  //----------------------------------------------------------------------------
  static List<Indiv> hits(Predicate p, Collection inds_in)
  {
    List<Indiv> inds_out = new ArrayList<Indiv>();

    for (Iterator i = inds_in.iterator(); i.hasNext(); )
    {
      Indiv ind = (Indiv) i.next();
      if (p.indivSatisfies(ind))
        inds_out.add(ind);
    }

    return Collections.unmodifiableList(inds_out);
  }

  //----------------------------------------------------------------------------
  static List<Indiv> hits( Predicate p,
                           int percent,
                           Collection inds_in )
  {
    percentage = percent;
    return (List<Indiv>) hits (p, inds_in);
  }

  //----------------------------------------------------------------------------
  static List<Indiv> hits( Predicate p,
                           boolean gtPhased,
                           Collection inds_in )
  {
    //markers = loci;
    List<Indiv> inds_out = new ArrayList<Indiv>();

    for (Iterator i = inds_in.iterator(); i.hasNext(); )
    {
      Indiv ind = (Indiv) i.next();
      if ( p.indivSatisfies(ind) && ind.getGtPhased() == gtPhased )
        inds_out.add(ind);
    }

    return Collections.unmodifiableList(inds_out);
  }


  //----------------------------------------------------------------------------
  static List<Indiv> hits( Predicate p,
                           int percent,
                           int[] loci,
                           Collection inds_in )
  {
    percentage = percent;
    markers    = loci;
    return (List<Indiv>) hits( p, inds_in);
  }
  //----------------------------------------------------------------------------
  static List<Indiv> hits( Predicate p,
                           boolean gtPhased,
                           int[] loci,
                           Collection inds_in )
  {
    markers = loci;
    return (List<Indiv>) hits(p, gtPhased, inds_in);
  }

}

