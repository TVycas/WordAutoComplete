package src;

import src.dictTree.DictionaryTree;

import java.io.*;

public class WordAutoComplete {

    /**
     * Loads words (lines) from the given file and inserts them into
     * a dictionary.
     *
     * @param f the file from which the words will be loaded
     * @return the dictionary with the words loaded from the given file
     * @throws IOException if there was a problem opening/reading from the file
     */
    static DictionaryTree loadWords(File f) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            String word;
            int pop = 100003;
            DictionaryTree d = new DictionaryTree();
            while ((word = reader.readLine()) != null) {
                d.insert(word, pop);
                pop--;
            }

            return d;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Loading dictionary ... ");
        if (args.length != 1) {
            System.out.println("Usage: src.WordAutoComplete wordPopularityFile");
            return;
        }
        DictionaryTree d = loadWords(new File(args[0]));
        System.out.println("done");

        System.out.println("Enter prefixes for prediction below.");

        try (BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.println("---> " + d.predict(fromUser.readLine(), 5
                ));
            }
        }
    }

}
