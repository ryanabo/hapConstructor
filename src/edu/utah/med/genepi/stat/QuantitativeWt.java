//*****************************************************************************
// Quantitative.java
//*****************************************************************************
package edu.utah.med.genepi.stat;

//=============================================================================
public class QuantitativeWt extends Quantitative 
{

  //---------------------------------------------------------------------------
  public QuantitativeWt()
  { 
    super();
    title = "Quantitative with Weighted Index, Two Tailed Test" ;
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable ( TableMaker tm )
  {
    return tm.getQuantWtTable();
  }

  //---------------------------------------------------------------------------
  public CCAnalysis.Table getTable ( TableMaker tm, Thread p )
  {
    return tm.getQuantWtTable(p);
  }

}
