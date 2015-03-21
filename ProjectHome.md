# JDet #
  * [JDet Home Page](http://csbg.cnb.csic.es/JDet)
  * [News](#News.md)
  * [What is JDet?](#What_is_JDet?.md)
  * [Libraries](#Libraries.md)
  * [Screenshots](#Screenshots.md)


---

**Publication reference:**
  * [Muth T, García-Martín JA, Rausell A, Juan D, Valencia A, Pazos F.: Bioinformatics 2011](http://bioinformatics.oxfordjournals.org/content/early/2011/12/09/bioinformatics.btr688).
  * If you use **JDet** as part of a paper, please include the reference above.


---


**JDet Homepage and group website:**
  * [Official JDet Homepage](http://csbg.cnb.csic.es/JDet/).
  * [Computational Systems Biology Group @CNB-CSIC](http://csbg.cnb.csic.es/).


---


[![](http://mac.softpedia.com/base_img/softpedia_free_award_f.gif)](http://mac.softpedia.com/get/Math-Scientific/JDet.shtml)


---


## News ##
_September 10. 2014:_  [JDet v1.4.5](http://csbg.cnb.csic.es/JDet/) is now available.
  * Bug fixing: When maximizing the alignment window instead of resizing, sequences at the end of the alignment out of the scope of the minimized window are not shown.

_May 15. 2014:_  [JDet v1.4.4](http://csbg.cnb.csic.es/JDet/) is now available.
  * Bug fixing: Structure alignments are based in the primary sequence of PDB files (SEQRES tag). Since structure information not always matches with the primary sequence, selected residues could differ between alignment and structure view .

_May . 2013:_  [JDet v1.4.3](http://csbg.cnb.csic.es/JDet/) is now available.
  * Bug fixing: Blast against RSBC webservices is no longer supported by RSBC. Since version 1.4.2 blast is performed against NCBI servers

_March 29. 2013:_ [JDet v1.4.1](http://code.google.com/p/jdet/downloads/list) is now available.

_December 06. 2011:_ JDet has been published by the journal _Bioinformatics_:
**JDet: Interactive calculation and visualization of function-related conservation patterns in multiple sequence alignments and structures.**
http://www.ncbi.nlm.nih.gov/pubmed/22171333

_December 01. 2011:_ [JDet v1.4](http://code.google.com/p/jdet/downloads/list) is now available.

_September 28. 2011:_ [JDet v1.3](http://code.google.com/p/jdet/downloads/list) is now available.


_October 08. 2010:_ [JDet v1.2](http://code.google.com/p/jdet/downloads/list) is now available:
  * Software now features a bottom statusbar with sequence number, alignment length and filename.
  * Filemenu has been updated with the load alignment feature and some minor changes.
  * Bug fixing: Reading of the AAs from the first sequence in the alignment
  * Bug fixing: Updated JMol settings


_June 01. 2010:_ [JDet v1.1](http://code.google.com/p/jdet/downloads/list) is now available:
  * Bidirectional mapping of Structure3DFrame and Alignment Main frame works.
  * JDet starts with an empty alignment frame and has a new look + menu structure.
  * The sequence which is suspected in the Structure3DFrame is marked in the proteinID field.
  * Bug fixing: Whole alignment can be exported now as PNG.
  * Bug fixing: Chain selection for the Structure3DFrame works.
  * Bug fixing: Selection in the alignment show correct residues in Structure3DFrame.


_May 10. 2010:_ [JDet v1.0](http://code.google.com/p/jdet/downloads/list) is now available:
  * Feature of downloading PDB files directly from the server by entering the 4-digit code and showing them in the structure viewer
  * ClusterFrame was updated the S3Det clusters are now shown in the same colors as in the alignment
  * Choosing a position in the alignment effects highlighting residues in the structure viewer
  * JDet supports now the adding of arbitrary methods with the format position/amino acid/score.
  * The alignment panel can now be exported as PNG file.
  * Bug fixing: Scrolling works now

[Go to top of page](#JDet.md)


---


## What is JDet? ##
JDet  is a multiplatform software for the interactive calculation and visualization of function-related conservation patterns in multiple sequence alignments and structures. It contains the set of tools and features we consider critical for the daily work with this kind of data, and that previously were disseminated in different packages and web servers. The package includes two of our recently developed programs for extracting this kind of information from protein alignments.

This software has been developed by Thilo Muth and Juan Antonio Garcia under the guidance of Florencio Pazos.

[Go to top of page](#JDet.md)


---


## Libraries ##
The following 3rd party libraries are being used in the software:
  * [SwingX](https://swingx.dev.java.net/)
  * [JGoodies](http://www.jgoodies.com/)
  * [Substance](https://substance.dev.java.net/)
  * [Java3d](https://java3d.dev.java.net/)
  * [Jmol](http://jmol.sourceforge.net/)

[Go to top of page](#JDet.md)


---



## Screenshots ##

(Click on the screenshot to see the full size version)

## Main User Interface ##
![![](http://csbg.cnb.csic.es/JDet/images/Main_Frame.png)](http://csbg.cnb.csic.es/JDet/images/Main_Frame.png)
<a href='Hidden comment: 
[http://jdet.googlecode.com/svn/trunk/jDet/img/mainframe1.png http://jdet.googlecode.com/svn/trunk/jDet/img/mainframe1_small.png]
'></a>

## Cluster3D Viewer ##
![![](http://csbg.cnb.csic.es/JDet/images/Sequence_Spaces.png)](http://csbg.cnb.csic.es/JDet/images/Sequence_Spaces.png)
<a href='Hidden comment: 
[http://jdet.googlecode.com/svn/trunk/jDet/img/clusterframe1.png http://jdet.googlecode.com/svn/trunk/jDet/img/clusterframe1_small.png]
'></a>

## Protein Structure Viewer ##
![![](http://csbg.cnb.csic.es/JDet/images/Structure_3D_View.png)](http://csbg.cnb.csic.es/JDet/images/Structure_3D_View.png)

<a href='Hidden comment: 
[http://jdet.googlecode.com/svn/trunk/jDet/img/structureframe1.png http://jdet.googlecode.com/svn/trunk/jDet/img/structureframe1_small.png]
'></a>

## Sequence Logo ##
![![](http://csbg.cnb.csic.es/JDet/images/Sequence_Logo.png)](http://csbg.cnb.csic.es/JDet/images/Sequence_Logo.png)

## Clustered Sequence Logo ##
![![](http://csbg.cnb.csic.es/JDet/images/Sequence_Logo_Cluster.png)](http://csbg.cnb.csic.es/JDet/images/Sequence_Logo_Cluster.png)

[Go to top of page](#JDet.md)