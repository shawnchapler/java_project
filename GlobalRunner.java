import java.util.*;
import java.io.*;
public class GlobalRunner{
   public static TreeMap<String, TreeMap<String, String>> masterMap = new TreeMap<>();
   public synchronized static void addmasterMap(String fileName, TreeMap<String, String> prettymap){
      masterMap.put(fileName,prettymap);
   }
   public static void main(String args[]) throws Exception{
      //start timing process
      long start = System.currentTimeMillis();
      //look for files in input directory
      File[] theFiles= new File(args[0]).listFiles();
      //count files to generate threads
      Integer fileCount = new File(args[0]).list().length;
      //Output folder input
      String outputFolder = new String(args[1]);
      //output file name
      File outputFile = new File(outputFolder+"/output.txt");
      //capture words per page input
      int wpp = Integer.parseInt(args[2]);
     // start threads one for each file pass file and wpp
      for(File file : theFiles){
           MyThread theWorkers = new MyThread(file, wpp);
           theWorkers.start();
           if(theWorkers.isAlive()){
              theWorkers.join();
           }
      }
      //Generate file once all threads are done.  Start map compilation
      //Create variable to hold headerrow
      String headerRow = new String("Word");
      //Create variable to hold childkey from prettymaps
      String childKey = new String();
      //Create variable to hold childvalue from prettymaps
      String childValue = new String();
      //Create variable to hold iteration of values from prettymaps to append to final map
      String valueList = new String();
      //Create final map to create and merge in key/value pairs from prettymaps
      TreeMap<String, String> finalMap = new TreeMap<String, String>();
      //iterate through masterMap contains key=textfile, value=prettymap
      //generate header row
      for(Map.Entry<String, TreeMap<String, String>> entry : masterMap.entrySet()){
          headerRow += ", "+entry.getKey().toString();
          //generate list of keys and default values to initialize from childMap
          TreeMap<String, String> childMap = entry.getValue();
          for(String keys : childMap.keySet()){
             String values = "";
             finalMap.put(keys,values);
      }
      }
      for(Map.Entry<String, TreeMap<String, String>> entry : masterMap.entrySet()){
        //iterate through prettymaps and match values to existing keys
             TreeMap<String, String> childMap = entry.getValue();
             //System.out.println(childMap);
             Iterator<Map.Entry<String, String>> fm = finalMap.entrySet().iterator();
             while(fm.hasNext()){
                 Map.Entry<String, String> masters = fm.next();
                 String masterkey = masters.getKey();
                 String mastervalue = masters.getValue();
                 //check if childmap contains masterkey value if true, gets mastervalue string and appends childMap values
                 if (childMap.containsKey(masterkey)){
                    for(Map.Entry<String, String> entry2 : childMap.entrySet()){
                      childKey=entry2.getKey();
                      if(childKey.equals(masterkey)){
                         childValue = mastervalue+", "+entry2.getValue().toString();
                         finalMap.put(masterkey, childValue);  
                      }   
                    }
                 }else {
                    //if masterkey not in childmap, formats comma space and addts to finalMap
                    childValue = mastervalue+", ";
                    finalMap.put(masterkey, childValue);
                  }  
             }
      }
      //start file write process
      FileWriter fileWriter = new FileWriter(outputFile);
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      //output header row
      bufferedWriter.write(headerRow);
      bufferedWriter.newLine();
      //output detail lines from map
      for (Map.Entry<String, String> finalentry: finalMap.entrySet()){
                String finalText =finalentry.getValue().toString();
                //write to a file 
                bufferedWriter.write(finalentry.getKey()+finalText);
                bufferedWriter.newLine();
      }
      bufferedWriter.close();
      long end = System.currentTimeMillis();
      System.out.println(end-start+" milliseconds");
  }
}
class MyThread extends Thread{
      private File file; 
      private int wpp;
      private String fileName;
      public TreeMap <String, String> prettymap;
      public MyThread(File file, int wpp){
         this.file = file;
	 this.wpp = wpp;
         }
      public void run(){
         try{
            Scanner inputFile = new Scanner(file, "UTF-8");
            fileName = new String(file.getName());
            TreeMap<String, Set<Integer>> map = new TreeMap<String, Set<Integer>>();
            int characters = 0;
            int page = 0 ;
            Set<Integer> set = new TreeSet<Integer>();
            prettymap = new TreeMap<String, String>();
            while(inputFile.hasNext()){
              String word = inputFile.next();
              characters+=word.length();
              page = (characters+wpp)/(wpp-1);
            //replace all non-alpha numeric charactes
              word = word.toLowerCase();
              //check map to see if word exists.  if exists retrieve set and add new page
              if (map.containsKey(word)){
                   set = map.get(word);
                   set.add(page);
                   map.put(word, set);
              }else{
                   set = new TreeSet<Integer>(); 
                   set.add(page);
                   map.put(word, set);
              }
           }
          //covert value to string. 
           for (Map.Entry<String, Set<Integer>> entry: map.entrySet()){
                String text =entry.getValue().toString();
                //remove parenthesis
                String prettytexta = text.substring(1,text.length()-1);
                //replace commas with colon separators
                String prettytext = prettytexta.replaceAll(", ",":");
                String keyvalue = entry.getKey().toString();
                //return formatted key/value pairs to prettymap
                String prettykey = keyvalue+","+prettytext;
                prettymap.put(keyvalue,  prettytext);
          }
        }catch (Exception e){
           System.out.println(e);
         }
         //write back to public GlobalRunner.addMasterMap()
         GlobalRunner.addmasterMap(fileName,prettymap);
        }
}
