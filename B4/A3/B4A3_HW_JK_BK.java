import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * Wendet ein isotropes inhomogenes Diffusionsfilter auf ein Grauwertbild an.
 */
@RequireImageType(GrayscaleImage.class)
public class B4A3_HW_JK_BK extends AbstractFilter {
    private static final String EPSILON_0 = "Epsilon_0", ITERATIONS = "Anzahl der Iterationen", LAMBDA = "Lambda";
    private double epsilon_0, lambda;
    private int width, height;

    public B4A3_HW_JK_BK() {
        properties.addDoubleProperty(EPSILON_0, 1);
        properties.addIntegerProperty(ITERATIONS, 500);
        properties.addDoubleProperty(LAMBDA, 1);
    }

    @Override
    public Image filter(Image input) {
        epsilon_0 = properties.getDoubleProperty(EPSILON_0);
        var iterations = properties.getIntegerProperty(ITERATIONS);
        lambda = properties.getDoubleProperty(LAMBDA);

        width = input.getWidth();
        height = input.getHeight();

        var values = new double[width][height];
        var values_t1 = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                values[x][y] = I(input, x, y);
            }
        }

        for (int iteration = 0; iteration < iterations; iteration++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // C.
                    var flowGradient = new Double[]{
                            j(values, x + 1, y)[0] - j(values, x - 1, y)[0],
                            j(values, x, y + 1)[1] - j(values, x, y - 1)[1]
                    };

                    // D.
                    var div = flowGradient[0] + flowGradient[1];

                    // E.
                    var value = I(values, x, y) - div;
                    values_t1[x][y] = value;
                }
            }

            values = values_t1;
            values_t1 = new double[width][height];
        }

        var output = ImageFactory.getPrecision(input).gray(input.getSize());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                output.setValue(x, y, values[x][y]);
            }
        }

        return output;
    }

    // A.
    private double[] getGradient(double[][] values, int x, int y) {
        var gradientX = I(values, x + 1, y) - I(values, x - 1, y);
        var gradientY = I(values, x, y + 1) - I(values, x, y - 1);

        return new double[]{gradientX, gradientY};
    }

    // B.
    private double[] j(double[][] values, int x, int y) {
        var gradient = getGradient(values, x, y);

        var gradientLengthDiscriminant = Math.pow(gradient[0], 2) + Math.pow(gradient[1], 2);
        var lambdaSquare = Math.pow(lambda, 2);
        var epsilon = epsilon_0 * lambdaSquare / (gradientLengthDiscriminant + lambdaSquare);

        var D = new double[][]{
                {epsilon, 0},
                {0, epsilon}
        };

        return new double[]{
                -D[0][0] * gradient[0],
                -D[1][1] * gradient[1]
        };
    }

    private double I(Image input, int x, int y) {
        x = getX(x);
        y = getY(y);

        return input.getValue(x, y, 0);
    }

    private double I(double[][] values, int x, int y) {
        x = getX(x);
        y = getY(y);

        return values[x][y];
    }

    private int getY(int y) {
        if (y < 0) y = 0;
        if (y >= height - 1) y = height - 1;
        return y;
    }

    private int getX(int x) {
        if (x < 0) x = 0;
        if (x >= width) x = width - 1;
        return x;
    }
}
