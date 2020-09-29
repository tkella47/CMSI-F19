/**
 * @author - Thomas Kelly
 * @date - April 12th, 2020
 *
 */
package huffman;

import java.util.*;
import java.io.*;

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------
    private static boolean debug = false;
    private HuffNode trieRoot;
    private TreeMap<Character, String> encodingMap;
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    Huffman (String corpus) {
        //Generate frequency Map
        TreeMap<Character, Integer> distMap = new TreeMap<Character,Integer>();
        for(int i = 0; i < corpus.length(); i++) {
            if(distMap.get(corpus.charAt(i)) == null) {
                distMap.put(corpus.charAt(i),1);
            } else {
                int count = distMap.get(corpus.charAt(i));
                distMap.put(corpus.charAt(i), count + 1);
            }
        }
            //generate pqueue and Nodes. 
        PriorityQueue<HuffNode> queue = new PriorityQueue<HuffNode>();
        for(Map.Entry<Character, Integer> nodeMap : distMap.entrySet()) {
            HuffNode newNode = new HuffNode(nodeMap.getKey(),nodeMap.getValue());
            queue.add(newNode);
        }
        //print(queue.size());
            //create tree
        while(queue.size() > 1) {
            HuffNode firstChild = queue.remove();
            //print("first child: " + firstChild.character + " " + firstChild.count);
            HuffNode secondChild = queue.remove();
            //print("second child: " + secondChild.character + " " + secondChild.count);
            HuffNode parent = new HuffNode('\0',secondChild.count+firstChild.count);
            parent.left = firstChild;   
            parent.right = secondChild;
            //print("new parent created: " + parent.count);
            queue.add(parent);
        }
        trieRoot = queue.remove();
        
        //Now traverse tree to get encoding map.
        //automatically done in recursive method
        encodingMap = new TreeMap<Character,String>();
        preOrder(trieRoot, "");
        
        // Done!
    }
    /**
     * preOrder - traverse tree in preOrder fashion to create Huff Trie
     * @param currentNode - Current node passed as argument
     * @param path - String representation of path 1 = right, 0 = left
     */
    public void preOrder(HuffNode currentNode, String path) {
        if (currentNode.character != '\0') {
            encodingMap.put(currentNode.character, path);

        } else {
            preOrder(currentNode.left, path + "0");
            
            preOrder(currentNode.right, path + "1");
        }
        
        return;
    }
    
    
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as 3 components: (1) the
     *         first byte contains the number of characters in the message,
     *         (2) the bitstring containing the message itself, (3) possible
     *         0-padding on the final byte.
     */
    public byte[] compress (String message) {
        ByteArrayOutputStream test = new ByteArrayOutputStream();
        test.write(message.length());
        String comPath = "";
        for(int i = 0; i < message.length(); i++) {
            comPath += encodingMap.get(message.charAt(i));
            if(comPath.length() >= 8) {
                String temp = comPath.substring(0,8);
                //print(temp);
                test.write(Integer.parseInt(temp,2));
                if(comPath.length() == 8) {
                    comPath = "";
                } else {
                    //print("Pre: " + comPath);
                    comPath = comPath.substring(8,comPath.length());
                    //print("Post: " + comPath);
                }
            } 
        }
        while(comPath.length() % 8 != 0) {
            comPath+="0";
        }
        //print(comPath);
        test.write(Integer.parseInt(comPath,2));
            
        
        return test.toByteArray();
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as 3 components: (1) the
     *        first byte contains the number of characters in the message,
     *        (2) the bitstring containing the message itself, (3) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress (byte[] compressedMsg) {
        int loop = compressedMsg[0];
        String message = "";
        String path = "";
        
        for(int i = 1; i < compressedMsg.length; i++) {
            path += String.format("%8s",Integer.toBinaryString(compressedMsg[i] & 0xFF)).replace(' ','0');
        }
        
        HuffNode currentNode = trieRoot;
        for(int i = 0; i < path.length(); i++) {
            if(path.charAt(i) == '0') {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
            if(currentNode.character != '\0') {
                message += currentNode.character;
                loop--;
                currentNode = trieRoot;
            }
            if(loop == 0) {
                break;
            }
        }
        return message;
    }
    
    /**
     * print
     * @param s - debug method that prints only if debug is true
     */
    public static void print(Object s) {
        if(debug) {
            System.out.println(s);
        }
    }
    /**
     * printA - print always occurs regardless of debug
     * @param s - prints to console
     */
    public static void printA(Object s) {
        System.out.println(s);
    }
    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left and right child), contains
     * a character field that it represents (in the case of a leaf, otherwise
     * the null character \0), and a count field that holds the number of times
     * the node's character (or those in its subtrees, in the case of inner 
     * nodes) appear in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode left, right;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () {
            return left == null && right == null;
        }
        
        public int compareTo (HuffNode other) {
            return this.count - other.count;
        }
        
    }

}
