
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import plot.Plot;
import plot.Plot.Line;

//import Line;

public class Benchmark {

	//Attributi
	Model model;				//Modello su cui eseguire
	String fileDocumentsPaths;	//in questo documento ogni riga è il percorso di un documento su cui eseguire le query
	String queryFile;			//File LISA.QUE  (topics2014.xml)
	String docExpected;			//File LISA.REL  (qrels)
	Index generalIndex; 		//Index
	LinkedList<String> ll; 		//List where to search (name, content)
	
	//Filled when executeBenchmark is called
	ArrayList<LinkedList<String>> expectedDocuments = new ArrayList<LinkedList<String>>();
	ArrayList<LinkedList<String>> retrivedDocuments = new ArrayList<LinkedList<String>>();
	ArrayList<LinkedList<String>> intersect = new ArrayList<LinkedList<String>>();
	ArrayList<Double>  precision = new ArrayList<Double>();
	ArrayList<Double> recall = new ArrayList<Double>();

	/**
	 * 
	 * @param model modello su cui viene eseguito il benchmark
	 * @param fileDocumentsPaths in questo file, ogni riga è un percorso di un documento su cui eseguire le query
	 * @param queryFile questo è il lisa.que file
	 * @param docExpected in questo file, per ogni query è indicata una lista di file rilevanti
	 */
	public Benchmark(Model model, String fileDocumentsPaths, String queryFile, String docExpected) {
		this.model = model;
		this.fileDocumentsPaths = fileDocumentsPaths;
		this.queryFile   = queryFile;
		this.docExpected = docExpected;

		this.generalIndex = Index.getIndex();
		this.generalIndex.setSimilarity(model.getSimilarity(), false);

		ll = new LinkedList<String>();
		ll.add("name");
		ll.add("content");
	}

	/**
	 * Questo metodo esegue i benchmark di LISA
	 * I documenti vengono aggiunti all'index.
	 * Array con i documenti attesi viene creato. (Documents that should be retrieved)
 	 * Array con documenti recuperati viene creato(Documents that are retrieved)
 	 * Array con intersezione tra i dueviene creato. (For calculating precision and recall)
	 */
	public void executeBenchmark() {
		
		//Reading queries from file
		ArrayList<String> queries = readQueries();

		//carica nell'indice tutti i file specificati in fileDocumentsPaths (tabella nella mian window)
		System.out.println("Loading index with " + fileDocumentsPaths);

		loadIndex();


		expectedDocuments = getExpectedDocuments();
		retrivedDocuments = retrieveDocuments(queries);
		//intersezione calcolata con getIntersection
		intersect = getIntersection(expectedDocuments, retrivedDocuments);
		
		saveResults("resFuz.save", intersect);

		precision = getPrecision(intersect, retrivedDocuments);
		recall = getRecall(intersect, expectedDocuments);

		int i=0;
		for (Double rec: recall)
			System.out.println("Recall query "+(++i)+": " + rec);

		i=0;
		for (Double rec: precision)
			System.out.println("Precision query "+(++i)+": " + rec);
	}

