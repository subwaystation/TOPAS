package pretesting.template_method_pattern;

public abstract class CrossCompiler {
	
	protected double version;
	
	public final void crossCompile() {
		this.version = 1.1;
		System.out.println(version);
		collectSource();
		compileToTarget();
	}

	//Template methods
	protected abstract void collectSource();

	protected abstract void compileToTarget();
}
