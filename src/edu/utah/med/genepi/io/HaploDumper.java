//******************************************************************************
// HaploDumper.java
//******************************************************************************
package edu.utah.med.genepi.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Iterator;

import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.AllelePair;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.ped.Indiv;
import edu.utah.med.genepi.ped.PedData;
import edu.utah.med.genepi.ped.Pedigree;
import edu.utah.med.genepi.util.GEException;

//==============================================================================
public class HaploDumper implements PedData.Printer {

  private Indiv.GtSelector gtSel;
  private int numloci;

  //----------------------------------------------------------------------------
  public String getType() { return "haplodump"; }

  //----------------------------------------------------------------------------
  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci, 
                    PrintWriter out, int index)
  throws IOException, GEException
  {
    numloci = nloci;
    Pedigree[] peds = pd.getPedigrees();

    for (int iped = 0; iped < peds.length; ++iped)
    {
      Pedigree ped = peds[iped];
      for (Iterator indit = ped.getMembers().iterator(); indit.hasNext();)
      {
        Indiv ind = (Indiv) indit.next();
        //System.out.print("ind : " + ind.getID() + "  ");
        Indiv.GtSelector obs = Indiv.GtSelector.OBS;
        Gtype gt = ind.getGtype(gs, index);
        //Qdata qd = ind.quant_val;
        String father = ((ind.getParent_Indiv('1') == null )  ? "0": ind.getParent_Indiv('1').getID() );
        String mother = ((ind.getParent_Indiv('2') == null )  ? "0" : ind.getParent_Indiv('2').getID() );
        String g1, g2;
        String h1 =  new String();
        String h2 =  new String();
        String filler, freq;
        //System.out.println(" num loci : " + numloci);
        for ( int i = 0; i < numloci; i++ )
        {
          filler = new String("-");
          g1 = "0";
          g2 = "0";
          if ( gt != null )
          {
            AllelePair ap = gt.getAllelePairAt(i);
            if ( ap != null )
            {
              //System.out.print("gt : " + gt.getAllelePairAt(i).toString());
              g1 = af.toString(ap.getAlleleCode(true));
              g2 = af.toString(ap.getAlleleCode(false));
              //System.out.println(" a1 : " + g1 + " a2 : " + g2);
            }
          }
          if ( i == numloci - 1 )
            filler = new String();

          h1 += g1 + filler;
          h2 += g2 + filler;

        }

        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(6);
 
        if ( gt == null )
          freq = "-";
        else 
          freq = String.valueOf(ind.getGtype(obs).getHaploFrequency());
        
        out.println(
          ped.getPed_id() + " " + ind.getID() + " " + 
          h1 + " / " + h2 + " " + freq );
      }
    }
  }
  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci,
                    PrintWriter out)
  throws IOException, GEException
  {
    print(pd, gs, af, nloci, out, 0);
  }

  public void print(PedData pd, Indiv.GtSelector gs, 
                    AlleleFormat af, int nloci,
                    PrintWriter out, int index,
                    boolean hasHeader)
  throws IOException, GEException
  {
    print(pd, gs, af, nloci, out, index);
  }
}

