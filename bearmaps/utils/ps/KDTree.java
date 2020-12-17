package bearmaps.utils.ps;
import java.util.List;


public class KDTree implements PointSet {
    KDTreeNode prevNode = null;

    public KDTree(List<Point> points) {

        for (Point pointNode : points) {
            KDTreeNode currentNode = new KDTreeNode(pointNode, null, null);
            if (prevNode == null) {
                prevNode = currentNode;
                continue;
            }
            insertHelper(currentNode, prevNode);
        }
    }
    private void insertHelper(KDTreeNode newNode, KDTreeNode oldNode) {
        if (newNode.point.getX() == oldNode.point.getX()
                && newNode.point.getY() == oldNode.point.getY()) {
            return;
        }

        if (oldNode.xSplit) {
            newNode.xSplit = false;
            if (newNode.point.getX() < oldNode.point.getX()) {
                if (oldNode.left == null) {
                    oldNode.left = newNode;
                } else {
                    insertHelper(newNode, oldNode.left);
                }
            }
            if (newNode.point.getX() >= oldNode.point.getX()) {
                if (oldNode.right == null) {
                    oldNode.right = newNode;
                } else {
                    insertHelper(newNode, oldNode.right);
                }
            }
        } else {
            newNode.xSplit = true;
            if (newNode.point.getY() < oldNode.point.getY()) {
                if (oldNode.left == null) {
                    oldNode.left = newNode;
                } else {
                    insertHelper(newNode, oldNode.left);
                }
            }
            if (newNode.point.getY() >= oldNode.point.getY()) {
                if (oldNode.right == null) {
                    oldNode.right = newNode;
                } else {
                    insertHelper(newNode, oldNode.right);
                }
            }
        }
    }

    public Point nearest(double x, double y) {
        Point starPoint = new Point(x, y);
        if (prevNode == null) {
            return null;
        }
        return nearestHelper(starPoint, prevNode, prevNode.point);
    }
    public Point nearestHelper(Point findNearest, KDTreeNode node, Point currBest) {
        if (node == null) {
            return currBest;
        }
        double recursiveDistance = Point.distance(findNearest, node.point);
        if (recursiveDistance < Point.distance(findNearest, currBest)) {
            currBest = node.point;
        }

        KDTreeNode good;
        KDTreeNode bad;
        if (node.xSplit) {

            if (findNearest.getX() < node.point.getX()) {
                good = node.left;
                bad = node.right;
            } else {
                good = node.right;
                bad = node.left;
            }
        } else {
            if (findNearest.getY() < node.point.getY()) {
                good = node.left;
                bad = node.right;
            } else {
                good = node.right;
                bad = node.left;
            }
        }
        currBest = nearestHelper(findNearest, good, currBest);
        Point pruneCheck;

        if (node.xSplit) {

            pruneCheck = new Point(node.point.getX(), findNearest.getY());
        } else {
            pruneCheck = new Point(findNearest.getX(), node.point.getY());
        }
        if (Point.distance(findNearest, pruneCheck) < Point.distance(currBest, findNearest)) {
            currBest = nearestHelper(findNearest, bad, currBest);
        }
        return currBest;
    }

    private class KDTreeNode {

        private Point point;
        private KDTreeNode left;
        private KDTreeNode right;
        private boolean xSplit = true;

        KDTreeNode(Point p) {
            this.point = p;
        }

        KDTreeNode(Point p, KDTreeNode left, KDTreeNode right) {
            this.point = p;
            this.left = left;
            this.right = right;
        }

        Point point() {
            return point;
        }

        KDTreeNode left() {
            return left;
        }

        KDTreeNode right() {
            return right;
        }
    }
}