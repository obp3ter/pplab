import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final int min=0;
    private static final int max=1000;
    private static final int min_it=1000;
    private static final int max_it=1000;
    public static void main(String[] args) {
        ArrayList<Integer> inputSequence = new ArrayList<>();

        for (int i = 0; i < (Math.random()*((max_it-min_it)+1))+min_it; i++) {
            inputSequence.add((int)((Math.random()*((max-min)+1))+min));
//            inputSequence.add(i);
        }

        ArrayList<List<Integer>> partialResults = new ArrayList<>();

        partialResults.add(inputSequence);


        int j=1;
        long start = System.nanoTime();
        for(;j<=log2(inputSequence.size());++j) {
            List<Integer> prev = partialResults.get(j - 1);
            partialResults.add(IntStream.range(0, prev.size() / 2).parallel().map(i -> i * 2).mapToObj(i -> prev.get(i) + prev.get(i + 1)).collect(Collectors.toList()));
        }


        inputSequence.forEach(v-> System.out.print(v.toString()+ "\t"));
        System.out.println("\n");

        List<Integer> result = IntStream.range(1,inputSequence.size()+1).parallel().mapToObj(i->IntStream.range(1,log2(inputSequence.size())+1).parallel()
                .map(pow->(i>>pow&1)==1?partialResults.get(pow).get((i/(int)Math.pow(2,pow))-1):0).sum()+((i%2==1)?inputSequence.get(i-1):0)).collect(Collectors.toList());

        System.out.println("Time: "+((System.nanoTime()-start)/1000000.0)+" ms");

        result.forEach(v-> System.out.print(v.toString()+ "\t"));

        ArrayList<Integer> result2= new ArrayList<>();

        AtomicInteger partial = new AtomicInteger(0);
        start = System.nanoTime();
        IntStream.range(0,inputSequence.size()).forEach(i->result2.add(partial.addAndGet(inputSequence.get(i))));
        System.out.println("Time: "+((System.nanoTime()-start)/1000000.0)+" ms");
    }

    private static int log2(int x)
    {
        return (int) (Math.log(x) / Math.log(2));
    }

}
