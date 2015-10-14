//*****************************************************************************
// OddsRatios.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

import java.util.Vector;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

//=============================================================================
public class OddsRatios extends CCStatImp 
{

	//---------------------------------------------------------------------------
	public OddsRatios() 
	{
		title = "Odds Ratios, 2-Tailed Test";
		//infExtraStatTitle = "Empirical Confidence Intervals : ";
	}

	//---------------------------------------------------------------------------
	public ComparisonTracker newComparisonTracker()
	{
		//return new ORCompTrackerExt(); 
		return new ORComparisonTracker();
	}

	public ComparisonTracker newComparisonTracker(int df)
	{
		return this.newComparisonTracker();
	}

	//---------------------------------------------------------------------------
	public Result computeStat ( Analysis a, AnalysisTable[] tables, int[] simIndices )
	{
		AnalysisTable t = tables[0];
		if ( simIndices[0] == 0 )
		{
			if ( a.checkDistinctRefWt() )
			{
				if ( StatUt.checkRefCell ( t.getRowFor(Ptype.CASE).getCells(), 
						t.getRowFor(Ptype.CONTROL).getCells(),
						a.getRefColIndex()           ) )
					//return new RatioCITripletSeries(
					//StatUt.oddsRatiosWithConfidence(
				{
					Result res = new  ResultImp.RealSeries (
							StatUt.oddsRatios(
									t.getRowFor(Ptype.CASE).getCells(),
									t.getRowFor(Ptype.CONTROL).getCells(),
									a.getRefColIndex() ) );
					//if ( res == null )
					//  System.out.println("null inside OR " );
					//System.out.println(" result is not null, "+ res.toString());
					return res;
				}
				else 
					return new ResultImp.StringResult
					("WARNING: reference cell has 0 value, **test** has not been performed.");
			}
			else 
				return new ResultImp.StringResult 
				("WARNING: Analysis Table has more than one reference column, **test** has not been performed.");		 
		}
		else
		{
			if ( StatUt.checkRefCell ( t.getRowFor(Ptype.CASE).getCells(),
					t.getRowFor(Ptype.CONTROL).getCells(), 
					a.getRefColIndex()            ) )
			{
				Result res = 
					new ResultImp.RealSeries(
							StatUt.oddsRatios(
									t.getRowFor(Ptype.CASE).getCells(),
									t.getRowFor(Ptype.CONTROL).getCells(),
									a.getRefColIndex()
							)
					);
				//if ( res == null )
				//  System.out.println(" simulated - result is null ");
				//System.out.println(" simulated result is not null, " + res.toString());
				return res;
			}
			else
				return new ResultImp.StringResult
				("WARNING: reference cell has 0 value, **test** has not been performed.");
		}
	}

	//---------------------------------------------------------------------------
//	public Result computeAt0 ( CCAnalysis a,
//			CCAnalysis.Table t )
//	{
//		if ( a.checkDistinctRefWt() )
//		{
//			if ( StatUt.checkRefCell ( t.getRowFor(Ptype.CASE).getCells(), 
//					t.getRowFor(Ptype.CONTROL).getCells(),
//					a.getReferenceColumnIndex()           ) )
//				//return new RatioCITripletSeries(
//						//StatUt.oddsRatiosWithConfidence(
//			{
//				Result res = new  ResultImp.RealSeries (
//						StatUt.oddsRatios(
//								t.getRowFor(Ptype.CASE).getCells(),
//								t.getRowFor(Ptype.CONTROL).getCells(),
//								a.getReferenceColumnIndex() ) );
//				//if ( res == null )
//					//  System.out.println("null inside OR " );
//				//System.out.println(" result is not null, "+ res.toString());
//				return res;
//			}
//			else 
//				return new ResultImp.StringResult
//				("WARNING: reference cell has 0 value, **test** has not been performed.");
//		}
//		else 
//			return new ResultImp.StringResult 
//			("WARNING: Analysis Table has more than one reference column, **test** has not been performed.");
//	}

	//---------------------------------------------------------------------------
//	public Result computeAtX ( CCAnalysis a,
//			CCAnalysis.Table t )
//	{
//		if ( StatUt.checkRefCell ( t.getRowFor(Ptype.CASE).getCells(),
//				t.getRowFor(Ptype.CONTROL).getCells(), 
//				a.getReferenceColumnIndex()            ) )
//		{
//			Result res = 
//				new ResultImp.RealSeries(
//						StatUt.oddsRatios(
//								t.getRowFor(Ptype.CASE).getCells(),
//								t.getRowFor(Ptype.CONTROL).getCells(),
//								a.getReferenceColumnIndex()
//						)
//				);
//			//if ( res == null )
//				//  System.out.println(" simulated - result is null ");
//			//System.out.println(" simulated result is not null, " + res.toString());
//			return res;
//		}
//		else
//			return new ResultImp.StringResult
//			("WARNING: reference cell has 0 value, **test** has not been performed.");
//	}

	//----------------------------------------------------------------------------
	//public Result getObservedResult(Result obsResult,
	public synchronized Result getObservedResult( Result obsResult,
			int    refColIndex )
	{
		/*String[][] resultSeries = new String[numCol - 1][3];
    double[] r0Vals = observedResult.doubleValues();
    for ( int j = 0, r = 0; j < numCol; j++ )
    {
      if ( j != referenceColIndex && r < r0Vals.length )
      {
        resultSeries[r][0] = Integer.toString(j+1);
        resultSeries[r][1] = Integer.toString(referenceColIndex + 1);
        resultSeries[r][2] = Double.toString(r0Vals[r]);
        r++;
      }
    }
    return new ResultImp.TripletSeries ( resultSeries );
		 */
		if ( obsResult instanceof ResultImp.StringResult )
			return obsResult;
		else
		{
			double[] r0Result = obsResult.doubleValues();
			int numCol = r0Result.length + 1;
			return ResultImp.convertTriplet( obsResult, numCol, refColIndex );
		}
	}

	//----------------------------------------------------------------------------
	public Result getInferentialResult(ComparisonTracker compTracker,
			int refColIndex )
	{
		ORComparisonTracker ORct = (ORComparisonTracker) compTracker;
		double[] rr = (ORct.getComparisonsResult()).doubleValues();
		int numCol = rr.length + 1;
		return ResultImp.convertTriplet(ORct.getComparisonsResult(),
				numCol,
				refColIndex );
	}

	//----------------------------------------------------------------------------
	/*public Result getInfExtraStat(ComparisonTracker compTracker)
  {
    ORCompTrackerExt ORct = (ORCompTrackerExt) compTracker;
    return ORct.getConfidenceIntervals();
  }
	 */
	//----------------------------------------------------------------------------
	public Vector<Vector<Double>> getInfSimStat(ComparisonTracker compTracker)
	{
		ORCompTrackerExt ORct = (ORCompTrackerExt) compTracker;
		return ORct.getSimulatedResults();
	}

	//---------------------------------------------------------------------------
	public AnalysisTable getTable( Analysis analysis, int studyIndex, int[] simIndices )
	{
		return (AnalysisTable) analysis.getContingencyTable(studyIndex,simIndices); 
	}
}
