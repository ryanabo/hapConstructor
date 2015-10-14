//******************************************************************************
// GSimulator.java
//******************************************************************************
package edu.utah.med.genepi.sim;

import edu.utah.med.genepi.app.rgen.Specification;
import edu.utah.med.genepi.gm.GDef;
import edu.utah.med.genepi.hc.compressGtype;
import edu.utah.med.genepi.ped.Study;
import edu.utah.med.genepi.util.GEException;

//==============================================================================
public interface GSimulator {
  public void setUserParameters(Specification spec, Study[] study);
  public void setPedData();
  public void setGDef(GDef gd);
  public void preProcessor () throws GEException;
  public interface Top extends GSimulator {
    public void simulateFounderGenotypes(int index) throws GEException;
    public void simulateFounderGenotypes(int index, compressGtype cGtype, int step) throws GEException;
  }
  public interface Drop extends GSimulator {
    public void simulateDescendantGenotypes(int index) throws GEException;
    public void simulateDescendantGenotypes(int index, compressGtype[] study_Gtypes, int step) throws GEException;
  }
}
