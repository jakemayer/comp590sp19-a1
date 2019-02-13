import java.util.*;
import java.io.*;
import io.*;

public class Encoder {
    public static void writeBitMessage(OutputStreamBitSink sink, String message) throws IOException {
        OptimalNode huffmanTree = makeOptimalHuffmanTree(message);
        int[] codeLengths = getCodeLengths(huffmanTree);
        CanonicalNode canonicalTree = Decoder.makeTree(codeLengths);
        int[] codes = getCodes(canonicalTree);
        for (int codeLength: codeLengths)
            sink.write(codeLength, 8);
        sink.write(message.length(), 32);
        for (char c: message.toCharArray())
            sink.write(codes[(int)c], codeLengths[(int)c]);
        sink.padToWord();
    }
    
    public static OptimalNode makeOptimalHuffmanTree(String message){
        int[] counts = getCounts(message);
        PriorityQueue<OptimalNode> queue = new PriorityQueue<OptimalNode>(256, new NodeComparator());
        for (int i = 0; i < 256; i++)
            queue.add(new OptimalNode((char)i, counts[i]));
        while (queue.size() > 1){
            OptimalNode A = queue.poll(), B = queue.poll();
            OptimalNode sum = new OptimalNode();
            sum.left = A;
            sum.right = B;
            sum.frequency = A.frequency + B.frequency;
            queue.add(sum);
        }
        return queue.poll();
    }

    public static int[] getCounts(String message){
        int[] counts = new int[256];
        for (char c: message.toCharArray())
            counts[(int)c]++;
        return counts;
    }

    public static int[] getCodeLengths(OptimalNode root){
        int[] lengths = new int[256];
        findLeafLength(root, 0, lengths);
        return lengths;
    }

    public static void findLeafLength(OptimalNode node, int length, int[] lengths){
        if (node.isLeaf)
            lengths[(int)node.value] = length;
        else {
            findLeafLength(node.left, length + 1, lengths);
            findLeafLength(node.right, length + 1, lengths);
        }
    }

    public static int[] getCodes(CanonicalNode root){
        int[] codes = new int[256];
        findLeafCode(root, 0, codes);
        return codes;
    }

    public static void findLeafCode(CanonicalNode node, int code, int[] codes){
        if (node.isLeaf)
            codes[(int)node.value] = code;
        else {
            findLeafCode(node.left, code << 1, codes);
            findLeafCode(node.right, (code << 1) + 1, codes);
        }
    }
}