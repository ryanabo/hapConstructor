//******************************************************************************
// QuantitativeTallier.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.gm.Qdata;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable.Column;
import edu.utah.med.genepi.util.Counter;

//==============================================================================
public class QuantitativeTallier extends TallierImp
{
  private final ColumnManager columnManager;
  private final int[] theRows;
  private final Column[] theCols;
  private Counter[][] theSqCounters = null;
  private Qdata[] quants = null;
  
  public QuantitativeTallier ( int[] quantIds, ColumnManager cm, Qdata[] qdata )
  {
	  super(qdata[0].getNQuants(),cm.getNCols());
	  quants = qdata;
	  theRows = quantIds;
	  theCols = cm.getCols();
	  columnManager = cm;
	  nRows = quantIds.length;
	  nCols = theCols.length;
	  theCounters = createCounters(0.0);
	  theSqCounters = createCounters(0.0);
	  
	  for ( int i = 0; i < nRows; i++ )
	  {
		  for ( int j = 0; j < nCols; j++ ){ theSqCounters[i][j].set(0.0); }
	  }
  }
  
	//----------------------------------------------------------------------------
	public boolean screenMissing( int indIndex ){ return false; }
	
	//----------------------------------------------------------------------------
	public boolean processQueryResult( int[] queryResult, int indIndex, int queryIndex )
	{
		boolean foundColumn = false;
		if ( queryResult != null )
		{
			int[] incColIndices = columnManager.processQueryResult(queryResult,queryIndex);
			for ( int i=0; i < incColIndices.length; i++ )
			{
				for ( int j=0; j < nRows; i++ )
				{
					Qdata qd = quants[indIndex];
					double quantval = qd.getQdata(j);
					if ( quantval != 0.00 )
					{
						double sqval = quantval * quantval;
						theCounters[j][incColIndices[i]].sum(quantval,1);
						theSqCounters[j][incColIndices[i]].sum(sqval,1);
					}
				}
			}

			String type = columnManager.getType();
		
			if ( type.equals("allele") && incColIndices.length == 2 ) foundColumn = true;
			else if ( type.equals("genotype") && incColIndices.length == 1 ) foundColumn = false;
		}
		return foundColumn;
	}
	
	//----------------------------------------------------------------------------
	public GenotypeDataQuery[] getDataQuery(){ return columnManager.getDataQuery(); }
  
  
  //----------------------------------------------------------------------------
  public AnalysisTable extractTable()
  {
    return new QuantitativeTable(theRows,theCols,theCounters,theSqCounters);
  }

//  //----------------------------------------------------------------------------
//  QuantitativeTallier( int[] quant_ids, CCAnalysis.Table.Column[] cols, 
//                       String atype )
//  {
//      super(quant_ids.length, cols.length);
//      theRows = quant_ids;
//      theCols = cols;
//      theAtype = atype;
//      nRows = quant_ids.length;
//      nCols = theCols.length;
//      theCounters = createCounters();
//      theSqCounters = createCounters();
//      for ( int i = 0; i < nRows; i++ )
//      {
//        for ( int j = 0; j < nCols; j++ ){ theSqCounters[i][j].set(0.0); }
//      }
//  }
//
//  //----------------------------------------------------------------------------
//  void sumExpressionEvent(Gtype gt, Pedigree ped, int qt, double quantval)
//  {
//    sumExpressionEvent(gt, ped, qt, quantval, null);
//  }
//
//  //----------------------------------------------------------------------------
//  void sumExpressionEvent( Gtype gt, Pedigree ped, int qt, 
//                           double quantval, Thread p )
//  {
//    if ( quantval != 0.0 )
//    {
//      if ( theAtype.matches("allele") )
//      {
//        for ( int i = 0; i < 2; i++ )
//        {
//          boolean first = ((i==0) ? true : false);
//          for ( int icol = 0; icol < nCols; icol++ )
//          {
//            //int value = theCols[icol].subsumesGtype(gt, first);
//            if ( (theCols[icol].subsumesAtype(gt, first)) == 1)
//            {
//              //for (int irow = 0; irow < nRows ; irow++ )
//              //{
//              //  if (irow == qt )
//              //  {
//                  //double sqval = quantval * quantval * value * value;
//                  double sqval = quantval * quantval;
//                  //theCounters[irow][icol].sum(quantval * value, value);
//                  theCounters[qt][icol].sum(quantval, 1);
//                  theSqCounters[qt][icol].sum(sqval, 1);
//	      //  }
//              //}
//              break;
//            }
//          }
//        }
//      }
//      else
//      {
//        for ( int icol = 0; icol < nCols; icol++ )
//        {
//          if ( (theCols[icol].subsumesGtype(gt)) == 1)
//          {
//            //for (int irow = 0; irow < nRows ; irow++ )
//            //{
//            //  if (irow == qt )
//            //  {
//                //double sqval = quantval * quantval * value * value;
//                double sqval = quantval * quantval;
//            //    theCounters[irow][icol].sum(quantval * value, value);
//                theCounters[qt][icol].sum(quantval, 1);
//                theSqCounters[qt][icol].sum(sqval, 1);
//            //  }
//            //}
//            break;
//          }
//        }
//      }
//    }
//  }

}
