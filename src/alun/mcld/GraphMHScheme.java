package alun.mcld;

import alun.graph.Graph;

public interface GraphMHScheme
{
	public Graph<Locus,Object> getGraph();
	public void initialize();
	public LogLikelihood update(JointScheme js, double thresh);
}
