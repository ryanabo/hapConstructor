//******************************************************************************
// IndivWtDropSim.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Marriage;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.Pedigree;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.util.Counter;
import edu.utah.med.genepi.util.EmpiricalRandomizer;
import edu.utah.med.genepi.util.GEException;
import edu.utah.med.genepi.util.Randy;
//import edu.utah.med.genepi.util.Ut;

//==============================================================================
public class IndivWtDropSim implements GSimulator.Drop {

  protected Study[]      allStudy;
  protected GDef         gdef;
  private Pedigree[]	ped;
  //private Indiv[]	inds, founderInds;
  public int		nWeightedCycle, nPeds, nStudy;
  private TreeMap<Indiv, Weight>	weightMap;
  private EmpiricalRandomizer sample;
  private final Randy  theRandomizer = Randy.getInstance();
  //protected List<Indiv> indslist;
  //protected Indiv person;

  //----------------------------------------------------------------------------
  public void preProcessor () throws GEException
  {
    for ( int i = 0; i < nStudy; i++)
    {
      Study study  = allStudy[i];
      PedData pd = study.getPedData();
      ped = pd.getPedigrees();
      nPeds = ped.length;

      for ( int j = 0; j < nPeds; j++ )
      {
        Collection<Indiv> anyInds = ped[j].getMembers();
        ArrayList<Indiv> founderList = new ArrayList<Indiv>();
        for ( Iterator anyit = anyInds.iterator(); anyit.hasNext(); )
        {
          Indiv any = (Indiv) anyit.next();
          if ( any.getIsFounder() )
          {
            founderList.add(any);
          }
        }
        Indiv[] founder = founderList.toArray(new Indiv[0]);

        //indslist = new ArrayList<Indiv>();
        weightMap = new TreeMap<Indiv, Weight>();
        assignFounderGenotypes(founder);
        for ( int k = 0; k < nWeightedCycle; k++ )
        {
          sample = new EmpiricalRandomizer(Randy.getInstance());
          updateSample(founder);
          Marriage[] marrs = ped[j].getFoundingMarriages();
          for (int imarr = 0, n = marrs.length; imarr < n; ++imarr)
            drop(marrs[imarr], k); // start recursive descend
          updateWeight();
        }

        //System.out.println("nCycle :" + nWeightedCycle);
        for (Iterator it = weightMap.keySet().iterator(); it.hasNext(); ) 
        {
          Indiv indiv = (Indiv) it.next();
          if ( indiv.getPtype() != Ptype.UNKNOWN )
          {
            Weight wt = weightMap.get(indiv);
            double wtIndex = wt.getWeightedIndex() / nWeightedCycle;
            indiv.setWeightedIndex( wtIndex );
          }
        }
      }
    }
  } 

  //----------------------------------------------------------------------------
  public void setUserParameters(Specification spec, Study[] std) 
  {
    allStudy = std;
    nStudy = std.length;
    nWeightedCycle  = spec.getWeightedCycle();
  }

  //----------------------------------------------------------------------------
  public void setPedData()
  {}

  //----------------------------------------------------------------------------
  public void setGDef(GDef gd)
  {
    //nLoci = gd.getLocusCount();
    //gtBuilder = gd.getGtypeBuilder();
    //gdef = gd;
  }

  //----------------------------------------------------------------------------
  public void assignFounderGenotypes(Indiv[] founder)
         throws GEException
  {
    // Assign founder unique alleles
    Integer num = 1;
    for ( int i = 0; i < founder.length; i++ )
    {
      Ptype founderPtype = founder[i].getPtype();

      Integer allele1 = num++;
      Integer allele2 = num++;
      Weight wt = new Weight(allele1, allele2, 0.0, 0);
      weightMap.put(founder[i], wt);
        //System.out.println("Assign founder : " + founder[i].getPedigree().getID() + "-" + founder[i].getID() + ", a1leles : " + allele1 + " / " + allele2 + " to weightMap ,size : " + weightMap.size() );
    }
  }

  //----------------------------------------------------------------------------
  public void updateSample(Indiv[] founder)
  {
    for ( int i = 0; i < founder.length; i++ )
    {
      Ptype founderPtype = founder[i].getPtype();
      if ( founderPtype != Ptype.UNKNOWN )
      {
        Weight founderWt = weightMap.get(founder[i]);
        if ( founderWt != null )
        {
          sample.sampleNext(founderWt.getAllele(true));
          sample.sampleNext(founderWt.getAllele(false));
        }
      }
    }
  }
  //----------------------------------------------------------------------------
  public void simulateDescendantGenotypes(int index)
         throws GEException
  {}

  //----------------------------------------------------------------------------
  // overloaded for hapBuilder
  public void simulateDescendantGenotypes(int index, compressGtype[] cGtype,
                                          int step)
  throws GEException
  {
    System.out.println("WARNING : simulated descendant without using compressGtype");
  }

