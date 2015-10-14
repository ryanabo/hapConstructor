package edu.utah.med.genepi.gui2;

import javax.swing.tree.DefaultMutableTreeNode;

public class DynamicTreeExt extends DynamicTree
{
  public DynamicTreeExt( String rootnodename )
  {
    super(rootnodename);
    rootNode = new DefaultMutableTreeNode(rootnodename);
  }
}
