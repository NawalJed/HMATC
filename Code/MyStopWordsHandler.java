package HMATC_Model;

import weka.core.stopwords.StopwordsHandler;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;



public class MyStopWordsHandler implements StopwordsHandler
{

	BufferedReader bufferedReader ;
	String text;
	List<String> myStopWords= new ArrayList<>();



	public MyStopWordsHandler(String filename) 
	{
		// TODO Auto-generated constructor stub
		try{	
			Reader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8");
			bufferedReader = new BufferedReader(reader);
			String strLine;

			//Read File Line By Line
			while ((strLine = bufferedReader.readLine()) != null)  
			{
				myStopWords.add(strLine);
			}

			bufferedReader.close(); 
			System.out.println(myStopWords.size());

			for(int i=0;i<5;i++)
			{
				System.out.println(myStopWords.get(i));
			}

		} catch (Exception e) 
		{
			e.printStackTrace();
		}

	}



	/** isStopword method to check if the given word is available in the stop-word list or not*/
	public boolean isStopword(String word) {
		// TODO Auto-generated method stub
		return myStopWords.contains(word);
	}

}
