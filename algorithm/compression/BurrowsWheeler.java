import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;


public class BurrowsWheeler {

    private static final int R = 256;

    public static void transform() {

        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);

        // position of the original string in the sorted suffix array
        int first;
        for (first = 0; first < s.length(); first++)
            if (csa.index(first) == 0)
                break;
        BinaryStdOut.write(first);

        // write the transformed sequence
        for (int i = 0; i < s.length(); i++)
            BinaryStdOut.write(s.charAt((csa.index(i) + s.length() - 1) % s.length()));

        BinaryStdOut.close();
    }

    public static void inverseTransform() {

        int first = BinaryStdIn.readInt();
        char[] t = BinaryStdIn.readString().toCharArray();
        int N = t.length;

        // get count array for key-index counting sort
        int[] count = new int[R+1];
        for (int i = 0; i < N; i++)
            count[t[i]+1]++;
        for (int r = 0; r < R; r++)
            count[r+1] += count[r];


        // get first column of sorted suffixes (ie sorted t[]) and next[]
        char[] aux = new char[N];
        int[] next = new int[N];
        for (int i = 0; i < N; i++) {
            next[count[t[i]]] = i;
            aux[count[t[i]]++] = t[i];
        }

        // reconstruct the sequence and write to StdOut
        for (int i = 0; i < N; i++) {
            BinaryStdOut.write(aux[first]);
            first = next[first];
        }

        BinaryStdOut.close();

    }

    public static void main(String[] args) {
        if (args[0].equals("-")) 
            transform();
        else if (args[0].equals("+"))
            inverseTransform();
    } 
}