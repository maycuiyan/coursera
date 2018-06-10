public class CircularSuffixArray {

    private final String s;
    private int[] suffixArray;

    public CircularSuffixArray(String s) {
        
        if (s == null) 
            throw new IllegalArgumentException("Input string cannot be null!");

        this.s = s;
        
        suffixArray = new int[s.length()];
        for (int i = 0; i < suffixArray.length; i++)
            suffixArray[i] = i;

        sortSuffixArray(0, s.length()-1, 0);

    }

    public int length() {
        return s.length();
    }

    /**
     * index of the i-th sorted suffix
    */   
    public int index(int i) {

        if (i < 0 || i >= suffixArray.length)
            throw new IllegalArgumentException("Input argument should be between 0 and " + suffixArray.length);

        return suffixArray[i];
    }

    /**
     * sort suffixArray[lo:hi]
     * using d-th character as key
     * based on 3-way string sort
     */
    private void sortSuffixArray(int lo, int hi, int d) {

        if (hi <= lo || d == s.length())
            return;
        
        // 3-way partition
        char pivot = charAt(suffixArray[lo], d);
        int lt = lo, gt = hi;
        int i = lt+1;
        while (i <= gt) {
            char key = charAt(suffixArray[i], d);
            if (key < pivot) exch(i++, lt++);
            else if (key > pivot) exch(i, gt--);
            else i++;
        }

        // recur on sub-arrays
        sortSuffixArray(lo, lt-1, d);
        sortSuffixArray(lt, gt, d+1);
        sortSuffixArray(gt+1, hi, d);
    }

    /**
     * get the d-th character of the circular suffix starting from s[i]
     */
    private char charAt(int i, int d) {

        return s.charAt((i + d) % s.length());
    }

    /**
     * exchange suffixArray[i] and suffixArray[j]
     */
    private void exch(int i, int j) {
        int temp = suffixArray[i];
        suffixArray[i] = suffixArray[j];
        suffixArray[j] = temp;
    }

    public static void main(String[] args) {
        String s = args[0];
        CircularSuffixArray a = new CircularSuffixArray(s);
        System.out.println("The length of the string is: " + a.length());
        System.out.println("The suffix array in sorted order is:");
        for (int i = 0; i < s.length(); i++) 
            System.out.println(a.index(i));
    }
}