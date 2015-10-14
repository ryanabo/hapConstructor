package edu.utah.med.genepi.gui;

public class AlleleModel implements Model
{
  int numCols = 2;
  ColumnPattern[] columnpattern = new ColumnPattern[numCols];
  String[] groupPattern;
  public AlleleModel()
  {
    groupPattern = new String[] {"1"};
    columnpattern[0] = new ColumnPattern("0", groupPattern);
    groupPattern = new String[] {"2"};
    columnpattern[1] = new ColumnPattern("1", groupPattern);
  }

  public ColumnPattern[] getColPattern()
  { return columnpattern; }
} 
