//******************************************************************************
// ContingencyTallier.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.genie.Phenotype;
import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

public class ContingencyTallier extends TallierImp 
{
	protected static final int NPTYPES = Ptype.instanceCount();
	protected Ptype[] pTypes = null;
	protected ColumnManager columnManager = null;
	protected final int[] pid2RowIx = new int[NPTYPES];
	protected Phenotype[] phenotypes = null;
	
	public ContingencyTallier( Ptype[] ptypes, ColumnManager cm, Phenotype[] phens )
	{
		super(ptypes.length,cm.getNCols());
		pTypes = ptypes;
		columnManager = cm;
		theCounters = createCounters(0);
		phenotypes = phens;

		for (int irow = 0; irow < nRows; ++irow)
		{
			int r = Integer.parseInt(pTypes[irow].getID());
			pid2RowIx[r] = irow;
		}
	}
	
	//----------------------------------------------------------------------------
	public boolean screenMissing( int indIndex )
	{
		Phenotype p = phenotypes[indIndex];
		boolean missing = p.getPhenotype() == 0 ? true : false;
		return missing;
	}
	
	//----------------------------------------------------------------------------
	public boolean processQueryResult( int[] queryResult, int indIndex, int queryIndex )
	{
		boolean foundColumn = false;
		if ( queryResult != null )
		{
			int[] incColIndices = columnManager.processQueryResult(queryResult,queryIndex);
			for ( int i=0; i < incColIndices.length; i++ )
			{
				int phenValue = phenotypes[indIndex].getPhenotype();
				//System.out.println(phenValue);
//				if ( incColIndices[i] > theCounters[pid2RowIx[phenValue]].length )
//				{
//					System.out.println("Error");
//				}

				theCounters[pid2RowIx[phenValue]][incColIndices[i]].add(1);
			}

			String type = columnManager.getType();
		
			if ( type.toLowerCase().equals("allele") && incColIndices.length == 2 ) foundColumn = true;
			else if ( type.toLowerCase().equals("genotype") && incColIndices.length == 1 ) foundColumn = true;
		}
		return foundColumn;
	}
	
	//----------------------------------------------------------------------------
	public GenotypeDataQuery[] getDataQuery(){ return columnManager.getDataQuery(); }

	//----------------------------------------------------------------------------
	public AnalysisTable extractTable()
	{
		return new ContingencyTable(pTypes,columnManager.getCols(),theCounters,pid2RowIx);
	}
}

//package edu.utah.med.genepi.stat;
//
//import edu.utah.med.genepi.gm.Gtype;
//import edu.utah.med.genepi.gm.Ptype;
//
////==============================================================================
//class ContingencyTallier extends TallierImp
//{
//  protected static final int NPTYPES = Ptype.instanceCount();
//
//  protected final Ptype[]              		thePtypes;
//  protected final CCAnalysis.Table.Column[]	theCols;
//  protected final String			theAtype;
//  protected final int[]				pid2RowIx = new int[NPTYPES];
//  //protected final Map				mGt2ColIx = new HashMap();
//
//  //----------------------------------------------------------------------------
//  ContingencyTallier( Ptype[] ptypes, 
//		      CCAnalysis.Table.Column[] cols,
//		      String atype )
//  {
//    super ( ptypes.length, cols.length);
//    thePtypes = ptypes;
//    theCols = cols;
//    theAtype = atype;
//    theCounters = createCounters();
//
//    // establish a mapping from row phenotype ids back to row indices
//    for (int irow = 0; irow < nRows; ++irow)
//    {
//      int r = Integer.parseInt(thePtypes[irow].getID());
//      //pid2RowIx[thePtypes[irow].getID()] = irow;
//      pid2RowIx[r] = irow;
//    }
//  }
//
//  //----------------------------------------------------------------------------
//  void countExpressionEvent(Gtype gt, Ptype pt)
//  {
//    countExpressionEvent(gt, pt, null);
//  }
//
//  //----------------------------------------------------------------------------
//  void countExpressionEvent(Gtype gt, Ptype pt, Thread p)
//  {
//    /*
//    for (int icol = 0; icol < nCols; ++icol)
//    {
//      //if (theCols[icol].subsumesGtype(gt))
//      //{
//      //  theCounters[pid2RowIx[pt.getID()]][icol].increment();
//      //  return;
//      //}
//      //
//      int value = theCols[icol].subsumesGtype(gt);
//      if ( value > 0 )
//      {
//        int c = Integer.parseInt(pt.getID());
//        //theCounters[pid2RowIx[pt.getID()]][icol].add(value);
//        theCounters[pid2RowIx[c]][icol].add(value);
//      }
//      if ( !theAtype.toLowerCase().matches("allele") && value > 0 )
//        return;
//    }
//    */
//    
//    //System.out.println("---- new Gt -- ");
//    int c = Integer.parseInt(pt.getID());
//    if ( theAtype.equals("allele") )
//    {
//      for ( int i = 0; i < 2; i++ )
//      {
//        boolean first = ( (i == 0) ? true : false );
//        for ( int icol = 0 ; icol < nCols; icol++ )
//        {
//          //System.out.println("matching column : " + icol );
//          if ( (theCols[icol].subsumesAtype(gt, first)) == 1)
//          {
//            theCounters[pid2RowIx[c]][icol].add(1);
//            //System.out.println("insert row : "+pid2RowIx[c]+" col : " +icol);
//            break;
//          }
//        }
//      }
//    }
//    else 
//    {
//      for ( int icol = 0 ; icol < nCols; icol++ )
//      {
//        if ( (theCols[icol].subsumesGtype(gt)) == 1)
//        {
//          theCounters[pid2RowIx[c]][icol].add(1);
//          //System.out.println("insert row : " +pid2RowIx[c]+ " col : " + icol);
//          break;
//        }
//      }
//    }
//      //else 
//        //System.out.println(" not insert - row : " +  pid2RowIx[c] + " col : " + icol + " subsume value : " + value);
//  }
//
//  //----------------------------------------------------------------------------
//  public CCAnalysis.Table extractTable()
//  {
//    return new ContingencyTable(thePtypes, theCols, theCounters, pid2RowIx);
//  }
//}
