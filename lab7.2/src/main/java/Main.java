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
    private static final int nrNumbers = 10000;
    public static final int queueCap=20;

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ArrayList<BigInteger> numbers = new ArrayList<>(nrNumbers);

        IntStream.range(0,nrNumbers).forEach(i->numbers.add(randomBigInteger()));

        List<ArrayBlockingQueue<BigInteger>> queues = new ArrayList<>(nrNumbers - 1);

        IntStream.range(0,nrNumbers-1).forEach(i->queues.add(new ArrayBlockingQueue<>(queueCap)));

        List<Callable<Integer>> callables = new ArrayList<>(nrNumbers - 1);

        callables.add(new AddCallable(numbers.get(0), numbers.get(1), queues.get(0)));

        IntStream.range(2,nrNumbers).forEach(i->callables.add(new AddCallable(numbers.get(i), queues.get(i - 2), queues.get(i - 1))));

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        List<Future<Integer>> futures = executorService.invokeAll(callables);
        printRes(numbers, queues, futures);


    }

    private static void printRes(ArrayList<BigInteger> numbers, List<ArrayBlockingQueue<BigInteger>> queues, List<Future<Integer>> futures) throws InterruptedException, ExecutionException {
        futures.get(futures.size()-1).get();

        StringBuilder res= new StringBuilder();
        ArrayBlockingQueue<BigInteger> resQueue = queues.get(queues.size() - 1);
        resQueue.removeIf(el->el.compareTo(BigInteger.ZERO.subtract(BigInteger.ONE))==0);
        resQueue.forEach(res::append);
        res.reverse();

//        numbers.forEach(System.out::println);

        BigInteger res2 = numbers.stream().reduce(BigInteger.ZERO, BigInteger::add);
        System.out.println(res);
        System.out.println(res2);
        System.out.println(res.toString().equals(res2.toString()));
    }


    private static BigInteger randomBigInteger(){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i <random.nextInt((max - min) + 1) + min; i++) {
            sb.append(random.nextInt(9) + 1);
        }

        return new BigInteger(sb.toString());

    }


}
