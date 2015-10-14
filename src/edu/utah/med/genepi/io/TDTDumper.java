//******************************************************************************
// TDTDumper.java
//******************************************************************************
package edu.utah.med.genepi.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.Qdata;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.Pedigree;

//==============================================================================
public class TDTDumper implements PedData.Printer {

  private Indiv.GtSelector gtSel;
  private int numloci;

  //----------------------------------------------------------------------------
  public String getType() { return "ped"; }

  //----------------------------------------------------------------------------
  public void print( PedData pd,
                     Indiv.GtSelector gs,
                     AlleleFormat af,
                     int nloci,
                     PrintWriter out )
  throws IOException
  {
    print (pd, gs, af, nloci, out, 0);
  }

  public void print( PedData pd,
                     Indiv.GtSelector gs,
                     AlleleFormat af,
                     int nloci,
                     PrintWriter out, 
                     int idump,
                     boolean hasHeader )
  throws IOException
  {
    print (pd, gs, af, nloci, out, idump);
  }

  //----------------------------------------------------------------------------
  public void print( PedData pd,
                     Indiv.GtSelector gs,
                     AlleleFormat af,
                     int nloci,
                     PrintWriter out,
                     int idump )
  throws IOException
  {
  // need to add AlleleFormat 
    numloci = nloci;
    Pedigree[] peds = pd.getPedigrees();

    for (int iped = 0; iped < peds.length; ++iped)
    {
      Pedigree ped = peds[iped];
      for (Iterator indit = ped.getMembers().iterator(); indit.hasNext();)
      {
        Indiv ind = (Indiv) indit.next();
        Gtype gt = ind.getGtype(gs, idump);
        Qdata qd = ind.quant_val;
        String father = ((ind.getParent_Indiv('1') == null )  ? "0" : ind.getParent_Indiv('1').getID() );
        String mother = ((ind.getParent_Indiv('2') == null )  ? "0" : ind.getParent_Indiv('2').getID() );
        String gt0;
        if ( gt == null )
        {
          gt0 = "0/0";
          for ( int i = 1; i < numloci; i++ )
            gt0 = gt0 + " " + "0/0";
        }
        else gt0 = gt.toString();

        out.println(
          ped.getPed_id() + " " + ind.getID() + " " + 
	  father + " " +  mother + " " + 
          ind.getSex_id() + " " + ind.getPtype().getID()
          + " " + gt0 + " " + qd.toString()
        );
      }
    }
  }
}

