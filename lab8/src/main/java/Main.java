import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Main {
    private static final int size=500;
    private static final int threadCount=8;
    private static final boolean print=true;
    public static void main(String[] args) throws InterruptedException {

        DirectedGraph graph=generateRandomHamiltonian(size);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        ArrayList<FinderCallable> pool=new ArrayList<>();

        for (int i = 0; i < graph.size(); i++){
            pool.add(new FinderCallable(graph, i));
        }
        long start=System.nanoTime();
        List<Future<List<Integer>>> futures = executorService.invokeAll(pool);
        System.out.println("Time: "+((System.nanoTime()-start)/1000000.0)+" ms");
        executorService.shutdown();
        if(print)
        {
            List<Optional<String>> result = futures.stream().map(listFuture -> {
                try {
                    return listFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return null;
                }
            }).filter(Objects::nonNull).map(list -> list
                    .stream().map(Objects::toString).reduce((a, b) -> a + "->" + b)).collect(Collectors.toList());

            System.out.println(result.size()==0?"No cycle found!":"Cycle: "+result.get(0).get());
        }


    }
    private static DirectedGraph generateRandomHamiltonian(int size) {
        DirectedGraph graph = new DirectedGraph(size);

        List<Integer> nodes = graph.getNodes();

        java.util.Collections.shuffle(nodes);

        for (int i = 1; i < nodes.size(); i++){
            graph.addEdge(nodes.get(i - 1),  nodes.get(i));
        }

        graph.addEdge(nodes.get(nodes.size() -1), nodes.get(0));

        Random random = new Random();

        for (int i = 0; i < size / 2; i++){
            int nodeA = random.nextInt(size - 1);
            int nodeB = random.nextInt(size - 1);

            graph.addEdge(nodeA, nodeB);
        }
//        IntStream.range(0,nodes.size()-1).forEach(i->graph.addEdge(i,i+1));
//        graph.addEdge(nodes.size()-1,0);

        return graph;
    }
}
