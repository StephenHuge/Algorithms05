import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class KdTree {

    public static final boolean VERTICAL = false;

    private static final boolean HORIZONTAL = true;

    private TreeSet<Node> points;

    public KdTree()                               // construct an empty set of points 
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
        points.add(new Node(p));
    }
    public boolean contains(Point2D p)            // does the set contain point p? 
    {
        validate(p);
        return false;
    }
    public void draw()                         // draw all points to standard draw
    {}
    public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle (or on the boundary)
    {
        if (rect == null)   throw new java.lang.IllegalArgumentException();
        return null;
    }
    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        validate(p);
        return null;
    }
    private void validate(Point2D p) {
        if (p == null) throw new java.lang.IllegalArgumentException();
    }
    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
        private boolean orientation;
        private Node father;

        public Node(Point2D p) {
            this(p, null, null, null, VERTICAL);
            if (this.father == null || this.father.orientation == VERTICAL)    
                this.orientation = HORIZONTAL;
            if ((this.orientation == HORIZONTAL && this.p.x() < this.father.p.x()) ||
                    (this.orientation == VERTICAL && this.p.y() < this.father.p.y())) {
                this.father.lb = this;
            } else {
                this.father.rt = this;
            }
        }  
        public Node(Point2D p, RectHV rect, Node lb, Node rt, boolean orientation) {
            super();
            this.p = p;
            this.rect = rect;
            this.lb = lb;
            this.rt = rt;
            this.orientation = orientation;
        }

    }
    public static void main(String[] args)                  // unit testing of the methods (optional)
    {}
}
