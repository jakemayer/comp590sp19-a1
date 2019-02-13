import java.util.*;
import java.io.*;
import io.*;

public class Decoder {
    public static Message getMessage(InputStreamBitSource source) throws IOException, InsufficientBitsLeftException {
        CanonicalNode tree = makeTree(source);
        return decodeMessage(source, tree);
    }

    public static Message decodeMessage(InputStreamBitSource source, CanonicalNode root) throws IOException, InsufficientBitsLeftException {
        int length = source.next(32);
        int bitCount = 0;
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++){
            CanonicalNode node = root;
            while (!node.isLeaf){
                node = (source.next(1) == 0)? node.left: node.right;
                bitCount++;
            }
            text.append(node.value);
        }
        
        return new Message(text.toString(), ((double)bitCount)/((double)text.length()));
    }

    public static CanonicalNode makeTree(InputStreamBitSource source) throws IOException, InsufficientBitsLeftException {
        int[] codeLengths = new int[256];
        for (int i = 0; i < 256; i++)
            codeLengths[i] = source.next(8);
        return makeTree(codeLengths);
    }

    public static CanonicalNode makeTree(int[] codeLengths) {
        TreeMap<Integer,List<Character>> map = new TreeMap<Integer,List<Character>>();
        for (int i = 0; i < 256; i++){
            int height = codeLengths[i];
            if (!map.containsKey(height))
                map.put(height, new ArrayList<Character>());
            map.get(height).add((char)i);
        }
        
        CanonicalNode root = new CanonicalNode();
        for (Map.Entry<Integer,List<Character>> entry: map.entrySet()){
            int height = entry.getKey();
            for (char c: entry.getValue())
                placeNode(root, height, c);
        }
        return root;
    }

    public static void placeNode(CanonicalNode node, int height, char c){
        if (height == 0){
            node.value = c;
            node.isLeaf = true;
            node.isFull = true;
        } else {
            if (node.left == null){
                node.left = new CanonicalNode();
                placeNode(node.left, height - 1, c);
            } else if (!node.left.isFull){
                placeNode(node.left, height - 1, c);
            } else if (node.right == null){
                node.right = new CanonicalNode();
                placeNode(node.right, height - 1, c);
            } else if (!node.right.isFull){
                placeNode(node.right, height - 1, c);
            }
            
            if (node.left != null && node.right != null && node.left.isFull && node.right.isFull)
                node.isFull = true;
        }
    }
}