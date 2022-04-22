package question1;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    // Static
    public static int aMaxBranchingFactor = 0;
    public static AtomicInteger treeSize = new AtomicInteger(); // used for node ids and size

    public int id;
    private volatile int size = 0; // children count
    private LinkedList<Node> children = new LinkedList<>();
    private Point2D.Double aPoint;

    // When nodes are created they are given a unique id in [0, n[
    public Node(Point2D.Double pPoint) {
        aPoint = pPoint;
        id = treeSize.getAndIncrement();
    }

    // There is at most 1 expansion task per node, therefore should not be a  need for concurrency control, but I am
    // a bit brain-dead at the moment of writing this, therefore I've added synchronized anyway for safety
    // (no noticeable performance decrease)
    public synchronized void addChild(Node child){
        if(child.id >= q1.maxTreeSize) return;
        size++;
        children.add(child);
    }

    // Getters
    public LinkedList<Node> getChildren(){
        return children;
    }
    public int size(){
        return size;
    }
    public Point2D.Double getPoint(){
        return aPoint;
    }

}
