package edu.utah.med.genepi.gui;
 
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class TitledEtched 
{
  public TitledBorder title;
  public TitledEtched ( String titleName )
  {
    Border etched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    title = BorderFactory.createTitledBorder
                                       (etched,
                                        titleName,
                                        TitledBorder.CENTER,
                                        TitledBorder.DEFAULT_POSITION);
  }
}
