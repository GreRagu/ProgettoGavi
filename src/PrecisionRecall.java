
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.lucene.benchmark.quality.Judge;
import org.apache.lucene.benchmark.quality.QualityBenchmark;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.QualityQueryParser;
import org.apache.lucene.benchmark.quality.QualityStats;
import org.apache.lucene.benchmark.quality.trec.TrecJudge;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;
import org.apache.lucene.benchmark.quality.utils.SimpleQQParser;
import org.apache.lucene.benchmark.quality.utils.SubmissionReport;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import plot.Plot;
import plot.Plot.Line;

// From appendix C

/* Codice preso da Lucene
   contrib/benchmark sources */

public class PrecisionRecall {

	private ArrayList<Double> precision;
	private ArrayList<Double> avgPrecision;
	private ArrayList<Double> recall;
	private ArrayList<Double> rprecision10;
	private ArrayList<Double> rprecision15;
	private ArrayList<Double> rprecision5;
	private String s;
	private MyModel M;

	public PrecisionRecall(String path, MyModel M) {
		this.s = path;
		this.M = M;
		System.out.println(M.getModelString());
	}


	public void start() throws Exception {
		File topicsFile = new File("./dataset/clinical_dataset/Topics/MyQuery/Topics2015A.txt");
		File qrelsFile = new File("./dataset/clinical_dataset/Qrels/qrels-sampleval-2015.txt");

		FSDirectory open = FSDirectory.open(Paths.get(s));

		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(open));
		System.out.println(M.getModelSymilarity());
		searcher.setSimilarity(M.getModelSymilarity());
		System.out.println(searcher.getSimilarity(true));

		String docNameField = "filename";

		// L'output di precison e recall viene stampato su file
		PrintWriter logger = new PrintWriter(new FileOutputStream("output.txt"), true);

		// lettura delle query dal file topicFile:
		TrecTopicsReader qReader = new TrecTopicsReader();
		QualityQuery qqs[] = qReader.readQueries(new BufferedReader(new FileReader(topicsFile)));

		System.out.println("numero componenti topic file: " + qqs.length);

		// Lettura del qrelsFile di TREC
		Judge judge = new TrecJudge(new BufferedReader(new FileReader(qrelsFile)));

		if (judge.equals(null)) {

			System.out.println("JUDGE = NULL");

		}

		// Controllo match tra query e qrels:
		judge.validateData(qqs, logger);

		// System.out.println("step 2 ");

		// Creazione di un parser per trasformare le query nelle Lucene query
		QualityQueryParser qqParser = new SimpleQQParser("description", "contents");

		QualityBenchmark qrun = new QualityBenchmark(qqs, qqParser, searcher, docNameField);

		SubmissionReport submitLog = null;
		System.out.println("step 3 ");

		// esecuzione del benchmark
		QualityStats stats[] = qrun.execute(judge, submitLog, logger);
		System.out.println("step 4 ");

		// Stampa delle misure di precision e recall
		QualityStats avg = QualityStats.average(stats);
		avg.log("SUMMARY", 2, logger, "  ");
		open.close();

