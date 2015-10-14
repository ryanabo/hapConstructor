//******************************************************************************
// IndivWtDrop.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Marriage;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.PedQuery;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class IndivWtDrop implements GSimulator.Drop {

  protected Study[]	study;
  protected Indiv[] 	founders;
  public int		nStudy;
  public int 		nfounders;
  private double[]      founderAverage;
  private List<Indiv>[]	ldescendants;
  private PedData	peddata;

  //----------------------------------------------------------------------------
  public void preProcessor () throws GEException
  {
    for ( int i = 0; i < nStudy; i++ )
    {
      peddata = study[i].getPedData();
      founders = peddata.getIndividuals(PedQuery.IS_FOUNDER);
      nfounders = founders.length;
      founderAverage = new double[nfounders];
      ldescendants = new List[nfounders];

      samplingWeight();
      updateIndivWeight();
    }
  } 

  //----------------------------------------------------------------------------
  public void setUserParameters(Specification spec, Study[] std) 
  {
    study = std;
    nStudy = study.length;
  }

  //----------------------------------------------------------------------------
  public void setPedData()
  {}

  //----------------------------------------------------------------------------
  public void setGDef(GDef gd)
  {} 

  //----------------------------------------------------------------------------
  public void simulateDescendantGenotypes(int index, int simcycle) 
         throws GEException
  {}
  
  // overloaded for hapBuilder
  public void simulateDescendantGenotypes(int index, compressGtype[] cGtype,
                                          int step)
  throws GEException
  {
    System.out.println("WARNING : simulated descendant without using compressGtype");
  }

  
  //----------------------------------------------------------------------------
  public void samplingWeight() throws GEException 
  {
    for ( int i = 0; i < nfounders; i++)
    {
      if ( founders[i].getPtype() != Ptype.UNKNOWN )
        founderAverage[i] = 1.0;
      else 
        founderAverage[i] = 0.0;
      ldescendants[i] = new ArrayList<Indiv>();
      ldescendants[i].add(founders[i]);
      drop(founders[i], 1, i);
      if ( founderAverage[i] > 0.0 )
        founderAverage[i] = 1 / founderAverage[i];
    }
  }
 
  //----------------------------------------------------------------------------
  private void drop(Indiv indiv, double indivWt, int i) throws GEException
  {
    List<Marriage> lmarriages = indiv.getMarriages();
    System.out.println("Indiv : " + indiv.getID() + " wt : " + indivWt);
    if ( lmarriages != null)
    {
      for (Iterator mit = lmarriages.iterator(); mit.hasNext(); )
      {
        Marriage marr = (Marriage) mit.next();
    
        List<Indiv> lkids = marr.getKids();
        for ( Iterator kidit = lkids.iterator(); kidit.hasNext(); )
        {
          Indiv kid = (Indiv) kidit.next();
          Ptype kidPtype = kid.getPtype();
          double wt = indivWt / 2;
          if ( kidPtype != Ptype.UNKNOWN )
          {
            // don't need testing, add will check kid in the list
            //if ( !ldescendants[i].contains(kid) )
            //{
            ldescendants[i].add(kid);
              System.out.println("add kid : "+kid.getID()+" to list : "+i);
            //}
            founderAverage[i] += wt;
            System.out.println("now founder "+i+" wt is "+founderAverage[i]);
          }
          //System.out.println("last parent wt : " + wt);

          drop( kid, wt, i );
        }
      }
    }
    else 
    { System.out.println("   no marriages " );}
  }
 
  //----------------------------------------------------------------------------
  public void updateIndivWeight()
  {
    Indiv[] indiv = peddata.getIndividuals(PedQuery.IS_ANY);
    int numfounders = founderAverage.length;

    for ( int i = 0; i < indiv.length; i++ )
    {
      if ( indiv[i].getPtype() != Ptype.UNKNOWN )
      {
        double wt = 0.0;
        int count = 0;
        for ( int k = 0; k < numfounders; k++ )
        {
          if ( ldescendants[k].contains(indiv[i]))
          {
            //System.out.println("indiv : " + indiv[i].getID() + " in list : " + i + " his average is : " + founderAverage[k]);
            wt += founderAverage[k];
            count++;
            //System.out.println("updated wt : " + wt + "count : " + count);
            //System.out.println("list : " + j + " Contains ind: " + indiv[i].getID() + " count : " + count );
          }
        }
        indiv[i].setWeightedIndex(wt / count); 
        //System.out.println("indiv : " + indiv[i].getID() + " weight : " + indiv[i].getWeightedIndex());
      }
    }
  }
}