	/**
	 * Quest metodo legge le query dal file lisa.que (da sostituire con topics2014.xml)
	 * Le queries sono separate con un '#', quindi ognuna di esse termina così. 
	 * !da modificare in base al nostro formato di query!!
	 * @return list of queries
	 */
	public ArrayList<String> readQueries() {

		//Carica le query nel file 

		ArrayList<String> queries = new ArrayList<String>();

		try {
			File queryF = new File(queryFile);
			BufferedReader br = new BufferedReader(new FileReader(queryF));

			String line = "";
			String query = "";

			while ( (line=br.readLine()) != null) {
				//So, if it isn't the line with query number
				if(line.length()>2) {
					query += line + " ";
				}

				//If is the last line of the query
				if(line.endsWith("#")) {
					//Add the line until '#'
					query = query.substring(0, query.length()-2);
					queries.add(query);
					query = "";
				}
			}
			br.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return queries;
	}

	/**
	 * Carica nel file i documenti da recuperare 
	 * Legge ogni riga del file (una riga = un documento) e lo aggiunge all'indice
	 */
	private void loadIndex(){
		String docPath = "";

		try {
			File docF = new File(fileDocumentsPaths);		//file di path specificati da utente
			System.out.println("doc path:"+ docF.getAbsolutePath());
			BufferedReader br = new BufferedReader(new FileReader(docF));

			while ( (docPath = br.readLine()) != null) {
				generalIndex.addDocument(docPath);
			}
			br.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * recupera la lista di documenti attesi da ogni quey dal file LISA.REL (dasostituire con qrels)
	 * @return lista dei documenti attesi.
	 */
	private ArrayList<LinkedList<String>> getExpectedDocuments() {
		ArrayList<LinkedList<String>> expectedDocuments = new ArrayList<LinkedList<String>>();
		LinkedList<String> rel = null;	
		String line = "";
		int query_num = 1;

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(docExpected)));

			while( (line=br.readLine()) != null) {
				rel = new LinkedList<String>();

				if (!line.contains("Refs") &&  !line.contains("Query") && line.length() > 0) {
					boolean terminator = false;
					while( !terminator ){ 
						String [] docs = line.split(" ");

						for (String doc : docs) {
							int num = Integer.parseInt(doc);
							if (num != -1) {
								rel.add(doc);
							} else {
								terminator = true;
								break;
							}
						}

						if(!terminator) {
							line = br.readLine();
						}
					}
					System.out.println("Documents expected for query " + query_num + ": " + rel.toString());
					query_num++;
					expectedDocuments.add(rel);
				}
			}
			br.close();
		} catch (Exception e) {
			System.err.println(e);
		}			

		return expectedDocuments;
	}

	/**
	 * Questo metodo recupera i documenti, passando le queries dal benchmark.
	 * @param queries lista di queries
	 * @return documenti recuperati per ogni query
	 */
	private ArrayList<LinkedList<String>> retrieveDocuments(ArrayList<String> queries){
		ArrayList<LinkedList<String>> documentsRetrieved = new ArrayList<LinkedList<String>>();
		LinkedList<String> results = null;
		LinkedList<Hit> indexResults = null;
		int query_num = 1;

		for(String query : queries) {
			results = new LinkedList<String>();

			indexResults = generalIndex.submitQuery(query, ll, model, false);
			for(Hit indRes : indexResults) {
				results.add(indRes.getDocName().substring(0, indRes.getDocName().lastIndexOf(".")));
			}
			System.out.println("Results for query " + query_num + ": " + results.toString());
			System.out.print("******************************************\n");
			query_num++;
			documentsRetrieved.add(results);
		}
		return documentsRetrieved;
	}

	/**VALUTAZIONE
	 * Interseca i risultati attesi con quelli ottenuti.
	 * @param expectedDocuments
	 * @param retrievedDocuments
	 * @return intersezione
	 */
	private ArrayList<LinkedList<String>> getIntersection(ArrayList<LinkedList<String>> expectedDocuments, ArrayList<LinkedList<String>> retrievedDocuments){
		ArrayList<LinkedList<String>> intersect = new ArrayList<LinkedList<String>>();

		LinkedList<String> intersection = null;

		//cicla sui doc expected
		for (int query = 0 ; query < expectedDocuments.size(); query++) {
			intersection = new LinkedList<String>();
			//crea una lista di strighe per ognuno
			//cicla sui documenti recuperati
			for (int i = 0; i < retrievedDocuments.get(query).size(); i++) {
				for (int j = 0; j < expectedDocuments.get(query).size(); j++) {
					if ( expectedDocuments.get(query).get(j).equals(retrievedDocuments.get(query).get(i)) ){
						if (!intersection.contains(expectedDocuments.get(query).get(j))){
							intersection.add(expectedDocuments.get(query).get(j));
						}
					}
				}
			}
			System.out.println("Intersection for query " + (query+1) + ": " + intersection.toString());
			intersect.add(intersection);
		}
		return intersect;
	}
	
