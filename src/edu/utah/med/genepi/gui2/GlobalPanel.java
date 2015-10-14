/*     */ package edu.utah.med.genepi.gui2;
/*     */ 
/*     */ import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GlobalPanel extends JPanel
/*     */ {
/*     */   PkgPanel pkgPanel;
/*     */   SimulationPanel simulationPanel;
/*     */ 
/*     */   public String getPkg()
/*     */   {
/* 278 */     throw new Error("Unresolved compilation problem: \n");
/*     */   }
/*     */   public String getRseed() {
/* 281 */     throw new Error("Unresolved compilation problem: \n");
/*     */   }
/*     */   public String getNsim() {
/* 284 */     throw new Error("Unresolved compilation problem: \n");
/*     */   }
/*     */   public String getTop() {
/* 287 */     throw new Error("Unresolved compilation problem: \n");
/*     */   }
/*     */ 
/*     */   public String getDrop()
/*     */   {
/* 295 */     throw new Error("Unresolved compilation problem: \n");
/*     */   }
/*     */ 
/*     */   public String getSampling()
/*     */   {
/* 303 */     throw new Error("Unresolved compilation problem: \n");
/*     */   }
/*     */   public String getDump() {
/* 306 */     throw new Error("Unresolved compilation problem: \n");
/*     */   }
/*     */ 
/*     */   public class DropSimulation extends JPanel
/*     */     implements ActionListener
/*     */   {
/*     */     ButtonGroup dropBgroup;
/*     */ 
/*     */     public DropSimulation()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 210 */       throw new Error("Unresolved compilation problem: \n");
/*     */     }
/*     */   }
/*     */ 
/*     */   public class Dumper extends JPanel
/*     */     implements ActionListener
/*     */   {
/*     */     ButtonGroup dumpBgroup;
/*     */ 
/*     */     public Dumper()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 274 */       throw new Error("Unresolved compilation problem: \n");
/*     */     }
/*     */   }
/*     */ 
/*     */   public class MethodsPanel extends JPanel
/*     */     implements ActionListener
/*     */   {
/*     */     GlobalPanel.TopSimulation topPanel;
/*     */     GlobalPanel.DropSimulation dropPanel;
/*     */     GlobalPanel.SamplingMethod samplingPanel;
/*     */     GlobalPanel.Dumper dumpPanel;
/*     */ 
/*     */     public MethodsPanel()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setDisplay(String pkgName)
/*     */     {
/* 141 */       throw new Error("Unresolved compilation problem: \n");
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 158 */       throw new Error("Unresolved compilation problem: \n");
/*     */     }
/*     */   }
/*     */ 
/*     */   public class PkgPanel extends JPanel
/*     */     implements ActionListener
/*     */   {
/*     */     JRadioButton hapmcRB;
/*     */     JRadioButton pedGenieRB;
/*     */     ButtonGroup pkgBgroup;
/*     */ 
/*     */     public PkgPanel()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/*  53 */       throw new Error("Unresolved compilation problem: \n");
/*     */     }
/*     */   }
/*     */ 
/*     */   public class SamplingMethod extends JPanel
/*     */     implements ActionListener
/*     */   {
/*     */     ButtonGroup SamplingBgroup;
/*     */ 
/*     */     public SamplingMethod()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 245 */       throw new Error("Unresolved compilation problem: \n");
/*     */     }
/*     */   }
/*     */ 
/*     */   public class SimulationPanel extends JPanel
/*     */   {
/*     */     GlobalPanel.VariablesPanel variablesPanel;
/*     */     GlobalPanel.MethodsPanel methodsPanel;
/*     */ 
/*     */     public SimulationPanel()
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public class TopSimulation extends JPanel
/*     */     implements ActionListener
/*     */   {
/*     */     ButtonGroup topBgroup;
/*     */ 
/*     */     public TopSimulation()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 186 */       throw new Error("Unresolved compilation problem: \n");
/*     */     }
/*     */   }
/*     */ 
/*     */   public class VariablesPanel extends JPanel
/*     */     implements ActionListener
/*     */   {
/*     */     JTextField rseedF;
/*     */     JTextField numSimF;
/*     */ 
/*     */     public VariablesPanel()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 117 */       throw new Error("Unresolved compilation problem: \n");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/ryanabo/Desktop/hapconstructor2.0.1.jar
 * Qualified Name:     edu.utah.med.genepi.gui2.GlobalPanel
 * JD-Core Version:    0.6.1
 */