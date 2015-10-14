package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.gm.Gtype;
import edu.utah.med.genepi.gm.GtypeMatcher;
import edu.utah.med.genepi.gm.Ptype;

public interface AnalysisTable 
{
	public int getColumnCount();
	public Column getColumnAt(int index);
	public Row getRowAt(int index);
	public Row getRowFor(Ptype pt);
	public Totals getTotals();
	public String getTableName();
	public String getColumnHeading();
	public String getRowHeading();
	public String getColumnLabelAt(int index);
	public int getRowCount();
	public String getAttachMessage();

	public interface Column 
	{
		public int subsumesAtype(Gtype gt, boolean first);
		public int subsumesGtype(Gtype gt);
		public int getWeight();
		public GtypeMatcher[] getGtypeMatcher();
	}
	
	public interface Row 
	{
		public Ptype getPtype();
		public Number[] getCells();
		public String getLabel();
		public int[] getCellsN();
	}
	
	public interface Totals 
	{
		public Number[] forColumns();
		public Number[] forRows();
		public Number forTable();
	}
}
