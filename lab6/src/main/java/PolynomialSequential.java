public class PolynomialSequential {

    double[] coefficients;

    public PolynomialSequential(double[] coefficients) throws Exception {

        if (!isPowerOfTwo(coefficients.length)) {
            throw new Exception("Polynomial should have number of coefficients equal to integral power of 2.");
        }
        this.coefficients = coefficients;
    }

    /**
     * Multiply using naive algorithm
     *
     * @param multiplier
     * @return a polynomial representing the product of this one and the parameter
     * @throws Exception
     */
    public PolynomialSequential naiveMultiply(PolynomialSequential multiplier) throws Exception {

        if (this.coefficients.length != multiplier.length()) {
            throw new Exception("Can only multiply a polynomial with " + this.coefficients.length + " coefficients.");
        }

        double[] product = new double[2 * this.coefficients.length];

        //Multiply the two polynomials
        for (int multiplicandIndex = 0; multiplicandIndex < this.coefficients.length; ++multiplicandIndex) {
            for (int multiplierIndex = 0; multiplierIndex < multiplier.length(); ++multiplierIndex) {
                product[multiplicandIndex + multiplierIndex] += this.coefficients[multiplicandIndex] * multiplier.get(multiplierIndex);
            }
        }

        return new PolynomialSequential(product);
    }

    /**
     * Multiply using Karatsuba algorithm
     *
     * @param multiplier
     * @return a polynomial representing the product of this one and the parameter
     * @throws Exception
     */
    public PolynomialSequential karatsubaMultiply(PolynomialSequential multiplier) throws Exception {

        if (this.coefficients.length != multiplier.length()) {
            throw new Exception("Can only multiply a polynomial with " + this.coefficients.length + " coefficients.");
        }

        return new PolynomialSequential(karatsubaMultiplyRecursive(this.coefficients, multiplier.coefficients));

    }

    /**
     * @param multiplier
     * @return array of doubles containing coefficients of the product with the multiplier
     */
    private double[] karatsubaMultiplyRecursive(double[] multiplicand, double[] multiplier) {

        double[] product = new double[2 * multiplicand.length];

        //Handle the base case where the polynomial has only one coefficient
        if (multiplicand.length == 1) {
            product[0] = multiplicand[0] * multiplier[0];
            return product;
        }

        int halfArraySize = multiplicand.length / 2;

        //Declare arrays to hold halved factors
        double[] multiplicandLow = new double[halfArraySize];
        double[] multiplicandHigh = new double[halfArraySize];
        double[] multipliplierLow = new double[halfArraySize];
        double[] multipliierHigh = new double[halfArraySize];

        double[] multiplicandLowHigh = new double[halfArraySize];
        double[] multipliplierLowHigh = new double[halfArraySize];

        //Fill in the low and high arrays
        for (int halfSizeIndex = 0; halfSizeIndex < halfArraySize; ++halfSizeIndex) {

            multiplicandLow[halfSizeIndex] = multiplicand[halfSizeIndex];
            multiplicandHigh[halfSizeIndex] = multiplicand[halfSizeIndex + halfArraySize];
            multiplicandLowHigh[halfSizeIndex] = multiplicandLow[halfSizeIndex] + multiplicandHigh[halfSizeIndex];

            multipliplierLow[halfSizeIndex] = multiplier[halfSizeIndex];
            multipliierHigh[halfSizeIndex] = multiplier[halfSizeIndex + halfArraySize];
            multipliplierLowHigh[halfSizeIndex] = multipliplierLow[halfSizeIndex] + multipliierHigh[halfSizeIndex];

        }

        //Recursively call method on smaller arrays and construct the low and high parts of the product
        double[] productLow = karatsubaMultiplyRecursive(multiplicandLow, multipliplierLow);
        double[] productHigh = karatsubaMultiplyRecursive(multiplicandHigh, multipliierHigh);

        double[] productLowHigh = karatsubaMultiplyRecursive(multiplicandLowHigh, multipliplierLowHigh);

        //Construct the middle portion of the product
        double[] productMiddle = new double[multiplicand.length];
        for (int halfSizeIndex = 0; halfSizeIndex < multiplicand.length; ++halfSizeIndex) {
            productMiddle[halfSizeIndex] = productLowHigh[halfSizeIndex] - productLow[halfSizeIndex] - productHigh[halfSizeIndex];
        }

        //Assemble the product from the low, middle and high parts. Start with the low and high parts of the product.
        for (int halfSizeIndex = 0, middleOffset = multiplicand.length / 2; halfSizeIndex < multiplicand.length; ++halfSizeIndex) {
            product[halfSizeIndex] += productLow[halfSizeIndex];
            product[halfSizeIndex + multiplicand.length] += productHigh[halfSizeIndex];
            product[halfSizeIndex + middleOffset] += productMiddle[halfSizeIndex];
        }

        return product;

    }

    public double get(int index) throws Exception {

        if (index >= this.coefficients.length) {
            throw new Exception("Index " + index + " is outside the coefficients for " + this.toString() + ".");
        }

        return this.coefficients[index];
    }

    /**
     * @return the number of coefficients
     */
    public int length() {
        return this.coefficients.length;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     * Returns a String representation of the Polynomial
     */
    public String toString() {

        StringBuffer returnValue = new StringBuffer();
        boolean firstTime = true;

        returnValue.append("\n[");
        for (double coefficient : this.coefficients) {
            if (firstTime) {
                firstTime = false;
            } else {
                returnValue.append(", ");
            }
            returnValue.append(coefficient);
        }
        returnValue.append("]\n");

        return returnValue.toString();
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