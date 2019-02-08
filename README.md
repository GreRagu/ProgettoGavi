# Hegregio

**Hegregio** is a Lucene based information retrieval tool developed in Java (Eclipse workspace)  
It offer the possibility of searching within the **[TREC](http://www.trec-cds.org/2016.html)** dataset using different ir Models and different Tolerant Retrieval functions.

## How to install

the src directory contains the code of the project and the lib directory contains
all the lucene libs necessary for compiling the code.
Specifically, the jar libraries used for the project are
 
```
lucene-analyzers-common-7.4.0.jar
lucene-benchmark-7.4.0.jar 	
lucene-core-7.4.0.jar 	
lucene-demo-7.4.0.jar 	
lucene-queryparser-7.4.0.jar 	
lucene-suggest-7.4.0.jar 
```

## How to use 
The GUI structure is based on a menu bar.  
The first menu element that is going to be used is "Index"

**Create Index Path** creates a file that lists all the document the user wants to index  
**Create Index** Start indexing the document listed on the IndexPath file with the selected model  
**Load Index** Load an existing index

**Models** In this menu the user can select what model prefers to apply    
The choices are:  
* Standard model(Default)
* Vector Space Model  
* Boolean Model  
* Fuzzy Model 

**Efficiency** Calculates and plots graphs about precision and recall about the benchmark queries

In the search bar the user can insert the query who wants to search.  
Wildcard queries are supported.  
Before the search it is offered a "Did you mean..?" feature with a suggestion for
an alternative search.

The results are displayed in the table below with a default limit of 100 rows (editable during the search).
It is possible to open and view the selected document directly from the table.