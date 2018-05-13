import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;

public class SAP {

   private final Digraph G;
   private int ancestor;
   private int distance;
   private boolean[] marked;
   private int[] distTo;

   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G) {

      if (G == null) 
         throw new IllegalArgumentException("Input graph is null!");

      this.G = new Digraph(G.V());
      for (int v=0; v<G.V(); v++)
         for (int w: G.adj(v))
            this.G.addEdge(v, w);

   }

   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w) {

      if (!validVertex(v) || !validVertex(w))
         throw new IllegalArgumentException("Input vertex is invalid!");

      shortestAncestralPath(Arrays.asList(v), Arrays.asList(w));
      return ancestor==-1 ? -1 : distance;

   }

   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
   public int ancestor(int v, int w) {

      if (!validVertex(v) || !validVertex(w))
         throw new IllegalArgumentException("Input vertex is invalid!");

      shortestAncestralPath(Arrays.asList(v), Arrays.asList(w));
      return ancestor;

   }

   // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
   public int length(Iterable<Integer> v, Iterable<Integer> w) {

      if (v == null || w == null)
         throw new IllegalArgumentException("Input vertices are null!");
      for (int x: v)
         if (!validVertex(x))
            throw new IllegalArgumentException("Input vertex is invalid!");
      for (int x: w)
         if (!validVertex(x))
            throw new IllegalArgumentException("Input vertex is invalid!");

      shortestAncestralPath(v, w);
      return ancestor==-1 ? -1 : distance;
   }

   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {

      if (v == null || w == null)
         throw new IllegalArgumentException("Input vertices are null!");
      for (int x: v)
         if (!validVertex(x))
            throw new IllegalArgumentException("Input vertices are invalid!");
      for (int x: w)
         if (!validVertex(x))
            throw new IllegalArgumentException("Input vertices are invalid!");

      shortestAncestralPath(v, w);
      return ancestor;

   }

   // check if input vertex is valid
   private boolean validVertex(int v) {
      return v >= 0 && v < G.V();
   }

   // breadth first search
   private void bfs(Iterable<Integer> v) {

      marked = new boolean[G.V()];
      distTo = new int[G.V()];
      Queue<Integer> queue = new LinkedList<>();
      for (int x: v) {
         queue.add(x);
         marked[x] = true;
      }
      while (!queue.isEmpty()) {
         int x = queue.remove();
         for (int y: G.adj(x))
            if (!marked[y]) {
               queue.add(y);
               marked[y] = true;
               distTo[y] = distTo[x]+1;
            }
      }

   }

   // a helper function to find shortest ancetral path
   private void shortestAncestralPath(Iterable<Integer> v, Iterable<Integer> w) {

      // run bfs from v
      bfs(v);
      boolean[] markedV = marked.clone();
      int[] distToV = distTo.clone();

      // run bfs from w
      bfs(w);
      boolean[] markedW = marked.clone();
      int[] distToW = distTo.clone();
      
      // find shortest path ancestor
      ancestor = -1;
      distance = Integer.MAX_VALUE;
      for (int i=0; i<G.V(); i++)
         if (markedV[i] && markedW[i] && distToV[i]+distToW[i]<=distance) {
            ancestor = i;
            distance = distToV[i]+distToW[i];
         }

   }

   // client test
   public static void main(String[] args) {

      In in = new In(args[0]);
      Digraph G = new Digraph(in);
      SAP sap = new SAP(G);
      while (!StdIn.isEmpty()) {
        int v = StdIn.readInt();
        int w = StdIn.readInt();
        int length   = sap.length(v, w);
        int ancestor = sap.ancestor(v, w);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
      }

   }

}