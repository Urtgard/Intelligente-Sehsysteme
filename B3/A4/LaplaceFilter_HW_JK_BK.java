import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.util.ArrayList;
import java.util.List;

public class LaplaceFilter_HW_JK_BK extends AbstractFilter {
    private static final String OPERATOR = "Laplace-Operator";
    private final double[][] L4 =
            new double[][]{
                    {0, 1, 0},
                    {1, -4, 1},
                    {0, 1, 0}
            };
    private final double[][] L8 =
            new double[][]{
                    {1, 1, 1},
                    {1, -8, 1},
                    {1, 1, 1}
            };

    public LaplaceFilter_HW_JK_BK() {
        properties.addOptionProperty(OPERATOR, "L4", "L4", "L8");
    }

    private static boolean isNulldurchgang(double value, List<Double> shifts) {
        for (var shift : shifts) {
            if (value * shift < 0) return true;
        }

        return false;
    }

    private static double getMax(double[][] values) {
        double max = values[0][0];
        int width = values.length - 1;
        int height = values[0].length - 1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var value = values[x][y];
                if (value > max) max = value;
            }
        }
        return max;
    }

    private static double getMin(double[][] values) {
        double min = values[0][0];
        int width = values.length - 1;
        int height = values[0].length - 1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var value = values[x][y];
                if (value < min) min = value;
            }
        }
        return min;
    }

    private static double[][] LineareGrauwertspreizung(double[][] values) {
        var I_minGiven = getMin(values);
        var I_maxGiven = getMax(values);

        var I_min = 0;
        var I_max = 255;

        var c1 = I_min - I_minGiven;
        var c2 = (I_max - I_min) / (I_maxGiven - I_minGiven);

        var width = values.length - 1;
        var height = values[0].length - 1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var value = values[x][y];
                values[x][y] = (value + c1) * c2;
            }
        }

        return values;
    }

    @Override
    public Image filter(Image input) {
        var operator = properties.getOptionProperty(OPERATOR);

        var kernel = operator == "L4" ? L4 : L8;
        int width = input.getWidth();
        int height = input.getHeight();
        double[][] values = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double value = calculateConvolution(input, kernel, x, y);

                List<Double> shifts = new ArrayList<>();
                shifts.add(calculateConvolution(input, kernel, x + 1, y));
                shifts.add(calculateConvolution(input, kernel, x, y + 1));
                if (operator == "L8") {
                    shifts.add(calculateConvolution(input, kernel, x - 1, y + 1));
                    shifts.add(calculateConvolution(input, kernel, x + 1, y + 1));
                }

                if (!isNulldurchgang(value, shifts)) {
                    value = 0;
                }

                values[x][y] = value;
            }
        }

        values = LineareGrauwertspreizung(values);

        GrayscaleImage output = ImageFactory.getPrecision(input).gray(input.getSize());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                output.setValue(x, y, values[x][y]);
            }
        }

        return output;
    }

    private double calculateConvolution(Image input, double[][] kernel, int x, int y) {
        var m = kernel.length;
        double value = 0;

        int max = (m - 1) / 2;
        int min = -max;
        for (int u = min; u <= max; u++) {
            for (int v = min; v <= max; v++) {
                double f = f(kernel, u, v);
                double g = g(input, x - u, y - v);
                value += f * g;
            }
        }
        return value;
    }

    private double f(double[][] kernel, int u, int v) {
        var offset = (kernel.length - 1) / 2;

        return kernel[offset + u][offset + v];
    }

    private double g(Image input, int x, int y) {
        if (x < 0) x = 0;
        if (x >= input.getWidth()) x = input.getWidth() - 1;
        if (y < 0) y = 0;
        if (y >= input.getHeight() - 1) y = input.getHeight() - 1;

        return input.getValue(x, y, 0);
    }
}
