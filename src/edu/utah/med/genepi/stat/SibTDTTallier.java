//******************************************************************************
// SibTDTTallier.java
//******************************************************************************
package edu.utah.med.genepi.stat;
 

//==============================================================================
class SibTDTTallier extends TallierImp
{
  private String[] columntitle;
  CCAnalysis.Table.Column[] theCols;

  //----------------------------------------------------------------------------
  SibTDTTallier ( CCAnalysis.Table.Column[] cols, String[] colnames )
  {
    super(1, colnames.length);
    nRows = 1;
    nCols = colnames.length;
    theCols = cols;
    theCounters = createCounters();
    columntitle = colnames;
  }

  //----------------------------------------------------------------------------
  public void countExpressionEvent( int col, int value, Thread p)
  {
    for ( int i = 0; i < nCols; i++ )
    {
      if ( i == col )
        theCounters[0][i].add(value);
    }
  } 

  //----------------------------------------------------------------------------
  public void sumExpressionEvent( int col, double value, Thread p)
  {
    for ( int i = 0; i < nCols; i++ )
    {
      if ( i == col )
        theCounters[0][i].sum(value);
    }
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table extractTable()
  {
    return new SibTDTTable(theCols, theCounters );
  }

}
