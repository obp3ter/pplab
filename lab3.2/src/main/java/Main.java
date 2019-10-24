import org.javatuples.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Main{

    private static final int size1 = 50;
    private static final int size2 = 50;
    private static final int size3 = 50;
    private static final int size4 = 50;
    private static final int size5 = 50;
    private static final int size6 = 50;
    private static final boolean add = true;
    private static final boolean multiply = true;

    public static void main(String[] args) throws InterruptedException {

        Integer[][] m1 = new Integer[size1][size2];
        Integer[][] m2 = new Integer[size3][size4];
        Integer[][] m_result = new Integer[size5][size6];

        if(add){
            if(size1!=size3 || size1!=size5 ||size2!=size4||size2!=size6)
            {
                System.out.println("matrices do not match for add");
                return;
            }



            for (int ii = 1; ii < size5*size6+1; ii++) {
                long start = System.nanoTime();
                int nrthreads = ii;

                ArrayList<Pair<Integer,Integer>> positions = new ArrayList<>();
                ArrayList<ArrayList<Pair<Integer,Integer>>> adder_lists = new ArrayList<>();

                for (int i = 0; i < nrthreads; i++) {
                    adder_lists.add(new ArrayList<>());
                }

                for (int i = 0; i < size5; i++) {
                    for (int j = 0; j < size6; j++) {
                        positions.add(Pair.with(i,j));
                        m1[i][j]=i*size1+j;
                        m2[i][j]=i*size1+j;
                    }
                }

                positions.forEach(p -> adder_lists.get(positions.indexOf(p)%nrthreads).add(positions.get(positions.indexOf(p))));

//            adder_lists.get(0).forEach(pair -> System.out.println(pair.getValue0() + " " + pair.getValue1()));

                ArrayList<Adder> adders = new ArrayList<>();

                for (int i = 0; i < nrthreads; i++) {
                    adders.add(new Adder(m1,m2,m_result, adder_lists.get(i)));
                }

                ExecutorService executorService = Executors.newFixedThreadPool(5);

                List<Future<ArrayList<Pair<Pair<Integer, Integer>, Integer>>>> results = executorService.invokeAll(adders);

                results.stream().map(arrayListFuture -> {
                    try {
                        return arrayListFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return new ArrayList<Pair<Pair<Integer,Integer>,Integer>>();
                    }

                }).map(Collection::stream).flatMap(i -> i).map(posval -> m_result[posval.getValue0().getValue0()][posval.getValue0().getValue1()]=posval.getValue1()).collect(Collectors.toList());


//                ArrayList<Thread> threads = new ArrayList<>();
//
//                adders.forEach( adder -> threads.add(new Thread(adder)));
//                threads.forEach(Thread::run);
//                for (Thread thread : threads) {
//                    try {
//                        thread.join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

        for (int i = 0; i < size5; i++) {
            for (int j = 0; j < size6; j++) {
                System.out.print(m_result[i][j]+"\t");
            }
            System.out.print("\n");
        }
                System.out.println("Added in:" + ((System.nanoTime() - start) / 5000000)+" ms "+nrthreads+" Threads");
            }
        }

        if(multiply)
        {
            if(size1!=size5 || size2!=size3 ||size4!=size6)
            {
                System.out.println("matrices do not match for multiplication");
                return;
            }

            for (int ii = 1; ii < size5*size6+1; ii++) {
                long start = System.nanoTime();
                int nrthreads = ii;

                ArrayList<Pair<Integer,Integer>> positions = new ArrayList<>();
                ArrayList<ArrayList<Pair<Integer,Integer>>> multiplier_list = new ArrayList<>();

                for (int i = 0; i < nrthreads; i++) {
                    multiplier_list.add(new ArrayList<>());
                }

                for (int i = 0; i < size5; i++) {
                    for (int j = 0; j < size6; j++) {
                        positions.add(Pair.with(i,j));
                        m1[i][j]=i*size1+j;
                        m2[i][j]=i*size1+j;
                    }
                }

                positions.forEach(p -> multiplier_list.get(positions.indexOf(p)%nrthreads).add(positions.get(positions.indexOf(p))));

//            adder_lists.get(0).forEach(pair -> System.out.println(pair.getValue0() + " " + pair.getValue1()));

                ArrayList<Multiplier> multipliers = new ArrayList<>();

                for (int i = 0; i < nrthreads; i++) {
                    multipliers.add(new Multiplier(m1,m2,m_result, multiplier_list.get(i)));
                }

                ExecutorService executorService = Executors.newFixedThreadPool(5);

                List<Future<ArrayList<Pair<Pair<Integer, Integer>, Integer>>>> results = executorService.invokeAll(multipliers);

                results.stream().map(arrayListFuture -> {
                    try {
                        return arrayListFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return new ArrayList<Pair<Pair<Integer,Integer>,Integer>>();
                }).map(Collection::stream).flatMap(i -> i).collect(Collectors.toList())
                        .forEach(posval -> m_result[posval.getValue0().getValue0()][posval.getValue0().getValue1()]=posval.getValue1());
//                ArrayList<Thread> threads = new ArrayList<>();
//
//                multipliers.forEach( adder -> threads.add(new Thread(adder)));
//                threads.forEach(Thread::run);
//                for (Thread thread : threads) {
//                    try {
//                        thread.join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

//        for (int i = 0; i < size5; i++) {
//            for (int j = 0; j < size6; j++) {
//                System.out.print(m_result[i][j]+"\t");
//            }
//            System.out.print("\n");
//        }
                System.out.println("Multiplied in:" + ((System.nanoTime() - start) / 5000000)+" ms "+nrthreads+" Threads");
            }
        }

    }


}
