//******************************************************************************
// PedData.java
//******************************************************************************
package edu.utah.med.genepi.ped;

import java.io.IOException;
import java.io.PrintWriter;

import edu.utah.med.genepi.gm.AlleleFormat;
import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.Qdata;
import edu.utah.med.genepi.util.GEException;

//==============================================================================
public interface PedData {

  public String getID();
  public Pedigree[] getPedigrees();
  public Indiv[] getIndividuals(PedQuery.Predicate p);
  public Indiv[] getIndividuals(PedQuery.Predicate p, int percent);
  public Indiv[] getIndividuals(PedQuery.Predicate p, boolean gtPhased );
  public Indiv[] getIndividuals(PedQuery.Predicate p, boolean gtPhased,
                                int[] loci );
  public Indiv[] getIndividuals(PedQuery.Predicate p, int percent, int[] loci);
  public Indiv[] getAll();

  public interface Loader {
    //public void loadIndividual(int ped_id, int ind_id, int dad_id, int mom_id,
    //                           int sex_id, int ptype_id, Gtype gt, Qdata qt);
    public void loadIndividual(String ped_id, String ind_id, 
                               String dad_id, String mom_id,
                               char sex_id, String ptype_id,
                               String liab_id, Gtype gt,
                               Qdata qt, int numSimData);
  }

  public interface Printer {
    public String getType();
    public void print(PedData pd, Indiv.GtSelector gs, 
                      AlleleFormat af, int nloci,
                      PrintWriter out) 
           throws IOException, GEException;
    public void print(PedData pd, Indiv.GtSelector gs, 
                      AlleleFormat af, int nloci, 
                      PrintWriter out, int index) 
           throws IOException, GEException;
    public void print(PedData pd, Indiv.GtSelector gs, 
                      AlleleFormat af, int nloci, 
                      PrintWriter out, int index,
                      boolean header) 
           throws IOException, GEException;
  }
}

