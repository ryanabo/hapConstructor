package edu.utah.med.genepi.gui2;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreexDetail
{
  String nodename;
  DefaultMutableTreeNode treenode;
  JPanel screen;

  public TreexDetail ( String inNodeName,
                       DefaultMutableTreeNode inNode,
                       JPanel inPanel )
  {
    nodename = inNodeName;
    treenode = inNode;
    screen = inPanel;
  }
}
