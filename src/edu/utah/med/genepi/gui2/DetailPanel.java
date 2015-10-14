package edu.utah.med.genepi.gui2;

import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

public interface  DetailPanel 
{
  public String toString(); 
  //public String getChildName();
  public void buildDetail(int index); 
  public void setParentNode ( DefaultMutableTreeNode parent );
  public void setMyNode( DefaultMutableTreeNode myNode );
  public void setTreexDetailPanel ( TreexDetailPanel top );
  public DetailPanelImp createChildPanel()
                        throws Exception; 
  public boolean getAllowsChildren();
  public int getChildCount();
  public int getIndex();
  public void displayPanel();
  public boolean getFlag();
  public TreeMap<DefaultMutableTreeNode, DetailPanelImp> getDetailPanel();
  public TreeMap<DefaultMutableTreeNode, DetailPanelImp> 
         getDetailPanel(DefaultMutableTreeNode inNode);
  public ImageIcon createImageIcon (String path,
                                              String description);

}
