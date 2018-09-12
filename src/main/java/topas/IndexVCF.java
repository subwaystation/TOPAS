package topas;

import io.vcf.VcfIndexCreator;
import io.vcf.VcfIndexWriter;

import java.util.List;

import topas.Topas.TOPASModule;
import topas.parameters.IndexVCFParameters;
import vcf.index.VcfIndex;

@TOPASModule(
		purpose = "generate vcf index from a vcf file"
		)

public class IndexVCF {

	public static void main(String[] args) throws Exception {

		System.out.println(IndexVCF.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		IndexVCFParameters.createInstance(args);
		
		String inputFile = IndexVCFParameters.getInstance().getParameter("i").toString();
		String outputFile = IndexVCFParameters.getInstance().getParameter("o").toString();
		String g = IndexVCFParameters.getInstance().getParameter("gap").toString();
		int gap = 10000;
		if (!g.equals("--")) {
			gap = Integer.parseInt(g);
		}
		if(outputFile.equals("--")) {
			outputFile = inputFile + ".vai";
		} else {
			if(outputFile.endsWith(".vai")) {
				outputFile = outputFile.substring(0, outputFile.length()-4);
			}
		}
		
		System.out.println();
		
		System.out.println("Parameters chosen: ");
		System.out.println("Input file           : "+inputFile);
		System.out.println("Output file          : "+outputFile);
		System.out.println("Gap                  : "+gap);
		
		System.out.println();
		
		VcfIndexCreator vIC = new VcfIndexCreator(inputFile, gap);
		
		List<VcfIndex> vaidxList = vIC.createVcfIndices();
		
		System.out.println("Generated " + vaidxList.size() + " indices.");
		System.out.println();
		if(outputFile.equals("--")) {
			outputFile = inputFile;
		}
		if(outputFile.endsWith(".vai")) {
			outputFile = outputFile.substring(0, outputFile.length()-4);
		}
		// write index
		VcfIndexWriter vIW = new VcfIndexWriter(outputFile, vaidxList);
		vIW.writeVcfIndex();
		
		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(IndexVCF.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
	}
}
