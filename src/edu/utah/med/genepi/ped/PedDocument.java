//******************************************************************************
// PedDocument.java
//******************************************************************************
package edu.utah.med.genepi.ped;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.util.GEException;
//d

//==============================================================================
public class PedDocument implements PedData {

  public interface FileParser {
    public void parse
          ( File fsource, QuantIndiv[] qInd, int[] quant_ids,
            GDef def, int numSimData, PedData.Loader loader,
            boolean hasHeader)
    throws IOException, GEException;
  }

  private final GDef gDef;
  private Pedigree[] thePeds;
  private List<Indiv> lIndivs;
  private String     sourceID;

  //----------------------------------------------------------------------------
  public PedDocument(GDef def)
  {
    gDef = def;
  }

  //----------------------------------------------------------------------------
  public void read(File f, QuantIndiv[] qInd, int[] covar_ids, 
                   int numSimData, FileParser p,
                   boolean header) 
	throws GEException
  {
    DataLoaderImp loader = new DataLoaderImp();

    try { p.parse(f, qInd, covar_ids, gDef, 
                  numSimData, loader, header); }
    catch (Exception e) {
      throw new GEException("can't parse '" + f + "'", e);
    }

    thePeds = (Pedigree[]) ((Set<Pedigree>) loader.buildPedigreeSet()).toArray(new Pedigree[0]);
    lIndivs = new ArrayList<Indiv>();
    for (int i = 0; i < thePeds.length; ++i)
      lIndivs.addAll(thePeds[i].getMembers());
    sourceID = f.getPath();
  }

  /// PedData implementation ///

  //----------------------------------------------------------------------------
  public String getID() { return sourceID; }

  //----------------------------------------------------------------------------
  public Pedigree[] getPedigrees() { return thePeds; }

  //----------------------------------------------------------------------------
  public Indiv[] getIndividuals( PedQuery.Predicate p )
  {
    return (Indiv[]) PedQuery.hits(p, lIndivs).toArray(new Indiv[0]);
  }

  //----------------------------------------------------------------------------
  public Indiv[] getIndividuals( PedQuery.Predicate p, int percent)
  {
    return (Indiv[]) 
           PedQuery.hits(p, percent, lIndivs).toArray(new Indiv[0]);
  }

  //----------------------------------------------------------------------------
  public Indiv[] getIndividuals(PedQuery.Predicate p,
                                boolean gtPhased)
  {
    return (Indiv[])
           PedQuery.hits(p, gtPhased, lIndivs).toArray(new Indiv[0]);
  }

  //----------------------------------------------------------------------------
  public Indiv[] getIndividuals(PedQuery.Predicate p,
                                boolean gtPhased,
                                int[] loci )
  {
    return (Indiv[])
           PedQuery.hits(p, gtPhased, loci, lIndivs).toArray(new Indiv[0]);
  }

  //----------------------------------------------------------------------------
  public Indiv[] getIndividuals( PedQuery.Predicate p,
                                 int percent,
                                 int[] loci)
  {
    return (Indiv[]) 
           PedQuery.hits(p, percent, loci, lIndivs).toArray(new Indiv[0]);
  }
 
  public Indiv[] getAll()
  {
    return (Indiv[]) lIndivs.toArray(new Indiv[0]);
  }
}

