import java.util.*;
import java.io.*;
public class Index{
   public static void main(String args[]) throws Exception{
      //start timing process
      long start = System.currentTimeMillis();
      //look for files in input directory
      File[] theFiles= new File(args[0]).listFiles();
      for (File file : theFiles){
        if (file.isFile()){
           Scanner inputFile = new Scanner(file, "UTF-8");
           String fileName = new String(file.getName());
           String outputFolder = new String(args[1]);
           TreeMap<String, Set<Integer>> map = new TreeMap<String, Set<Integer>>();
           int characters = 0;
           //convert user input characters per page to int
           int wpp = Integer.parseInt(args[2]);
           int page = 0 ;
           Set<Integer> set = new TreeSet<Integer>();
           while(inputFile.hasNext()){
              String word = inputFile.next();
              //count characters
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
           //trim extension from inputfile
           String shortfileName = fileName.substring(0, fileName.lastIndexOf("."));
           String outputfileName = outputFolder+"/"+shortfileName+"_output.txt";
      
           //write to a file 
           FileWriter fileWriter = new FileWriter(outputfileName);
           BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      
           for (Map.Entry<String, Set<Integer>> entry: map.entrySet()){
                String text =entry.getValue().toString();
                //Strip brackets from hashset list
                String prettytext = text.substring(1,text.length()-1);
                //write to a file 
                bufferedWriter.write(entry.getKey()+" "+prettytext);
                bufferedWriter.newLine();
           }
           bufferedWriter.close();
        }
      } 
      long end = System.currentTimeMillis();
      System.out.println(end-start+" milliseconds");
  }
}
