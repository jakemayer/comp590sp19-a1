public class OptimalNode {
    public OptimalNode left = null;
    public OptimalNode right = null;
    public boolean isLeaf = false;
    public int frequency = 0;
    public char value = 0;

    public OptimalNode(){}

    public OptimalNode(char value, int frequency){
        this.value = value;
        this.frequency = frequency;
        isLeaf = true;
    }
}