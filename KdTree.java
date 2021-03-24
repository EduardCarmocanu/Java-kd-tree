
public class KdTree {
    private Node treeRoot = null;
    private int size = 0;

    public boolean isEmpty() {
        return treeRoot == null;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }

        if (treeRoot == null) {
            treeRoot = new Node(p);
            size++;
        }
        else {
            insert(treeRoot, p);
        }
    }

    private void insert(Node root, Point2D p) {
        double insertionValue;
        double compareValue;

        while (true) {
            if (root.value.equals(p)) return;

            if (root.depth % 2 == 0) {
                insertionValue = p.x();
                compareValue = root.value.x();
            }
            else {
                insertionValue = p.y();
                compareValue = root.value.y();
            }

            if (insertionValue < compareValue) {
                if (root.left == null) {
                    root.left = new Node(p);
                    root.left.depth = root.depth + 1;
                    size++;

                    return;
                }
                else {
                    root = root.left;
                }
            }
            else {
                if (root.right == null) {
                    root.right = new Node(p);
                    root.right.depth = root.depth + 1;
                    size++;

                    return;
                }
                else {
                    root = root.right;
                }
            }
        }
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }

        return find(treeRoot, p);
    }

    private boolean find(Node root, Point2D p) {
        while (root != null) {
            if (root.value.equals(p)) {
                return true;
            }

            double rootCompareValue;
            double searchCompareValue;

            if (root.depth % 2 == 0) {
                rootCompareValue = root.value.x();
                searchCompareValue = p.x();
            }
            else {
                rootCompareValue = root.value.y();
                searchCompareValue = p.y();
            }

            if (searchCompareValue < rootCompareValue) {
                root = root.left;
            }
            else {
                root = root.right;
            }
        }

        return false;
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }

        return findInRange(treeRoot, rect, new LinkedList<Point2D>());
    }

    private Iterable<Point2D> findInRange(Node root, RectHV rect, LinkedList<Point2D> result) {
        if (root == null) {
            return result;
        }

        if (rect.contains(root.value)) {
            result.add(root.value);

            findInRange(root.right, rect, result);
            findInRange(root.left, rect, result);

            return result;
        }

        double dimensionValue;
        double rectDimensionMax;
        double rectDimensionMin;

        if (root.depth % 2 == 0) {
            dimensionValue = root.value.x();
            rectDimensionMax = rect.xmax();
            rectDimensionMin = rect.xmin();
        }
        else {
            dimensionValue = root.value.y();
            rectDimensionMax = rect.ymax();
            rectDimensionMin = rect.ymin();
        }

        if (dimensionValue > rectDimensionMin && dimensionValue < rectDimensionMax) {
            findInRange(root.right, rect, result);
            findInRange(root.left, rect, result);
        }
        else if (rectDimensionMin > dimensionValue) {
            findInRange(root.right, rect, result);
        }
        else if (rectDimensionMax < dimensionValue) {
            findInRange(root.left, rect, result);
        }

        return result;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }

        Node result = findNearest(treeRoot, p);

        return result != null ? result.value : null;
    }

    private Node findNearest(Node root, Point2D p) {
        if (root == null || root.value.equals(p)) {
            return null;
        }

        Node nearest = root;
        Node nearestLeft = null;
        Node nearestRight = null;

        double rootCompValue;
        double targetCompValue;
        Point2D prunePoint;

        if (root.depth % 2 == 0) {
            rootCompValue = root.value.x();
            targetCompValue = p.x();
            prunePoint = new Point2D(rootCompValue, p.y());
        }
        else {
            rootCompValue = root.value.y();
            targetCompValue = p.y();
            prunePoint = new Point2D(p.x(), rootCompValue);
        }

        if (targetCompValue < rootCompValue) {
            nearestLeft = findNearest(root.left, p);
            if (prunePoint.distanceSquaredTo(p) < nearest.value.distanceSquaredTo(p)) {
                nearestRight = findNearest(root.right, p);
            }
        }
        else {
            nearestRight = findNearest(root.right, p);
            if (prunePoint.distanceSquaredTo(p) < nearest.value.distanceSquaredTo(p)) {
                nearestLeft = findNearest(root.left, p);
            }
        }

        if (nearestLeft != null && nearestLeft.value.distanceSquaredTo(p) < nearest.value.distanceSquaredTo(p)) {
            nearest = nearestLeft;
        }

        if (nearestRight != null && nearestRight.value.distanceSquaredTo(p) < nearest.value.distanceSquaredTo(p)) {
            nearest = nearestRight;
        }

        return nearest;
    }

    public void draw() {
        drawAllNodes(treeRoot);
    }

    private void drawAllNodes(Node node) {
        StdDraw.filledCircle(node.value.x(), node.value.y(), 0.01);

        if (node.left != null) {
            drawAllNodes(node.left);
        }

        if (node.right != null) {
            drawAllNodes(node.right);
        }
    }

    private class Node {
        Point2D value;
        Node left;
        Node right;
        int depth = 0;

        public Node(Point2D value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }
}
