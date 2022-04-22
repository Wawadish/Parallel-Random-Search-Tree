package question1;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class Room {

    // Width and height of an obstacle square
    private static double obstacleWidth = 0.05;
    // Width and height of the room
    private static double roomWidth = 1.0;

    // Room "border"
    private Rectangle2D.Double aOuterSquare;
    private ArrayList<Rectangle2D.Double> aObstacles = new ArrayList<>();
    
    public Room(){

        Random rand = new Random();

        aOuterSquare = new Rectangle2D.Double(0.0, 0.0, roomWidth, roomWidth);
        Rectangle2D bounds = aOuterSquare.getBounds2D();

        // Create obstacles strictly within the room's border
        for(int i = 0; i < 20; i++) {
            double x;
            double y;
            do {
                x = rand.nextDouble(bounds.getMinX() + obstacleWidth, bounds.getMaxX() - obstacleWidth);
                y = rand.nextDouble(bounds.getMinY() + obstacleWidth, bounds.getMaxY() - obstacleWidth);
            } while (x == bounds.getMinX() || y == bounds.getMinY()); // ensures obstacle is strictly within room

            aObstacles.add(new Rectangle2D.Double(x, y, obstacleWidth, obstacleWidth));
        }
    }

    // Returns true if the line intersects an obstacle, false otherwise. Assumes both line points are strictly within
    // the room.
    public boolean isObstacleFree(Line2D.Double line){
        return aObstacles.stream().allMatch(ob -> !ob.intersectsLine(line));
    }

    // Returns true if the point is not contained in any obstacles, assumes the point is strictly within the room
    public boolean isValidPoint(Point2D.Double point){
        return aObstacles.stream().allMatch(ob -> !ob.contains(point));
    }

    // Generates a point strictly within the room (the point cannot be on the room's border).
    // The point may be contained within obstacles.
    public Point2D.Double getPoint(Random r) {
        Rectangle2D bounds = aOuterSquare.getBounds2D();
        double x;
        double y;
        do {
            x = r.nextDouble(bounds.getMinX(), bounds.getMaxX());
            y = r.nextDouble(bounds.getMinY(), bounds.getMaxY());
        } while (x == bounds.getMinX() || y == bounds.getMinY());
        return new Point2D.Double(x, y);
    }
    
}
