package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;
import java.util.*;

public class FlightAStar {
    public static List<LngLat> astar(LngLat start, LngLat goal, Set<NamedRegion> namedAreas, NamedRegion centralArea, int maxDepth) {
        LngLatHandling handler = new LngLatHandler();

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.cost + heuristic(node.position, goal)));
        Set<Node> closedSet = new HashSet<>();

        Node startNode = new Node(start, 0, null, 0, handler.isInCentralArea(start, centralArea));
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll(); //.poll takes the one in the priorityQueue and pops it aswell

            if (handler.isCloseTo(currentNode.position, goal)) {
                return reconstructPath(currentNode); //returns the found path
            }

            if (currentNode.depth >= maxDepth) {
                continue; // Skip further looking for nodes exceeding maximum depth
            }

            closedSet.add(currentNode);

            for (Node successor : getSuccessors(currentNode, namedAreas, centralArea)) {
                int flag = 0;
                for (Node thisnode : closedSet) {

                    //if a node in the closed set is close to the successor then break out the outside for loop with next if
                    if (isClose(thisnode.position, successor.position)) {
                        flag = 1;
                        break;
                    }
                }
                if (flag == 1) {
                    continue;
                }
                double tentativeCost = currentNode.cost + heuristic(currentNode.position, successor.position);

                boolean isin = false;
                for (Node thisnode : openSet) {
                    //if a successor node is in the openset already
                    if (isClose(thisnode.position, successor.position)) {
                        isin = true;
                    }
                }
                if (!isin || tentativeCost < successor.cost) {
                    successor.cost = tentativeCost + heuristic(successor.position,goal);
                    successor.parent = currentNode;
                    successor.depth = currentNode.depth + 1;
                    if (!isin) {
                        openSet.add(successor);
                    }
                }

            }
        }

        return null; // No path found
    }

    public static double heuristic(LngLat position, LngLat goal) {
        return Math.abs(goal.lng() - position.lng()) + Math.abs(goal.lat() - position.lat());
    }

    public static List<Node> getSuccessors(Node node, Set<NamedRegion> namedAreas, NamedRegion centralArea) {
        List<Node> successors = new ArrayList<>();
        LngLatHandling handler = new LngLatHandler();
        List<LngLat> possibleMoves = generatePossibleMoves(node.position);
        for (LngLat move : possibleMoves) {
            int counter = 0;
            for (NamedRegion region : namedAreas) {
                if (!handler.isInRegion(move, region)) {
                    //if the move is not in central area and the last node is
                    if (!moveInCentralArea(move, centralArea) && node.central) {
                        break;
                    } else {
                        counter += 1;
                    }
                }
            }
            if (counter == namedAreas.size()) { //added
                successors.add(new Node(move, Double.POSITIVE_INFINITY, null, 0, handler.isInCentralArea(move, centralArea)));
            }
        }

        return successors;
    }

    public static boolean moveInCentralArea(LngLat move, NamedRegion centralArea) {
        LngLatHandling handler = new LngLatHandler();
        return handler.isInCentralArea(move, centralArea);
    }

    public static List<LngLat> generatePossibleMoves(LngLat position) {
        LngLatHandling handler = new LngLatHandler();
        List<LngLat> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 16; i++) { //for all the 16 directions
            possibleMoves.add(handler.nextPosition(position, 22.5 * i)); //22.5 for all the angles 360/16
        }
        return possibleMoves;
    }

    public static List<LngLat> reconstructPath(Node node) {
        List<LngLat> path = new ArrayList<>();
        while (node != null) {
            path.add(node.position);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public static boolean isClose(LngLat pos, LngLat pos2) {
        LngLatHandling handler = new LngLatHandler();
        double distance = handler.distanceTo(pos, pos2);
        return distance < 0.0001;
    }
}