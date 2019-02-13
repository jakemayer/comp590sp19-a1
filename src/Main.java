import java.io.*;
import io.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException, InsufficientBitsLeftException {
        InputStreamBitSource source = new InputStreamBitSource(new FileInputStream("../data/compressed.dat"));
        Message message = Decoder.getMessage(source);
        System.out.println("Original entropy:    " + message.entropy);

        PrintStream printer = new PrintStream(new FileOutputStream("../data/uncompressed.txt"));
        printer.print(message.text);

        OutputStreamBitSink sink = new OutputStreamBitSink(new FileOutputStream("../data/recompressed.dat"));
        Encoder.writeBitMessage(sink, message.text);

        InputStreamBitSource testSource = new InputStreamBitSource(new FileInputStream("../data/recompressed.dat"));
        Message testMessage = Decoder.getMessage(testSource);
        System.out.println("Reduced entropy:     " + testMessage.entropy);

        double theoreticalEntropy = calculateEntropy(Encoder.getCounts(message.text));
        System.out.println("Theoretical Entropy: " + theoreticalEntropy);
        
        // System.out.println("Are the messages identical? " + ((message.text.equals(testMessage.text))? "Yes!":"No :("));
    }

    public static double calculateEntropy(int[] counts){
        int sum = 0;
        for (int count: counts)
            sum += count;
        double entropy = 0;
        for (int count: counts){
            double prob = ((double)count)/((double)sum);
            entropy -= (count == 0)? 0: prob*Math.log(prob)/Math.log(2);
        }
        return entropy;
    }
}