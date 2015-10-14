//******************************************************************************
// GenoDataDumper.java
//******************************************************************************
package edu.utah.med.genepi.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.Pedigree;

//==============================================================================
public class GenoDataDumper implements PedData.Printer {

  private Indiv.GtSelector gtSel;
  private int numloci;
  private int index;

  //----------------------------------------------------------------------------
  public String getType() { return "genodump"; }

  //----------------------------------------------------------------------------
  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci,
                    PrintWriter out)
  throws IOException
  {
    print(pd, gs, af, nloci, out, 0, false);
  }

  //----------------------------------------------------------------------------
  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci, 
                    PrintWriter out, int iValue)
  throws IOException
  {
    print(pd, gs, af, nloci, out, iValue, false);
  }

  //----------------------------------------------------------------------------
  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci, 
                    PrintWriter out, int iValue, boolean hasHeader)
  throws IOException
  {
    index = iValue;
    numloci = nloci;
    Pedigree[] peds = pd.getPedigrees();

    if (hasHeader)
    {
      out.print("pedigree indiv dad mom sex pheno liab");
      for ( int i = 1; i <= numloci; i++ )
        out.print(" marker" + i);
      out.println();
    }

    for (int iped = 0; iped < peds.length; ++iped)
    {
      Pedigree ped = peds[iped];
      for (Iterator indit = ped.getMembers().iterator(); indit.hasNext();)
      {
        Indiv ind = (Indiv) indit.next();
        Gtype gt = ind.getGtype(gs, index);
        //Qdata qd = ind.quant_val;
        String father = ((ind.getParent_Indiv('1') == null )  ? "0": ind.getParent_Indiv('1').getID() );
        String mother = ((ind.getParent_Indiv('2') == null )  ? "0" : ind.getParent_Indiv('2').getID() );
        String gt0;
        if ( gt == null )
        {
          gt0 = "0 0";
          for ( int i = 1; i < numloci; i++ )
            gt0 = gt0 + " " + "0 0";
        }
        else gt0 = gt.toString().replace("/", " ");

        // included liab 
        out.println(
          ped.getPed_id() + " " + ind.getID() + " " + 
	  father + " " +  mother + " " + 
          ind.getSex_id() + " " + ind.getPtype().getID()
          + " " + ind.getLiab_id() + " " + gt0
        );
      }
    }
  }
}
