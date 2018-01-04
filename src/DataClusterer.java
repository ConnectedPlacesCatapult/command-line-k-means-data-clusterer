import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 * Data clusterer tailored for k-Means data compilation using the Weka library. Given an input data file of standardised statistics,
 * the program will apply k-Means clustering for a specified k-value provided an argument at the command line. 
 * 
 * @author Rudi Ball
 * 
 * License:  This  program  is  free  software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the  Free Software Foundation; either version 3 of the License, or (at your
 * option)  any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 */
public class DataClusterer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length == 1){
			
			//Do exploration by printing out the attributes/features
			DataClusterer.runExploration(args[0]);
			
			
		} else if ((args.length == 2) || (args.length == 3)) {
			
			//Do Clustering
			DataClusterer.runKMeansClustering(args);
			
		} else {
			
			//Show usage instructions
			System.out.println("Usage:");
			System.out.println("java -jar DataClusterer [statistics_file.csv/arff] [number_of_clusters] [fields_to_filter]\n");
			System.out.println("Example:");
			System.out.println("Explore attributes: java -jar DataClusterer statistics.csv");
			System.out.println("Perform clustering: java -jar DataClusterer statistics.csv 8 Lat,Lon,Geography,Geography_Code,Date,Greenspace:_Local_Parks,Cycle_Hire_Loccations_Count,Foodagency:_Establishment_Count,Cycle_Hire_Locations_Count");
			System.out.println("\n* Please use an underscore as a space if the feature heading contains a space.");
			
		}	
	}
	
	/**
	 * Run exploration of the input file and list out the attributes.
	 * 
	 * @param filename
	 */
	private static void runExploration(String filename) {
		
		System.out.println("Exploration");
		System.out.println("Listing features:");
		
		
		//Does the file exist?
		DataClusterer.fileExists(filename);
		
		try {
			
			//Load CSV
			CSVLoader loader = new CSVLoader();
			loader.setSource(new File(filename));
			Instances data = loader.getDataSet();
			
			//List Attributes Available
			for (int k = 0 ; k < data.numAttributes(); k++){
				System.out.println("index: " + (k+1) + " \t " + data.attribute(k).name());
			}
			
		} catch (IOException e) {
			System.out.println("IO Exception: runExploration");
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Check file exists. If no file exists then exit.
	 * @param filename 
	 */
	private static void fileExists(String filename){
		
		//Does the file exist
		File f = new File(filename);
				
		if (!f.exists()){
			System.out.println("Error: The argument provided does not point to a file which exists.");
			System.exit(0);
		}
	}
	

	/**
	 * Method to compute the k-means clustering using default settings and Euclidean distance.
	 * 
	 * @param args
	 */
	private static void runKMeansClustering(String[] args){
		
		long starttime = System.currentTimeMillis();
		
		try {
			
			//Parse Input Params
			System.out.println("Running k-Means Clustering");
			System.out.println("Input File: " + args[0]);
			System.out.println("Number of Clusters: " + args[1]);
			
			//Does the file exist?
			DataClusterer.fileExists(args[0]);
			
			String removeColumns = "";
			
			if (args.length >= 3){
				System.out.println("Filter Columns: " + args[2]);
				removeColumns = args[2].replace(' ', '_');
			} else {
				System.out.println("Filter Columns: No features provided for filtering.");
			}
			
			String inputfile = args[0];
			String numberOfClusters = args[1];
			
			ArrayList<String> fields = new ArrayList<String>();
			
			System.out.println("****************************************************************");
			
			System.out.println("Loading Data...");
			//Pre-process the file so that we remove spaces
			//Create a temporary file and load this file for editing.
			StringBuffer outputbuffer = new StringBuffer();
			
			//Load CSV
			CSVLoader loader = new CSVLoader();
			loader.setSource(new File(inputfile));
			Instances data = loader.getDataSet();
			
			CSVLoader copyloader = new CSVLoader();
			copyloader.setSource(new File(inputfile));
			Instances copydata = copyloader.getDataSet();
			
			System.out.println("****************************************************************");
			System.out.println("Input Feature List:");
			
			//List Attributes Available
			for (int k = 0 ; k < copydata.numAttributes(); k++){
				
				System.out.println("index: " + (k+1) + " \t " + copydata.attribute(k).name());
				fields.add(copydata.attribute(k).name().replace(' ', '_'));
			}
			
			//Filter
			String[] filtercolumns = removeColumns.split(",");
			
			for (String f : filtercolumns){
			
				if (fields.contains(f)){
				
					int index = DataClusterer.lookupIndex(fields, f);
					
					if (index >= 0){
						System.out.println("Filtering: " + f); // + " [" + index + "]");
						data.deleteAttributeAt(index);
						fields.remove(index);
					} 
				}
			}
			
			System.out.println("****************************************************************");
			System.out.println("Filtered Feature List:");
			//Print Output Feature List
			for (int d = 0 ; d < fields.size(); d++){
				System.out.println("index: " + (d+1) + " \t " + fields.get(d));
			}
			
			System.out.println("****************************************************************");
			System.out.println("Data Details");
			System.out.println("Number of Attributes: " + data.numAttributes());
			System.out.println("Running Clusterer."); 
			
			String[] options = new String[5];
			options[0] = "-I";                 	//seed
			options[1] = "500";
			options[2] = "-N";					//number of clusters
			options[3] = numberOfClusters;	
			options[4] = "-O";
			
			
			StringBuffer headings = new StringBuffer();
			
			//Append the clusters
			//System.out.println(clusterer.toString());
			for (int col = 0 ; col < copydata.numAttributes(); col++){
			
				if (col < (copydata.numAttributes() - 1)){
					headings.append("\"").append(copydata.attribute(col).name()).append("\"").append(",");
				} else {
					headings.append("\"").append(copydata.attribute(col).name()).append("\"");
				}
			}
			
			headings.append(",\"").append("Cluster").append("\"").append("\n");
			outputbuffer = new StringBuffer(headings);
			
			//Headings
			//System.out.println(headings);
			//Run Clusterers
			SimpleKMeans clusterer = new SimpleKMeans();   // new instance of clusterer
			clusterer.setOptions(options);     // set the options
			clusterer.buildClusterer(data);    // build the clusterer
			
			
			//Clustering output
			String meansoutputfilename = inputfile.substring(0, inputfile.indexOf('.')) + ".centroids.txt";
			DataClusterer.writeFile(new StringBuffer(clusterer.toString()), meansoutputfilename);
			System.out.println("Writing means output: " + meansoutputfilename);
			
			//#####################################################################################################33
			int[] clusterassigned = clusterer.getAssignments();
			
			for (int i = 0; i < clusterassigned.length; i++){
				outputbuffer.append(copydata.instance(i)).append(',').append(clusterassigned[i]).append("\n");
			}
			
			//Construct the outputfilename
			String outputfilename = inputfile.substring(0, inputfile.indexOf('.')) + ".clusters.csv";
			
			//Write output
			System.out.println("Writing output: " + outputfilename);
			DataClusterer.writeFile(outputbuffer, outputfilename);
			
			System.out.println("Finished.\nTook " + ((double) (System.currentTimeMillis() - starttime))/1000 + " seconds.");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method forces case insensitive comparison of fields lookup using the attribute. Fields as an arraylist 
	 * represents the index of attributes.
	 * 
	 * @param fields columns or headers
	 * @param attribute
	 * @return index
	 */
	private static int lookupIndex(ArrayList<String> fields, String attribute) {
		
		for (int m = 0 ; m < fields.size(); m++){
			
			if (fields.get(m).equalsIgnoreCase(attribute)){
				return m;
			}
		}
		
		return -1;
	}

	/**
	 * Write text to filename. Write once StringBuffer.
	 * 
	 * @param text content to write.
	 * @param filename path and file to write to.
	 */
	private static void writeFile(StringBuffer text, String filename){
		
		try {
			
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));  
			out.write(text.toString());  
			out.flush();  
			out.close();
		
		} catch (IOException e) {
			System.err.println("Exception: Failed to write file: " + filename + " with data length: " + text.length());
			e.printStackTrace();
		}  
	}
	
	
}//end of class
