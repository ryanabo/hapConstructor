//******************************************************************************
// CombTDTTallier.java
//******************************************************************************
package edu.utah.med.genepi.stat;
 

//==============================================================================
class CombTDTTallier extends TallierImp
{
  String[] rowtitle, columntitle;
  CCAnalysis.Table.Column[] theCols;
  
  //----------------------------------------------------------------------------
  CombTDTTallier ( CCAnalysis.Table.Column[] cols,
                   String[] rownames, String[] columnnames )
  {
    super(rownames.length, cols.length);
    nRows = rownames.length;
    nCols = columnnames.length;
    theCols = cols;
    rowtitle = rownames;
    columntitle = columnnames;
    theCounters = createCounters();
  }

  //----------------------------------------------------------------------------
  public void countExpressionEvent(int row, int column, int value)
  {
    countExpressionEvent(row, column, value, null);
  }

  //----------------------------------------------------------------------------
  public void countExpressionEvent(int row, int column, int value, Thread p)
  {
    for ( int r = 0; r < rowtitle.length; r++ )
    { 
      if ( r == row )
      {
        for ( int c = 0; c < columntitle.length; c++ )
        {
          if ( c == column )
              theCounters[r][c].add(value);
        }
      }
    } 
  } 

  //----------------------------------------------------------------------------
  public void sumExpressionEvent(int row, int column, double value)
  {
    sumExpressionEvent(row, column, value, null);
  }

  //----------------------------------------------------------------------------
  public void sumExpressionEvent(int row, int column, double value, Thread p)
  {
    for ( int r = 0; r < rowtitle.length; r++ )
    {
      if ( r == row )
      {
        for ( int c = 0; c < columntitle.length; c++ )
        {
          if ( c == column )
              theCounters[r][c].sum(value);
        }
      }
    }
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table extractTable()
  {
    return new CombTDTTable( theCols, rowtitle, columntitle, theCounters );
  }

}
