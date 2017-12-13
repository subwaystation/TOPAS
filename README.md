# TOPAS - TOolkit for Processing and Annotating Sequence data
This toolkit allows the efficient manipulation of sequence data in various ways. It is organized into modules: The `FASTA` processing modules, the `FASTQ` processing modules, the `GFF` processing modules and the `VCF` processing modules.

Example Invocation:
  `java -jar topas.jar -?`
This lists all available modules.

| FASTA Processing Modules  | FASTQ Processing Modules | GFF Processing Modules | VCF Processing Modules |
| ------------- | ------------- |------------- | ------------- |
| Validate FASTA  | Validate FASTQ  | Validate GFF3 | Index VCF |
| Correct FASTA  | Format FASTQ  | Sort GFF3 | Filter VCF |
| Index FASTA  |  | Filter GFF3 | Annotate VCF |
| Extract FASTA  |  |  | Analyse VCF |
| Tabulate FASTA  |  |  | **GenConS** |

## VCF Processing Modules
### GenConS - Generate Consensus Sequence
GenConS reads a FASTA reference and a corresponding GATK Unified Genotyper VCF file of which a consensus sequence is created. **The key feature of this module is that it is able to handle ancient DNA data.** For instance, the user can set the expected DNA damage individually. This is reflected in the resulting consensus sequence. **For a detailed description of the tool see the wiki page https://github.com/subwaystation/TOPAS/wiki#gencons.**

For a more detailed insight see **https://github.com/subwaystation/TOPAS/blob/master/BSC_Thesis_Heumos.pdf**

# LICENSE
TOPAS is available free of charge for academic purposes. The toolkit is available under the CC-BY license.