		doGraph();

	}

	public void doGraph() {

		ArrayList<Double> recallLevel = getRecallLevel(); // recallLevel (0.33, 0.66, 1.0)
		initVar();

		// Array range(0,num_queries) for plot these numbers
		ArrayList<Double> num_queries = new ArrayList<Double>();

		for (int i = 0; i < precision.size(); i++) {
			// System.out.println("precision q "+i+" : "+precision.get(i));
			num_queries.add(i + 1.0);
		}

		System.out.println("Plotting precision graph");
		// Precision
		Plot plot = Plot
				.plot(Plot.plotOpts().title("Precision graph").width(1000).height(600).legend(Plot.LegendFormat.TOP))
				.xAxis("Query #", Plot.axisOpts().format(Plot.AxisFormat.NUMBER_INT).range(0, num_queries.size()))
				.yAxis("Precision", Plot.axisOpts().range(0, getMax(precision)))
				.series(".", Plot.data().xy(num_queries, precision), Plot.seriesOpts().line(Line.NONE)
						.marker(Plot.Marker.COLUMN).color(Color.BLUE).markerColor(Color.BLUE));
		try {
			plot.save(new java.io.File(".").getCanonicalPath() + "/results/" + M.getModelString() + "/ precision", "png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Plotting recall graph");
		// Recall
		plot = Plot.plot(Plot.plotOpts().title("Recall graph").width(1000).height(600).legend(Plot.LegendFormat.TOP))
				.xAxis("Query #", Plot.axisOpts().format(Plot.AxisFormat.NUMBER_INT).range(0, num_queries.size()))
				.yAxis("Recall", Plot.axisOpts().range(0, getMax(recall)))
				.series(".", Plot.data().xy(num_queries, recall), Plot.seriesOpts().line(Line.NONE)
						.marker(Plot.Marker.COLUMN).color(Color.BLUE).markerColor(Color.BLUE));
		try {
			plot.save(new java.io.File(".").getCanonicalPath() + "/results/" + M.getModelString() + "/recall", "png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Plotting R-Precision (5)");
		// R-Precision 5
		plot = Plot.plot(Plot.plotOpts().title("R-Precision 5").width(1000).height(600).legend(Plot.LegendFormat.TOP))
				.xAxis("Query #", Plot.axisOpts().format(Plot.AxisFormat.NUMBER_INT).range(0, num_queries.size()))
				.yAxis("Precision", Plot.axisOpts().range(0, getMax(rprecision5)))
				.series(".", Plot.data().xy(num_queries, rprecision5), Plot.seriesOpts().line(Line.NONE)
						.marker(Plot.Marker.COLUMN).color(Color.BLUE).markerColor(Color.BLUE));
		try {
			plot.save(new java.io.File(".").getCanonicalPath() + "/results/" + M.getModelString() + "/rprecision5", "png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Plotting R-Precision (10)");
		// R-Precision 10
		plot = Plot.plot(Plot.plotOpts().title("R-Precision 10").width(1000).height(600).legend(Plot.LegendFormat.TOP))
				.xAxis("Query #", Plot.axisOpts().format(Plot.AxisFormat.NUMBER_INT).range(0, num_queries.size()))
				.yAxis("Precision", Plot.axisOpts().range(0, getMax(rprecision10)))
				.series(".", Plot.data().xy(num_queries, rprecision10), Plot.seriesOpts().line(Line.NONE)
						.marker(Plot.Marker.COLUMN).color(Color.BLUE).markerColor(Color.BLUE));
		try {
			plot.save(new java.io.File(".").getCanonicalPath() + "/results/" + M.getModelString() + "/rprecision10", "png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Plotting R-Precision (15)");
		// R-Precision 15
		plot = Plot.plot(Plot.plotOpts().title("R-Precision 15").width(1000).height(600).legend(Plot.LegendFormat.TOP))
				.xAxis("Query #", Plot.axisOpts().format(Plot.AxisFormat.NUMBER_INT).range(0, num_queries.size()))
				.yAxis("Precision", Plot.axisOpts().range(0, getMax(rprecision15)))
				.series(".", Plot.data().xy(num_queries, rprecision15), Plot.seriesOpts().line(Line.NONE)
						.marker(Plot.Marker.COLUMN).color(Color.BLUE).markerColor(Color.BLUE));
		try {
			plot.save(new java.io.File(".").getCanonicalPath() + "/results/" + M.getModelString() + "/rprecision15", "png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Plotting Avg Precision");
		// Avg Precision
		plot = Plot.plot(Plot.plotOpts().title("Avg Precision").width(1000).height(600).legend(Plot.LegendFormat.TOP))
				.xAxis("Recall Level", Plot.axisOpts().range(0, 1))
				.yAxis("Avg Precision", Plot.axisOpts().range(0, getMax(precision)))
				.series(".", Plot.data().xy(recallLevel, precision), Plot.seriesOpts().line(Line.NONE)
						.marker(Plot.Marker.COLUMN).color(Color.BLUE).markerColor(Color.BLUE));
		try {
			plot.save(new java.io.File(".").getCanonicalPath() + "/results/" + M.getModelString() + "/Avg Precision", "png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Plotting finished");

	}

	/**
	 * Funzione che estrae precision - recall - avgPrecision - precision at
	 * 
	 * 
	 * 
	 **/
	private void initVar() {
		// TODO Auto-generated method stub

		// System.out.println("INIT VAR");

		precision = new ArrayList<Double>();
		recall = new ArrayList<Double>();
		avgPrecision = new ArrayList<Double>();
		rprecision5 = new ArrayList<Double>();
		rprecision10 = new ArrayList<Double>();
		rprecision15 = new ArrayList<Double>();

		boolean summary = false;
		boolean getPrecision = true;
		boolean getRecall = true;
		boolean getRprecision = true;

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

			if (line.contains("SUMMARY")) {
				summary = true;
				getPrecision = false;
				getRecall = false;
				getRprecision = false;
			}

			if (line.contains("Average Precision:") && getPrecision) {
				sub = line.substring(line.length() - precisionOffset);
				// System.out.println(sub);

				precision.add(Double.parseDouble(sub));
			}

			if (line.contains("Recall:") && getRecall) {
				sub = line.substring(line.length() - precisionOffset);
				// System.out.println(sub);

				recall.add(Double.parseDouble(sub));
			}

			if (line.contains("Precision At 5:") && getRprecision) {
				sub = line.substring(line.length() - precisionOffset);
				// System.out.println(sub);

				rprecision5.add(Double.parseDouble(sub));
			}

			if (line.contains("Precision At 10:") && getRprecision) {
				sub = line.substring(line.length() - precisionOffset);
				// System.out.println(sub);

				rprecision10.add(Double.parseDouble(sub));
			}

			if (line.contains("Precision At 15:") && getRprecision) {
				sub = line.substring(line.length() - precisionOffset);
				// System.out.println(sub);

				rprecision15.add(Double.parseDouble(sub));
			}

			if (line.contains("Average Precision:") && summary) {
				sub = line.substring(line.length() - precisionOffset);
				// System.out.println(sub);

				avgPrecision.add(Double.parseDouble(sub));
			}

		}

	}

	/**
	 * Ottiene i livelli standard di recall
	 * 
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
	 * 
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
