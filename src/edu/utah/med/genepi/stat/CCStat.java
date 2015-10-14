//******************************************************************************
// CCStat.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import java.util.Vector;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
//import umontreal.iro.lecuyer.probdist.ChiSquareDist;
//==============================================================================
public interface CCStat {

  public String getName();
  public String getStatType();
  public String getCompStatTitle();
  public ComparisonTracker newComparisonTracker();
  public ComparisonTracker newComparisonTracker(int df);
  public AnalysisTable getTable(Analysis analysis,int studyIndex,int[] simIndices);
  public CCAnalysis.Table[] getNonZeroTables( CCAnalysis a,
                                              CCAnalysis.Table[] t,
                                              ComparisonTracker compTracker);
  public Result computeStat ( Analysis a, AnalysisTable[] t, int[] simIndices );
  //public Result computeStat ( CCAnalysis a, CCAnalysis.Table t, int simIndex );
//  public Result computeAtX ( CCAnalysis a, CCAnalysis.Table[] t );
//  public Result computeAtX ( CCAnalysis a, CCAnalysis.Table t );
  public Result getObservedResult (Result result, int refCol);
  public Result getInferentialResult (ComparisonTracker compTracker,
                                      int refCol );
  public String getObsExtraStatTitle();
  public Result getObsExtraStat(Result result);
  public String getInfExtraStatTitle();
  public Result getInfExtraStat(ComparisonTracker compTracker);
  public Vector<Vector<Double>> getInfSimStat(ComparisonTracker compTracker);
  public int getDegreeOfFreedom(int ncol);

  public interface ComparisonTracker {
//    public void setStartingResult(Result r0);
//    public void compareResultAtX(Result rx);
	public void setResult(Result r, int simIndex);
    public Result getComparisonsResult();
    public int[] getComparisonCount();
    public int[] getnotval();
    public void setMessages(String inString);
    public String getMessages();
    public void setDegreeOfFreedom(int df);
  }

  public interface Result {
    public int elementCount();
    public double[] doubleValues();
    public String toString();
  }
}
 
