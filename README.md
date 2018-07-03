<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Solution</title>
  <link rel="stylesheet" href="https://stackedit.io/style.css" />
</head>

<body class="stackedit">
  <div class="stackedit__html"><p>**</p>
<h2 id="dictionary-tree-assignment---matthew-jones">Dictionary Tree Assignment - Matthew Jones</h2>
<p>**</p>
<p> link to repositry https://git.cs.bham.ac.uk/mmj744/DictionaryTree </p>
<p>Explanation of each method:</p>
<ul>
<li>
<p>void insert(String word)</p>
<ul>
<li>This method simply checked that the word was not already in the tree and then called the insert(String word, int popularity) method with the word and a popularity of 0 since none was specified.</li>
</ul>
</li>
<li>
<p>void insert(String word, int popularity)</p>
<ul>
<li>This method recursively added the word to the tree and once there was only one letter left to add it would set the popularity in the dictionary tree responding to the last letter marking it as the end of a word and marking the popularity.</li>
<li>If any part of the word was already in the tree it would instead go down the path until it was no longer already in the tree or the end of the word had been reached and then overwrite the popularity.</li>
</ul>
</li>
<li>
<p>boolean remove(String word)</p>
<ul>
<li>This method would first recursively go down the tree until it reached the word it was given, if the word wasn’t reached it would return false and exit as the word isn’t there to be removed.</li>
<li>If the end of the word was reached and it was marked as a word (has a popularity) it would check if the word could be deleted without effecting the rest of the tree.</li>
<li>If it could (the tree had no children) then the method would return true informing the recursive call that called it that it was safe to remove the tree from its list of children. That tree would then return true if it also had no children and wasn’t itself marked as the end of a word informing the tree above it can be removed or false if not.</li>
</ul>
</li>
<li>
<p>boolean contains(String word)</p>
<ul>
<li>This method works by following the branches down the tree letter by letter recursively, if the letter is not found in the map children the false is returned.</li>
<li>Once the word is down to the final letter true is only returned if the letter is marked as the end of a word (It has a popularity).</li>
</ul>
</li>
<li>
<p>Optional &lt;String&gt; predict(String prefix)</p>
<ul>
<li>This method works by building the word and traversing the tree at the same time using recursion.</li>
<li>If the prefix is not empty yet then the tree still needs to be traversed to the end of the prefix if at any point during this it can’t an empty optional will be returned.</li>
<li>While traversing the tree the letter of the prefix that has been traversed is added to the word.</li>
<li>If the prefix is empty it will get the first character from the map and choose that as the next letter of the word since there is no popularity to be considered in this method.</li>
<li>Once there are no more children in the tree the method will return the word it has built as long as it is at least as long as the prefix it was given this is to ensure that the word is actually a word.</li>
</ul>
</li>
<li>
<p>String longestWord()</p>
<ul>
<li>This method works by finding the tree with the biggest height out of all the children of the tree and then adding the tree’s character to the word before traversing further.</li>
<li>This ensures the word is the longest in the tree as the higher the tree the longer the word.</li>
</ul>
</li>
<li>
<p>List&lt;String&gt; allWords()</p>
<ul>
<li>This method makes use of the recursive helper method List&lt;String&gt; buildWords(List&lt;String&gt; words, String word)</li>
<li>The helper method holds a list to contain the words and a string to build the words.</li>
<li>Firstly it checks if the letter its currently at is a word and if it is it will add the word(the string) to the list.</li>
<li>Then it calls itself on each child continuing the building and preserving the current list by assigning its result to the list.</li>
<li>In the event of an empty tree there will be no children to loop through and the empty list given as a parameter will be returned avoiding the use of null.</li>
</ul>
</li>
<li>
<p>List predict(String prefix, int n)</p>
<ul>
<li>This method uses the helper method predictRec that is almost the same as predict but with an additional parameter.</li>
<li>This String allows for the prefix to be remembered as the tree is traversed</li>
<li>While the prefix isn’t empty the method goes down the tree returning an empty list if it cant.</li>
<li>Once the tree has been traversed the to the end of the prefix the buildPop method comes in. This method returns a TreeMap with the words coming after the prefix and their popularity.</li>
<li>With this TreeMap the n most popular words (if there are that many) can be found easily and inserted into the list with the original prefix appended to the front of them.</li>
<li>That list is then returned.</li>
</ul>
</li>
<li>
<p>&lt;A&gt; A fold(BiFunction&lt;DictionaryTree, Collection&lt;A&gt;, A&gt; f)</p>
<ul>
<li>To implement the fold I made a collection in the form of an ArrayList and added each child to the collection after calling fold on them.</li>
<li>The method returns the result of applying the function to the collection for the final result.</li>
</ul>
</li>
<li>
<p>int numLeaves()</p>
<ul>
<li>The method is implemented using fold</li>
<li>It returns the sum of the results in the collection + 1 if the Tree is a leaf (has no children) and simply just the sum of the results in the collection if not.</li>
</ul>
</li>
<li>
<p>int maximumBranching()</p>
<ul>
<li>The method is implemented using fold</li>
<li>It returns the highest value out of the number of children the tree has and the highest result from the collection. If there are none then it will return the highest between the the size of its children and 0.</li>
</ul>
</li>
<li>
<p>int height()</p>
<ul>
<li>The method is implemented using fold</li>
<li>It returns 1 + the sum of the results in the collection (the height of the rest of the tree)</li>
<li>If there are no results in the rest of the tree it returns 0 (1 + -1)</li>
</ul>
</li>
<li>
<p>int size()</p>
<ul>
<li>The method is implemented using fold</li>
<li>returns 1 + the sum of the results in the collection (the size of the rest of the tree)</li>
</ul>
<h2 id="using-a-tree-for-word-prediction">Using a tree for word prediction</h2>
<ul>
<li>
<p>Advantages</p>
<ul>
<li>Possible in logarithmic time compared to linear time of a normal algorithm.</li>
<li>It only looks at words that have the prefix in them.</li>
<li>Just simply retrieving the words is faster.</li>
</ul>
</li>
<li>
<p>Disadvantages</p>
<ul>
<li>all words with that prefix are found first as its not a ordered list so if you only want the top two words there is much more computation if there are 10000 words with that prefix than going down a ordered list of all words looking for words with that prefix.</li>
<li>The Tree is less efficient if your looking for a small number of words and/or are using a common prefix.</li>
</ul>
</li>
</ul>
</li>
</ul>
</div>
</body>

</html>
