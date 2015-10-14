//******************************************************************************
// TrioTDTTallier.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import edu.utah.med.genepi.gm.Gtype;

//==============================================================================
class TrioTDTTallier extends TallierImp
{
  private final CCAnalysis.Table.Column[] 	theCols;
  private int 					triocount = 0;

  //----------------------------------------------------------------------------
  TrioTDTTallier (CCAnalysis.Table.Column[] cols)
  {
    super(6, cols.length);
    theCols = cols;
    nRows = 6;
    nCols = theCols.length;
    theCounters = createCounters();
  }

  //----------------------------------------------------------------------------
  void countExpressionEvent ( Gtype caseGtype,
                              Gtype motherGtype,
                              Gtype fatherGtype )
  {
    countExpressionEvent ( caseGtype, motherGtype, fatherGtype, null);
  }

  //----------------------------------------------------------------------------
  void countExpressionEvent ( Gtype caseGtype,
                              Gtype motherGtype,
                              Gtype fatherGtype,
                              Thread p )
  {
    // This test is designed for genotype testing only, not allele
    triocount ++;
    for (int icol = 0; icol < nCols; ++icol)
    {
      //if (theCols[icol].subsumesGtype(caseGtype))
      if (theCols[icol].subsumesGtype(caseGtype) > 0 )
      {
        Gtype testGtype = null;

        for ( int jcol = 0; jcol < nCols; jcol++ )
        {
          if ( motherGtype != null && fatherGtype != null )
          {
            if ( jcol == 0 )
            {
              //if ( theCols[0].subsumesGtype(motherGtype) ) 
              if (theCols[0].subsumesGtype(motherGtype) > 0 ) 
                testGtype = fatherGtype;
              else if ( theCols[0].subsumesGtype(fatherGtype) > 0 )
                testGtype = motherGtype;

              for ( int kcol = 0; kcol < nCols; ++kcol )
              {
                if ( theCols[kcol].subsumesGtype(testGtype) > 0 )
                {
                  theCounters[kcol][icol].increment();
                  return;
                }
              }
            }
        
            else
            {
              if ( theCols[jcol].subsumesGtype(motherGtype) > 0)
              {
                for ( int kcol = 1; kcol < nCols; kcol++ )
                {  
                  if ( theCols[kcol].subsumesGtype(fatherGtype) > 0)
                  {
                    theCounters[jcol + kcol + 1][icol].increment();
                    return;
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  //----------------------------------------------------------------------------
  public int getTrioCount()
  { return triocount; }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table extractTable()
  {
    return new TrioTDTTable(theCols, theCounters, triocount);
  }

}
