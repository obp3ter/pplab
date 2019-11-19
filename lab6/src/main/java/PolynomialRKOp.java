import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class PolynomialRKOp extends RecursiveTask<double[]> {

    private double[] multiplicand;
    private double[] multiplier;

    public PolynomialRKOp(double[] multiplicand, double[] multiplier) throws Exception {

        if (!isPowerOfTwo(multiplicand.length)) {
            throw new Exception("Polynomial should have number of coefficients equal to integral power of 2.");
        }
        this.multiplicand = multiplicand;
        if (!isPowerOfTwo(multiplicand.length)) {
            throw new Exception("Polynomial should have number of coefficients equal to integral power of 2.");
        }
        this.multiplier = multiplier;
    }

    @Override
    protected double[] compute() {
        double[] product = new double[2 * multiplicand.length];

        Arrays.fill(product, 0.0);

        if (multiplicand.length == 1) {
            product[0] = multiplicand[0] * multiplier[0];
            return product;
        }


        List<PolynomialRKOp> toComp= new ArrayList<>();

        int halfArraySize = multiplicand.length / 2;

        double[] multiplicandLow = new double[halfArraySize];
        double[] multiplicandHigh = new double[halfArraySize];
        double[] multiplierLow = new double[halfArraySize];
        double[] multiplierHigh = new double[halfArraySize];

        double[] multiplicandLowHigh = new double[halfArraySize];
        double[] multiplierLowHigh = new double[halfArraySize];

        System.arraycopy(multiplicand, 0, multiplicandLow, 0, halfArraySize);
        System.arraycopy(multiplicand, halfArraySize, multiplicandHigh, 0, halfArraySize);

        for (int halfSizeIndex = 0; halfSizeIndex < halfArraySize; ++halfSizeIndex)
            multiplicandLowHigh[halfSizeIndex] = multiplicandLow[halfSizeIndex] + multiplicandHigh[halfSizeIndex];
        System.arraycopy(multiplier, 0, multiplierLow, 0, halfArraySize);
        System.arraycopy(multiplier, halfArraySize, multiplierHigh, 0, halfArraySize);
        for (int halfSizeIndex = 0; halfSizeIndex < halfArraySize; ++halfSizeIndex)
            multiplierLowHigh[halfSizeIndex] = multiplierLow[halfSizeIndex] + multiplierHigh[halfSizeIndex];

        try {
            PolynomialRKOp productLowOp = new PolynomialRKOp(multiplicandLow, multiplierLow);
            PolynomialRKOp productHighOp = new PolynomialRKOp(multiplicandHigh, multiplierHigh);
            PolynomialRKOp productLowHighOp = new PolynomialRKOp(multiplicandLowHigh, multiplierLowHigh);

            toComp.add(productLowOp);
            toComp.add(productHighOp);
            toComp.add(productLowHighOp);

            List<double[]> results = ForkJoinTask.invokeAll(toComp).stream().map(ForkJoinTask::join).collect(Collectors.toList());

            double[] productLow= results.get(0);
            double[] productHigh= results.get(1);
            double[] productLowHigh = results.get(2);

            double[] productMiddle = new double[multiplicand.length];
            for (int halfSizeIndex = 0; halfSizeIndex < multiplicand.length; ++halfSizeIndex) {
                productMiddle[halfSizeIndex] = productLowHigh[halfSizeIndex] - productLow[halfSizeIndex] - productHigh[halfSizeIndex];
            }

            for (int halfSizeIndex = 0, middleOffset = multiplicand.length / 2; halfSizeIndex < multiplicand.length; ++halfSizeIndex) {
                product[halfSizeIndex] += productLow[halfSizeIndex];
                product[halfSizeIndex + multiplicand.length] += productHigh[halfSizeIndex];
                product[halfSizeIndex + middleOffset] += productMiddle[halfSizeIndex];
            }

            return product;

        }
        catch (Exception e){e.printStackTrace();}

        return null;

    }




    /**
     * @param numberOfCoefficients of coefficients
     * @return true if input is an integral power of 2
     */
    private boolean isPowerOfTwo(int numberOfCoefficients) {

        double numberOfCoefficientsLg = Math.log10(numberOfCoefficients) / Math.log10(2.0);
        int numberOfCoefficientsLgInt = Double.valueOf(numberOfCoefficientsLg).intValue();

        return numberOfCoefficientsLgInt == numberOfCoefficientsLg;

    }

}
