import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class LZ78Codec {

    public class LZ78Pair {
        private int index;
        private char endsWithCharacter;

        public LZ78Pair(int index, char endsWithCharacter) {
            this.index = index;
            this.endsWithCharacter = endsWithCharacter;
        }

        public int getIndex() {
            return index;
        }
        
        public char getEndsWith() {
            return endsWithCharacter;
        }

        @Override
        public String toString() {
            return "<" + index + "," + endsWithCharacter + ">";
        }
    }

    private HashMap<Integer, String> dictionary;
    private ArrayList<String> characters;  //instantiated on readFile
    private String outputFileName;

    public static void main(String[] args) {
        LZ78Codec lz78Codec = new LZ78Codec("output.txt");
        try {
            lz78Codec.readFile("test.txt");
            lz78Codec.encode();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public LZ78Codec(String outputFileName) {
        this.outputFileName = outputFileName;
        this.dictionary = new HashMap<Integer, String>();
        this.characters = new ArrayList<String>();
        //in the dictionary, we default the key index 0 to "", so that new characters add to empty string
        this.dictionary.put(0,"");
    }

    /**
     * Reads the file contents character by character and stores them 
     * in an ArrayList internally, in the LZ78 object.
     * @param path location of the file
     */
    public void readFile(String path) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        
        int i;
        while((i=bufferedReader.read())!=-1){  
            this.characters.add(String.valueOf((char)i));
        }  
        bufferedReader.close();
    }

    public void encode() throws Exception{

        //stringBuilder will acts as the symbols reader from the input
        int i = 0;
        int inputInitialSize = characters.size();
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileName))) {
            while (i < inputInitialSize-1) {

                //read the correct number of symbols from the input
                stringBuilder.append(characters.get(i));
                while (dictionary.values().contains(stringBuilder.toString())) {
                    i++;
                    stringBuilder.append(characters.get(i));
                }

                
                //now check if length of stringBuilder is 1, then we have a new symbol
                addNewElementInDictionary(stringBuilder.toString());
                
                int index =  getIndexOfValue(stringBuilder.substring(0, stringBuilder.length()-1));
                char endsWithCharacter = stringBuilder.charAt(stringBuilder.length()-1);
                if (index == -1) {
                    throw new Exception();
                }

                //write output to a the outputFileName file
                LZ78Pair outputPair = new LZ78Pair(index, endsWithCharacter);
                bufferedWriter.append(outputPair.toString());

                i++;
                stringBuilder.setLength(0);
            }
            //finally save the dictionary to a file too            
        } catch (Exception e) {
            throw new Exception();
        }
            
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("dictionary.txt"));) {
            bufferedWriter.write(dictionary.toString());
        } catch (Exception e) {

        }
    }


    /**
     * Adds a new sequence of symbols in the dictionary
     * @param sequence string to be added
     */
    private void addNewElementInDictionary(String sequence) {
        this.dictionary.put(this.dictionary.size(), sequence);
    }


    /**
     * Finds the matching pattern in the dictionary to link
     * @param value sequence to find inside the dictionary HashMap
     * @return the key value of the {@code value} parameter 
     */
    private int getIndexOfValue(String value) {

        for (Map.Entry<Integer, String> entry : this.dictionary.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        return -1;
    }

    public void printCharacters() {
        System.out.print("Content characters: ");
        for (String character : characters) {
            System.out.print(character);
        }
        System.out.println();
        System.out.println("Length: " + characters.size() + " chatacters");
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

}