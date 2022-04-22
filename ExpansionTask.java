package question1;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class ExpansionTask implements Runnable, Callable<ExpansionTaskResult>{

    public static double aMaxNodeDistance;

    public Node aNodeToExpand;

    public ExpansionTask(Node pNodeToExpand){
        aNodeToExpand = pNodeToExpand;
    }

    @Override
    public ExpansionTaskResult call() throws Exception {
        Point2D.Double nodePoint = aNodeToExpand.getPoint();
        Point2D.Double randPoint;
        Line2D.Double line;
        randPoint = q1.room.getPoint(ThreadLocalRandom.current());
        line = new Line2D.Double(nodePoint, randPoint);

        double segmentLength = length(line);

        if(segmentLength > aMaxNodeDistance || segmentLength == 0 || !q1.room.isObstacleFree(line)){
            return new ExpansionTaskResult( aNodeToExpand, null);
        }

        Node newNode = new Node(randPoint);
        aNodeToExpand.addChild(newNode);
        return new ExpansionTaskResult(aNodeToExpand, newNode);
    }

    @Override
    public void run() {


    }

    // Euclidean distance
    private double length(Line2D.Double line){

        double diffX = (line.getX1() - line.getX2());
        double diffY = (line.getY1() - line.getY2());

        return Math.sqrt(diffX * diffX + diffY * diffY);
    }


}
