
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import plot.Plot;
import plot.Plot.Line;

import org.apache.lucene.benchmark.quality.*;
import org.apache.lucene.benchmark.quality.utils.*;
import org.apache.lucene.benchmark.quality.trec.*;
 
// From appendix C
 
/* This code was extracted from the Lucene
   contrib/benchmark sources */
 
public class PrecisionRecall {
	
	private static ArrayList<Double> precision;
	private static ArrayList<Double> avgPrecision;
	private static ArrayList<Double> recall;
	private static PrintWriter writer;
	private static String qrelPath = "./dataset/clinical_dataset/Qrels/MyQrels/NewQrels-treceval-2014.txt";
 
public static void main(String[] args) throws Throwable {
 
	/*ReadXMLFile reader = new ReadXMLFile();
	for ( int i = 1; i <= 30; i++  ) { 
		
		reader.SearchQuery( i );
		
	}*/
	
	//initQrel();
	
	
	
	File topicsFile = new File("./dataset/clinical_dataset/Topics/MyQuery/Topics2014.txt");
    File qrelsFile = new File("./dataset/clinical_dataset/Qrels/qrels-sampleval-2014.txt");
    
    String s = "./index_pmc";
    FSDirectory open = FSDirectory.open(Paths.get(s));
    
    IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(open));
         
    String docNameField = "filename";
 
    PrintWriter logger = new PrintWriter(new FileOutputStream("output.txt"), true);
 
    TrecTopicsReader qReader = new TrecTopicsReader();   //#1
    QualityQuery qqs[] = qReader.readQueries(            //#1
        new BufferedReader(new FileReader(topicsFile))); //#1
    
    String k = qqs[1].getValue("description");
    
    System.out.println(k);
    
    System.out.println("numero componenti topic file: "+qqs.length);
    
    Judge judge = new TrecJudge(new BufferedReader(      //#2
        new FileReader(qrelsFile)));                     //#2
    
    if ( judge.equals(null) ) {
    	
    	System.out.println("JUDGE = NULL");
    	
    }
    
    judge.validateData(qqs, logger);                     //#3
    //System.out.println("step 2 ");
    QualityQueryParser qqParser = new SimpleQQParser("description", "contents");//#4
 
    QualityBenchmark qrun = new QualityBenchmark(qqs, qqParser, searcher, docNameField);
    
    SubmissionReport submitLog = null;
    System.out.println("step 3 ");

    QualityStats stats[] = qrun.execute(judge,           //#5
            submitLog, logger);
    System.out.println("step 4 ");

    QualityStats avg = QualityStats.average(stats);      //#6
    avg.log("SUMMARY",2,logger, "  ");
    open.close();
    
    doGraph();
    
  }

