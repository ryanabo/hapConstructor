package edu.utah.med.genepi.gui;

public class AdditiveModel implements Model
{
  int numCols = 3;
  ColumnPattern[] columnpattern = new ColumnPattern[numCols];
  String[] groupPattern;
  public AdditiveModel()
  {
    groupPattern = new String[] {"1/1"};
    columnpattern[0] = new ColumnPattern("0", groupPattern);
    groupPattern = new String[] {"(1/2)|(2/1)"};
    columnpattern[1] = new ColumnPattern("1", groupPattern);
    groupPattern = new String[] {"(2/2)"};
    columnpattern[2] = new ColumnPattern("2", groupPattern);
  }

  public ColumnPattern[] getColPattern()
  { return columnpattern; }
} 
