package edu.utah.med.genepi.analysis;

import java.util.Vector;

public interface Report {
    public String getTitle();
    public String getCompStatTitle();
    public int[]  getNumSimulationReport();
    public String getFullObservationalReport();
    public String getObservationalReport();
    public String getObsExtraReport();
    public String getFullInferentialReport();
    public String getInferentialReport();
    public String getInfExtraStatTitle();
    public String getInfExtraReport();
    public String getObsValue();
    public Vector<Vector<Double>> getInfSimReport();
    public String getWarning();
}
