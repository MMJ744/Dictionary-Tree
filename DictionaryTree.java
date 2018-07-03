import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class DictionaryTree {

	private Map<Character, DictionaryTree> children = new LinkedHashMap<>();
	private Optional<Integer> popularity = Optional.empty();

	public Optional<Integer> getPopularity() {
		return popularity;
	}

	public void setPopularity(Optional<Integer> popularity) {
		this.popularity = popularity;
	}

	/**
	 * Inserts the given word into this dictionary. If the word already exists,
	 * nothing will change.
	 *
	 * @param word
	 *            the word to insert
	 */
	void insert(String word) {
		if (word.isEmpty() || contains(word)) return;
		insert(word, 0);
	}

	/**
	 * Inserts the given word into this dictionary with the given popularity. If the
	 * word already exists, the popularity will be overriden by the given value.
	 *
	 * @param word
	 *            the word to insert
	 * @param popularity
	 *            the popularity of the inserted word
	 */
	void insert(String word, int popularity) {
		if (word.isEmpty()) return;
		char head = word.charAt(0);
		if (word.length() == 1) {
			if (!children.containsKey(head)) children.put(head, new DictionaryTree());
			children.get(head).setPopularity(Optional.of(popularity));
		} else {
			if (!children.containsKey(head)) children.put(head, new DictionaryTree());
			children.get(head).insert(word.substring(1), popularity);
		}
	}

	/**
	 * Removes the specified word from this dictionary. Returns true if the caller
	 * can delete this node without losing part of the dictionary, i.e. if this node
	 * has no children after deleting the specified word.
	 *
	 * @param word
	 *            the word to delete from this dictionary
	 * @return whether or not the parent can delete this node from its children
	 */
	boolean remove(String word) {
		if (word.isEmpty()) return false;
		char head = word.charAt(0);
		if(!children.containsKey(head)) return false;
		DictionaryTree tree = children.get(head);
		if(word.length() == 1) {
			if(!tree.getPopularity().isPresent()) return false;
			tree.setPopularity(Optional.empty());
			if(tree.children.isEmpty()) {
				children.remove(head);
				return children.isEmpty() && !popularity.isPresent();
			}
			return false;
		}
		if(tree.remove(word.substring(1))) {
			children.remove(head);
			return children.isEmpty() && !popularity.isPresent();
		}
		return false;
	}

	/**
	 * Determines whether or not the specified word is in this dictionary.
	 *
	 * @param word
	 *            the word whose presence will be checked
	 * @return true if the specified word is stored in this tree; false otherwise
	 */
	boolean contains(String word) {
		if (word.isEmpty())
			return false;
		char head = word.charAt(0);
		if (!children.containsKey(head))
			return false;
		if (word.length() == 1)
			return children.get(head).getPopularity().isPresent();
		return children.get(head).contains( word.substring(1));
	}

	/**
	 * @param prefix
	 *            the prefix of the word returned
	 * @return a word that starts with the given prefix, or an empty optional if no
	 *         such word is found.
	 */
	Optional<String> predict(String prefix) {
		String word = "";
		if (prefix.isEmpty() && !children.isEmpty()) {
			Character c = (Character) children.keySet().toArray()[0];
			word = word + c + children.get(c).predict("").orElse("");
		} else if (!prefix.isEmpty()) {
			char head = prefix.charAt(0);
			if (!children.containsKey(head)) return Optional.empty();
			word = word + head + children.get(head).predict(prefix.substring(1)).orElse("");
		}
		if (word.length() >= prefix.length())
			return Optional.of(word);
		return Optional.empty();
	}
	
	/**
	 * Predicts the (at most) n most popular full English words based on the
	 * specified prefix. If no word with the specified prefix is found, an empty
	 * list is returned.
	 *
	 * @param prefix
	 *            the prefix of the words found
	 * @return the (at most) n most popular words with the specified prefix
	 */
	List<String> predict(String prefix, int n) {
		return predictRec(prefix, n, "");
	}
	
	List<String> predictRec(String prefix, int n, String word) {
		List<String> list = new ArrayList<String>();
		if(!prefix.isEmpty()) {
			char c = prefix.charAt(0);
			if(!children.containsKey(c)) return list;
			list = children.get(c).predictRec(prefix.substring(1), n, word + c);
		} else {
			TreeMap<Integer, String> words = buildPop(new TreeMap<Integer, String>(), "");
			n = Math.min(n, words.size());
			for(int i = 0; i < n; i ++) {
				list.add(word + words.pollLastEntry().getValue());
			}
		}
		return list;
	}
	
	TreeMap<Integer, String> buildPop(TreeMap<Integer, String> words, String word) {
		if(popularity.isPresent()) words.put(popularity.get(), word);
		for (Map.Entry<Character, DictionaryTree> child : children.entrySet())
			words = child.getValue().buildPop(words, word + child.getKey());
		return words;
	}
	/**
	 * @return the number of leaves in this tree, i.e. the number of words which are
	 *         not prefixes of any other word.
	 */
	int numLeaves() {
		return fold((a,b) -> ((a.children.isEmpty()) ? 1 : 0 + b.stream().mapToInt(i -> i).sum()));
	}

	/**
	 * @return the maximum number of children held by any node in this tree
	 */
	int maximumBranching() {
		return fold((a,b) -> Math.max(a.children.size(), b.stream().reduce(Integer::max).orElse(0)));
	}

	/**
	 * @return the height of this tree, i.e. the length of the longest branch
	 */
	int height() {
		return fold((a,b) -> 1 + b.stream().reduce(Integer::max).orElse(-1));
	}

	/**
	 * @return the number of nodes in this tree
	 */
	int size() {
		return fold((a,b) -> 1 + b.stream().mapToInt(i -> i).sum());
	}

	/**
	 * @return the longest word in this tree
	 */
	String longestWord() {
		String word = "";
		if (children.isEmpty())
			return word;
		Character longest = Character.MIN_VALUE;
		int highest = -1;
		for (Map.Entry<Character, DictionaryTree> child : children.entrySet()) {
			if (child.getValue().height() > highest) {
				longest = child.getKey();
				highest = child.getValue().height();
			}
		}
		return word + longest + children.get(longest).longestWord();
	}

	/**
	 * @return all words stored in this tree as a list
	 */
	List<String> allWords() {
		return buildWords(new ArrayList<String>(), "");
	}
	
	List<String> buildWords(List<String> words, String word) {
		if(popularity.isPresent()) words.add(word);
		for (Map.Entry<Character, DictionaryTree> child : children.entrySet())
			words = child.getValue().buildWords(words, word + child.getKey());
		return words;
	}
	
	/**
	 * Folds the tree using the given function. Each of this node's children is
	 * folded with the same function, and these results are stored in a collection,
	 * cResults, say, then the final result is calculated using f.apply(this,
	 * cResults).
	 *
	 * @param f
	 *            the summarising function, which is passed the result of invoking
	 *            the given function
	 * @param <A>
	 *            the type of the folded value
	 * @return the result of folding the tree using f
	 */
	<A> A fold(BiFunction<DictionaryTree, Collection<A>, A> f) {
		Collection<A> cResults = new ArrayList<A>();
		for (Map.Entry<Character, DictionaryTree> child : children.entrySet())
			cResults.add(child.getValue().fold(f));
		return f.apply(this, cResults);
	}

}
