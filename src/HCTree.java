/*
 * Name: TODO
 * PID: TODO
 */

import java.io.*;
import java.util.SortedSet;
import java.util.Stack;
import java.util.PriorityQueue;

/**
 * The Huffman Coding Tree
 */
public class HCTree {
    // alphabet size of extended ASCII
    private static final int NUM_CHARS = 256;
    // number of bits in a bytef
    private static final int BYTE_BITS = 8;

    // the root of HCTree
    private HCNode root;
    // the leaves of HCTree that contain all the symbols
    private HCNode[] leaves = new HCNode[NUM_CHARS];

    /**
     * The Huffman Coding Node
     */
    protected class HCNode implements Comparable<HCNode> {

        byte symbol; // the symbol contained in this HCNode
        int freq; // the frequency of this symbol
        HCNode c0, c1, parent; // c0 is the '0' child, c1 is the '1' child

        /**
         * Initialize a HCNode with given parameters
         *
         * @param symbol the symbol contained in this HCNode
         * @param freq   the frequency of this symbol
         */
        HCNode(byte symbol, int freq) {
            this.symbol = symbol;
            this.freq = freq;
        }

        /**
         * Getter for symbol
         *
         * @return the symbol contained in this HCNode
         */
        byte getSymbol() {
            return this.symbol;
        }

        /**
         * Setter for symbol
         *
         * @param symbol the given symbol
         */
        void setSymbol(byte symbol) {
            this.symbol = symbol;
        }

        /**
         * Getter for freq
         *
         * @return the frequency of this symbol
         */
        int getFreq() {
            return this.freq;
        }

        /**
         * Setter for freq
         *
         * @param freq the given frequency
         */
        void setFreq(int freq) {
            this.freq = freq;
        }

        /**
         * Getter for '0' child of this HCNode
         *
         * @return '0' child of this HCNode
         */
        HCNode getC0() {
            return c0;
        }

        /**
         * Setter for '0' child of this HCNode
         *
         * @param c0 the given '0' child HCNode
         */
        void setC0(HCNode c0) {
            this.c0 = c0;
        }

        /**
         * Getter for '1' child of this HCNode
         *
         * @return '1' child of this HCNode
         */
        HCNode getC1() {
            return c1;
        }

        /**
         * Setter for '1' child of this HCNode
         *
         * @param c1 the given '1' child HCNode
         */
        void setC1(HCNode c1) {
            this.c1 = c1;
        }

        /**
         * Getter for parent of this HCNode
         *
         * @return parent of this HCNode
         */
        HCNode getParent() {
            return parent;
        }

        /**
         * Setter for parent of this HCNode
         *
         * @param parent the given parent HCNode
         */
        void setParent(HCNode parent) {
            this.parent = parent;
        }

        /**
         * Check if the HCNode is leaf (has no children)
         *
         * @return if it's leaf, return true. Otherwise, return false.
         */
        boolean isLeaf() {
            if (c0 == null && c1 == null) {
                return true;
            }
            return false;
        }

        /**
         * String representation
         *
         * @return string representation
         */
        public String toString() {
            return "Symbol: " + this.symbol + "; Freq: " + this.freq;
        }

        /**
         * Compare two nodes
         *
         * @param o node to compare
         * @return int positive if this node is greater
         */
        public int compareTo(HCNode o) {
            if (this.freq > o.freq) {
                return 1;
            } else if (this.freq < o.freq) {
                return -1;
            } else if (this.freq == o.freq) {
                if ((this.symbol & 0xff) > (o.symbol & 0xff)) {
                    return 1;
                } else if ((this.symbol & 0xff) < (o.symbol & 0xff)) {
                    return -1;
                }
            }

            return 0;
        }
    }

    /**
     * Returns the root node
     *
     * @return root node
     */
    public HCNode getRoot() {
        return root;
    }

    /**
     * Sets the root node
     *
     * @param root node to set
     */
    public void setRoot(HCNode root) {
        this.root = root;
    }

