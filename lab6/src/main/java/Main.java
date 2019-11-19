import com.google.common.util.concurrent.AtomicDouble;

import java.util.Random;
import java.util.stream.IntStream;

public class Main {
    private static final int length=(int)Math.pow(2,17);
    public static void main(String[] args) throws Exception {

        System.out.println(length);
        double[] first=new double[length];
        double[] second=new double[length];

        for (int i = 0; i < length; i++) {
            first[i]= new Random().nextDouble();
            second[i]= new Random().nextDouble();
        }

        PolynomialSequential f= new PolynomialSequential(first);
        PolynomialSequential s= new PolynomialSequential(second);

        AtomicDouble[]prod=new AtomicDouble[length*2];
        for (int i = 0; i < length * 2; i++) {
            prod[i]= new AtomicDouble(0.0);
        }

        long start = System.nanoTime();
        IntStream.range(0,length).parallel().forEach(i->IntStream.range(0,length).parallel()
                .forEach(j->prod[i+j].addAndGet(first[i]*second[j])));
        //parallel multiplication with Naive method
        System.out.println("NP:"+((System.nanoTime()-start)/1000000.0)+" ms");
        start = System.nanoTime();
        PolynomialSequential pres = f.naiveMultiply(s);
        System.out.println("NS:"+((System.nanoTime()-start)/1000000.0)+" ms");

//        Arrays.stream(pres.coefficients).forEach(ss->System.out.print(ss+" "));

        PolynomialRKOp p = new PolynomialRKOp(first,second);
//        System.out.println("\n");
        start = System.nanoTime();
        double[] compute = p.compute();
        System.out.println("KP:"+((System.nanoTime()-start)/1000000.0)+" ms");
//        Arrays.stream(compute).forEach(ss->System.out.print(ss+" "));

        start = System.nanoTime();
        PolynomialSequential pres2 = f.karatsubaMultiply(s);
        System.out.println("KS:"+((System.nanoTime()-start)/1000000.0)+" ms");

    }
}
