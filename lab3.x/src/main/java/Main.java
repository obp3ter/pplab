import com.google.common.collect.Streams;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Main {
    private static final int size1 = 500;
    private static final int size2 = 500;
    private static final int size3 = 500;
    private static final int size4 = 500;
    private static final int size5 = 500;
    private static final int size6 = 500;
    private static final boolean add = false;
    private static final boolean multiply = true;

    public static void main(String[] args) {

        Integer[][] m1 = new Integer[size1][size2];
        Integer[][] m2 = new Integer[size3][size4];
        Integer[][] m_result = new Integer[size5][size6];

        long avg = 0;
        int it = 250;

        for (int ii = 0; ii < it; ii++) {
            long start = System.nanoTime();

            for (int i = 0; i < size1; i++) {
                for (int j = 0; j < size2; j++) {
                    m1[i][j]=i*size1+j;
                    m2[i][j]=i*size1+j;
                }
            }

            AtomicInteger pos = new AtomicInteger(0);

            if (add)
            {
                Streams.zip(Arrays.stream(m1).map(Arrays::stream).flatMap(i -> i), Arrays.stream(m1).map(Arrays::stream)
                        .flatMap(i -> i), (a,b)->m_result[pos.get()/size5][pos.getAndIncrement()%size5]=a+b).toArray();
            }

            if(multiply)
            {
                IntStream.range(0,size5).parallel().map(i->IntStream.range(0,size6).parallel()
                        .map(j->m_result[i][j] = IntStream.range(0,size4)
                                .map(k->m1[i][k]*m2[k][j]).sum()).sum()).toArray();
            }

//        for (int i = 0; i < size5; i++) {
//            for (int j = 0; j < size6; j++) {
//                System.out.print(m_result[i][j]+"\t");
//            }
//            System.out.println("\n");
//        }

//            System.out.println("Added in:" + ((System.nanoTime() - start) / 1000000)+" ms ");
            avg+= ((System.nanoTime() - start) );

        }

        System.out.println("Done in: "+avg/it);


    }
}
