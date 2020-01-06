
/*
 * This java program demonstrates the classification and evaluation phases of the HMATC model.
 * It required to set the MULAN and WEKA jar files in the class path. 
 */

package HMATC_Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import mulan.classifier.meta.HOMER;
import mulan.classifier.meta.HierarchyBuilder;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import mulan.evaluation.measure.ExampleBasedAccuracy;
import mulan.evaluation.measure.HammingLoss;
import mulan.evaluation.measure.HierarchicalLoss;
import mulan.evaluation.measure.Measure;
import mulan.evaluation.measure.MicroFMeasure;
import mulan.evaluation.measure.MicroPrecision;
import mulan.evaluation.measure.MicroRecall;
import mulan.evaluation.measure.SubsetAccuracy;
import weka.classifiers.functions.SMO;
import weka.core.Instances;




public class ClassificationPhase 
{


	public static void main(String[] args) throws Exception 
	{ 

		try {


			/** Read the dataset (after feature selection phase) to do the classification task */		
			Reader reader = new InputStreamReader(new FileInputStream("arff"), "UTF-8");
			BufferedReader BufferedR = new BufferedReader(reader);
			Instances dataset = new Instances(BufferedR);
			BufferedR.close();  
			System.out.println("Loading the dataset...");


			/** Get the XML file associated with the dataset to define the hierarchical structure of the labels */			
			MultiLabelInstances  mlData = new MultiLabelInstances(dataset, "xml");


			/** Print the dataset information */
			System.out.println("Dataset Information");
			System.out.println( "Number of Attributes = "+ dataset.numAttributes());
			System.out.println( "Number of Instances = "+ mlData.getNumInstances());
			System.out.println( "Number of Labels = "+ mlData.getNumLabels());



			/**  Evaluation Metrics */
			Evaluator eval = new Evaluator(); 
			MultipleEvaluation mresults; 
			int LabelNum=mlData.getNumLabels();   // get number of labels of the used dataset

			List<Measure> measuresList = new ArrayList<Measure>();
			measuresList.add(new HammingLoss());
			measuresList.add(new HierarchicalLoss(mlData) );
			measuresList.add(new ExampleBasedAccuracy() );
			measuresList.add(new SubsetAccuracy());
			measuresList.add(new MicroPrecision(LabelNum));
			measuresList.add(new MicroRecall(LabelNum));
			measuresList.add(new MicroFMeasure(LabelNum));


			/** Do the hierarchical classification task using HOMER algorithm */
			HOMER hmr=new HOMER(new LabelPowerset(new SMO()),8,HierarchyBuilder.Method.BalancedClustering);


			/** Evaluate the model performance using 5 folds cross-validation approach */
			mresults=eval.crossValidate(hmr, mlData, measuresList, 5);


			/** Print the evaluation results */
			System.out.println("The evaluation results are the following");
			System.out.println("result="+ mresults.toString());



			/** Save the evaluation results to the text file */
			File Textfile = new File("Evaluation_results.txt");
			FileOutputStream FOStream = new FileOutputStream(Textfile);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(FOStream));
			bw.write("The evaluation results are the following");
			bw.newLine();
			bw.write(mresults.toString());
			bw.flush();
			bw.close();

		} catch (Exception e) 
		{ 
			e.printStackTrace(); 
		} 
	} 
}
