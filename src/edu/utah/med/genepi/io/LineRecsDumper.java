//******************************************************************************
// LineRecsDumper.java
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
import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class LineRecsDumper implements PedData.Printer {

  private Indiv.GtSelector gtSel;

  //----------------------------------------------------------------------------
  public String getType() { return "rgend"; }

  //----------------------------------------------------------------------------
  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci,
		    PrintWriter out, int index)
  throws IOException, GEException
  {
    Pedigree[] peds = pd.getPedigrees();
    for (int iped = 0, npeds = peds.length; iped < npeds; ++iped)
    {
      Pedigree ped = peds[iped];
      for (Iterator indit = ped.getMembers().iterator(); indit.hasNext();)
      {
        Indiv ind = (Indiv) indit.next();
        Gtype gt = ind.getGtype(gs, index);
        out.println(
          ped + " " + ind + " " + ind.getPtype().getID()
          + " " + (gt != null ? gt.toString() : "")
        );
      }
    }
  }

  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci, 
                    PrintWriter out ) 
  throws IOException, GEException
  {
    print(pd, gs, af, nloci, out, 0);
  }

  public void print(PedData pd, Indiv.GtSelector gs,
                    AlleleFormat af, int nloci,
                    PrintWriter out, int index,
                    boolean hasHeader )
  throws IOException, GEException
  {
    print(pd, gs, af, nloci, out, index);
  }

}

