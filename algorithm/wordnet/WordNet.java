import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class WordNet {

   private final HashMap<Integer, String> idToSynset;
   private final HashMap<String, List<Integer>> wordToId;
   private final Digraph G;
   private boolean[] marked;
   private boolean[] instack;
   private int[] distTo;
   private SAP sap;

   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms) {

      if (synsets==null || hypernyms==null)
         throw new IllegalArgumentException();

      // load synsets into dictionary
      In in = new In(synsets);
      idToSynset = new HashMap<>();
      wordToId = new HashMap<>();
      while (in.hasNextLine()) {
         String[] line = in.readLine().split(",");
         int id = Integer.parseInt(line[0]);
         String synset = line[1];
         idToSynset.put(id, synset);
         for (String word: synset.split("\\s+")) {
            if (!wordToId.containsKey(word))
               wordToId.put(word, new ArrayList<>());
            wordToId.get(word).add(id);
         }
      }

      // create a digraph
      G = new Digraph(idToSynset.size());
      in = new In(hypernyms);
      int numberOfLines = 0;
      while (in.hasNextLine()) {
         numberOfLines++;
         String[] line = in.readLine().split(",");
         int v = Integer.parseInt(line[0]);
         for (int i=1; i<line.length; i++) {
            int w = Integer.parseInt(line[i]);
            G.addEdge(v, w);
         }
      }

      // check if there are more than one root
      if (G.V() - numberOfLines > 1)
         throw new IllegalArgumentException("There are more than one root!");

      // check if G is DAG
      if (!isDAG())
         throw new IllegalArgumentException("Graph is not a DAG");

      // create a SAP object of which length() and ancestor() methods are called
      sap = new SAP(G);

   }

   // returns all WordNet nouns
   public Iterable<String> nouns() {

      return wordToId.keySet();

   }

   // is the word a WordNet noun?
   public boolean isNoun(String word) {

      if (word == null)
         throw new IllegalArgumentException("Input argument is null!");

      return wordToId.containsKey(word);

   }

   // distance between nounA and nounB
   public int distance(String nounA, String nounB) {

      if (!isNoun(nounA) || !isNoun(nounB))
         throw new IllegalArgumentException("Input words are not in the wordnet");

      Iterable<Integer> a = wordToId.get(nounA);
      Iterable<Integer> b = wordToId.get(nounB);
      return sap.length(a, b);

   }

   // common ancestor of nounA and nounB in a shortest ancestral path
   public String sap(String nounA, String nounB) {

      if (!isNoun(nounA) || !isNoun(nounB))
         throw new IllegalArgumentException("Input words are not in word net");

      Iterable<Integer> a = wordToId.get(nounA);
      Iterable<Integer> b = wordToId.get(nounB);
      return idToSynset.get(sap.ancestor(a, b));

   }

   // check if the wordnet is acyclic
   private boolean isDAG() {

      marked = new boolean[G.V()];
      instack = new boolean[G.V()];
      for (int v = 0; v < G.V(); v++)
         if (!marked[v] && !acyclic(v))
            return false;
      return true;

   }

   // depth-first search to check acyclicity
   private boolean acyclic(int v) {

      marked[v] = true;
      instack[v] = true;
      for (int w: G.adj(v)) {
         if (!marked[w]) {
            if (!acyclic(w)) return false;
         }
         else {
            if (instack[w]) return false;
         }
      }
      instack[v] = false;
      return true;

   }

   // client testing
   public static void main(String[] args) {

      WordNet wordnet = new WordNet(args[0], args[1]);
      StdOut.println(wordnet.distance("Chinese_parasol_tree", "sash"));

   }

}
