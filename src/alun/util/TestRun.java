/*     */ package alun.util;
/*     */ 
/*     */ /*     */ 
/*     */ class TestRun
/*     */   implements SafeRunnable
/*     */ {
/* 164 */   int i = 0;
/*     */ 
/*     */   public void loop()
/*     */   {
/* 161 */     System.out.println(++this.i);
/*     */   }
/*     */ }

/* Location:           /Users/ryanabo/Desktop/hapconstructor2.0.1.jar
 * Qualified Name:     alun.util.TestRun
 * JD-Core Version:    0.6.1
 */