  //----------------------------------------------------------------------------
  private void drop(Marriage m, int icycle) throws GEException
  {
    Indiv father = m.getHim();
    Indiv mother = m.getHer();

    if ( weightMap.get(father) != null && weightMap.get(mother) != null )
    {
      Weight hisWt = weightMap.get(father);
      Weight herWt = weightMap.get(mother);
      
      //System.out.println("father is : " + father.getPedigree().getID() + "-" + father.getID() + " weight " + hisWt.getWeightedIndex());
      //System.out.println(" mother is : " + mother.getPedigree().getID() + "-" + mother.getID() + " weight " + herWt.getWeightedIndex());

      if ( (hisWt.getCycleIndex() == icycle || father.getIsFounder() ) &&
           (herWt.getCycleIndex() == icycle || mother.getIsFounder()) )
      {
        // for each kid...
        for (Iterator kidit = m.getKids().iterator(); kidit.hasNext(); )
        {
          //System.out.println("parents have alleles assigned " );
          Indiv  kid = (Indiv) kidit.next();
	  Integer allele1 = hisWt.getAllele(theRandomizer.nextBoolean(0.5));
          Integer allele2 = herWt.getAllele(theRandomizer.nextBoolean(0.5));
          Weight kidWt = weightMap.get(kid);
          // if we haven't yet reached this kid descending via spouse
          if  ( kidWt == null ) 
          {
            kidWt = new Weight(allele1, allele2, 0.0, icycle);
            //System.out.println("Add kid : " + kid.getPedigree().getID() + "-" + kid.getID() + " to weightMap " + kidWt.getAllele(true) + "/" + kidWt.getAllele(false));
            weightMap.put(kid, kidWt);
          }
          else if ( kidWt.getCycleIndex() < icycle )
          {
            kidWt.setWeight(allele1, allele2, icycle);
            //System.out.println("update kid " + kid.getID() + " allele and icycle: " + allele1 + "/" + allele2 + "  " + icycle);
          }

          if ( kid.getPtype() != Ptype.UNKNOWN  )
          {
            sample.sampleNext(allele1);
            sample.sampleNext(allele2);
            //System.out.println(" - added kid : " + kid.getID() + " to sample");
          }
          
          // for each of this kid's marriages, if any...
          List marrs = kid.getMarriages();
          if (marrs != null)
          for (Iterator marrit = marrs.iterator(); marrit.hasNext(); )
            drop((Marriage) marrit.next(), icycle);   // recurse
        }
      }
    }
    else 
      return;
  }
 
  //----------------------------------------------------------------------------
  public void updateWeight()
  {
    TreeMap<Object, Counter> sampleMap = sample.getCounterMap();
    sample.closeSampling();

    //System.out.println("UPDATEWEIGHT ---");
    for (Iterator it = weightMap.keySet().iterator(); it.hasNext(); )
    {
      Indiv indiv = (Indiv) it.next();
      if ( indiv.getPtype() != Ptype.UNKNOWN )
      {
        Weight weight = weightMap.get(indiv);
        Counter c1 = sampleMap.get(weight.getAllele(true));
        Counter c2 = sampleMap.get(weight.getAllele(false));
        //if ( c1 == null || c2 == null )
          //System.out.println(" ____ indiv has null count  " + indiv.getPedigree().getID() + "-" + indiv.getID());
        double w1 = (double) 1/ c1.current().intValue();
        double w2 = (double) 1/ c2.current().intValue();
        double tempWt = (w1 + w2) / 2;
        //System.out.println("ind : " + indiv.getID() + "----- matched allele " + weight.getAllele(true) + "/" + weight.getAllele(false) + "current weight : " + weight.getWeightedIndex() + " increase wt by : " + (double) 1 / c1.current().intValue() + " and " + (double) 1 / c2.current().intValue() + " total : " + tempWt );
        weight.increaseWeightBy(tempWt);
        //System.out.println("   New Weighted Index is : " + weight.getWeightedIndex());
      }
    }
    //System.out.println("new wt index : " + wt.getWeightedIndex());
  }

  //----------------------------------------------------------------------------
  public class Weight 
  {
    Integer allele1, allele2;
    double weightedIndex = 0.0;
    int cycleIndex = 0;

    public Weight(Integer a1, Integer a2, double wtIndex)
    {  
      allele1 = a1;
      allele2 = a2;
      weightedIndex = wtIndex;
    }

    public Weight(Integer a1, Integer a2, double wtIndex, int icycle)
    {
      allele1 = a1;
      allele2 = a2;
      weightedIndex = wtIndex;
      cycleIndex = icycle;
    }
 
    public Integer getAllele(boolean inBoolean)
    { return ( inBoolean ? allele1 : allele2); }
  
    public void increaseWeightBy( double inValue)
    { weightedIndex += inValue; }
  
    public double getWeightedIndex()
    { return weightedIndex; }

    public void setCycleIndex ( int inCycle)
    { 
      //System.out.println("cycle index : " + inCycle);
      cycleIndex = inCycle; 
      //System.out.println("cycle index completed");
    }
  
    public int getCycleIndex()
    { return cycleIndex; }
      
    public void setWeight(Integer a1, Integer a2, int inCycle)
    {
      allele1 = a1;
      allele2 = a2;
      setCycleIndex(inCycle);
    }
  }
}
