import edu.princeton.cs.algs4.In;
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
            insert(p, root, root);    // direction is different from root's
        }
        size++;
    }

    /**
     * @param p
     * @param mRoot
     * @param dir this direction is different from root's, it's used for this node
     * @param rect
     * @return
     */
    private Node insert(Point2D p, Node mRoot, Node father) {

        // if root's orientation is vertical, compare x-coordinate, else compare y-coordinate

        int cpr;
        if (father.dir() == VERTICAL)  cpr = Point2D.X_ORDER.compare(p, father.p);
        else                           cpr = Point2D.Y_ORDER.compare(p, father.p); 

        if (mRoot == null) {
            RectHV rectHV;  // get current rect
            if (cpr < 0) {
                rectHV = pruneRectLB(father.p, father.rect, father.dir());
            } else {
                rectHV = pruneRectRT(father.p, father.rect, father.dir());
            }   
            mRoot = new Node(p, rectHV, !father.dir());     
            return mRoot;
        }

        if (mRoot.p.equals(p)) {    
            size--;                                                    // in case there is one p, minus 1 to keep size right
            return mRoot;
        }  
        if (mRoot.dir() == VERTICAL)  cpr = Point2D.X_ORDER.compare(p, mRoot.p);
        else                          cpr = Point2D.Y_ORDER.compare(p, mRoot.p); 
        if (cpr < 0) {
            mRoot.lb = insert(p, mRoot.lb, mRoot);  // recursively insert in left / below
        } else {
            mRoot.rt = insert(p, mRoot.rt, mRoot);  // recursively insert in right / top
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
        int cpr;
        boolean ans = false;
        if (mRoot.dir() == VERTICAL)  cpr = Point2D.X_ORDER.compare(p, mRoot.p);
        else                          cpr = Point2D.Y_ORDER.compare(p, mRoot.p);
        if (cpr < 0)                  ans = contains(p, mRoot.lb);
        else if (cpr > 0)             ans = contains(p, mRoot.rt);
        else {
            if (mRoot.lb != null && mRoot.lb.rect.contains(p)) ans = contains(p, mRoot.lb);
            if (mRoot.rt != null && mRoot.rt.rect.contains(p)) ans = contains(p, mRoot.rt);
            if (mRoot.p.equals(p))  ans = true;
        }

        return ans;
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
    private void range(RectHV rect, Node mRoot, Queue<Point2D> inners) {
        if (mRoot == null)  return;
        if (rect.intersects(mRoot.rect)) {
            range(rect, mRoot.lb, inners);
            range(rect, mRoot.rt, inners);
            if (rect.contains(mRoot.p))  inners.enqueue(mRoot.p);
        }
    }
    public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
    {
        validate(p);
        if (root == null)   return null;
        Point2D ans = root.p;
        ans = nearest(p, root, ans);
        return ans;
    }
    private Point2D nearest(Point2D p, Node mRoot, Point2D ans) {
        if (mRoot == null)  return ans;
        double d = p.distanceSquaredTo(ans);
        ans = d < p.distanceSquaredTo(mRoot.p) ? ans : mRoot.p;

        int cpr;
        if (mRoot.dir() == VERTICAL)  cpr = Point2D.X_ORDER.compare(p, mRoot.p);
        else                          cpr = Point2D.Y_ORDER.compare(p, mRoot.p);
        //        if (cpr < 0)                  ans = contains(p, mRoot.lb);
        //        else if (cpr > 0)             ans = contains(p, mRoot.rt);
        if (cpr < 0) {
            if (mRoot.lb != null && mRoot.lb.rect.distanceSquaredTo(p) < d)    ans = nearest(p, mRoot.lb, ans);
            
            d = p.distanceSquaredTo(ans);
            ans = d < p.distanceSquaredTo(mRoot.p) ? ans : mRoot.p;
            
            if (mRoot.rt != null && mRoot.rt.rect.distanceSquaredTo(p) < d)    ans = nearest(p, mRoot.rt, ans);
        } else {
            if (mRoot.lb != null && mRoot.lb.rect.distanceSquaredTo(p) < d)    ans = nearest(p, mRoot.lb, ans);
            
            d = p.distanceSquaredTo(ans);
            ans = d < p.distanceSquaredTo(mRoot.p) ? ans : mRoot.p;
            
            if (mRoot.rt != null && mRoot.rt.rect.distanceSquaredTo(p) < d)    ans = nearest(p, mRoot.rt, ans);
        }

//        if (mRoot.lb != null && mRoot.lb.rect.distanceSquaredTo(p) < d)    ans = nearest(p, mRoot.lb, ans);
//        if (mRoot.rt != null && mRoot.rt.rect.distanceSquaredTo(p) < d)    ans = nearest(p, mRoot.rt, ans);

        //        boolean lAvaliable = mRoot.lb != null && mRoot.lb.rect.distanceSquaredTo(p) < d;
        //        boolean rAvaliable = mRoot.rt != null && mRoot.rt.rect.distanceSquaredTo(p) < d;
        //        if (lAvaliable || rAvaliable) {
        //            if (cpr < 0)    ans = nearest(p, mRoot.lb, ans);
        //            else            ans = nearest(p, mRoot.rt, ans);
        //        }  
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
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();

        StdDraw.enableDoubleBuffering();
        double qx = in.readDouble();
        double qy = in.readDouble();
        Point2D query = new Point2D(qx, qy);

        while (!in.isEmpty()) {
            String name = in.readString();
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);

            while (true) {
                if (StdDraw.isKeyPressed(0x20)) {
                    //                    StdDraw.clear();
                    StdDraw.pause(100);
                    kdtree.draw();
                    StdDraw.text(p.x(), p.y(), name + " " + p.toString());
                    StdDraw.show();
                    break;
                }
            }
            boolean ans = kdtree.contains(p);
            if (ans)    System.out.println(String.format("contains %s ? %b", p, ans));
            else        System.err.println(String.format("contains %s ? %b", p, ans));

        }

        //        Point2D query = new Point2D(0.71, 0.39);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.05);
        StdDraw.point(query.x(), query.y());
        StdDraw.text(query.x(), query.y(), "query " + query.toString());
        StdDraw.show();

        System.out.println(new Point2D(0.9375, 0).distanceSquaredTo(new Point2D(0.875, 0.25)));
        System.out.println(new Point2D(0.9375, 0).distanceSquaredTo(new Point2D(0.6875, 0.0625)));
    }
}
