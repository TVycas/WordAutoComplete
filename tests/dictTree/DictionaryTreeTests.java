package tests.dictTree;

import org.junit.Test;
import src.dictTree.DictionaryTree;

import static org.junit.Assert.*;

public class DictionaryTreeTests {

    @Test
    public void heightOfRootShouldBeZero() {
        DictionaryTree unit = new DictionaryTree();
        assertEquals(0, unit.height());
    }

    @Test
    public void size() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("a");
        assertEquals(2, unit.size());
    }

    @Test
    public void contains() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("a");
        assertEquals(1, unit.numLeaves());
    }

    @Test
    public void heightOfWordShouldBeWordLength() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word", 0);
        assertEquals("word".length(), unit.height());
    }

    @Test
    public void heightTest() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("");
        assertEquals("".length(), unit.height());
    }

    @Test
    public void heightShouldBeTheLongestWordLength() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word", 0);
        unit.insert("apples", 0);
        unit.insert("tea", 0);
        unit.insert("people", 0);
        assertEquals("people".length(), unit.height());
    }

    @Test
    public void itShouldContainAnInsertedWord() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        assertTrue(unit.contains("word"));
    }

    @Test
    public void itShouldContainAnInsertedWord2() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("wo");
        assertTrue(unit.contains("wo"));
    }

    @Test
    public void itShouldContainAnInsertedWord1() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word", 5);
        assertTrue(unit.contains("word"));
    }

    @Test
    public void itNotShouldContainAWordThatIsNotInserted() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("anotherWord");
        assertFalse(unit.contains("word"));
    }

    @Test
    public void containsTrueOnlyIfTheWholeWordIsInTheDictionary() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("another");
        assertFalse(unit.contains("anotherWord"));
    }

    @Test
    public void numbOfLeafsShouldBe3() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("another");
        unit.insert("anothe");
        unit.insert("words");
        unit.insert("digger");
        assertEquals(3, unit.numLeaves());
    }

    @Test
    public void shouldNotContainRemovedWord() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        assertTrue(unit.contains("word"));
        unit.remove("word");
        assertFalse(unit.contains("word"));
    }


    @Test
    public void shouldNotRemovePrefix() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        unit.insert("world");
        assertTrue(unit.contains("word"));
        assertTrue(unit.contains("world"));
        unit.remove("word");
        //assertEquals(true, unit.contains("word"));
        assertTrue(unit.contains("world"));
    }

    @Test
    public void shouldReturnFalseWhenNotRemoved() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        unit.insert("world");
        assertTrue(unit.contains("word"));
        assertTrue(unit.contains("world"));
        //	assertEquals(false, unit.remove("word"));
        assertTrue(unit.contains("word"));
        assertTrue(unit.contains("world"));
    }

    @Test
    public void shouldReturnTrueWhenRemoved() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        unit.insert("differentWord");
        assertTrue(unit.contains("word"));
        assertTrue(unit.remove("word"));
        assertFalse(unit.contains("word"));
    }

    @Test
    public void shouldOnlyRemoveEndOfTheWord() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        unit.insert("wordA");

        assertEquals(6, unit.size());
        assertTrue(unit.remove("wordA"));
        assertEquals(5, unit.size());
        assertFalse(unit.contains("wordA"));
        assertTrue(unit.contains("word"));
    }

    @Test
    public void shouldOnlyRemoveEndOfTheWord1() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        unit.insert("wordA");
        unit.insert("world");

        assertEquals(8, unit.size());
        assertTrue(unit.remove("world"));
        assertEquals(6, unit.size());
        assertTrue(unit.contains("wordA"));
        assertTrue(unit.contains("word"));
    }

    @Test
    public void shouldReturnTheLongestInsertedWord() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        unit.insert("differentWord");
        unit.insert("differentWordLonger");
        unit.insert("differentWordLo");
        assertEquals("differentWordLonger", unit.longestWord());
    }

    @Test
    public void shouldReturnTheLongestInsertedWord1() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        unit.insert("world");
        unit.insert("differentWord");
        unit.insert("differentWordLonger");
        unit.insert("differentWordLo");
        assertEquals("differentWordLonger", unit.longestWord());
    }

    @Test
    public void shouldReturnOneOfTheWords() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("wordA");
        unit.insert("wordB");
        unit.insert("wordC");
        assertEquals("wordC", unit.longestWord());
    }

    @Test
    public void shouldReturn3AsMaxBranching() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        unit.insert("world");
        unit.insert("worldA");
        unit.insert("worldBC");
        unit.insert("worldBCASDASD");
        unit.insert("worldDEG");

        assertEquals(3, unit.maximumBranching());
    }

    @Test
    public void allWords() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        unit.insert("wordA");
        unit.insert("wordB");
        unit.insert("wordCD");

        assertEquals("[word, wordA, wordB, wordCD]", unit.allWords().toString());
    }

    @Test
    public void allWords654() {
        DictionaryTree unit = new DictionaryTree();
        unit.setPop(-5);

        assertEquals(-5, unit.getPop());
    }

    @Test
    public void test() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word", 1);
        unit.insert("wordA", 6);
        unit.insert("wordB", 3);
        unit.insert("wordCD", 2);

        assertEquals("wordA_6", unit.mostPopularWord(unit));
    }


    @Test
    public void test3() {
        DictionaryTree unit = new DictionaryTree();
        unit.insert("word");
        unit.insert("wordasdf");
        assertFalse(unit.remove("word"));
        assertFalse(unit.contains("word"));
        assertTrue(unit.contains("wordasdf"));
    }

}
