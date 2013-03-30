


                               X D E T

                                v 3.4 

                        F. Pazos & A. Valencia

                                 -o-


Information
===========

XDET implements two methods for detecting positions in multiple 
sequence alignments with a "family-dependent" or "function-dependent" 
conservation pattern. These positions have been shown to be related 
to functionality, and they complement the fully conserved positions 
as predictors of functionality from sequence information. They are 
usually related to functional specificity.

The first method is the "mutational behaviour (MB) method". This method,
previously implemented in the "mtreedet" program, is based on the 
comparison of the mutational behaviour of a position with the mutational
behaviour of the whole alignment with the idea that positions showing a
family-dependent conservation pattern would have a similar mutational 
behaviour as the whole family. The mutational behaviour of a position is
represented by a matrix containing all the similarities between the 
aminoacids of the proteins at that position. The mutational behaviour 
of the whole alignment is represented by an equivalent matrix containing
the similarities between the corresponding proteins. Both matrices are 
compared with a non-parametric rank correlation criteria. Hence, for 
each position the method produces an score which is that correlation 
coefficient. Positions with high scores are taken as the predicted 
functional sites. 

The second method is a variation of the previous one which incorporates
the possibility of using an external arbitrary functional classification
instead of relying on the one implicit in the alignment. Such possibility
is intended for cases where some degree of phylogeny/function 
disagreement is suspected. The external functional classification is 
incorporated in the form of a matrix of "functional similarities" between
proteins.

The original "MB-method" is described in:

* Antonio del Sol, Florencio Pazos & Alfonso Valencia. (2003).
  Automatic Methods for Predicting Functionally Important Residues.
  Journal of Molecular Biology. 326(4):1289-1302.

While the new method able to incorporate an external functional 
classification is described in:

* Florencio Pazos, Antonio Rausell & Alfonso Valencia. (2006).
  Phylogeny-independent detection of functional residues.
  Bioinformatics. 22(12):1440-1448.

NOTE: Contact Antonio Rausell (arausell@cnio.es) for obtaining the other
program described in the second paper ("MCdet").

Please, cite these references when reporting any result obtained using
this program.

See http://pdg.cnb.csic.es/pazos/mtreedet for updated versions of the 
program, additional data and links to other resources.



Using the program
=================

The program is distributed as an standalone executable file ("xdet"
for UNIX-based OSs or "XDET.EXE" for MS-Windows(R)). Running the 
program without command-line arguments prints a short information on
how to use it and a short description of the output:

-----------------------------------------------------------------------------
Xdet-Mtreedet v. 3.4
Florencio Pazos
pazos@cnb.csic.es

** Usage:  xdet  aln_file(HSSP|PIR/FASTA)  matrix(Maxhom|raw)  [options]

Options (Take a look at the user manual for a detailed description):
-E Skip entropy calculation.
-S=n
   Generate 'n' random alns in order to associate Z-scores and P-values.
   to the correlation scores.
   The default is to skip shuffling and report a single correlation score.
   It can take a long time for the program to run for large 'n' and many sequences.
-M=file
   Read and use protein 'similarity' matrix from an external file
   instead of calculating it from the alignment.
   This option is used to 'impose' and external functional classification.
   instead of the one implicit in the alignment.
   Line format:prot_nr1<TAB>prot_nr2<TAB>functional_similarity
-N
   Use Pearson's normal correlation instead of the default Spearman's non-parametric
   rank correlation.

Output format:
|%4d %4c %4d %c   %3d  %6.3f   %c %3d  xx %9.4f [%9.4f %e %9.4f %e]|
nr aa pdbn chain acc entrop secstr var xx MTD_VAL [Zscr_pos Pval_pos Zscr_glob Pval_glob]
-----------------------------------------------------------------------------

The basic input for the program is a multiple sequence alignment. This can
be provided either in HSSP, PIR or FASTA formats. Programs for generating 
multiple sequence alignments, such as ClustalW, can usually generate PIR 
and/or FASTA files.

