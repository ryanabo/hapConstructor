//******************************************************************************
// WeightedIndexTallier.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.Ptype;

//==============================================================================
class WeightedIndexTallier extends TallierImp
{
  protected static final int NPTYPES = Ptype.instanceCount();

  protected final Ptype[]              		thePtypes;
  protected final CCAnalysis.Table.Column[]	theCols;
  protected final String			theAtype;
  protected final int[]				pid2RowIx = new int[NPTYPES];
  //protected final Map				mGt2ColIx = new HashMap();

  //----------------------------------------------------------------------------
  WeightedIndexTallier( Ptype[] ptypes, 
		      CCAnalysis.Table.Column[] cols,
		      String atype )
  {
    super ( ptypes.length, cols.length);
    thePtypes = ptypes;
    theCols = cols;
    theAtype = atype;
    theCounters = createCounters();

    // establish a mapping from row phenotype ids back to row indices
    for (int irow = 0; irow < nRows; ++irow)
    {
      int r = Integer.parseInt(thePtypes[irow].getID());
      pid2RowIx[r] = irow;
    }
  }

  //----------------------------------------------------------------------------
  void sumExpressionEvent(Gtype gt, Ptype pt, double weight)
  {
    sumExpressionEvent(gt, pt, weight, null);
  }

  //----------------------------------------------------------------------------
  void sumExpressionEvent(Gtype gt, Ptype pt, double weight, Thread p)
  {
    //System.out.println("---- new Gt -- ");
    int c = Integer.parseInt(pt.getID());
    if ( theAtype.equals("allele") )
    {
      for ( int i = 0; i < 2; i++ )
      {
        boolean first = ( (i == 0) ? true : false );
        for ( int icol = 0 ; icol < nCols; icol++ )
        {
          //System.out.println("matching column : " + icol );
          if ( (theCols[icol].subsumesAtype(gt, first)) == 1)
          {
            theCounters[pid2RowIx[c]][icol].sum(weight, 1);
            //System.out.println("insert row : "+pid2RowIx[c]+" col : " +icol);
            break;
          }
        }
      }
    }
    else 
    {
      for ( int icol = 0 ; icol < nCols; icol++ )
      {
        if ( (theCols[icol].subsumesGtype(gt)) == 1)
        {
          theCounters[pid2RowIx[c]][icol].sum(weight, 1);
          //System.out.println("insert row : " +pid2RowIx[c]+ " col : " + icol);
          break;
        }
      }
    }
      //else 
        //System.out.println(" not insert - row : " +  pid2RowIx[c] + " col : " + icol + " subsume value : " + value);
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table extractTable()
  {
    return new WeightedIndexTable(thePtypes, theCols, theCounters, pid2RowIx);
  }
}
