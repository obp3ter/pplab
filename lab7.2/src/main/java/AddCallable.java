import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class AddCallable implements Callable<Integer> {
    private ArrayBlockingQueue<BigInteger> queueIN1;
    private ArrayBlockingQueue<BigInteger> queueIN2;
    private ArrayBlockingQueue<BigInteger> queueOUT;
    BigInteger remainder = BigInteger.ZERO;
    BigInteger sum = BigInteger.ZERO;
    BigInteger digitIn;
    BigInteger digitNr;



    AddCallable(BigInteger number, BigInteger secondNumber, ArrayBlockingQueue<BigInteger> queueOUT) {

        this.queueOUT = queueOUT;

        queueIN1 =new  ArrayBlockingQueue<BigInteger>(Main.queueCap);
        queueIN2 =new  ArrayBlockingQueue<BigInteger>(Main.queueCap);

        queueIN1=splitNumberInQueueIN(number,queueIN1);
        queueIN2=splitNumberInQueueIN(secondNumber,queueIN2);
    }

    AddCallable(BigInteger number, ArrayBlockingQueue<BigInteger> queueIN, ArrayBlockingQueue<BigInteger> queueOUT) {
        this.queueIN2 = queueIN;
        this.queueOUT = queueOUT;
        queueIN1 = new ArrayBlockingQueue<>(Main.queueCap);
        queueIN1=splitNumberInQueueIN(number,queueIN1);
    }


    @Override
    public Integer call() throws Exception {



        do{
            getNextInDigit();
            getNextNrDigit();

            sum = digitIn.add(digitNr).add(remainder);

            if (sum.compareTo(BigInteger.TEN) >= 0) {
                remainder = sum.divide(BigInteger.TEN);
                sum = sum.mod(BigInteger.TEN);
            } else {
                remainder = BigInteger.ZERO;
            }
            if(sum.compareTo(BigInteger.ZERO)!=0 || !doneIn || !doneNr)
            queueOUT.offer(sum);



        }while (!doneNr || !doneIn);

        queueOUT.offer(new BigInteger("-1"));


        return null;
    }

    private boolean doneIn = false;

    private void getNextInDigit() throws InterruptedException {
        if(doneIn){
            return;
        }
        digitIn = this.queueIN2.poll(5, TimeUnit.SECONDS);
        if (Objects.requireNonNull(digitIn).compareTo(new BigInteger("-1"))==0) {
            doneIn = true;
            digitIn = BigInteger.ZERO;
        }
    }

    private boolean doneNr = false;

    private  void getNextNrDigit() throws InterruptedException {
        if(doneNr){
            return;
        }
        digitNr = this.queueIN1.poll(5, TimeUnit.SECONDS);
        if (Objects.requireNonNull(digitNr).compareTo(new BigInteger("-1"))==0) {
            doneNr = true;
            digitNr = BigInteger.ZERO;
        }
    }

    private ArrayBlockingQueue<BigInteger> splitNumberInQueueIN(BigInteger number, ArrayBlockingQueue<BigInteger> queueIN) {
        queueIN.clear();

        while (number.compareTo(BigInteger.ZERO) != 0) {
            queueIN.add(number.mod(BigInteger.TEN));
            number = number.divide(BigInteger.TEN);
        }

        queueIN.add(new BigInteger("-1"));
        return queueIN;
    }




}
