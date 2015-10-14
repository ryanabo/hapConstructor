//******************************************************************************
// IndivDumper.java
//******************************************************************************
package edu.utah.med.genepi.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Iterator;

import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.Pedigree;

//==============================================================================
public class IndivDumper extends GenoDataDumper
{
  private Indiv.GtSelector gtSel;
  //private int numloci;
  //private int index;

  //----------------------------------------------------------------------------
  public IndivDumper ()
  {
    super();
  }

  //----------------------------------------------------------------------------
  public String getType() { return "indivdump"; }

  //----------------------------------------------------------------------------
  //public void print(PedData pd, Indiv.GtSelector gs, 
  //                  AlleleFormat af, int nloci,
  //                  PrintWriter out)
  //throws IOException
  //{
  //  print(pd, gs, af, nloci, out, 0);
  //}

  //----------------------------------------------------------------------------
  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci, 
                    PrintWriter out, int iValue)
  throws IOException
  {
    print( pd, gs, af, nloci, out, iValue, false );
  }

  //----------------------------------------------------------------------------
  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci, 
                    PrintWriter out, int iValue, boolean hasHeader)
  {
    Pedigree[] peds = pd.getPedigrees();
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(6);

    if (hasHeader)
      out.println("pedigree indiv dad mom sex pheno weighted");

    for (int iped = 0; iped < peds.length; ++iped)
    {
      Pedigree ped = peds[iped];
      for (Iterator indit = ped.getMembers().iterator(); indit.hasNext();)
      {
        Indiv ind = (Indiv) indit.next();
        String father = ((ind.getParent_Indiv('1') == null )  ? "0": ind.getParent_Indiv('1').getID() );
        String mother = ((ind.getParent_Indiv('2') == null )  ? "0" : ind.getParent_Indiv('2').getID() );

        //  removed liab and added weighted index 1/29/08
        out.println(
          ped.getPed_id() + " " + ind.getID() + " " + 
	  father + " " +  mother + " " + 
          ind.getSex_id() + " " + ind.getPtype().getID()
          + " " + nf.format(ind.getWeightedIndex()) 
        );
      }
    }
  }
}
