import java.util.ArrayList;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {
    
    private TreeSet<Point2D> points;
    
    public PointSET()                               // construct an empty set of points 
    {
        points = new TreeSet<>();
    }
    public boolean isEmpty()                      // is the set empty? 
    {
        return size() == 0;
    }
    public int size()                         // number of points in the set 
    {
        return points.size();
    }
    public void insert(Point2D p)              // add the point to the set (if it is not already in the set)
    {
        validate(p);
        points.add(p);
    }
    public boolean contains(Point2D p)            // does the set contain point p? 
    {
        validate(p);
        return points.contains(p);
    }
    public void draw()                         // draw all points to standard draw
    {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01); 
        for (Point2D p2d : points) {
            StdDraw.point(p2d.x(), p2d.y());
        }
    }
    public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle (or on the boundary)
    {
        if (rect == null) throw new java.lang.IllegalArgumentException();
        ArrayList<Point2D> inners = new ArrayList<>();
        for (Point2D p2d : points) {
            if (rect.contains(p2d)) {
                inners.add(p2d);
            }
        }
        return inners;
    }
    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        validate(p);
        Point2D nearest = points.first();
        for (Point2D p2d : points) {
            if (p2d.distanceTo(p) < nearest.distanceTo(p)) nearest = p2d;
        }
        return nearest;
    }

    private void validate(Point2D p) {
        if (p == null) throw new java.lang.IllegalArgumentException();
    }
    public static void main(String[] args)                  // unit testing of the methods (optional)
    {
        PointSET pset = new PointSET();
        
        pset.insert(new Point2D(1, 2));
        pset.insert(new Point2D(2, 2));
        pset.insert(new Point2D(3, 2));
        pset.insert(new Point2D(4, 2));
        
        System.out.println(pset.nearest(new Point2D(1.5, 2)));
        
    }
}