    /**
     * builds a tree by going through the freq and building an Priority queue
     *
     * @param freq the fre table from acciss table
     */
    public void buildTree(int[] freq) {
        PriorityQueue<HCNode> PQueue = new PriorityQueue<>();
        for (int i = 0; i < freq.length; i++) {
            // goes through the freq
            if (freq[i] > 0) {
                //checks if a freq exists
                HCNode temp = new HCNode((byte) i, freq[i]); // creates a new node
                this.leaves[i] = temp; // adds node to leaves
                PQueue.add(temp); // adds node to Priority Queue
            }
        }
        while (!PQueue.isEmpty()) {
            HCNode child0 = PQueue.remove(); //set the first element to child 0
            if (PQueue.isEmpty()) {
                setRoot(child0); //set root when its empty
                break;
            }
            HCNode child1 = PQueue.remove(); //set the first element to child 1
            HCNode parentnode = new HCNode(child0.symbol,
                    child0.freq + child1.freq); //create new parent node by adding the two childs
            parentnode.setC0(child0); //set child 0 for parent
            parentnode.setC1(child1); //set child 1 for parent
            child0.setParent(parentnode); // set parent for child 0
            child1.setParent(parentnode); // set parent for child 1
            PQueue.add(parentnode); //add the parent back to pqueue
        }

    }

    /**
     * giving a symbol get the HCtree encoding bits
     *
     * @param symbol the symbol in byte
     * @param out the out stream
     * @throws IOException
     */
    public void encode(byte symbol, BitOutputStream out) throws IOException {
        int ascii = symbol & 0xff; //finds the ascii of symbol
        HCNode temp = leaves[ascii]; //finds the node from leaves

        Stack<Integer> result = new Stack<>(); // creates a result stack to collect all the bits
        while (temp != root) {
            //loops through the tree to get to the node
            HCNode parent = temp.getParent();
            if (parent.getC1() == temp) {
                result.push(1); //inserts 1 if its child 1
            } else if (parent.getC0() == temp) {
                result.push(0); //inserts 0 if its child 0
            }
            temp = parent;
        }
        while (!result.isEmpty()) {
            //loops trough the results string and writes out to out
            out.writeBit(result.pop());
        }
    }

    /**
     * giving the HCtree encoding bits, it outputs the symbol
     *
     * @param in the in stream
     * @return the symbyl byte
     * @throws IOException throws exception
     */
    public byte decode(BitInputStream in) throws IOException {
        HCNode temp = root;
        while (!temp.isLeaf()) {
            // goes throug the tree until get to root
            int tempi = in.readBit(); // reads a bit on every loop
            if (tempi == 0) {
                temp = temp.getC0(); // child 0
            } else if (tempi == 1) {
                temp = temp.getC1(); // child 1
            }
        }
        return temp.getSymbol();
    }

    /**
     * given a node build the tree from out using pre-order traversing
     *
     * @param node the node to start going down the tree
     * @param out the out stream for every bit
     * @throws IOException throws exception
     */
    public void encodeHCTree(HCNode node, BitOutputStream out) throws IOException {
        if (node.isLeaf()) {
            // if the node is leaf write bit 1 and write the byte
            out.writeBit(1);
            out.writeByte(node.getSymbol());
        } else {
            // if its not a leaf get recurse to the children and write 0
            out.writeBit(0);
            encodeHCTree(node.getC0(), out);
            encodeHCTree(node.getC1(), out);
        }
    }

    /**
     * start from the leaves and work up
     *
     * @param in is the stream of bits
     * @return the node of each recursion and the root at the end
     * @throws IOException the exception
     */
    public HCNode decodeHCTree(BitInputStream in) throws IOException {
        if (in.readBit() == 1) {
            // if its a leaf create a leaf node with the next 8 bits
            HCNode temp = new HCNode(in.readByte(), 1);
            int ascii = temp.getSymbol() & 0xff; //finds the ascii of symbol
            this.leaves[ascii] = temp;
            setRoot(temp);
            return temp;

        } else {
            HCNode parent = new HCNode((byte) 0, 1);
            HCNode child0 = decodeHCTree(in); // initializes child 0
            HCNode child1 = decodeHCTree(in); // initializes child 1
            child0.setParent(parent); // sets parent of child 0
            child1.setParent(parent); // sets parent of child 1
            parent.setC0(child0); // sets child 0 of parent
            parent.setC1(child1); // sets child 1 of parent
            setRoot(parent);
            return parent;
        }
    }

}