The results of the program TOTALLY depend on the multiple sequence alignment
used. For the MB-method (old Mtredet program)  the alignment should contain 
a good representation of the sequence-space around the query sequence, 
removing sequences too far from the query and redundant sequences. A set of 
parameters for generating multiple sequence alignments which is producing good 
results for this method is:
  - Retrieve homologs up to BLAST E-value 10e-2.
  - Re-align these homologs (clustalw, t_cofee, ...). Do not use the original
    BLAST alignment.
  - From the alignment, remove fragments (sequences aligning in less than 
    60% of the master).
  - Remove outliers (%SEQID<25% with master).
  - Remove redundancy (%SEQID>95%)
  - Do not use alignments with less than 14 sequences.
But the user should try many alignments with different combinations of
parameters.

For the method which uses an external set of functional similarities it
does not matter how divergent the sequences in the alignment are (structural
alignments, etc) as long as the alignment is correct.

An aminoacid homology matrix is also required as input. The program can
read the Maxhom format (see the web page above to obtain matrices in this
format) and a raw format which opens the possibility for the user to 
incorporate any other matrix. The raw format is just:

aa1<1SPC>aa2<1SPC>similarity

where 'similarity' is a real number. For example:
-----------
A A 28.0
A C -28.0
A D -13.0
A E -8.0
A F -37.0
A G -5.0
A H -31.0
A I -19.0
.....etc....
-----------

An external functional classification can be imported with the "-M" option.
The parameter for this option is a file with "functional similarities"
for each pair of proteins in the alignment. The format of this file is

prot_nr1<TAB>prot_nr2<TAB>functional_similarity

where 'functional_similarity' is a real number. Proteins are numbered
according with the order they have in the multiple sequence alignment.

For example:
----------------------
1	2	100.0
1	3	80.0
1	4	0.0
....etc....
----------------------
In this example, "functional similarities" are given as percentages. The 
1st protein in the alignment is functionally identical to the 2nd one, while
it does not have any functional similarity with the 4th one.

'functional_similarity' can be in any scale. Take a look at the reference
above for different examples of "functional similarities" and different
ways of quantifying them.

If this option is used, a 'functional_similarity' value should be given
for ALL pairs of proteins, the program complains otherwise.

To statistically asses the significance of the scores, the program can 
shuffle the alignment a number of times and calculate z-scores and p-values
of the original scores with respect to the distribution of scores of the
shuffled alignments. shuffling is done by changing the order of the sequences
in the multiple sequence alignment. To activate this shuffling, simply 
add "-S=n" as an option, where 'n' is the number of shuffled alignments. 
Two p-values and two z-scores are reported associated to the score of each 
position. They are based on two background distributions of scores obtained
from the shuffled alignments, one with the scores for that position only,
and another one with the scores of all positions. We are still working on 
defining a good null-model and background distributions for this problem.
So this option is still experimental. If this option is not used, p-values
and z-scores are not calculated and only the raw score is reported. Please
note that large 'n' and big alignments can result in very long running times.


Output description
==================

The output of the program consists of a line for each position in the multiple
sequence alignment. The main score of the program (correlation between the
aminoacid similarities within a position and the overall similarities -sequence
or "functional" similarities- between proteins) is in the 9th column. High 
values of this parameter are associated with positions related with functional 
specificity. This value is not high for fully-conserved positions (see below).
Nevertheless, fully conserved positions are the main indicators of functionality.
Conserved positions can be detected by the entropy value (5th column) or by
the HSSP VAR parameter (7th column) (see below). This list of positions only
contains the ones with a percentage of gaps lower than a hardcoded threshold (10%).

----------------------------------------------------
   7    N    7      26  -1.000   H  46  xx    0.2651
   8    C    8      14   0.000   H   0  xx   -2.0000
   9    I    9       0   2.286   C  30  xx    0.3138
  10    K   10      65   2.842   C  40  xx    0.1919
  11    C   11       0   0.979   C  16  xx   -0.0020
  12    K   12      18   2.541   C  39  xx    0.2425
  16    C   16      13   0.000   H   0  xx   -2.0000
  17    V   17       5   2.450   H  35  xx   -0.0509
  18    E   18     118   2.712   H  35  xx    0.1402
