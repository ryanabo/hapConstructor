//******************************************************************************
// DescentDumper.java
//******************************************************************************
package edu.utah.med.genepi.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.Marriage;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.Pedigree;

//==============================================================================
public class DescentDumper implements PedData.Printer {

  private Indiv.GtSelector gtSel;
  private int index;

  //----------------------------------------------------------------------------
  public String getType() { return "html"; }

  //----------------------------------------------------------------------------
  public void print(PedData pd, Indiv.GtSelector gs,
                    AlleleFormat af, int nloci, 
	            PrintWriter out, int i)
  throws IOException
  {
    index = i;
    gtSel = gs;

    String title = "'" + pd.getID() + "' descent-trees showing " + gtSel
                 + " genotypes";

    out.println("<html><head><title>" + title + "</title></head>");
    out.println("<body><h1>" + title + "</h1>");

    Pedigree[] peds = pd.getPedigrees();
    for (int iped = 0, npeds = peds.length; iped < npeds; ++iped)
    {
      Pedigree ped = peds[iped];

      out.println("<p><strong>Ped" + ped.getID() + "</strong></p>");

      if (ped.getMemberCount() == 1)
        printIndividual((Indiv) ped.getMembers().iterator().next(), out);
      else
      {
        Marriage[] marrs = ped.getFoundingMarriages();
        for (int imarr = 0, nm = marrs.length; imarr < nm; ++imarr)
        {
          // start recursive descend
          out.println("<ul>");
          printDescendantTree(marrs[imarr], marrs[imarr].getHim(), out);
          out.println("</ul>");
        }
      }
    }

    out.println("</body></html>");
  }
  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci, 
                    PrintWriter out)
  throws IOException
  {
    print(pd, gs, af, nloci, out, 0);
  }

  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci, 
                    PrintWriter out, int index, boolean hasheader)
  throws IOException
  {
    print (pd, gs, af, nloci, out, index );
  }

  //----------------------------------------------------------------------------
  private void printDescendantTree(Marriage m, Indiv ind, PrintWriter out)
  {
    out.print("<li>");
    printIndividual(ind, out);

    if (m == null)
      return;

    out.print("<br/>");
    printIndividual(ind == m.getHer() ? m.getHim() : m.getHer(), out);

    out.println("<ul>");
    for (Iterator kidit = m.getKids().iterator(); kidit.hasNext(); )
    {
      Indiv kid = (Indiv) kidit.next();
      List  marrs = kid.getMarriages();

      if (marrs != null)
        for (Iterator marrit = marrs.iterator(); marrit.hasNext(); )
          printDescendantTree((Marriage) marrit.next(), kid, out); // recurse
      else
        printDescendantTree(null, kid, out);                       // recurse
    }
    out.println("</ul>");
  }

  //----------------------------------------------------------------------------
  private void printIndividual(Indiv ind, PrintWriter out)
  {
    Gtype gt = ind.getGtype(gtSel, index);
    out.println(
      "Ind" + ind.getID()
      + " G=" + (gt != null ? gt.toString() : "Unknown")
      + " P=" + ind.getPtype()
    );
  }
}

