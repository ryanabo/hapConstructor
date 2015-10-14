package edu.utah.med.genepi.phase;

import edu.utah.med.genepi.genie.DataInterface;
import edu.utah.med.genepi.genie.GenieParameterData;

public interface Phase {

	public void phase();
	public void storeData(GenieParameterData gp, DataInterface di, int simIndex);
	public void output();

}
