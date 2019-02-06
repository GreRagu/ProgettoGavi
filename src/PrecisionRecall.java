
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.benchmark.quality.*;
import org.apache.lucene.benchmark.quality.utils.*;
import org.apache.lucene.benchmark.quality.trec.*;


/* This code was extracted from the Lucene
   contrib/benchmark sources */

public class PrecisionRecall {


	public static void main(String[] args) throws Throwable {

		/*ReadXMLFile reader = new ReadXMLFile("./dataset/clinical_dataset/Topics/MyQuery/Topics2016.txt");
		for (int i = 1; i <= 30; i++) {

			reader.SearchQuery(i);

		}*/

		File topicsFile = new File("./dataset/clinical_dataset/Topics/MyQuery/Topics2015A.txt");
		File qrelsFile = new File("./dataset/clinical_dataset/Qrels/qrels-treceval-2015.txt");

		String s = "./index_pmc";
		FSDirectory open = FSDirectory.open(Paths.get(s));

		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(open));

		String docNameField = "filename";

		PrintWriter logger = new PrintWriter(System.out, true);

		TrecTopicsReader qReader = new TrecTopicsReader(); 			// #1
		QualityQuery qqs[] = qReader.readQueries( 					// #1
				new BufferedReader(new FileReader(topicsFile))); 	// #1

		System.out.println("numero componenti topic file: " + qqs.length);

		Judge judge = new TrecJudge(new BufferedReader( 			// #2
				new FileReader(qrelsFile))); 						// #2

		if (judge.equals(null)) {

			System.out.println("JUDGE = NULL");

		}

		judge.validateData(qqs, logger); 							// #3
		// System.out.println("step 2 ");
		QualityQueryParser qqParser = new SimpleQQParser("title", "contents"); // #4

		QualityBenchmark qrun = new QualityBenchmark(qqs, qqParser, searcher, docNameField);
		SubmissionReport submitLog = null;
		// System.out.println("step 3 ");

		QualityStats stats[] = qrun.execute(judge, // #5
				submitLog, logger);
		// System.out.println("step 4 ");

		QualityStats avg = QualityStats.average(stats); // #6
		avg.log("SUMMARY", 2, logger, "  ");
		open.close();
	}
}

/*
 * #1 Read TREC topics as QualityQuery[] 
 * #2 Create Judge from TREC Qrel file 
 * #3 Verify query and Judge match 
 * #4 Create parser to translate queries into Lucene queries 
 * #5 Run benchmark #6 Print precision and recall measures
 */
