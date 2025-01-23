package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

class Node {
    LngLat position;
    double cost;
    Node parent;
    int depth;
    boolean central;

    public Node(LngLat position, double cost, Node parent, int depth, boolean central) {
        this.position = position;
        this.cost = cost;
        this.parent = parent;
        this.depth = depth;
        this.central = false;
    }
}