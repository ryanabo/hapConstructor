package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.genie.Phenotype;
import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable;

public class ConstructorTallier extends TallierImp 
{
	protected static final int NPTYPES = Ptype.instanceCount();
	protected Ptype[] pTypes = null;
	protected ColumnManager columnManager = null;
	protected final int[] pid2RowIx = new int[NPTYPES];
	protected Phenotype[] phenotypes = null;
	
	public ConstructorTallier( Ptype[] ptypes, ColumnManager cm, Phenotype[] phens )
	{
		super(ptypes.length,cm.getNCols());
		pTypes = ptypes;
		columnManager = cm;
		theCounters = createCounters();
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
				theCounters[pid2RowIx[phenValue]][incColIndices[i]].add(1);
			}

			String type = columnManager.getType();
		
			if ( type.equals("allele") && incColIndices.length == 2 ) foundColumn = true;
			else if ( type.equals("genotype") && incColIndices.length == 1 ) foundColumn = false;
		}
		return foundColumn;
	}
	
	//----------------------------------------------------------------------------
//	void countExpressionEvent(Gtype gt, Ptype pt, Thread p)
//	{
//		int c = Integer.parseInt(pt.getID());
//		if ( theAtype.equals("allele") )
//		{
//			for ( int i = 0; i < 2; i++ )
//			{
//				boolean first = ( (i == 0) ? true : false );
//				for ( int icol = 0 ; icol < nCols; icol++ )
//				{
//					if ( (theCols[icol].subsumesAtype(gt, first)) == 1)
//					{
//						theCounters[pid2RowIx[c]][icol].add(1);
//						break;
//					}
//				}
//			}
//		}
//		else 
//		{
//			for ( int icol = 0 ; icol < nCols; icol++ )
//			{
//				if ( (theCols[icol].subsumesGtype(gt)) == 1)
//				{
//					theCounters[pid2RowIx[c]][icol].add(1);
//					break;
//				}
//			}
//		}
//	}
	
	//----------------------------------------------------------------------------
	public GenotypeDataQuery[] getDataQuery(){ return columnManager.getDataQuery(); }

	//----------------------------------------------------------------------------
	public AnalysisTable extractTable()
	{
		return new ContingencyTable(pTypes,theCols,theCounters,pid2RowIx);
	}
}
