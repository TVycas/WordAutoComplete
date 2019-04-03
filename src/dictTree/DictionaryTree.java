package src.dictTree;

import java.util.*;
import java.util.function.BiFunction;

public class DictionaryTree {


    private Map<Character, DictionaryTree> children = new LinkedHashMap<>();
    private Optional<Integer> pop = Optional.empty();

    /**
     * Returns the popularity of the word.
     * If the word is not there, returns -1.
     */
    public int getPop() {
        return pop.orElse(-1);
    }

    public void setPop(int popularity) {
        pop = Optional.of(popularity);
    }

    /**
     * Inserts the given word into this dictionary. If the word already exists,
     * nothing will change.
     *
     * @param word the word to insert
     */
    public void insert(String word) {
        if (word.length() > 0) {
            boolean exists = false;
            Character firstLetter = word.charAt(0);

            if (word.length() > 1) {
                // Goes through the tree letter by letter
                for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
                    if (child.getKey().equals(firstLetter)) {
                        child.getValue().insert(word.substring(1));
                        exists = true;
                    }
                }
                // if it can not find the letter it creates a node to store it and calls the
                // insert function again
                if (!exists) {
                    children.put(firstLetter, new DictionaryTree());
                    children.get(firstLetter).insert(word.substring(1));
                }

            } else {
                for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
                    // If the word already exists do nothing. else insert it
                    if (child.getKey().equals(firstLetter)) {
                        exists = true;
                        if (!child.getValue().pop.isPresent())
                            child.getValue().setPop(-1);
                    }
                }

                if (!exists) {
                    // if the word does not exist create a word with a popularity of -1 (so that it
                    // would not interfere with the insert(word, popularity) function)
                    children.put(firstLetter, new DictionaryTree());
                    children.get(firstLetter).setPop(-1);
                }
            }
        }
    }

    /**
     * Inserts the given word into this dictionary with the given popularity. If the
     * word already exists, the popularity will be overwritten by the given value.
     *
     * @param word       the word to insert
     * @param popularity the popularity of the inserted word
     */
    public void insert(String word, int popularity) {
        if (word.length() > 0) {
            boolean exists = false;
            Character firstLetter = new Character(word.charAt(0));

            if (word.length() > 1) {
                for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
                    if (child.getKey().equals(firstLetter)) {
                        child.getValue().insert(word.substring(1), popularity);
                        exists = true;
                    }
                }

                if (!exists) {
                    children.put(firstLetter, new DictionaryTree());
                    children.get(firstLetter).insert(word.substring(1), popularity);
                }

            } else {
                for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
                    if (child.getKey().equals(firstLetter)) {
                        exists = true;
                        child.getValue().setPop(popularity);
                    }
                }

                if (!exists) {
                    children.put(firstLetter, new DictionaryTree());
                    children.get(firstLetter).setPop(popularity);
                }
            }
        }
    }

    /**
     * Removes the specified word from this dictionary. Returns true if the caller
     * can delete this node without losing part of the dictionary, i.e. if this node
     * has no children after deleting the specified word.
     *
     * @param word the word to delete from this dictionary
     * @return whether or not the parent can delete this node from its children
     */
    public boolean remove(String word) {
        boolean removed = false;
        char key = 0;

        // if the length of the word is 0, there is no word to be deleted so just return
        // false
        if (word.length() > 0) {
            Character firstLetter = word.charAt(0);
            // if the word is more than one symbol long, follow the branch with every letter
            // of the word in order
            if (word.length() > 1) {
                for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
                    if (child.getKey().equals(firstLetter)) {
                        // this recursively calls remove function of the children and stores the value
                        // to output whether or not the word was removed
                        removed = child.getValue().remove(word.substring(1));
                        // if the resulting tree has no children and is not storing any word, then it is
                        // safe to remove it
                        if (child.getValue().children.isEmpty() && !child.getValue().pop.isPresent())
                            key = child.getKey();

                    }
                }
                // the removing call needs to be outside the for loop because of
                // ConcurrentModificationExceptions
                if (key != 0)
                    children.remove(key);

            } else {
                for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
                    if (child.getKey().equals(firstLetter)) {
                        // Returns true if the word can be deleted and deletes the node of the last
                        // letter
                        if (child.getValue().children.isEmpty()) {
                            children.remove(child.getKey());
                            return true;
                        } else {
                            child.getValue().pop = Optional.empty();
                            return false;
                        }
                    }
                }
            }
        }

        return removed;
    }

    /**
     * Determines whether or not the specified word is in this dictionary.
     *
     * @param word the word whose presence will be checked
     * @return true if the specified word is stored in this tree; false otherwise
     */
    public boolean contains(String word) {
        // Only return true if popularity value is present
        if (word.length() == 0 && pop.isPresent())
            return true;

        // Follows the branch of the word until there are no letters to follow
        if (word.length() > 0) {
            Character firstLetter = word.charAt(0);

            for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
                if (child.getKey().equals(firstLetter)) {
                    return child.getValue().contains(word.substring(1));
                }
            }
        }

        return false;
    }

    /**
     * @param prefix the prefix of the word returned
     * @return a word that starts with the given prefix, or an empty optional if no
     * such word is found.
     */
    Optional<String> predict(String prefix) {
        Optional<String> predictedWord = Optional.empty();

        // Checks if the tree already contains the given prefix and returns it if so
        if (contains(prefix)) {
            return Optional.of(prefix);
        }

        Optional<DictionaryTree> tree = followPrefix(prefix);

        if (tree.isPresent()) {
            String ending = mostPopularWord(tree.get()).split("_")[0];
            // Adds returned ending to the prefix
            if (!ending.equals(""))
                predictedWord = Optional.of(prefix + ending);

        }

        return predictedWord;
    }

    private Optional<DictionaryTree> followPrefix(String prefix) {
        Optional<DictionaryTree> tree = Optional.empty();

        if (prefix.length() > 0) {
            Character firstLetter = prefix.charAt(0);

            for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {

                // Return the tree of a child that has the last letter of the prefix as a key
                if (child.getKey().equals(firstLetter) && prefix.length() == 1) {
                    tree = Optional.of(child.getValue());

                    // Keep going through the tree if there are more letters to go through
                } else if (child.getKey().equals(firstLetter)) {
                    return child.getValue().followPrefix(prefix.substring(1));
                }
            }
        }
        return tree;
    }

    public String mostPopularWord(DictionaryTree dicTree) {
        DictionaryTree tree = dicTree;
        String mostPopular = "_" + 0;
        String word;
        String keyOfPop = "";

        if (!tree.children.isEmpty()) {
            // Go through the tree to find all the words and their popularity
            for (Map.Entry<Character, DictionaryTree> child : tree.children.entrySet()) {
                word = child.getValue().mostPopularWord(child.getValue());
                // the words are returned in format: word_popularity(int) so to determine the
                // most popular word the string needs to be split and the popularity values
                // compared. The one with the higher value are more popular and is stored to be
                // returned
                if (Integer.parseInt(word.split("_")[1]) >= Integer.parseInt(mostPopular.split("_")[1])) {
                    mostPopular = word;
                    keyOfPop = Character.toString(child.getKey());
                }
            }

            // Another check is made to see if there is a word ending at this node. If so,
            // if its value is higher that the most popular returned word, it is returned
            // instead
            if (pop.isPresent() && pop.get() >= Integer.parseInt(mostPopular.split("_")[1]))
                return "_" + pop.get();

            // keyOfPop is a value of the node that was from the most popular word and is
            // added to the word to extend it
            return keyOfPop + mostPopular;
        }

        // If a node has no children, it is an ending of a word so it will have
        // popularity
        return "_" + pop.get();
    }

    private List<String> mostPopularList(DictionaryTree dicTree, int n) {
        DictionaryTree tree = dicTree;
        List<String> mostPopularList = new LinkedList<>();
        List<String> listOfReturnedWords;

        if (!tree.children.isEmpty()) {
            // goes through each of the children and collects their list of words
            for (Map.Entry<Character, DictionaryTree> child : tree.children.entrySet()) {
                listOfReturnedWords = child.getValue().mostPopularList(child.getValue(), n);

                // For every item in the list adds the key of their node and adds everything to a
                // list of all of the returned words
                for (String listOfReturnedWord : listOfReturnedWords) {
                    mostPopularList.add(child.getKey() + listOfReturnedWord);
                }

            }

            // If at that node there is a word ending and its popularity is bigger than the
            // least popular word of the list(which is always the last one as the method
            // keeps the list sorted based on popularity), it gets added to the list
            if (pop.isPresent()
                    && pop.get() > Integer.parseInt(mostPopularList.get(mostPopularList.size() - 1).split("_")[1])) {
                mostPopularList.remove(mostPopularList.size() - 1);
                mostPopularList.add("_" + pop.get());
            }

            // Sorts the list based on popularity
            mostPopularList.sort((firstString, secondString) -> {
                Integer first = Integer.parseInt(firstString.split("_")[1]);
                Integer second = Integer.parseInt(secondString.split("_")[1]);

                return second.compareTo(first);
            });

            // The top n members of the list by their popularity gets returned
            if (mostPopularList.size() > n)
                mostPopularList = mostPopularList.subList(0, n);

            return mostPopularList;
        }

        // If a node has no children bus has a popularity it is the ending of the word
        // and gets added to the list
        if (pop.isPresent())
            mostPopularList.add("_" + pop.get());

        return mostPopularList;
    }

    /**
     * Predicts the (at most) n most popular full English words based on the
     * specified prefix. If no word with the specified prefix is found, an empty
     * list is returned.
     *
     * @param prefix the prefix of the words found
     * @return the (at most) n most popular words with the specified prefix
     */

    public List<String> predict(String prefix, int n) {
        Optional<DictionaryTree> tree = followPrefix(prefix);
        List<String> predictedList = new LinkedList<>();

        if (tree.isPresent()) {
            predictedList = mostPopularList(tree.get(), n);
        }

        if (contains(prefix)) {
            predictedList.add(0, "_0");
            if (predictedList.size() > n)
                predictedList = predictedList.subList(0, n);
        }

        for (ListIterator<String> i = predictedList.listIterator(); i.hasNext(); ) {
            String element = i.next();
            i.set(prefix + element.split("_")[0]);
        }

        return predictedList;
    }

    /**
     * @return the number of leaves in this tree, i.e. the number of words which are
     * not prefixes of any other word.swich
     */
    public int numLeaves() {
        return fold((tree, result) -> {
            int numberOfLeaves = 0;

            for (int i : result) {
                numberOfLeaves += i;
            }

            if (tree.children.isEmpty()) {
                numberOfLeaves += 1;
            }
            return numberOfLeaves;
        });

    }

    /**
     * @return the maximum number of children held by any node in this tree
     */
    public int maximumBranching() {
        return fold((tree, result) -> {
            int maximum = Integer.MIN_VALUE;

            for (int i : result) {
                maximum = Math.max(tree.children.size(), i);
            }

            return maximum;
        });
    }

    /**
     * @return the height of this tree, i.e. the length of the longest branch
     */
    public int height() {
        return fold((tree, result) -> {
            int height = -1;

            for (int i : result) {
                height = Math.max(height, i);
            }

            return height + 1;
        });
    }

    /**
     * @return the number of nodes in this tree
     */
    public int size() {
        return fold((tree, result) -> {
            int size = 0;

            for (int i : result) {
                size += i;
            }

            return size + 1;
        });
    }

    /**
     * @return the longest word in this tree
     */
    public String longestWord() {
        String longestWord = "";
        String keyOfLongest = "";
        String word;

        if (!children.isEmpty()) {
            // Goes through every node
            for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
                word = child.getValue().longestWord();
                // Compares the returned longest word with the one it has and switches
                // accordingly
                if (word.length() >= longestWord.length()) {
                    longestWord = word;
                    keyOfLongest = Character.toString(child.getKey());
                }
            }
            // Adds the key of the node to the longest word
            return keyOfLongest + longestWord;
        }
        return longestWord;
    }

    /**
     * @return all words stored in this tree as a list
     */
    public List<String> allWords() {
        List<String> listOfWords = new LinkedList<>();
        List<String> listOfReturnedWords;

        if (!children.isEmpty()) {
            // Goes through every node
            for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
                listOfReturnedWords = child.getValue().allWords();

                // Checks if any of the children ends a word. If so adds them to the list
                if (child.getValue().pop.isPresent())
                    listOfWords.add(Character.toString(child.getKey()));

                // Adds the key of the node to every word in the returned list of words
                for (String listOfReturnedWord : listOfReturnedWords) {
                    listOfWords.add(child.getKey() + listOfReturnedWord);
                }
            }
        }

        return listOfWords;
    }

    /**
     * Folds the tree using the given function. Each of this node's children is
     * folded with the same function, and these results are stored in a collection,
     * cResults, say, then the final result is calculated using f.apply(this,
     * cResults).
     *
     * @param f   the summarising function, which is passed the result of invoking
     *            the given function
     * @param <A> the type of the folded value
     * @return the result of folding the tree using f
     */
    <A> A fold(BiFunction<DictionaryTree, Collection<A>, A> f) {
        List<A> accumulatedResult = new ArrayList<>();

        for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
            A result = child.getValue().fold(f);
            accumulatedResult.add(result);
        }

        return f.apply(this, accumulatedResult);
    }
}