	/**
	 * Salva le intersezioni su un file
	 * @param fileName
	 * @param intersect
	 */
	private void saveResults(String fileName, ArrayList<LinkedList<String>> intersect) {
		try {
			FileWriter fw = new FileWriter(new File(fileName));
			for (int i = 0; i < intersect.size(); i++) {
				fw.append("Docs intersected for query "+(i+1)+":\n");
				for (int j = 0; j < intersect.get(i).size(); j++) {
					fw.append(intersect.get(i).get(j)+"\n");
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** RECALL
	 * Calcola recall
	 * @param intersect intersezione ottenuta prima
	 * @param expectedDocuments expected documents
	 * @return list of recall for every query
	 */
	public ArrayList<Double> getRecall(ArrayList<LinkedList<String>> intersect, ArrayList<LinkedList<String>> expectedDocuments) {
		ArrayList<Double> recall = new ArrayList<Double>();

		for (LinkedList<String> intersection: intersect)
			recall.add(intersection.size()+0.0);
		//For each query get intersection.size / relevants.size
		//recall = |intersect|/|relevants|
		for (int i = 0; i < expectedDocuments.size(); i++)
			if (expectedDocuments.get(i).size() != 0)
				recall.set(i, recall.get(i)/expectedDocuments.get(i).size());
			else
				recall.set(i, 0.0);

		return recall;
	}

	/** PRECISION
	 * Calcola precision
	 * @param intersect intersezione calcolata in getIntersection
	 * @param retrievedDocuments documenti recuperati
	 * @return lista con le precisioni di ogni query
	 */
	public ArrayList<Double> getPrecision(ArrayList<LinkedList<String>> intersect, ArrayList<LinkedList<String>> retrievedDocuments) {
		ArrayList<Double> precision = new ArrayList<Double>();

		for (LinkedList<String> intersection: intersect) {
			precision.add(intersection.size()+0.0);
		}
		//For each query get intersection.size / relevants.size
		//precision = |intersect|/|result|
		for (int i = 0; i < retrievedDocuments.size(); i++)
			if (retrievedDocuments.get(i).size() != 0)
				precision.set(i, precision.get(i)/retrievedDocuments.get(i).size());
			else
				precision.set(i, 0.0);
		return precision;
	}

	/** 
	 * Ottiene i livelli standard di recall
	 * @return
	 */
	private ArrayList<Double> getRecallLevel() {
		ArrayList<Double> recall = new ArrayList<Double>();
		
		recall.add(0.33);
		recall.add(0.66);
		recall.add(1.0);
		
		return recall;
	}
	
	/**
	 * Ottiene i livelli standard di precisione
	 * @return Precision
	 */
	public ArrayList<ArrayList<Double>> getPrecision() {
		int lvl0;
		int lvl33;
		int lvl66;
		int lvl100;
		
		ArrayList<ArrayList<Double>> precision = new ArrayList<ArrayList<Double>>();
		
		ArrayList<Double> temp = new ArrayList<Double>();
		
		for (int i = 0; i < this.precision.size(); i++) {
			lvl0 = 0;
			lvl33 = retrivedDocuments.get(i).size()*33/100;
			lvl66 = retrivedDocuments.get(i).size()*66/100;
			lvl100 = retrivedDocuments.get(i).size();
			
			temp.add( ( (double) getIntersect(expectedDocuments.get(i), retrivedDocuments.get(i), lvl33).size() / (double) lvl33 ));
			temp.add( ((double)getIntersect(expectedDocuments.get(i), retrivedDocuments.get(i), lvl66).size() / (double) lvl66) );
			temp.add( ((double)getIntersect(expectedDocuments.get(i), retrivedDocuments.get(i), lvl100).size() / (double) lvl100 ) );
			
			precision.add(temp);
			temp = new ArrayList<Double>();
		}
		
		return precision;
	}

	/**
	 * Calcola R-Precision
	 * @param level R for R-Precision
	 * @return value of r-precision for every query
	 */
	public ArrayList<Double> getRPrecision(int level) {
		ArrayList<Double> rprec = new ArrayList<>();
		
		for (int i = 0; i < this.precision.size(); i++) {
			int intersect = getIntersect(expectedDocuments.get(i), retrivedDocuments.get(i), level).size();
			rprec.add( (double) intersect / (double) level );
		}
		
		return rprec;
	}
	
	/**
	 * Ottiene precisione media
	 * @param precision precisione di getPrecision()
	 * @return avgprecision for every standard level
	 */
	public ArrayList<Double> getAvgPrecision(ArrayList<ArrayList<Double>> precision) {
		ArrayList<Double> avgPrecision = new ArrayList<Double>();
		ArrayList<Double> recallLevel = getRecallLevel();
		double tmp = 0.0;
		
		for (int j = 0; j < recallLevel.size(); j++) {
			for (int i = 0; i < this.precision.size(); i++) {
				tmp += ( (double) precision.get(i).get(j) / (double) this.precision.size() );
			}
			avgPrecision.add(tmp);
			tmp = 0;
		}
		
		return avgPrecision;		
	}
	
	/**
	 * Crea l'intersezione tra due liste
	 * @param expectedDocuments
	 * @param retrievedDocuments
	 * @param much quanti elementi di quelli recuperati vengono letti
	 * @return new lista con le intersezioni
	 */
	private ArrayList<String> getIntersect(LinkedList<String> expectedDocuments, LinkedList<String> retrievedDocuments, int much){
		ArrayList<String> intersect = new ArrayList<String>();
	
		for (String s: expectedDocuments)
			for (int i = 0; i < much; i++)
				if (retrievedDocuments.get(i).equals(s))
					intersect.add(s);
		
		return intersect;
	}
	
	/**
	 * Calcola F-measure
	 * @param precision lista della precisione di ogni query
	 * @param recall lista di recall per ogni query
	 * @return f-measure
	 */
	public ArrayList<Double> getFMeasure(ArrayList<Double> precision, ArrayList<Double> recall) {
		ArrayList<Double> fmeasure = new ArrayList<Double>();
		for (int i = 0; i < precision.size(); i++)
			if ( (precision.get(i)+recall.get(i)) != 0 )
				fmeasure.add(i, ( (2*precision.get(i)*recall.get(i))/(precision.get(i)+recall.get(i)) ) );
			else
				fmeasure.add(i,0.0);
		return fmeasure;
	}
	
	/**
	 * Calcola E-measure
	 * @param precision lista di precisione per ogni query
	 * @param recall lista di recall per ogni query
	 * @param b parametro b per la e-measure 
	 * @return emeasure
	 */
	public ArrayList<Double> getEMeasure(ArrayList<Double> precision, ArrayList<Double> recall, double b) {
		ArrayList<Double> emeasure = new ArrayList<Double>();
		
		for (int i = 0; i < precision.size(); i++)
			emeasure.add(i, ( 1-( (1+Math.pow(b, 2))/( (Math.pow(b, 2)/recall.get(i) + (1/precision.get(i)) ) ))));
		return emeasure;
	}
	
	/**
	 * Salva la misura (f o e) su file. 
	 * @param measure misura da salvare
	 * @param fileName file name
	 */
	public void saveMeasure(ArrayList<Double> measure, String fileName) {
		try {
			File f = new File(fileName);
			FileWriter fw = new FileWriter(f);
			for (int i = measure.size()-1; i >= 0; i-- )
				fw.append( measure.get(i).toString()+"\n" );
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	/**
	 * Procedura per la creazione di un grafo sul benchmark
	 * Procedure to do graph of benchmark
	 */
	public void doGraph() {
		
		ArrayList<Double> recallLevel = getRecallLevel(); //recallLevel (0.33, 0.66, 1.0)
		ArrayList<ArrayList<Double>> precision = getPrecision();
		
		//R-Precision for different values of R
		ArrayList<Double> rprecision5 = getRPrecision(5);
		ArrayList<Double> rprecision10 = getRPrecision(10);
		ArrayList<Double> rprecision15 = getRPrecision(15);
		
		//Array range(0,num_queries) for plot these numbers
		ArrayList<Double> num_queries = new ArrayList<Double>();
		
		ArrayList<Double> avgPrecision = getAvgPrecision(precision);
		
		for (int i = 0; i < this.precision.size(); i++) {
			num_queries.add(i + 0.0);
		}
		
		try {
			
			System.out.println("Plotting precision graph");
			//Precision
			Plot plot = Plot.plot(Plot.plotOpts().
					title("Precision graph").
					width(1000).
					height(600).
					legend(Plot.LegendFormat.TOP)).	
				xAxis("Query #", Plot.axisOpts().
					format(Plot.AxisFormat.NUMBER_INT).
					range(0, num_queries.size())).
				yAxis("Precision", Plot.axisOpts().
					range(0, getMax(this.precision))).series(".", Plot.data().
							xy(num_queries, this.precision),
							Plot.seriesOpts().
								line(Line.NONE).
								marker(Plot.Marker.COLUMN).
								color(Color.BLUE).markerColor(Color.BLUE));
			plot.save( new java.io.File( "." ).getCanonicalPath()+"\\results\\precision", "png");
			
			System.out.println("Plotting recall graph");
			//Recall
			plot = Plot.plot(Plot.plotOpts().
					title("Recall graph").
					width(1000).
					height(600).
					legend(Plot.LegendFormat.TOP)).	
				xAxis("Query #", Plot.axisOpts().
					format(Plot.AxisFormat.NUMBER_INT).
					range(0, num_queries.size())).
				yAxis("Recall", Plot.axisOpts().
					range(0, getMax(this.recall))).series(".", Plot.data().
							xy(num_queries, this.recall),
							Plot.seriesOpts().
								line(Line.NONE).
								marker(Plot.Marker.COLUMN).
								color(Color.BLUE).markerColor(Color.BLUE));
			plot.save(new java.io.File( "." ).getCanonicalPath()+"\\results\\recall", "png");
			
			System.out.println("Plotting R-Precision (5)");
			//R-Precision 5
			plot = Plot.plot(Plot.plotOpts().
					title("R-Precision 5").
					width(1000).
					height(600).
					legend(Plot.LegendFormat.TOP)).	
				xAxis("Query #", Plot.axisOpts().
					format(Plot.AxisFormat.NUMBER_INT).
					range(0, num_queries.size())).
				yAxis("Precision", Plot.axisOpts().
					range(0, getMax(rprecision5))).series(".", Plot.data().
							xy(num_queries, rprecision5),
							Plot.seriesOpts().
								line(Line.NONE).
								marker(Plot.Marker.COLUMN).
								color(Color.BLUE).markerColor(Color.BLUE));
			plot.save(new java.io.File( "." ).getCanonicalPath()+"\\results\\rprecision5", "png");
			
			System.out.println("Plotting R-Precision (10)");
			//R-Precision 10
			plot = Plot.plot(Plot.plotOpts().
					title("R-Precision 10").
					width(1000).
					height(600).
					legend(Plot.LegendFormat.TOP)).	
				xAxis("Query #", Plot.axisOpts().
					format(Plot.AxisFormat.NUMBER_INT).
					range(0, num_queries.size())).
				yAxis("Precision", Plot.axisOpts().
					range(0, getMax(rprecision10))).series(".", Plot.data().
							xy(num_queries, rprecision10),
							Plot.seriesOpts().
								line(Line.NONE).
								marker(Plot.Marker.COLUMN).
								color(Color.BLUE).markerColor(Color.BLUE));
			plot.save(new java.io.File( "." ).getCanonicalPath()+"\\results\\rprecision10", "png");
			
			System.out.println("Plotting R-Precision (15)");
			//R-Precision 15
			plot = Plot.plot(Plot.plotOpts().
					title("R-Precision 15").
					width(1000).
					height(600).
					legend(Plot.LegendFormat.TOP)).	
				xAxis("Query #", Plot.axisOpts().
					format(Plot.AxisFormat.NUMBER_INT).
					range(0, num_queries.size())).
				yAxis("Precision", Plot.axisOpts().
					range(0, getMax(rprecision15))).series(".", Plot.data().
							xy(num_queries, rprecision15),
							Plot.seriesOpts().
								line(Line.NONE).
								marker(Plot.Marker.COLUMN).
								color(Color.BLUE).markerColor(Color.BLUE));
			plot.save(new java.io.File( "." ).getCanonicalPath()+"\\results\\rprecision15", "png");
			
			System.out.println("Plotting Avg Precision");
			//Avg Precision
			plot = Plot.plot(Plot.plotOpts().
					title("Avg Precision").
					width(1000).
					height(600).
					legend(Plot.LegendFormat.TOP)).	
				xAxis("Recall Level", Plot.axisOpts().
					range(0, 1)).
				yAxis("Avg Precision", Plot.axisOpts().
					range(0, getMax(avgPrecision))).series(".", Plot.data().
							xy(recallLevel, avgPrecision),
							Plot.seriesOpts().
								line(Line.NONE).
								marker(Plot.Marker.COLUMN).
								color(Color.BLUE).markerColor(Color.BLUE));
			plot.save(new java.io.File( "." ).getCanonicalPath()+"\\results\\avgPrecision", "png");
			
			System.out.println("Plotting Recall/Precision for each query");
			//Recall Level/Precision
			for (int i = 0; i < this.recall.size(); i++) {
				
				plot = Plot.plot(Plot.plotOpts().
						title("Precision - Recall Query "+(i+1)).
						width(1000).
						height(600).
						legend(Plot.LegendFormat.NONE)).	
					xAxis("Recall", Plot.axisOpts().
						range(0.33, 1)).
					yAxis("Precision", Plot.axisOpts().
						range(0, getMax(precision.get(i)))).
					series(".", Plot.data().
						xy(recallLevel.get(2), precision.get(i).get(2)).
						xy(recallLevel.get(1), precision.get(i).get(1)).
						xy(recallLevel.get(0), precision.get(i).get(0)),
						Plot.seriesOpts().
							lineWidth(3).
							marker(Plot.Marker.CIRCLE).
							color(Color.BLUE));
				plot.save(new java.io.File( "." ).getCanonicalPath()+"\\results\\recall-precision"+(i+1), "png");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Plotting finished");
	}
	
	/**
	 * Ritorna il valore massimo da un vettore di liste
	 * @param list
	 * @return
	 */
	private static double getMax(ArrayList<Double> list) {
		double max = 0.0;
		if (list.size() > 0) 
			max = list.get(0);
		for (int i = 1; i < list.size(); i++)
			if (max < list.get(i))
				max = list.get(i);
		return max;
	}
	
	/**
	 * Ritorna il valore minimo da un vettore di liste
	 * @param list
	 * @return
	 */
	private static double getMin(ArrayList<Double> list) {
		double min = 0.0;
		if (list.size() > 0) 
			min = list.get(0);
		for (int i = 1; i < list.size(); i++)
			if (min > list.get(i))
				min = list.get(i);
		return min;
	}
			
}