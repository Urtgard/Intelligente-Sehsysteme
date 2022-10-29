/**
 * Wendet ein 3x3 Mittelwertfilter auf ein Grauwertbild an.
 */
public class Mittelwertfilter3x3_HW_JK_BK extends ConvolutionFilter_HW_JK_BK {
    @Override
    public double[][] getKernel() {
        double v = 1.0 / 9;

        return new double[][]{
                {v, v, v},
                {v, v, v},
                {v, v, v}
        };
    }
}
