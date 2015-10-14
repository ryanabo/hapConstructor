//******************************************************************************
// XContingencyTallier.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.Ptype;

//==============================================================================
class XContingencyTallier extends ContingencyTallier 
{
  //----------------------------------------------------------------------------
  XContingencyTallier( Ptype[] ptypes, 
		      CCAnalysis.Table.Column[] cols,
		      String atype )
  {
    super ( ptypes, cols, atype);
  }

  //----------------------------------------------------------------------------
  void countExpressionEvent(Gtype gt, Ptype pt, char sex_id)
  {
    countExpressionEvent(gt, pt, sex_id, null);
  }

  //----------------------------------------------------------------------------
  void countExpressionEvent(Gtype gt, Ptype pt, char sex_id, Thread p)
  {
    int c = Integer.parseInt(pt.getID());
    int nAllele = 1;
    if ( sex_id == '2' || sex_id == 'f')
      nAllele = 2;
    
    for ( int i = 0; i < nAllele; i++ )
    {
      boolean first = ( (i == 0) ? true : false );
      for ( int icol = 0 ; icol < nCols; icol++ )
      {
        //System.out.println("matching column : " + icol );
        if ( (theCols[icol].subsumesAtype(gt, first)) == 1)
        {
          theCounters[pid2RowIx[c]][icol].add(1);
          //System.out.println("Sex : " + sex_id + " pheno : " + pt.toString() + " gtype : " + gt.toString() + " insert row : "+pid2RowIx[c]+" col : " +icol);
          break;
        }
      }
    }
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table extractTable()
  {
    return new ContingencyTable(thePtypes, theCols, theCounters, pid2RowIx);
  }
}
