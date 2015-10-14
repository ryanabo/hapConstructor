package edu.utah.med.genepi.gui2;

import java.util.Enumeration;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import edu.utah.med.genepi.util.Ut;

public class DetailPanelImp extends JPanel implements DetailPanel
{
  int childPaneCount = 0;
  String title = null;
  String childname;
  boolean allowsChildren;
  DefaultMutableTreeNode parentNode;
  DefaultMutableTreeNode myNode;
  TreexDetailPanel treexdetailpanel;
  int index;
  private static final String SPKG_GUI = Ut.pkgOf(DetailPanel.class);
  DetailPanelImp[] list;

  public DetailPanelImp()
  {
  }
  
  public void setParentNode( DefaultMutableTreeNode parent )
  {
    parentNode = parent;
  }

  public void setMyNode( DefaultMutableTreeNode thisNode )
  {
    myNode = thisNode;
  }

  public void setTreexDetailPanel ( TreexDetailPanel treexdetail )
  {
    treexdetailpanel = treexdetail;
  }

  public void buildDetail(int myIndex)
  {
    index = myIndex;
  }

  public String toString() 
  { return title + " " + index; }

  public DetailPanelImp createChildPanel() 
                        throws Exception
  {
    childPaneCount++;
    String suffix = new String("Detail");
    Object newPanel = Ut.newModule(SPKG_GUI, childname, suffix);
    DetailPanelImp childDetail = (DetailPanelImp) newPanel;
    childDetail.setParentNode( myNode );
    childDetail.setTreexDetailPanel(treexdetailpanel);
    childDetail.buildDetail(childPaneCount);
    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode
                                       (childDetail,
                                        childDetail.getAllowsChildren() );
    childDetail.setMyNode(childNode);
    DefaultMutableTreeNode node = treexdetailpanel.addNode(myNode, childNode); 
    return (DetailPanelImp) childDetail;
  }

  public int getChildCount()
  {
    return childPaneCount;
  }

  public void setTitle( String inTitle )
  {
    title = inTitle;
  }
  
  public int getIndex()
  {
    return index; 
  }

  public void displayPanel()
  {}

  public boolean getFlag()
  {
    return true;
  }
 
  public void setAllowsChildren( boolean inBoolean )
  { allowsChildren = inBoolean; }

  public boolean getAllowsChildren()
  { return allowsChildren; }

  //public String getChildName()
  //{ return childName; }
  
  public TreeMap<DefaultMutableTreeNode, DetailPanelImp> 
         getDetailPanel()
  {
    DefaultMutableTreeNode node = treexdetailpanel.rootNode;
    return getDetailPanel(node);
  }

  public TreeMap<DefaultMutableTreeNode, DetailPanelImp> 
         getDetailPanel(DefaultMutableTreeNode inNode)
  {
    TreeMap<DefaultMutableTreeNode, DetailPanelImp> detailnode =
           new TreeMap<DefaultMutableTreeNode, DetailPanelImp>();
    for ( Enumeration e = inNode.children(); e.hasMoreElements(); )
    {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                    e.nextElement();
      detailnode.put(node, (DetailPanelImp) node.getUserObject());
    }
    return detailnode;
  }

  public DetailPanelImp[] getChildDetail()
  {
    list = new DetailPanelImp[myNode.getChildCount()];
    int i = 0 ; 
    for ( Enumeration e = myNode.children(); e.hasMoreElements(); )
    {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
      list[i] = (DetailPanelImp) node.getUserObject();
      i++;
    }
    return list;
  }

  public ImageIcon createImageIcon (String path,
                                              String description)
  {
    java.net.URL imgURL = getClass().getResource(path);
    if (imgURL != null)
      return new ImageIcon(imgURL, description);
    else
    {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

}
