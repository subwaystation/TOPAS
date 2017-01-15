# TOPAS - TOolkit for Processing and Annotating Sequence data
This toolkit allows the efficient manipulation of sequence data in various ways.

Example Invocation:
  `java -jar topas.jar -?`
This lists all available modules.

## GenConS - Generate Consensus Sequence
GenConS reads a FASTA reference and a corresponding GATK Unified Genotyper VCF file of which a consensus sequence is created. The key feature of this module is that it is able to handle ancient DNA data. For instance, the user can set the expected DNA damage individually. This is reflected in the resulting consensus sequence.

# LICENSE
TOPAS is available free of charge for academic purposes. The toolkit is available under the CC-BY license.
