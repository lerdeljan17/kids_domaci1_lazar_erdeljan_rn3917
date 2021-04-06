package scanners;

import lombok.AllArgsConstructor;
import main.ApplicationProperties;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.RecursiveTask;

@AllArgsConstructor
public class FileScannerThread extends  RecursiveTask<Map<String,Integer>> {

    private List<File> files;
    private  Map<String,Integer> wordCountMap ;

    public FileScannerThread(List<File> filesToScan) {
        this.files = new ArrayList<>(filesToScan);
        this.wordCountMap = new HashMap<>();
    }

    @Override
    protected Map<String, Integer> compute() {


        System.out.println("computing for" + files.toString());
        List<File> dividedFiles = divideFiles(files);



        if(files.size() > 0 ){


            FileScannerThread left = new FileScannerThread(files);
            FileScannerThread right = new FileScannerThread(dividedFiles);
            left.fork();

            Map<String,Integer> rightResult = right.compute();
            Map<String,Integer> leftResult = left.join();

            wordCountMap.putAll(rightResult);


            for (Map.Entry<String,Integer> entry : leftResult.entrySet()) {
                if(wordCountMap.containsKey(entry.getKey())){
//                    entry.setValue(entry.getValue() + leftResult.get(entry.getKey()));
                    Integer sum = wordCountMap.get(entry.getKey()) + entry.getValue();
                    wordCountMap.put(entry.getKey(),sum);
                }else {
                    wordCountMap.put(entry.getKey(),entry.getValue());
                }

            }

            System.out.println("rezzz2" + wordCountMap);
            return wordCountMap;

        }else {

            scanFiles(dividedFiles);
            System.out.println("rezzz" + wordCountMap);
            return wordCountMap;
        }


    }

    private  void scanFiles(List<File> filesToscan){

        for (File file : filesToscan) {
            countWords(file.getAbsolutePath());
        }

    }

    private void countWords(String filename)
    {
        Scanner file= null;
        try {
            file = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(file.hasNext()){
            String word=file.next();
            word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase();
            if(!ApplicationProperties.getInstance().getKeywords().contains(word)){
                continue;
            }
            Integer count=wordCountMap.get(word);
            if(count!=null)
                count++;
            else
                count=1;
            wordCountMap.put(word,count);
        }
        file.close();
    }

    private List<File> divideFiles(List<File> files) {

        int fileLengthSum = 0;
        List<File> dividedFiles = new ArrayList<>();

        for (File file : files) {
            fileLengthSum+=file.length();

            dividedFiles.add(file);

            if (fileLengthSum > ApplicationProperties.getInstance().getFile_scanning_size_limit()){
                break;
            }
        }

        files.removeAll(dividedFiles);


        return dividedFiles;

    }


//    public static void main(String[] args)
//    {
//        Map<String,Integer> words=new HashMap<String, Integer>();
//        countWords("src/main/resources/test1/test/corpus_1/corpus_lala");
//
//        List<File> filess = new ArrayList<>();
//        filess.add(new File("src/main/resources/test1/test/corpus_1/corpus_lala"));
//        filess.add(new File("src/main/resources/test1/test/corpus_21/file21"));
//        scanFiles(filess);
//        System.out.println(wordCountMap);
//    }

}
