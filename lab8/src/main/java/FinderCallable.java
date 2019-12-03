import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

public class FinderCallable implements Callable<List<Integer>> {
    private DirectedGraph graph;
    private int startingNode;
    private List<Integer> path;
    private List<Integer> result = new ArrayList<>();

    FinderCallable(DirectedGraph graph, int node) {
        this.graph = graph;
        this.startingNode = node;
        path = new ArrayList<>();
    }

    @Override
    public List<Integer> call() {
        return visit(startingNode);
    }

    private List<Integer> visit(int node) {
        path.add(node);

        if (path.size() == graph.size()) {
            if (graph.neighboursOf(node).contains(startingNode)) {
                this.result.clear();
                this.result.addAll(this.path);
                this.result.add(startingNode);
//                System.out.println(this.result.stream().map(Object::toString).reduce((a, b) -> a + " " + b));
            }
            return result;
        }

        for (int neighbour : graph.neighboursOf(node)) {
            if (!this.path.contains(neighbour)) {
//                System.out.println(path.stream().map(Object::toString).reduce((a,b)->a+ " " +b));
                visit(neighbour);
            }
        }
        if (result.size() != graph.size() + 1)
            return null;
        else
            return result;
    }
}
