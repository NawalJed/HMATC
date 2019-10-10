
/*
 * This java program demonstrates the pre-processing phase of the HMATC model.
 * It required to set the MULAN and (WEKA 3.7.12) jar files in the class path. 
 * It required this version of WEKA because it contains the (setStopwordsHandler) method in the 
 * (StringToWordVector) class.
 */

package HMATC_Model;


import java.io.File;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;
import weka.core.stemmers.SnowballStemmer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;




public class PreprocessingPhase 
{

	public static Instances processedData = null;   //Define an instance object to store the dataset after pre-processing


	public static void main(String[] args) 
	{

		loadData("RawDataset.arff");		//--Load the raw data to be preprocessed

	}


	/** loadData method to read and preprocessed the require dataset*/
	public static void loadData(String Filename) {

		try {

			/** Read the raw data as an ARFF file */
			ArffLoader loader = new ArffLoader();
			loader.setSource(new File(Filename));
			Instances rawData = loader.getDataSet();


			/** ------ Pre-processing task involves (Tokenization, Stemming, and Stop-word removal) ------*/

			/** Create an object from MyStopWordsHandler class to deal with the stop-word list and removing them*/
			MyStopWordsHandler stopWord = new MyStopWordsHandler("StopWordList.txt");

			/** Create an object from WordTokenizer class to make the Tokenization task*/
			WordTokenizer wt = new WordTokenizer();
			wt.setDelimiters(" ");

			/** Create an object from SnowballStemmer class to stem the words using this stemmer*/
			SnowballStemmer stemm = new SnowballStemmer();
			stemm.setStemmer("arabic");
			System.out.println(stemm.getStemmer());


			//-----------------------------------------//

			System.out.println("Print the summary about the raw dataset ");		
			System.out.println(rawData.toSummaryString());


			/** Create an object from StringToWordVector class and setting it with the required inputs and parameters*/
			StringToWordVector STWfilter = new StringToWordVector(11000);
			STWfilter.setInputFormat(rawData);       //---Set the input dataset that need to be preprocessed.
			STWfilter.setTokenizer(wt);                //---Set the tokenizer algorithm to use.     
			STWfilter.setStopwordsHandler(stopWord);     //---Set the StopwordsHandler object. 
			STWfilter.setStemmer(stemm);                   //---Set the Stemmer object. 
			STWfilter.setOutputWordCounts(true);             //Set the outputWordCounts to true if word counts should be output.
			STWfilter.setWordsToKeep(11000);                   //---Set the number of words to be resulted after the pre-processing phase. 	
			
			
			/** These parameters are setting to true if we want to represent 
			 * the resulted features using (TF-IDF) weighting scheme. */
			STWfilter.setTFTransform(true);
			STWfilter.setIDFTransform(true);
			


			/** Apply the StringToWordVector filter (STWfilter) on the dataset*/
			processedData = Filter.useFilter(rawData, STWfilter);
			System.out.println("Total number of the resulted words (features)= " + STWfilter.getOutputWordCounts());


			/** Save the preprocessed dataset as ARFF file */
			ArffSaver saverArff = new ArffSaver();
			saverArff.setInstances(processedData); 
			saverArff.setFile(new File("arff"));
			saverArff.writeBatch();


			/** Save the preprocessed dataset as CSV file */
			CSVSaver SaverCsv = new CSVSaver();
			SaverCsv.setInstances(processedData); 
			SaverCsv.setFile(new File("csv"));
			SaverCsv.writeBatch();

		} catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

}
