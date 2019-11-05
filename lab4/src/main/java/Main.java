import org.javatuples.Pair;
import org.thavam.util.concurrent.blockingMap.BlockingHashMap;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    static final int size1 = 50;
    static final int size2 = 50;
    static final int size3 = 50;
    static final int size4 = 50;
    static final int size5 = 50;
    static final int size6 = 50;
    static final boolean add = false;
    static final boolean multiply = true;
    static final boolean print = false;
    static final int tc1=5;
    static final int tc2=5;

    public static void main(String[] args) throws InterruptedException {

        BlockingHashMap<Pair<Integer, Integer>, Integer> m1 = new BlockingHashMap<>();
        BlockingHashMap<Pair<Integer, Integer>, Integer> m2 = new BlockingHashMap<>();
        BlockingHashMap<Pair<Integer, Integer>, Integer> m3 = new BlockingHashMap<>();
        Integer[][] m_result_m = new Integer[size5][size6];
        long avg=0;
        int it=250;

//        if (add) {
//            if (size1 != size3 || size1 != size5 || size2 != size4 || size2 != size6) {
//                System.out.println("matrices do not match for add");
//                return;
//            }
//
//
//            BlockingHashMap<Pair<Integer, Integer>, Integer> m_result = new BlockingHashMap<>();
//            BlockingHashMap<Pair<Integer, Integer>, Integer> m_partial_result = new BlockingHashMap<>();
//
//            for (int ii = 1; ii < size5 * size6 + 1; ii++) {
//                long start = System.nanoTime();
//                int nrthreads = size5*size6;
//
//                ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
//                ArrayList<ArrayList<Pair<Integer, Integer>>> adder_lists = new ArrayList<>();
//
//                for (int i = 0; i < nrthreads; i++) {
//                    adder_lists.add(new ArrayList<>());
//                }
//
//                for (int i = 0; i < size5; i++) {
//                    for (int j = 0; j < size6; j++) {
//                        positions.add(Pair.with(i, j));
//                        m1.put(Pair.with(i, j), i * size1 + j);
//                        m2.put(Pair.with(i, j), i * size1 + j);
//                        m3.put(Pair.with(i, j), i * size1 + j);
//                    }
//                }
//
//                positions.forEach(p -> adder_lists.get(positions.indexOf(p) % nrthreads).add(positions.get(positions.indexOf(p))));
//
//                ArrayList<Adder> adders = new ArrayList<>();
//
//                for (int i = 0; i < nrthreads; i++) {
//                    adders.add(new Adder(m1, m2, m_partial_result, adder_lists.get(i)));
//                }
//                for (int i = 0; i < nrthreads; i++) {
//                    adders.add(new Adder(m_partial_result, m3, m_result, adder_lists.get(i)));
//                }
//
//                ExecutorService executorService = Executors.newFixedThreadPool(5);
//
//                executorService.invokeAll(adders);
//
//                IntStream.range(0, size5).mapToObj(iii -> IntStream.range(0, size6).mapToObj(jjj -> m_result_m[iii][jjj] = m_result.get(Pair.with(iii, jjj))).collect(Collectors.toList())).collect(Collectors.toList());
//
//
//                if (print) {
//                    for (int i = 0; i < size5; i++) {
//                        for (int j = 0; j < size6; j++) {
//                            System.out.print(m_result_m[i][j] + "\t");
//                        }
//                        System.out.print("\n");
//                    }
//                }
//                System.out.println("Added in:" + ((System.nanoTime() - start) / 500000) + " ms " + nrthreads + " Threads");
//            }
//        }

        if (multiply) {
            if (size1 != size5 || size2 != size3 || size4 != size6) {
                System.out.println("matrices do not match for multiplication");
                return;
            }


            BlockingHashMap<Pair<Integer, Integer>, Integer> m_result = new BlockingHashMap<>();
            BlockingHashMap<Pair<Integer, Integer>, Integer> m_partial_result = new BlockingHashMap<>();

            for (int ii = 0; ii < it; ii++) {
                long start = System.nanoTime();
                int nrthreads = size5*size6;

                ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
                ArrayList<ArrayList<Pair<Integer, Integer>>> multiplier_list = new ArrayList<>();

                for (int i = 0; i < nrthreads; i++) {
                    multiplier_list.add(new ArrayList<>());
                }

                for (int i = 0; i < size5; i++) {
                    for (int j = 0; j < size6; j++) {
                        positions.add(Pair.with(i, j));
                        m1.put(Pair.with(i, j), i * size1 + j);
                        m2.put(Pair.with(i, j), i * size1 + j);
                        m3.put(Pair.with(i, j), i * size1 + j);
                    }
                }

                positions.forEach(p -> multiplier_list.get(positions.indexOf(p) % nrthreads).add(positions.get(positions.indexOf(p))));

//                ArrayList<Multiplier> multipliers = new ArrayList<>();
//
//                for (int i = 0; i < nrthreads; i++) {
//                    multipliers.add(new Multiplier(m1, m2, m_partial_result, multiplier_list.get(i)));
//                }
//                for (int i = 0; i < nrthreads; i++) {
//                    multipliers.add(new Multiplier(m_partial_result, m2, m_result, multiplier_list.get(i)));
//                }
//
//                ExecutorService executorService = Executors.newFixedThreadPool(5);
//
//                executorService.invokeAll(multipliers);

                ArrayList<Multiplier> multipliers = new ArrayList<>();
                ArrayList<Multiplier> multipliers2 = new ArrayList<>();

                for (int i = 0; i < nrthreads; i++) {
                    multipliers.add(new Multiplier(m1, m2, m_partial_result, multiplier_list.get(i)));
                }
                for (int i = 0; i < nrthreads; i++) {
                    multipliers2.add(new Multiplier(m_partial_result, m3, m_result, multiplier_list.get(i)));
                }

                ExecutorService executorService = Executors.newFixedThreadPool(tc1);

                executorService.invokeAll(multipliers);
                ExecutorService executorService2 = Executors.newFixedThreadPool(tc2);

                executorService2.invokeAll(multipliers2);

                IntStream.range(0, size5).mapToObj(iii -> IntStream.range(0, size6).mapToObj(jjj -> m_result_m[iii][jjj] = m_result.get(Pair.with(iii, jjj))).collect(Collectors.toList())).collect(Collectors.toList());


                if (print) {
                    for (int i = 0; i < size5; i++) {
                        for (int j = 0; j < size6; j++) {
                            System.out.print(m_result_m[i][j] + "\t");
                        }
                        System.out.print("\n");
                    }
                }
//                System.out.println("Multiplied in:" + ((System.nanoTime() - start) / 1000000) + " ms ");
                avg+=((System.nanoTime() - start) );
            }
            System.out.println("Done avg. of "+it+" runs: "+avg/it/1000000);
        }

    }
}
