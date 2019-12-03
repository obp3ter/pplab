import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Main {

    private static final int nrSize=5;
    private static final int min = 5;
    private static final int max = 20;
    private static final int nrNumbers = 100000;
    public static final int queueCap=20;

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ArrayList<BigInteger> numbers = new ArrayList<>(nrNumbers);

        IntStream.range(0,nrNumbers).forEach(i->numbers.add(randomBigInteger()));

//        List<ArrayBlockingQueue<BigInteger>> queues1 = new ArrayList<>(nrNumbers - 1);

//        IntStream.range(0,nrNumbers-1).forEach(i->queues1.add(new ArrayBlockingQueue<>(queueCap)));

        List<List<ArrayBlockingQueue<BigInteger>>> queues = new ArrayList<>(nrNumbers - 1);
        List<Callable<Integer>> callables = new ArrayList<>();

//        queues.add(new AddCallable(numbers.get(0), numbers.get(1), queues1.get(0)));

//        IntStream.range(2,nrNumbers).forEach(i->queues.add(new AddCallable(numbers.get(i), queues1.get(i - 2), queues1.get(i - 1))));
        int nrc=nrNumbers/2;
        int it=0;
        while (nrc!=1) {
            ArrayList<ArrayBlockingQueue<BigInteger>> tempList = new ArrayList<>();
            for(int i=0;i<nrc;i++)
                tempList.add(new ArrayBlockingQueue<BigInteger>(queueCap));
            queues.add(tempList);
            nrc=nrc/2+nrc%2;
        }
        ArrayList<ArrayBlockingQueue<BigInteger>> tempList = new ArrayList<>();
        tempList.add(new ArrayBlockingQueue<BigInteger>(queueCap));
        queues.add(tempList);

        IntStream.range(0,nrNumbers/2).forEach(i->callables.add(new AddCallable(numbers.get(2*i),numbers.get(2*i+1),queues.get(0).get(i))));
        IntStream.range(1,queues.size())
                .forEach(i->{
                    int size = queues.get(i - 1).size();
                    IntStream.range(0, size /2)
                            .forEach(j->callables.add(new AddCallable(
                                    queues.get(i-1).get(2*j),
                                    queues.get(i-1).get(2*j+1),
                                    queues.get(i).get(j))));
                    if(size%2==1)
                        queues.get(i).set(queues.get(i).size()-1,queues.get(i-1).get(queues.get(i-1).size()-1));
                });



        ExecutorService executorService = Executors.newFixedThreadPool(8);
        long start=System.nanoTime();
        List<Future<Integer>> futures = executorService.invokeAll(callables);

        printRes(numbers, queues.get(queues.size()-1), futures,"Time: "+((System.nanoTime()-start)/1000000.0)+" ms");


    }

    private static void printRes(ArrayList<BigInteger> numbers, List<ArrayBlockingQueue<BigInteger>> queues, List<Future<Integer>> futures,String resTime) throws InterruptedException, ExecutionException {
        futures.get(futures.size()-1).get();

        StringBuilder res= new StringBuilder();
        ArrayBlockingQueue<BigInteger> resQueue = queues.get(queues.size() - 1);
        resQueue.removeIf(el->el.compareTo(BigInteger.ZERO.subtract(BigInteger.ONE))==0);
        resQueue.forEach(res::append);
        res.reverse();

//        numbers.forEach(System.out::println);

        long start=System.nanoTime();
        BigInteger res2 = numbers.stream().reduce(BigInteger.ZERO, BigInteger::add);
        String resTime2="TimeNormal: "+((System.nanoTime()-start)/1000000.0)+" ms";
        System.out.println(res);
        System.out.println(res2);
        System.out.println(res.toString().equals(res2.toString()));
        System.out.println(resTime);
        System.out.println(resTime2);

    }

    private static BigInteger randomBigInteger(){

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i <random.nextInt((max - min) + 1) + min; i++) {
            sb.append(random.nextInt(9) + 1);
        }
        return new BigInteger(sb.toString());

    }

    private static int log2(int x)
    {
        return (int) (Math.log(x) / Math.log(2));
    }


}