private static void initQrel() {
	// TODO Auto-generated method stub
	
	//creazione file newQrels2014
	 try {
		writer = new PrintWriter(qrelPath , "UTF-8");
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
	 //lettura file qrels-sampleval-2014
	    Scanner sc = null;
	    try {
	        sc = new Scanner(new File("./dataset/clinical_dataset/Qrels/qrels-treceval-2014.txt"));
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    
	    String line = "";
	    String qNum = "";
	    String fileName = "";
	    String rilevance = "";
	    
	    while (sc.hasNextLine()) {
	    	line = sc.nextLine();
	    	qNum = line.substring(0,2);
	    	fileName = line.substring(4,11)+".nxml";
	    	rilevance = getRilevance(line);
	    	
	    	writer.println( qNum + "    0" + "    " + fileName + "    " + rilevance );
	    	
	    	//System.out.println(rilevance);
	    	
	    }
	    writer.close();
}


private static String getRilevance(String line) {
	// TODO Auto-generated method stub
	
	String s = line.substring(line.length()-4,line.length()-2);
	if( s.contains(" 2") || s.contains("2") ) {
		return "1"; //rilevante	
	}
	else {
		return "0"; //non rilevante	
	}
	
}

public static void doGraph() {
	
			ArrayList<Double> recallLevel = getRecallLevel(); //recallLevel (0.33, 0.66, 1.0)
			initVar();
	
			//Array range(0,num_queries) for plot these numbers
			ArrayList<Double> num_queries = new ArrayList<Double>();
						
			for (int i = 0; i < precision.size(); i++) {
				//System.out.println("precision q "+i+" : "+precision.get(i));
				num_queries.add(i + 1.0);
			}
			
			System.out.println("Plotting precision graph");
			//Precision
			Plot plot = Plot.plot(Plot.plotOpts().
					title("Precision graph - ").
					width(1000).
					height(600).
					legend(Plot.LegendFormat.TOP)).	
				xAxis("Query #", Plot.axisOpts().
					format(Plot.AxisFormat.NUMBER_INT).
					range(0, num_queries.size())).
				yAxis("Precision", Plot.axisOpts().
					range(0, getMax(precision))).series(".", Plot.data().
							xy(num_queries, precision),
							Plot.seriesOpts().
								line(Line.NONE).
								marker(Plot.Marker.COLUMN).
								color(Color.BLUE).markerColor(Color.BLUE));
			try {
				plot.save( new java.io.File( "." ).getCanonicalPath()+"/results/precision", "png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
					range(0, getMax(recall))).series(".", Plot.data().
							xy(num_queries, recall),
							Plot.seriesOpts().
								line(Line.NONE).
								marker(Plot.Marker.COLUMN).
								color(Color.BLUE).markerColor(Color.BLUE));
			try {
				plot.save(new java.io.File( "." ).getCanonicalPath()+"\\results\\recall", "png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
			
			
			
			/*try {
				
				/*System.out.println("Plotting R-Precision (5)");
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
				for (int i = 0; i < recall.size(); i++) {
					
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
			System.out.println("Plotting finished");*/
	
	
}


/**Funzione che estrae precision - recall - avgPrecision - precision at
 * 
 * 
 * 
 * **/
private static void initVar() {
	// TODO Auto-generated method stub
	
	System.out.println("INIT VAR");
	
	precision = new ArrayList<Double>();
	recall = new ArrayList<Double>();
	avgPrecision = new ArrayList<Double>();
	  boolean summary = false;
	  boolean getPrecision = true;
	  boolean getRecall = true;
	  
    Scanner sc = null;
    try {
        sc = new Scanner(new File("Output.txt"));
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    String line = "";
    String sub = "";
    int precisionOffset = 5;
    while (sc.hasNextLine()) {
  	  line = sc.nextLine();
    	  
    	  if ( line.contains( "SUMMARY" ) ) {
    		  summary = true;
    		  getPrecision = false;
    		  getRecall    = false;
    	  }
    	  
    	  if ( line.contains("Average Precision:") && getPrecision ) {
    		  	sub = line.substring(line.length()- precisionOffset);
    		  	//System.out.println(sub);
    		  	
    		  	precision.add(Double.parseDouble(sub));
    	  }
    	  
    	  if ( line.contains("Recall:") && getRecall ) {
  		  	sub = line.substring(line.length()- precisionOffset);
  		  	//System.out.println(sub);
  		  	
  		  	recall.add(Double.parseDouble(sub));
  	  }
    	  

		if ( line.contains("Average Precision:") && summary  ) {
  		  	sub = line.substring(line.length()- precisionOffset);
  		  	//System.out.println(sub);
  		  	
  		  	avgPrecision.add(Double.parseDouble(sub));
    }
		
		
    	  
    	
    }
	
	
}

/** 
 * Ottiene i livelli standard di recall
 * @return
 */
private static ArrayList<Double> getRecallLevel() {
	// TODO Auto-generated method stub

		ArrayList<Double> recall = new ArrayList<Double>();
		
		recall.add(0.33);
		recall.add(0.66);
		recall.add(1.0);
		
		return recall;
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

}
 
/*
#1 Read TREC topics as QualityQuery[]
#2 Create Judge from TREC Qrel file
#3 Verify query and Judge match
#4 Create parser to translate queries into Lucene queries
#5 Run benchmark
#6 Print precision and recall measures
*/
