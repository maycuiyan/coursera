import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

	private WordNet wordnet;

	// constructor takes a WordNet object
	public Outcast(WordNet wordnet) {

		this.wordnet = wordnet;

	}

	// given an array of WordNet nouns, return an outcast
	public String outcast(String[] nouns) {

		int N = nouns.length;
		int[] dist = new int[N];

		// computet the distance between each word and the other
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				dist[i] += wordnet.distance(nouns[i], nouns[j]);
		
		// find the outcast (word with max distance)
		int maxDist = dist[0];
		String outcast = nouns[0];
		for (int i=0; i<N; i++)
			if (maxDist<dist[i]) {
				maxDist = dist[i];
				outcast = nouns[i];
			}
		return outcast;
	}

	// client test
	public static void main(String[] args) {

	    WordNet wordnet = new WordNet(args[0], args[1]);
	    Outcast outcast = new Outcast(wordnet);
	    for (int t = 2; t < args.length; t++) {
	        In in = new In(args[t]);
	        String[] nouns = in.readAllStrings();
	        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	    }

	}

}