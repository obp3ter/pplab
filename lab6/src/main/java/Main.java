import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {
    public static final int length=(int)Math.pow(2,5);
    public static void main(String[] args) throws Exception {
//        double[] first= {2.1,3.1,2.2,4.5,5.6,7.8,1.1,12};
//        double[] Second= {2.1,3.1,2.2,4.5,5.6,7.8,1.1,12};

        System.out.println(length);
        double[] first=new double[length];
        double[] second=new double[length];

        for (int i = 0; i < length; i++) {
            first[i]= new Random().nextDouble();
            second[i]= new Random().nextDouble();
        }

        PolynomialSequential f= new PolynomialSequential(first);
        PolynomialSequential s= new PolynomialSequential(second);

        double[]prod=new double[length*2];
        long start = System.nanoTime();
        IntStream.range(0,length).parallel().forEach(i->IntStream.range(0,length).parallel().forEach(j->prod[i+j]+=first[i]*second[j]));
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
