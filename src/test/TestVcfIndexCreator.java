package test;

import java.util.List;

import vcf.index.VcfIndex;
import io.vcf.VcfIndexCreator;
import io.vcf.VcfIndexReader;
import io.vcf.VcfIndexWriter;

public class TestVcfIndexCreator {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		
		VcfIndexCreator vIC = new VcfIndexCreator(args[0],10000);
		List<VcfIndex> vaidxList = vIC.createVcfIndices();
		
//		System.out.println(vaidxList);
		
		VcfIndexWriter vW = new VcfIndexWriter("test2.vcf", vaidxList);
		vW.writeVcfIndex();
		VcfIndexReader vR = new VcfIndexReader("test2.vcf.vai");
		vaidxList = vR.readVcfIndices();
		
		System.out.println(vaidxList);

		long end = System.currentTimeMillis();
		
		System.out.println();
		System.out.println("Time in seconds: " + (end-start)/1000);
	} 

}