.........
......
....
.
---------------------------------------------------- 
   1    2    3 c     4       5   6   7   8         9

Column 1: Position number. Positions are numbered as in the multiple sequence
          alignment. GAPS are included in this numbering.
  "    2: Aminoacid in master sequence (1st sequence of the alignment).
  "    3: PDB numbering. Database HSSP alignments include the PDB numbering 
          of the master sequence. If this information is not available the
	  position number (1st column) is reported also here.
  "    c: Database HSSP alignments may include a PDB chain identifier. That
          would be reported in this column.
  "    4: Solvent accessibility taken from the HSSP file. "-1" if not available.
  "    5: Sequence entropy of the position. A measure of conservation 
          (0: fully conserved). "-1.000" indicates that entropy has not 
	  been calculated for that positions because it contains a fraction
	  of gaps higher than a hardcoded limit, or that entropy calculation
	  has been disabled with the "-E" option).
  "    6: Secondary structure code taken from the HSSP file. "-" when not 
          available.
  "    7: Variability (VAR) taken from the HSSP file. Another measure of 
          conservation (0:fully conserved; 100: fully variable). "-1" when 
	  the input file is not HSSP.
  "    8: Reserved. Always "xx".
  "    9: Correlation value. Main score of the method. This value goes from -1.0
          (position "anti-correlated" with the functional classification) to 1.0
	  (position perfectly correlated with the functional classification).
	  Values lower than -1.0 are flags to indicate that the calculation were
	  not done (i.e. "gappy position"). Fully conserved positions 
	  (entropy=0.0 -5th column-) also have a value lower than -1.0 (i.e.
	  positions 8 and 16 in the example above).

If the "-S" option is used, 4 additional columns contain the z-score and
p-values calculated with respect to the shuffled scores for that position and
the shuffled scores for all positions respectively (see above). For the
positions where calculations could not be done (raw_score < -1.0) the z-scores
and p-values are labelled as "[]" and 2.0E+00 respectively.

------------------------------------------------------------------------------------------------------ 
   9    V    9      -1   1.694   -  -1  xx    0.1142     3.7149  0.000000e+00     1.6702  5.958904e-02
  10    G   10      -1   0.000   -  -1  xx   -2.0000         []  2.000000e+00         []  2.000000e+00
  11    A   11      -1   2.174   -  -1  xx    0.1929     3.2576  0.000000e+00     2.8426  7.534247e-03
  12    G   12      -1   2.102   -  -1  xx    0.3471     9.7135  0.000000e+00     5.1424  0.000000e+00
  .............
------------------------------------------------------------------------------------------------------


Examples
========

1: xdet 5fd1.hssp Maxhom_McLachlan.metric > 5fd1.xdet
  
2: xdet myaln.fasta  Maxhom_McLachlan.metric | sort -nr +8 > myaln.xdet

   (Output already sorted by score. Note: for HSSP files which include the 
   PDB chain the sort should be "sort -nr +9", since an additional column is
   present.)

3: ~/bin/xdet  myaln.fasta  mymatrix.txt -E  | sort -nr +8 | head -10 >myaln.treedet

   (Report only the 10 positions with highest scores. Do not calculate entropy.)

4: XDET.EXE TEST.PIR MATRIX.TXT -M=LIGAND_SIMILARITY.TXT 

   (Use an external matrix of functional similarities between proteins. Instead
   of using the one implicit in the sequence relationships of the alignment.)
   




--

See http://pdg.cnb.csic.es/pazos/mtreedet for updated versions of the 
program, additional data and links to other resources.

Please, cite the references above when reporting any data obtained 
using this program.

Send any query/comment to the following address. Use this address also for
reporting bugs. We will be very happy to know on any result (good or bad ;-)
you may obtain using this program.

Florencio Pazos Cabaleiro.
Protein Design Group.
Centro Nacional de Biotecnologia (CNB-CSIC)
Campus UAM Cantoblanco. 28049 Madrid.
e-mail: pazos@cnb.csic.es
Tlf. +34.915854669. Fax. +34.915854506
