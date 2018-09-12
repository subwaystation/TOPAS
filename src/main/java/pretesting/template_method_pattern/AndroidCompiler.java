package pretesting.template_method_pattern;

public class AndroidCompiler extends CrossCompiler
{
	protected void collectSource()
	{
		//anything specific to this class
	}
	
	protected void compileToTarget()
	{
		this.version = 4.4;
		System.out.println(this.version);
		//android specific compilation
		System.out.println("android");
	}

}