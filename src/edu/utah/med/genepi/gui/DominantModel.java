package edu.utah.med.genepi.gui;

public class DominantModel implements Model
{
  int numCols = 3;
  ColumnPattern[] columnpattern = new ColumnPattern[numCols];
  String[] groupPattern;
  public DominantModel()
  {
    groupPattern = new String[] {"1/1"};
    columnpattern[0] = new ColumnPattern("0", groupPattern);
    groupPattern = new String[] {"(2/.)|(./2)"};
    columnpattern[2] = new ColumnPattern("1", groupPattern);
  }

  public ColumnPattern[] getColPattern()
  { return columnpattern; }
} 
