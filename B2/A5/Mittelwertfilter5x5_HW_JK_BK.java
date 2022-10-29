/**
 * Wendet ein 5x5 Mittelwertfilter auf ein Grauwertbild an.
 */
public class Mittelwertfilter5x5_HW_JK_BK extends ConvolutionFilter_HW_JK_BK {
    @Override
    public double[][] getKernel() {
        return new double[][]{
                {0.04, 0.04, 0.04, 0.04, 0.04},
                {0.04, 0.04, 0.04, 0.04, 0.04},
                {0.04, 0.04, 0.04, 0.04, 0.04},
                {0.04, 0.04, 0.04, 0.04, 0.04},
                {0.04, 0.04, 0.04, 0.04, 0.04}
        };
    }
}
