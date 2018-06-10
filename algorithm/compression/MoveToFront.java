import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int R = 256;

    public static void encode() {
        
        // alphabet is a sorted dictionary, alphabet[i] gives the i-th character 
        // indices[c] gives the index of character c in the sorted dictionary
        char[] alphabet = new char[R];
        char[] indices = new char[R];
        for (char i = 0; i < R; i++) {
            alphabet[i] = i;
            indices[i] = i;
        }

        // read character from standard input and maintain alphabet[] and indices[]
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            char i = indices[c];
            BinaryStdOut.write(i);
            for (char j = i; j > 0; j--) {
                alphabet[j] = alphabet[j-1];
                indices[alphabet[j-1]]++;
            }
            alphabet[0] = c;
            indices[c] = 0;
        }

        BinaryStdOut.close();
    }

    public static void decode() {
        
        char[] alphabet = new char[R];
        for (char i = 0; i < R; i++)
            alphabet[i] = i;

        while (!BinaryStdIn.isEmpty()) {
            char i = BinaryStdIn.readChar();
            char c = alphabet[i];
            BinaryStdOut.write(c);
            for (char j = i; j > 0; j--)
                alphabet[j] = alphabet[j-1];
            alphabet[0] = c;
        }

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if (args[0].equals("-")) 
            encode();
        else if (args[0].equals("+"))
            decode();
    }
}