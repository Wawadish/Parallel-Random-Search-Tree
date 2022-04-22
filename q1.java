package question1;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Random;

public class q1 {

    public static int maxTreeSize;
    public static Room room;
    private static int numThreads;

    public static void main(String[] args) throws InterruptedException {

        parseInputs(args);
        Node root = sequentialSetup();
        timeAndExecute(root);
        validateResults(root);
    }

    private static void parseInputs(String[] args){
        if (args.length != 4) {
            throw new IllegalArgumentException("Invalid arg count question1.q1 n b r t");
        }
        int n = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        double r = Double.parseDouble(args[2]);
        int t = Integer.parseInt(args[3]);
        //System.out.println(String.format("n: %d\tb: %d\tr: %.3f\tt: %d", n, b, r, t));
        if (n <= 1 || b < 2 || r <= 0.0 || r >= 1.0 || t < 1) {
            throw new IllegalArgumentException("Invalid arg values");
        }
        maxTreeSize = n;
        Node.aMaxBranchingFactor = b;
        ExpansionTask.aMaxNodeDistance = r;
        numThreads = t;
    }

    private static Node sequentialSetup(){
        Random rand = new Random();

        // Sequential generate room and obstacles
        room = new Room();

        // Generate valid root (not contained in obstacles)
        Point2D.Double rootPoint;
        do{ rootPoint = room.getPoint(rand);} while (!room.isValidPoint(rootPoint));
        Node root = new Node(rootPoint);

        return  root;
    }

    private static void timeAndExecute(Node root) throws InterruptedException {
        // Setup executor and launch workers
        MyExecutorService executorService = new MyExecutorService(numThreads);

        // Begin parallel execution
        //long start = System.currentTimeMillis();
        executorService.execute(new ExpansionTask(root));
        while(executorService.isActive){
            Thread.sleep(1);
        }
        // Aggregate workers local task count and join threads
        executorService.shutdown();
        //long time = System.currentTimeMillis() - start;

        //System.out.println(String.format("Time taken: %d.%03d seconds", time/1000, time%1000));
    }

    // Ensure the tree exhibits the desired properties given in the assignment
    private static void validateResults(Node root){
        LinkedList<Node> stack = new LinkedList<>(); // don't really care if this is a stack or a queue
        stack.addFirst(root);
        int size = 0;
        while(!stack.isEmpty()){
            size++;
            Node currentNode = stack.pop();
            if(currentNode.size() > Node.aMaxBranchingFactor){
                throw new IllegalStateException("Node has too many children");
            }
            if(!currentNode.getChildren().stream().allMatch(child ->
                    room.isObstacleFree(new Line2D.Double(currentNode.getPoint(), child.getPoint())))){
                throw new IllegalStateException("Invalid child point");
            }
            stack.addAll(currentNode.getChildren());
        }
        if(size != maxTreeSize){
            throw new IllegalStateException(String.format("Invalid size %d expected %d", size, maxTreeSize));
        }
    }
}
