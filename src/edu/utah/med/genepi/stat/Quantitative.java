//*****************************************************************************
// Quantitative.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

//=============================================================================
public class Quantitative extends CCStatImp
{
  public int notval;
  private int refColIndex, nCol;

  //---------------------------------------------------------------------------
  public Quantitative()
  { 
    title = "Quantitative, Two Tailed Test" ;
    obsExtraStatTitle = "Observed Overall Statistic : ";
    infExtraStatTitle = "Empirical Overall p-value : ";
  }

  //---------------------------------------------------------------------------
  public ComparisonTracker newComparisonTracker(int df)
  {
    return new QuantComparisonTracker();
  }

  //---------------------------------------------------------------------------
  public Result computeAt0 ( CCAnalysis a, 
			     CCAnalysis.Table t )
  {
    if ( a.checkDistinctRefWt() )
    {
      QuantitativeTable qt = ( QuantitativeTable) t;
      QuantitativeTable.TotalsExt totals = (QuantitativeTable.TotalsExt) qt.getTotals();
      nCol = qt.getColumnCount();
      refColIndex = a.getReferenceColumnIndex();
  
      resultAt0 = new ResultImp.RealSeries
                      ( StatUt.quantitative( 	totals.forColumns(),
  		        			totals.forCellSqColumns(),
  		        			totals.forTable(),
		        			totals.forCellSqTable(),
		        			totals.forCol_counts(),
		        			refColIndex ) );
      return resultAt0;
    }
    else
       return new ResultImp.StringResult
        ("WARNING: Analysis Table has more than one reference column, **test** has not been performed.");
  }

  //---------------------------------------------------------------------------
  public CCStat.Result computeAtX ( CCAnalysis a,
				    CCAnalysis.Table t )
  {
    return computeAt0( a, t );
  }

  //---------------------------------------------------------------------------
  public CCStat.Result getObservedResult(CCStat.Result result,
                                         int refColIndex )
  {
    double[] r0Result = result.doubleValues();
    int numCol = 0;

    if ( r0Result.length > 2 )
    {
      numCol = r0Result.length;
      double[] r0vals = new double[r0Result.length - 1];
      for ( int i = 0; i < r0Result.length - 1; i++ )
      {
        r0vals[i] = r0Result[i]; 
      }
      return ResultImp.convertTriplet( new ResultImp.RealSeries(r0vals),
                                       numCol,
                                       refColIndex );
    }
    else
    {
      numCol = r0Result.length + 1;
      return ResultImp.convertTriplet( resultAt0,
                                       numCol,
                                       refColIndex );
    }
  }
 
  //---------------------------------------------------------------------------
  public CCStat.Result getInferentialResult(ComparisonTracker compTracker,
                                            int refColIndex )
  {
    QuantComparisonTracker qct = (QuantComparisonTracker) compTracker;
    CCStat.Result aaResult = qct.getComparisonsResult();
    double[] compResult = aaResult.doubleValues(); 
    int infN = 0;
    int nCol = 0;
    if ( compResult.length > 2 )
    {
      nCol = compResult.length;
      infN = nCol - 1;
    }
    else 
    { 
      nCol = compResult.length + 1;
      infN = compResult.length;
    }

    double[] infResult = new double[infN];

    for ( int i = 0; i < infResult.length; i++ )
      infResult[i] = compResult[i];

    return ResultImp.convertTriplet( new ResultImp.RealSeries(infResult),
                                     nCol,
                                     refColIndex );
  }
                                     
  //---------------------------------------------------------------------------
  public CCStat.Result getObsExtraStat(CCStat.Result r0result )
  {
    obsExtraStatResult = null;
    double[] r0 = r0result.doubleValues();
    if ( r0.length > 2 )
    {
      if ( Double.isNaN(r0[r0.length - 1]) )
        obsExtraStatResult = new ResultImp.StringResult("-");
      else 
        obsExtraStatResult = new ResultImp.Real ( r0[r0.length - 1] ); 
    }
    return obsExtraStatResult;
  }

  //---------------------------------------------------------------------------
  public CCStat.Result getInfExtraStat(ComparisonTracker compTracker)
  {
    QuantComparisonTracker qct = (QuantComparisonTracker) compTracker;
    double[] compResult =  
                         qct.getComparisonsResult().doubleValues();
    CCStat.Result infExtResult = null;
    if ( compResult.length > 2 )
    {
      if ( Double.isNaN(compResult[compResult.length - 1]) )
        infExtResult =  new ResultImp.StringResult("-");
      else
        infExtResult = new ResultImp.Real(compResult[compResult.length - 1]);
    } 
       
    return infExtResult;
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable ( TableMaker tm )
  {
    return tm.getQuantitativeTable();
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable ( TableMaker tm, Thread p )
  {
    return tm.getQuantitativeTable(p);
  }

}
