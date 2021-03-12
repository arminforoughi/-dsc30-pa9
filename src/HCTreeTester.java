import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;

public class HCTreeTester {

    /**
     * Tests encode() and decode().
     * @param tree HCTree to test
     * @param input the byte to reconstruct
     * @return whether the encode-decode can reconstruct the input byte
     * @throws IOException from stream
     */
    private static boolean testByte(HCTree tree, byte input) throws IOException {

        // build out-stream
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        BitOutputStream bitOut = new BitOutputStream(dataOut);

        // encode byte
        tree.encode(input, bitOut);

        // send data from out-stream to in-stream
        bitOut.flush();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        DataInputStream dataIn = new DataInputStream(byteIn);
        BitInputStream bitIn = new BitInputStream(dataIn);

        // decode byte and compare with input
        boolean result = (input == tree.decode(bitIn));

        // close streams
        dataOut.close();
        byteOut.close();
        dataIn.close();
        byteIn.close();
        return result;
    }

    /**
     * Checks if `expected` and `actual` have the same structure,
     * regardless of the instance variables on the nodes.
     * @param expected the root of the expected tree
     * @param actual the root of the actual tree
     * @return whether they share the same structure
     */
    private static boolean sameTreeStructure(HCTree.HCNode expected, HCTree.HCNode actual) {
        if (expected == null && actual == null) return true;
        if (expected == null || actual == null) return false;
        return sameTreeStructure(expected.c0, actual.c0)
                && sameTreeStructure(expected.c1, actual.c1);
    }

    /**
     * Tests encodeHCTree() and decodeHCTree().
     * @param tree HCTree to test
     * @return whether the encode-decode can reconstruct the tree
     * @throws IOException from stream
     */
    private static boolean testTree(HCTree tree) throws IOException {
        // build out-stream
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOut);
        BitOutputStream bitOut = new BitOutputStream(dataOut);

        // encode tree
        tree.encodeHCTree(tree.getRoot(), bitOut);

        // send data from out-stream to in-stream
        bitOut.flush();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        DataInputStream dataIn = new DataInputStream(byteIn);
        BitInputStream bitIn = new BitInputStream(dataIn);

        // decode tree and compare with input
        HCTree treeOut = new HCTree();
        treeOut.setRoot(treeOut.decodeHCTree(bitIn));
        boolean result = sameTreeStructure(tree.getRoot(), treeOut.getRoot());

        // close streams
        dataOut.close();
        byteOut.close();
        dataIn.close();
        byteIn.close();
        return result;
    }


    @Test
    public void example() throws IOException {
        HCTreeTester tester = new HCTreeTester();
        String ex = "dsc10dsc20dsc30dsc40adsc40bdsc80\n";
        String ex1 = "aaaaaaaaaaaaaaaaabbbbbbbbcccccccddddddddddddddeeeeeeeeef\n";
        String ex2 = "7634675643245678765";
        int[] fre = new int[256];
        int[] fre1 = new int[256];
        int[] fre2 = new int[256];
        int[] fre3 = new int[256];
        for (int i = 0; i < ex.length(); i++) {
            int ascii = ex.charAt(i) & 0xff;
            fre[ascii]++;
        }
        for (int i = 0; i < ex1.length(); i++) {
            int ascii = ex1.charAt(i) & 0xff;
            fre1[ascii]++;
        }
        for (int i = 0; i < ex2.length(); i++) {
            int temp = Character.getNumericValue(ex2.charAt(i));
            //System.out.println(temp);

            int ascii = (byte) temp & 0xff;
            //System.out.println(ascii);
            fre2[ascii]++;
        }
        int ascii = 1 & 0xff;
        fre3[ascii]++;
        HCTree tree = new HCTree();
        HCTree tree1 = new HCTree();
        HCTree tree2 = new HCTree();
        HCTree tree3 = new HCTree();

        tree.buildTree(fre);
        tree1.buildTree(fre1);
        tree2.buildTree(fre2);
        tree3.buildTree(fre3);

        try {
            System.out.println(tester.testTree(tree));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(tester.testTree(tree1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(tester.testTree(tree2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(tester.testByte(tree2, (byte) 7));
        System.out.println(tester.testByte(tree, (byte) '0'));
        System.out.println(tester.testByte(tree, (byte) 'd'));
        System.out.println(tester.testByte(tree1, (byte) 'a'));
        //System.out.println(tester.testByte(tree3, (byte) 1));






    }

}
