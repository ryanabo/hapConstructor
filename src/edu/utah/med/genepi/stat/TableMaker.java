//******************************************************************************
// TableMaker.java
//******************************************************************************
package edu.utah.med.genepi.stat;

import java.util.ArrayList;
import java.util.List;

import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.Ptype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Marriage;
import edu.utah.med.genepi.util.Counter;

//==============================================================================
public class TableMaker
{
  protected Indiv[] caseInds, controlInds, unknownInds;
  protected Indiv.GtSelector gtSelector;
  private CCAnalysis.Table ct, pt, qt, tt, st, bt, wt, xt;
  private CCAnalysis.Table.Column[] col_defs;
  private int[] quant_ids;
  private String theAType;
  private String tabletype;
  private int simIndex;
  private ContingencyTallier ctallier;

  public TableMaker ( Indiv[] indcase,
                      Indiv[] indcontrol,
                      Indiv[] indunknown,
                      Indiv.GtSelector gs,
                      int index,
                      CCAnalysis.Table.Column[] cols,
                      int[] quants,
		      String atype,
                      String inTabletype ) 
  {
     caseInds = indcase;
     controlInds = indcontrol;
     unknownInds = indunknown;
     gtSelector = gs;
     simIndex = index;
     ct = null;
     pt = null;
     qt = null;
     tt = null;
     st = null;
     bt = null;
     xt = null;
     col_defs = cols;
     quant_ids = quants;
     theAType = atype;
     tabletype = inTabletype;
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table getContingencyTable()
  {
    return getContingencyTable(null);
  }

  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getContingencyTable(Thread p)
  {
    if ( tabletype.equals("psuedo") )
    {
      return getPsuedoContingencyTable(p);
    }
    else if ( tabletype.equals("x-chr") )
    {
      return getXContingencyTable(p);
    }
    else
    {
      if ( ct == null )
      {
        ctallier = new ContingencyTallier 
                  ( new Ptype[] { Ptype.CASE, Ptype.CONTROL }, 
                    col_defs,
                    theAType ); 

        ctallier.resetTallies(0);
        for ( int i = 0; i < caseInds.length; i++ )
          ctallier.countExpressionEvent
             ( caseInds[i].getGtype(gtSelector, simIndex), Ptype.CASE, p);
        for ( int i = 0; i < controlInds.length; i++ )
          ctallier.countExpressionEvent
             ( controlInds[i].getGtype(gtSelector, simIndex), Ptype.CONTROL, p);

        ct = ctallier.extractTable();
      }
      return ct;
    }
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table getPsuedoContingencyTable()
  {
    return getPsuedoContingencyTable(null);
  }

  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getPsuedoContingencyTable(Thread p)
  {
    if ( pt == null )
    {
      ContingencyTallier ptallier = new ContingencyTallier (
                                      new Ptype[] { Ptype.CASE, Ptype.CONTROL },
                                      col_defs,
                                      theAType );
      ptallier.resetTallies(0);
  
      // count CASE  and psuedo Control
      int nCase = caseInds.length;
      //System.out.println("nCase : " + nCase);
      for ( int i = 0 ; i < nCase; i++ )
      {
        //System.out.print("case is " + caseInds[i].getID());
        Gtype caseGtype = caseInds[i].getGtype(gtSelector, simIndex);
        //System.out.print(" gtype : " + caseGtype.toString());
        ptallier.countExpressionEvent(caseGtype, Ptype.CASE, p);
        Gtype psuedoHaplotype = caseGtype.getPsuedoHaplotype(caseInds[i], 
                                                             Ptype.CASE,
                                                             gtSelector,
                                                             simIndex,
                                                             p);
        if ( psuedoHaplotype != null ) {
          ptallier.countExpressionEvent(psuedoHaplotype, Ptype.CONTROL, p);
	}
        //System.out.println("");
      } // end count CASE     
      
      // count CONTROL
      int nControl = controlInds.length;
      for ( int i = 0; i < nControl; i++ )
      {
        Gtype controlGtype = controlInds[i].getGtype(gtSelector, simIndex);
        if ( controlInds[i].possible_explcontrol )
          ptallier.countExpressionEvent(controlGtype, Ptype.CONTROL, p);
      } // end count CONTROL
      pt = ptallier.extractTable();
    } // end if pt is null 
    return pt;
  } // end getPsuedoContingencyTable

  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getXContingencyTable()
  {
    return getXContingencyTable(null);
  }

  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getXContingencyTable(Thread p)
  {
    if ( xt == null )
    {
      XContingencyTallier xtallier = new XContingencyTallier
                  ( new Ptype[] { Ptype.CASE, Ptype.CONTROL },
                    col_defs,
                    theAType );

      xtallier.resetTallies(0);
      for ( int i = 0; i < caseInds.length; i++ )
      {
        //System.out.println("   Indiv : " + caseInds[i].getID() );
        xtallier.countExpressionEvent
             ( caseInds[i].getGtype(gtSelector, simIndex), Ptype.CASE, 
               caseInds[i].getSex_id(), p);
      }
      for ( int i = 0; i < controlInds.length; i++ )
      {
        xtallier.countExpressionEvent
             ( controlInds[i].getGtype(gtSelector, simIndex), Ptype.CONTROL,
               controlInds[i].getSex_id(),  p);
      }
      xt = xtallier.extractTable();
    }
    return xt;
  }
  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getWeightedIndexTable()
  {
    return getWeightedIndexTable(null);
  }

  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getWeightedIndexTable(Thread p)
  {
    if ( wt == null )
    {
      WeightedIndexTallier wtallier = new WeightedIndexTallier
                ( new Ptype[] { Ptype.CASE, Ptype.CONTROL },
                  col_defs,
                  theAType );

      wtallier.resetTallies(0);
      for ( int i = 0; i < caseInds.length; i++ )
        wtallier.sumExpressionEvent
           ( caseInds[i].getGtype(gtSelector, simIndex),
             Ptype.CASE,
             caseInds[i].getWeightedIndex(),
             p);
      for ( int i = 0; i < controlInds.length; i++ )
        wtallier.sumExpressionEvent
           ( controlInds[i].getGtype(gtSelector, simIndex),
             Ptype.CONTROL,
             controlInds[i].getWeightedIndex(),
             p);

      wt = wtallier.extractTable();
    }
    return wt;

  }

  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getQuantitativeTable()
  {
    return getQuantitativeTable(null);
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table getQuantitativeTable( Thread p )
  {
    if ( qt == null )
    {
      QuantitativeTallier qtallier = 
                      new QuantitativeTallier ( quant_ids, col_defs, theAType );
      qtallier.resetTallies(0.0);
      
      for ( int i = 0; i < caseInds.length; i++ )
      {
        for ( int j = 0; j < quant_ids.length; j++ )
        {
          qtallier.sumExpressionEvent 
                  ( caseInds[i].getGtype(gtSelector, simIndex), 
                    caseInds[i].getPedigree(),
                    j,
                    (caseInds[i].quant_val).getQdata(j), p );
        }
      }

      for ( int i = 0; i < controlInds.length; i++ )
      {
        for ( int j = 0; j < quant_ids.length; j++ )
          qtallier.sumExpressionEvent 
                  ( controlInds[i].getGtype(gtSelector, simIndex), 
                    controlInds[i].getPedigree(),
                    j,
                    (controlInds[i].quant_val).getQdata(j), p );
      }

      for ( int i = 0; i < unknownInds.length; i++ )
      {  
        for ( int j = 0; j < quant_ids.length; j++ )
          qtallier.sumExpressionEvent
                  ( unknownInds[i].getGtype(gtSelector, simIndex),
                    unknownInds[i].getPedigree(),
                    j,
                    (unknownInds[i].quant_val).getQdata(j), p );
      }  

      qt = qtallier.extractTable();
    }
    return qt;
  }

  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getQuantWtTable()
  {
    return getQuantWtTable(null);
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table getQuantWtTable( Thread p )
  {
    if ( qt == null )
    {
      QuantitativeTallier qtallier =
                      new QuantitativeTallier ( quant_ids, col_defs, theAType );
      qtallier.resetTallies(0.0);

      for ( int i = 0; i < caseInds.length; i++ )
      {
        double wt = caseInds[1].getWeightedIndex();
        for ( int j = 0; j < quant_ids.length; j++ )
        {
          qtallier.sumExpressionEvent
                  ( caseInds[i].getGtype(gtSelector, simIndex),
                    caseInds[i].getPedigree(),
                    j,
                    wt * (caseInds[i].quant_val).getQdata(j),
                    p );
        }
      }
      for ( int i = 0; i < controlInds.length; i++ )
      {
        double wt = controlInds[1].getWeightedIndex();
        for ( int j = 0; j < quant_ids.length; j++ )
          qtallier.sumExpressionEvent
                  ( controlInds[i].getGtype(gtSelector, simIndex),
                    controlInds[i].getPedigree(),
                    j,
                    wt * (controlInds[i].quant_val).getQdata(j),
                    p );
      }

      for ( int i = 0; i < unknownInds.length; i++ )
      {
        double wt = unknownInds[1].getWeightedIndex();
        for ( int j = 0; j < quant_ids.length; j++ )
          qtallier.sumExpressionEvent
                  ( unknownInds[i].getGtype(gtSelector, simIndex),
                    unknownInds[i].getPedigree(),
                    j,
                    wt * (unknownInds[i].quant_val).getQdata(j),
                    p );
      }

      qt = qtallier.extractTable();
    }
    return qt;
  }

  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getTrioTDTTable()
  {
    return getTrioTDTTable(null);
  }

  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getTrioTDTTable( Thread p )
  {
    if ( col_defs.length == 3 )
    { 
      if ( tt == null )
        tt = trioTDTImp(caseInds, p);
    }
    return tt;
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table trioTDTImp( Indiv[] inds, Thread p )
  {
    TrioTDTTallier trioTDTTallier = new TrioTDTTallier( col_defs );
    trioTDTTallier.resetTallies(0);

    for ( int i = 0; i < inds.length; i++ )
    { 
      if ( !inds[i].getIsFounder() )
      {
        Indiv mother = inds[i].getParent_Indiv('2');
        Indiv father = inds[i].getParent_Indiv('1'); 

        trioTDTTallier.countExpressionEvent
                ( inds[i].getGtype(gtSelector, simIndex), 
                  father.getGtype(gtSelector, simIndex),
                  mother.getGtype(gtSelector, simIndex), p );
      }
    }
    return trioTDTTallier.extractTable();
  }
 
  //----------------------------------------------------------------------------
  public CCAnalysis.Table getSibTDTTable()
  {
    return getSibTDTTable(null);
  }

  //----------------------------------------------------------------------------
  public synchronized CCAnalysis.Table getSibTDTTable( Thread p )
  {
    if ( col_defs.length > 2 )
    {
      if ( st == null )
        st = sibTDTImp(caseInds, p);
    }
    return st;
  }
    
  //----------------------------------------------------------------------------
  public CCAnalysis.Table sibTDTImp( Indiv[] inds, Thread p )
  {
    SibTDTTallier stallier = new SibTDTTallier 
      ( col_defs, new String[] {"Mean", "Variance", "#M frm Aff"} );
    stallier.resetTallies(0.0);

    List<Marriage> marriageList = new ArrayList<Marriage>();
    
    for ( int i = 0; i < inds.length; i++ )
    {
      if ( !inds[i].getIsFounder() )
      {
        boolean addtolist = true;
        Marriage parentMarr = inds[i].getParentMarr();
        if ( marriageList.size() != 0 )
        {
          for ( int m = 0; m < marriageList.size(); m++ )
          {
            if ( marriageList.get(m) == parentMarr )
              addtolist = false;
          }
        }
        if ( addtolist )
        {
          marriageList.add(parentMarr);
        //}
      //}
    //}
   
    //for ( int m = 0; m < marriageList.size(); m++ )
    //{
    //  Marriage parentMarr = (Marriage) marriageList.get(m);
    // Change from retrieve from list to access it direct
          Indiv[] kids = (Indiv[]) parentMarr.getKids().toArray(new Indiv[0]);
      
          ContingencyTallier ctallier = new ContingencyTallier
              ( new Ptype[] { Ptype.CASE, Ptype.CONTROL }, col_defs, theAType);
          ctallier.resetTallies(0);

          for ( int k = 0; k < kids.length; k++ )
          {
            ctallier.countExpressionEvent
            ( kids[k].getGtype(gtSelector, simIndex), kids[k].getPtype(), p); 
          }
         
          Counter[][] scounter = ctallier.getCounters();
          int[] columntotal = new int[col_defs.length];
          int[] rowtotal    = new int[scounter.length];
          int   grandtotal  = 0;

          for ( int j = 0; j < columntotal.length; j++ )
            columntotal[j] = 0;
          for ( int k = 0; k < rowtotal.length; k++ )
            rowtotal[k] = 0;
        
          for ( int k = 0; k < scounter.length; k++ )
          {
            for ( int j = 0; j < scounter[k].length; j++)
            {
              rowtotal[k] += scounter[k][j].current().intValue();
              columntotal[j] += scounter[k][j].current().intValue();
              grandtotal += scounter[k][j].current().intValue();
            }
          }
          // At least one sib is affected and one sib is unaffected
          // members of sibship must not all have the same genotype
          boolean ok = true;
          int colcount = 0;
          for ( int r = 0; r < rowtotal.length; r++ )
            if ( rowtotal[r] == 0 )
              ok = false;
          
          for ( int c = 0; c < columntotal.length; c++ )
            if ( columntotal[c] > 0 )
              colcount++;
   
          if ( ok && colcount > 1 )
          {
            double mean = (double) ( 2 * columntotal[2] + columntotal[1] ) * 
                        rowtotal[0] / (double) grandtotal;
            double variance = (double) (rowtotal[0] * rowtotal[1] * 
                            ( 4 * columntotal[2] *
                            ( grandtotal - columntotal[1] - columntotal[2] ) +
                            columntotal[1] * ( grandtotal - columntotal[1] ))) /
                            (double) (grandtotal*grandtotal*(grandtotal - 1));
            int M = scounter[0][1].current().intValue() + 
                  2 * scounter[0][2].current().intValue();
            stallier.sumExpressionEvent( 1,  variance, p );
            stallier.countExpressionEvent( 2, M, p );
            stallier.sumExpressionEvent( 0, mean, p );
          }
        }
      }
    }
    return stallier.extractTable();
  }
  
  //----------------------------------------------------------------------------
  public CCAnalysis.Table getCombTDTTable()
  {
    return getCombTDTTable(null);
  }

  //----------------------------------------------------------------------------
  public CCAnalysis.Table getCombTDTTable(Thread p)
  {
    if ( col_defs.length == 3 )
    {
      if ( bt == null )
      {
        CombTDTTallier combtallier = new CombTDTTallier
                   ( col_defs, new String[] { "Trio", "Sib" },
                     new String[] { "Mean", "Variance", "#M frm Aff"} );
        combtallier.resetTallies(0.0);
      
        List<Indiv> triolist = new ArrayList<Indiv>();
        List<Indiv> siblist = new ArrayList<Indiv>();
        
        for ( int i = 0; i < caseInds.length; i++ )
        {
          if ( !caseInds[i].getIsFounder() )
          {
            Gtype motherGt=caseInds[i].getParent_Indiv('2').getGtype(gtSelector,
                                                                   simIndex);
            Gtype fatherGt=caseInds[i].getParent_Indiv('1').getGtype(gtSelector,
                                                                   simIndex); 
          
            if ( motherGt != null && fatherGt != null )
              triolist.add(caseInds[i]);
            else 
              siblist.add(caseInds[i]);
          }
        }
        Indiv[] trio = (Indiv[]) triolist.toArray(new Indiv[0]);
        Indiv[] sib  = (Indiv[]) siblist.toArray(new Indiv[0]);
  
        TrioTDTTable trioT = (TrioTDTTable) trioTDTImp(trio, p);
        SibTDTTable  sibT  = (SibTDTTable) sibTDTImp(sib, p);
     
        CCAnalysis.Table.Totals trioTotals = trioT.getTotals();
        CCAnalysis.Table.Totals sibTotals  = sibT.getTotals();
        //Number[] trioC = trioTotals.forColumns();
        Number[] sibC  = sibTotals.forColumns();
        int m20 = trioT.getRowAt(1).getCells()[0].intValue();
        int m21 = trioT.getRowAt(1).getCells()[1].intValue();
        int m40 = trioT.getRowAt(3).getCells()[0].intValue();
        int m41 = trioT.getRowAt(3).getCells()[1].intValue();
        int m42 = trioT.getRowAt(3).getCells()[2].intValue();
        int m51 = trioT.getRowAt(4).getCells()[1].intValue();
        int m52 = trioT.getRowAt(4).getCells()[2].intValue();
        int trioN = m20 + m21 + 2 * ( m40 + m41 + m42) + m51 + m52;
        double trioMean = (double) trioN / 2;
        double trioVar  = (double) trioN / 4;
        int trioX = trioT.getCellb();
        combtallier.sumExpressionEvent(0, 0, trioMean, p);
        combtallier.sumExpressionEvent(0, 1, trioVar, p);
        combtallier.countExpressionEvent(0, 2, trioX, p);
        combtallier.sumExpressionEvent(1, 0,  sibC[0].doubleValue(), p);
        combtallier.sumExpressionEvent(1, 1, sibC[1].doubleValue(), p);
        combtallier.countExpressionEvent(1, 2, sibC[2].intValue(), p);
        bt = combtallier.extractTable();
      }
    }
    return bt;
  }

}
