import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Main{

    private static final int size1 = 100;
    private static final int size2 = 100;
    private static final int size3 = 100;
    private static final int size4 = 100;
    private static final int size5 = 100;
    private static final int size6 = 100;
    private static final boolean add = true;
    private static final boolean multiply = true;

    public static void main(String[] args) throws InterruptedException {

        long avg=0;
        int it =250;

        Integer[][] m1 = new Integer[size1][size2];
        Integer[][] m2 = new Integer[size3][size4];
        Integer[][] m_result = new Integer[size5][size6];

        for (int ii = 0; ii < it; ii++) {
            if (multiply) {
                if (size1 != size5 || size2 != size3 || size4 != size6) {
                    System.out.println("matrices do not match for multiplication");
                    return;
                }

                System.out.println("it: "+ii);
                long start = System.nanoTime();
                int nrthreads = size5 * size6;

                ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
                ArrayList<ArrayList<Pair<Integer, Integer>>> multiplier_list = new ArrayList<>();

                for (int i = 0; i < nrthreads; i++) {
                    multiplier_list.add(new ArrayList<>());
                }

                for (int i = 0; i < size5; i++) {
                    for (int j = 0; j < size6; j++) {
                        positions.add(Pair.with(i, j));
                        m1[i][j] = i * size1 + j;
                        m2[i][j] = i * size1 + j;
                    }
                }

                positions.forEach(p -> multiplier_list.get(positions.indexOf(p) % nrthreads).add(positions.get(positions.indexOf(p))));

                ArrayList<Multiplier> multipliers = new ArrayList<>();

                for (int i = 0; i < nrthreads; i++) {
                    multipliers.add(new Multiplier(m1, m2, m_result, multiplier_list.get(i)));
                }

                ExecutorService executorService = Executors.newFixedThreadPool(5);

                executorService.invokeAll(multipliers).stream().map(arrayListFuture -> {
                    try {
                        return arrayListFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return new ArrayList<Pair<Pair<Integer, Integer>, Integer>>();
                }).map(Collection::stream).flatMap(i -> i).collect(Collectors.toList())
                        .forEach(posval -> m_result[posval.getValue0().getValue0()][posval.getValue0().getValue1()] = posval.getValue1());

//                System.out.println("Multiplied in:" + ((System.nanoTime() - start) / 5000000) + " ms " + nrthreads + " Threads");
                avg+=((System.nanoTime() - start) / 1000000);
            }
        }

        System.out.println("Multiplication avg. of "+it+" runs: "+avg/it);

        avg=0;

        for (int ii = 0; ii < it; ii++) {
            if (add) {
                if (size1 != size3 || size1 != size5 || size2 != size4 || size2 != size6) {
                    System.out.println("matrices do not match for add");
                    return;
                }

                System.out.println("it: "+ii);
                long start = System.nanoTime();
                int nrthreads = size5 * size6;

                ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
                ArrayList<ArrayList<Pair<Integer, Integer>>> adder_lists = new ArrayList<>();

                for (int i = 0; i < nrthreads; i++) {
                    adder_lists.add(new ArrayList<>());
                }

                for (int i = 0; i < size5; i++) {
                    for (int j = 0; j < size6; j++) {
                        positions.add(Pair.with(i, j));
                        m1[i][j] = i * size1 + j;
                        m2[i][j] = i * size1 + j;
                    }
                }

                positions.forEach(p -> adder_lists.get(positions.indexOf(p) % nrthreads).add(positions.get(positions.indexOf(p))));

                ArrayList<Adder> adders = new ArrayList<>();

                for (int i = 0; i < nrthreads; i++) {
                    adders.add(new Adder(m1, m2, m_result, adder_lists.get(i)));
                }

                ExecutorService executorService = Executors.newFixedThreadPool(5);

                executorService.invokeAll(adders).stream().map(arrayListFuture -> {
                    try {
                        return arrayListFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return new ArrayList<Pair<Pair<Integer, Integer>, Integer>>();
                    }

                }).map(Collection::stream).flatMap(i -> i).map(posval -> m_result[posval.getValue0().getValue0()][posval.getValue0().getValue1()] = posval.getValue1()).collect(Collectors.toList());


//                System.out.println("Multiplied in:" + ((System.nanoTime() - start) / 5000000) + " ms " + nrthreads + " Threads");
                avg+=((System.nanoTime() - start) / 1000000);
            }
        }

        System.out.println("Addition avg. of "+it+" runs: "+avg/it);


    }


}
