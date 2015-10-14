package alun.markov;


public interface Table extends Function, Iterable
{
	public void setValue(double d);
	public void increase(double d);
	public void multiply(double d);

	public void initToZero();
	public double sum();
	public void scale(double d);
	public void invert();

	public int size();
	public void freeSpace();
	public void allocateSpace();
}
