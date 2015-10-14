//******************************************************************************
// GDefBuilder.java
//******************************************************************************
package edu.utah.med.genepi.gm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.utah.med.genepi.util.Ut;
//==============================================================================
public class GDefBuilder {

  private final List lGDefLoci = new ArrayList();
  public boolean charCode = false;

  //----------------------------------------------------------------------------
  public void addLocus(int id, String marker, double distance, 
                       boolean ordered, int gene)
  {
    lGDefLoci.add(new LocusImp(id, marker, distance, ordered, gene));
  }

  //----------------------------------------------------------------------------
  public GDef build() 
  {
    return new GDef() {
      String pkg = Ut.pkgOf(AlleleFormat.class);
      public AlleleFormat alleleformat;
      private final List lLoci = Collections.unmodifiableList(
        (List) ((ArrayList) lGDefLoci).clone()
      );
      private final GtypeBuilder gtBuilder = new GtypeBuilder(this);
      public int getLocusCount() { return lLoci.size(); }
      public Locus getLocus(int index) { 
      return (Locus) lLoci.get(index);}
      public GtypeBuilder getGtypeBuilder() { return gtBuilder; }
      public void setAlleleFormat(AlleleFormat af) 
      { alleleformat = af; }
      //byte b = Integer.valueOf(inString).byteValue();
      public AlleleFormat getAlleleFormat()
      { return alleleformat; }
    };
  }
}

