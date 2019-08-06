

/*
 * This java program demonstrates the feature selection phase of the HMATC model.
 * It required to set the MULAN and WEKA jar files in the class path of this package. 
 */

package HMATC_Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import mulan.data.MultiLabelInstances;
import mulan.dimensionalityReduction.BinaryRelevanceAttributeEvaluator;
import mulan.dimensionalityReduction.Ranker;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ChiSquaredAttributeEval;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;



public class FeatureSelectionPhase 
{



	public static void main(String[] args) throws Exception 
	{ 

		try {


			/** Read the dataset (after pre-processing phase) to do the feature selection task */		
			Reader reader = new InputStreamReader(new FileInputStream("arff"), "UTF-8");
			BufferedReader BufferedR = new BufferedReader(reader);
			Instances dataset = new Instances(BufferedR);
			BufferedR.close();  

			/** Get the XML file associated with the dataset to define the hierarchical structure of the labels */
			MultiLabelInstances  mlData = new MultiLabelInstances(dataset, "xml");

			/** Print the dataset information */
			System.out.println("Dataset Information");
			System.out.println( "Number of Attributes = "+ dataset.numAttributes());
			System.out.println( "Number of Instances = "+ mlData.getNumInstances());
			System.out.println( "Number of Labels = "+ mlData.getNumLabels());



			/** ----- Feature selection task---- */

			ASEvaluation ase = new ChiSquaredAttributeEval(); //feature ranking method

			//---Binary Relevance (BR) problem transformation method -----//
			BinaryRelevanceAttributeEvaluator BReval = new BinaryRelevanceAttributeEvaluator(ase, mlData, "avg", "none", "eval");  



			/** Applying ranker to rank features based on their relevance to the labels */
			Ranker r = new Ranker(); 
			int[] result = r.search(BReval, mlData); 
			System.out.println(Arrays.toString(result)); 

			final int NUM_TO_KEEP = 4000;   //To select the top ranked 4000 features
			int[] toKeep = new int[NUM_TO_KEEP + mlData.getNumLabels()]; 
			System.arraycopy(result, 0, toKeep, 0, NUM_TO_KEEP); 
			int[] labelIndices = mlData.getLabelIndices(); 
			System.arraycopy(labelIndices, 0, toKeep, NUM_TO_KEEP, mlData.getNumLabels()); 

			Remove filterRemove = new Remove();  // To remove unselected features
			filterRemove.setAttributeIndicesArray(toKeep); 
			filterRemove.setInvertSelection(true); 
			filterRemove.setInputFormat(mlData.getDataSet()); 
			Instances filtered = Filter.useFilter(mlData.getDataSet(), filterRemove); 
			MultiLabelInstances mlFiltered = new MultiLabelInstances(filtered, mlData.getLabelsMetaData()); 

			System.out.println(mlFiltered.getNumLabels());
			System.out.println(mlFiltered.getNumInstances());



			/** Save the selected features into ARFF file */
			Instances savedIns=mlFiltered.getDataSet();
			ArffSaver saver=new ArffSaver();
			saver.setInstances(savedIns);
			saver.setFile(new File("SelectedFeatures.arff"));
			saver.writeBatch();
			System.out.println("The file was saved successfully");


		} catch (Exception e) 
		{ 
			e.printStackTrace(); 
		} 

	} 



}
