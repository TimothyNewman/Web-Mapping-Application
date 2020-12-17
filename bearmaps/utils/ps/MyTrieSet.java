package bearmaps.utils.ps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyTrieSet {
    private Node root;

    public MyTrieSet() {
        root = new Node('a', false);
    }

    public class Node {
        private HashMap<Character, Node> trieMap;
        private boolean isKey;
        private char letter;

        private Node(char c, boolean b) {
            isKey = b;
            letter = c;
            trieMap = new HashMap<>();
        }
    }

    public void clear() {
        root.trieMap = new HashMap<>();
    }

    /**
     * Returns true if the Trie contains KEY, false otherwise
     */
    public boolean contains(String key) {
        Node currentNode = root;
        for (int i = 0; i < key.length(); i++) {
            if (currentNode == null) {
                return false;
            }
            if (currentNode.trieMap.containsKey(key.charAt(i))) {
                currentNode = currentNode.trieMap.get(key.charAt(i));
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Inserts string KEY into Trie
     */
    public void add(String key) {
        if (key == null || key.length() < 1) {
            return;
        }
//        if (contains(key)) {
//            return;
//        }
        Node curr = root;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (!curr.trieMap.containsKey(c)) {
                curr.trieMap.put(c, new Node(c, false));
            }
            curr = curr.trieMap.get(c);
        }
        curr.isKey = true;
    }

    /**
     * Returns a list of all words that start with PREFIX
     */
    public List<String> keysWithPrefix(String prefix) {
        Node currentNode = root;
        List<String> toReturn = new ArrayList();
        for (int i = 0; i < prefix.length(); i++) {
            if (currentNode == null) {
                return toReturn;
            }
            if (currentNode.trieMap.containsKey(prefix.charAt(i))) {
                currentNode = currentNode.trieMap.get(prefix.charAt(i));
            }
        }
        if (currentNode.isKey) {
            toReturn.add(prefix);
        }
        return prefixHelper(toReturn, currentNode, prefix);
    }

    public List prefixHelper(List<String> wordList, Node currNode, String prefix) {
        for (Node get : currNode.trieMap.values()) {
            if (get.isKey) {
                wordList.add(prefix + get.letter);
                prefixHelper(wordList, get, prefix + get.letter);
            } else {
                prefixHelper(wordList, get, prefix + get.letter);
            }
        }
        return wordList;
    }
}