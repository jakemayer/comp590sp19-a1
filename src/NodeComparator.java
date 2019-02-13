import java.util.*;

public class NodeComparator implements Comparator<OptimalNode> {
    public int compare(OptimalNode A, OptimalNode B){
        return (A.frequency < B.frequency)? -1:1;
    }
}