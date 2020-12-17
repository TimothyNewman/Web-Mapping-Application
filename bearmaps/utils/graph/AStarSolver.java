package bearmaps.utils.graph;
import bearmaps.utils.pq.DoubleMapPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.*;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {

    private SolverOutcome outcome;
    private LinkedList<Vertex> solution;
    private double solutionWeight;
    private int totalStates = 0;
    private double timeSpent;

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        Stopwatch aStarTime = new Stopwatch();

        /**Initializing data dtructures**/
        DoubleMapPQ<Vertex> queue = new DoubleMapPQ<>();
        ArrayList<Vertex> visited = new ArrayList();
        HashMap<Vertex, Double> distanceTo = new HashMap<>();
        HashMap<Vertex, Vertex> pathTo = new HashMap<>();
        solution = new LinkedList<>();

        /**Initial values in Hashmaps with "start" */
        distanceTo.put(start, 0.0);
        pathTo.put(start, start);

        queue.insert(start, input.estimatedDistanceToGoal(start, end));

        /**Working through items in PQ and exploring edges/neighbors */
        while (queue.size() != 0 && aStarTime.elapsedTime() < timeout) {

            Vertex myVertex = queue.poll();
            totalStates++;
            if (visited.contains(myVertex)) {
                continue;
            }
            /**Relax method */
            for (WeightedEdge<Vertex> neighbor : input.neighbors(myVertex)) {

                Vertex p = neighbor.from();
                Vertex q = neighbor.to();
                double w = neighbor.weight();
                /**Determines whether we follow this path or not */
                if (!distanceTo.containsKey(q) || distanceTo.get(p) + w < distanceTo.get(q)) {
                    distanceTo.put(q, distanceTo.get(p) + w);
                    if (queue.contains(q)) {
                        queue.changePriority(q, distanceTo.get(q)
                                + input.estimatedDistanceToGoal(q, end));
                    } else {
                        queue.insert(q, distanceTo.get(q)
                                + input.estimatedDistanceToGoal(q, end));
                    }
                    pathTo.put(q, p);
                }
            }

            /**Adds the explored vertex to the visited array */
            visited.add(myVertex);
            if (myVertex.equals(end)) {
                Vertex current = myVertex;
                while (!current.equals(start)) {
                    solution.addFirst(current);
                    current = pathTo.get(current);
                }
                solution.addFirst(start);
                System.out.println(pathTo);
                outcome = SolverOutcome.SOLVED;
                solutionWeight = distanceTo.get(myVertex);
                timeSpent = aStarTime.elapsedTime();
                return;
            }
        }
        /**Results if there's a timeout */
        if (timeout <= aStarTime.elapsedTime()) {
            outcome = SolverOutcome.TIMEOUT;
            solution.clear();
            solutionWeight = 0;
            timeSpent = aStarTime.elapsedTime();
            return;
            /**Results if there is no solution */
        } else {
            solutionWeight = 0;
            solution.clear();
            outcome = SolverOutcome.UNSOLVABLE;
            timeSpent = aStarTime.elapsedTime();
        }
    }

    public SolverOutcome outcome() {
        return outcome;
    }

    public List<Vertex> solution() {
        return solution;
    }

    public double solutionWeight() {
        return solutionWeight;
    }

    public int numStatesExplored() {
        return totalStates;
    }

    public double explorationTime() {
        return timeSpent;
    }
}