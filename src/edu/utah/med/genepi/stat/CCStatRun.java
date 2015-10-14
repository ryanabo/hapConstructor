//*****************************************************************************
// CCStatRun.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import java.util.Vector;
//=============================================================================
public class CCStatRun {

  public interface Report {
    public String getTitle();
    public String getCompStatTitle();
    public int[]  getNumSimulationReport();
    public String getFullObservationalReport();
    public String getObservationalReport();
    public String getObsExtraReport();
    public String getFullInferentialReport();
    public String getInferentialReport();
    public String getInfExtraStatTitle();
    public String getInfExtraReport();
    public String getObsValue();
    public Vector<Vector<Double>> getInfSimReport();
    public String getWarning();
  }

  public interface Probe {
    public void logTableContents(CCAnalysis.Table[] t);
    public void logTableContents(CCAnalysis.Table t);
    public void logStatResult(CCStat.Result r);
    public void closeLog();
  }

  private final CCAnalysis               myOwner;
  protected final CCStat                   myStat;
  private final CCStat.ComparisonTracker compTracker;
  private final Probe                    myProbe;
  private       CCStat.Result            r0Result;
  protected     CCStat.Result		 observedResult;
  private       String 			 quantStatF;
  private 	CCStat.Result 			 pValue;
  private       int 			 myRefColIndex;

  //---------------------------------------------------------------------------
  CCStatRun(CCAnalysis owner, CCStat stat, Probe p)
  {
    myOwner = owner;
    myStat = stat;
    int nCol = owner.getColumnWeights().length;
    compTracker = myStat.newComparisonTracker(myStat.getDegreeOfFreedom(nCol));
    myProbe = p;
    myRefColIndex = myOwner.getReferenceColumnIndex();
  }

  //---------------------------------------------------------------------------
  void computeObserved(CCAnalysis.Table[] t)
  {
    //if (myOwner.checkDistinctWeights())
    //{
      r0Result = myStat.computeAt0(myOwner, t);
    //}
    //else 
    //{
     // r0Result = new ResultImp.StringResult
      //  ("WARNING : weights are not distinct in the table defined, **test** has not been performed");
    //}
    //int df = myStat.getDegreeOfFreedom(t[0].getColumnCount());
    //compTracker.setDegreeOfFreedom(df);
    compTracker.setStartingResult(r0Result);
    observedResult = myStat.getObservedResult( r0Result,
                                               myRefColIndex );
  }

  //---------------------------------------------------------------------------
  void computeObserved(CCAnalysis.Table t)
  {
    //if (myOwner.checkDistinctWeights())
    //{
      r0Result = myStat.computeAt0(myOwner, t);
    //}
    //else 
    //{
     // r0Result = new ResultImp.StringResult
      //  ("WARNING : weights are not distinct in the table defined, **test** has not been performed");
    //}

    compTracker.setStartingResult(r0Result);
    observedResult = myStat.getObservedResult( r0Result,
                                               myRefColIndex );
  }

  //---------------------------------------------------------------------------
  void computeSimulated(CCAnalysis.Table[] t)
  {
    if ( !(r0Result instanceof ResultImp.StringResult) )
    {
      CCStat.Result r = myStat.computeAtX(myOwner, t);
      compTracker.compareResultAtX(r);

      if (myProbe != null)
      {
        myProbe.logTableContents(t);
        myProbe.logStatResult(r);
      }
    }
  }

  //---------------------------------------------------------------------------
  void computeSimulated(CCAnalysis.Table t)
  {
    if ( !(r0Result instanceof ResultImp.StringResult) )
    {
      CCStat.Result r = myStat.computeAtX(myOwner, t);
      compTracker.compareResultAtX(r);

      if (myProbe != null)
      {
        myProbe.logTableContents(t);
        myProbe.logStatResult(r);
      }
    }
  }

  //---------------------------------------------------------------------------
  Report newReport()
  {
    if (myProbe != null)
      myProbe.closeLog();

    if ( r0Result instanceof ResultImp.StringResult )
      pValue = new ResultImp.StringResult("-");
  
    else 
      pValue = myStat.getInferentialResult(compTracker, myRefColIndex);

    final int[] numSimulations = compTracker.getComparisonCount();

    return new Report() {
      public String getTitle() 
      {
        return myStat.getName();
      }

      public String getCompStatTitle()
      { 
        return myStat.getCompStatTitle();
      }
      
      public String getObsValue()
      {
        return r0Result.toString();
      }

      public int[] getNumSimulationReport()
      { 
        return numSimulations;
      }

      public String getFullObservationalReport() 
      {
        return "Observed statistic : " + observedResult;
      }
     
      public String getObservationalReport() 
      //public CCStat.Result getObservationalReport() 
      {
        if ( observedResult == null )
        {
          //System.out.println("null for " + myStat.getName() + "result ");
          //return new ResultImp.StringResult("null");
          return "null";
        }
        else 
        {
          //System.out.println(" CCStatRun getObservationalReport ");
          return observedResult.toString();
          //return observedResult;
        }
      }

      public String getObsExtraReport()
      //public CCStat.Result getObsExtraReport()
      {
        String extraStat = null;
        CCStat.Result obsExtra = myStat.getObsExtraStat(r0Result);
        if ( obsExtra != null ) 
          extraStat = myStat.getObsExtraStatTitle() + obsExtra;
        return extraStat;
        //return myStat.getObsExtraStat(r0Result);
      }

      public String getFullInferentialReport() 
      {
        return "Empirical p-value : " + pValue;
      }
  
      //public CCStat.Result getInferentialReport() 
      public String getInferentialReport() 
      {
        return pValue.toString();
        //return pValue;
      }

      public String getInfExtraStatTitle()
      {
        return myStat.getInfExtraStatTitle();
      }

      public String getInfExtraReport()
      //public CCStat.Result getInfExtraReport()
      {
        String extraStat = null;
        if ( myStat.getInfExtraStat(compTracker) != null )
          extraStat = " " + myStat.getInfExtraStat(compTracker);
        return extraStat;
        //return myStat.getInfExtraStat(compTracker);
      }
 
      public Vector<Vector<Double>> getInfSimReport()
      {
        String extraStat = null;
        //if ( myStat.getInfSimStat(compTracker) != null )
          return myStat.getInfSimStat(compTracker);
      }


      public String getWarning()
      { 
        // Temporary remove this section, as notval is now int[] not just int
        //int[] notval = compTracker.getnotval();
        //if (notval > 0 )
        return compTracker.getMessages();
      }

    };
  }
  
  public String getStatName()
  { return myStat.getName(); }

}
