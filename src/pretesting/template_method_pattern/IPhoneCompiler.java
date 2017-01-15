package pretesting.template_method_pattern;

public class IPhoneCompiler extends CrossCompiler
{
	protected void collectSource()
	{
		//anything specific to this class
	}

	protected void compileToTarget()
	{
		this.version = 4.3;
		System.out.println(this.version);
		//iphone specific compilation
		System.out.println("iphone");
	}

}