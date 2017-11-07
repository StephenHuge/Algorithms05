import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private static final boolean VERTICAL = false;
    
//    private static final boolean HORIZONTAL = true;   // it's never used.

    private int size;
    
    private Node root;

    public KdTree()                               // construct an empty set of points 
    {
        size = 0;
    }
    public boolean isEmpty()                      // is the set empty? 
    {
        return size() == 0;
    }
    public int size()                         // number of points in the set 
    {
        return size;
    }
    public void insert(Point2D p)              // add the point to the set (if it is not already in the set)
    {
        validate(p);
        Node t = root;
        if (t == null) {
            RectHV startRect = new RectHV(0.0, 0.0, 1.0, 1.0);
            root = new Node(p, startRect, VERTICAL);   // insert root node
        } else {
            insert(p, root, !root.dir(), root.rect);    // direction is different from root's
        }
        size++;
//        System.out.println("size is  " + size());
    }
    private Node insert(Point2D p, Node mRoot, boolean dir, RectHV rect) {
        if (mRoot == null) {
            mRoot = new Node(p, rect, dir);     
//            System.out.println("insert node : \n" + mRoot);
            return mRoot;
        } 
        if (mRoot.p.equals(p))  return mRoot;
        // if root's orientation is vertical, compare x-coordinate, else compare y-coordinate
        int cpr;  
        RectHV rectHV;  // get current rect
        if (mRoot.orientation == VERTICAL)  cpr = Point2D.X_ORDER.compare(p, mRoot.p);
        else                                cpr = Point2D.Y_ORDER.compare(p, mRoot.p); 
        if (cpr < 0) {
            rectHV = pruneRectLB(mRoot.p, rect, mRoot.dir());
            mRoot.lb = insert(p, mRoot.lb, !mRoot.dir(), rectHV);  // recursively insert this point
        } else {
            rectHV = pruneRectRT(mRoot.p, rect, mRoot.dir());
            mRoot.rt = insert(p, mRoot.rt, !mRoot.dir(), rectHV);  // recursively insert this point
        }   
        return mRoot;
    }
    
    private RectHV pruneRectLB(Point2D father, RectHV rect, boolean fatherDir) {
        RectHV ans;
        if (fatherDir == VERTICAL)
            ans = new RectHV(rect.xmin(), rect.ymin(), father.x(), rect.ymax()); // get part left 
        else 
            ans = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), father.y()); // get part below 
        return ans;
    }
    private RectHV pruneRectRT(Point2D father, RectHV rect, boolean fatherDir) {
        RectHV ans;
        if (fatherDir == VERTICAL)
            ans = new RectHV(father.x(), rect.ymin(), rect.xmax(), rect.ymax()); // get part right
        else 
            ans = new RectHV(rect.xmin(), father.y(), rect.xmax(), rect.ymax()); // get part top
        return ans;
    }
    public boolean contains(Point2D p)            // does the set contain point p? 
    {
        validate(p);
        return contains(p, root);
    }
    private boolean contains(Point2D p, Node mRoot) {
        if (mRoot == null)   return false;
        if (mRoot.p.equals(p))  return true;
        boolean lAns = contains(p, mRoot.lb);
        boolean rAns = contains(p, mRoot.rt);
        return lAns || rAns;
    }
    public void draw()                         // draw all points to standard draw
    {
        Node n = root;
        draw(n);
    }
    private void draw(Node node) {
        if (node == null)   return;
        
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.point(node.p.x(), node.p.y());
        StdDraw.setPenRadius();
        
        if (node.dir() == VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.p.x(), node.rect().ymin(), node.p.x(), node.rect().ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect().xmin(), node.p.y(), node.rect().xmax(), node.p.y());
        }
        draw(node.lb);
        draw(node.rt);
        
    }
    public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle (or on the boundary)
    {
        if (rect == null)   throw new java.lang.IllegalArgumentException();
        Queue<Point2D> inners = new Queue<>();
        range(rect, root, inners);
        return inners;
    }
    private Iterable<Point2D> range(RectHV rect, Node mRoot, Queue<Point2D> inners) {
        if (mRoot == null)  return inners;
        if (rect.contains(mRoot.p))  inners.enqueue(mRoot.p);
        range(rect, mRoot.lb, inners);
        range(rect, mRoot.rt, inners);
        return inners;
    }
    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        validate(p);
        Point2D ans = root.p;
        ans = nearest(p, root, ans);
        return ans;
    }
    private Point2D nearest(Point2D p, Node mRoot, Point2D ans) {
        if (mRoot == null)  return ans;
        double d = p.distanceSquaredTo(ans);
        ans = d < p.distanceSquaredTo(mRoot.p) ? ans : mRoot.p;
        
        if (mRoot.lb != null && mRoot.lb.rect.distanceSquaredTo(p) < d)    ans = nearest(p, mRoot.lb, ans);
        if (mRoot.rt != null && mRoot.rt.rect.distanceSquaredTo(p) < d)    ans = nearest(p, mRoot.rt, ans);
        
        return  ans;
    }
    private void validate(Point2D p) {
        if (p == null) throw new java.lang.IllegalArgumentException();
    }
    private static class Node {
        private final Point2D p;      // the point
        private final RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private final boolean orientation;
        
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D mP, RectHV mRect, boolean mOrientation) {
            this.p = mP;
            this.rect = mRect;
            this.lb = null;
            this.rt = null;
            this.orientation = mOrientation;
        }
        public boolean dir() {
            return orientation;
        }
        public RectHV rect() {
            return rect;
        }
        @Override
        public String toString() {
            return "(p=" + p + ", \nrect=" + rect + 
                   ", \nlb=" + lb + ", \nrt=" + rt + 
                   ", \norientation=" + orientation + ")";
        }
        
    }
    public static void main(String[] args)                  // unit testing of the methods (optional)
    {
        // TODO
    }
}
