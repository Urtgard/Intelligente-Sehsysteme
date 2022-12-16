import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageConverter;
import itb2.image.ImageFactory;

import java.util.ArrayList;

public class B7A6_HW_JK extends AbstractFilter {
    private static final String SIGMA = "<html>&#963;</html>";
    private static final String OPERATION = "Operation";

    public B7A6_HW_JK() {
        properties.addDoubleProperty(SIGMA, 2);
        properties.addOptionProperty(OPERATION, "Reduce", "Reduce", "Expand");
    }

    private double[][] getGaussianKernel() {
        var sigma = properties.getDoubleProperty(SIGMA);

        var gaussian_kernel = new double[5][5];
        var sum = 0.;
        var offset = 2;
        for (var u = -2; u <= 2; u++) {
            for (var v = -2; v <= 2; v++) {
                var gaussian = 1 / (2 * Math.PI * Math.pow(sigma, 2)) * Math.exp(-(Math.pow(u, 2) + Math.pow(v, 2)) / (2 * Math.pow(sigma, 2)));
                gaussian_kernel[offset + u][offset + v] = gaussian;
                sum += gaussian;
            }
        }

        for (var u = -2; u <= 2; u++) {
            for (var v = -2; v <= 2; v++) {
                gaussian_kernel[offset + u][offset + v] = gaussian_kernel[offset + u][offset + v] / sum;
            }
        }
        return gaussian_kernel;
    }

    @Override
    public Image[] filter(Image[] input) {
        var grayscaleInput = ImageConverter.convert(input[0], GrayscaleImage.class);

        var gaussianKernel = getGaussianKernel();

        var output = new ArrayList<GrayscaleImage>();

        var operation = properties.getOptionProperty(OPERATION);
        if (operation == "Reduce") {
            var reduced = grayscaleInput;
            do {
                reduced = reduce(reduced, gaussianKernel);
                output.add(reduced);
            } while (reduced.getWidth() / 2 > 1 && reduced.getHeight() / 2 > 1);
        } else {
            var exp = new Expansion();
            var expanded = exp.expand(gaussianKernel, grayscaleInput);
            output.add(expanded);
        }

        return output.toArray(new GrayscaleImage[0]);
    }

    private GrayscaleImage reduce(GrayscaleImage input, double[][] kernel) {
        var width = input.getWidth();
        var height = input.getHeight();

        var reduced = ImageFactory.bytePrecision().gray(width / 2, height / 2);
        var conv = new Convolution();
        for (var x = 0; x < width / 2 * 2; x += 2) {
            for (var y = 0; y < height / 2 * 2; y += 2) {
                var value = conv.calculateConvolution(input, kernel, x, y);
                reduced.setValue(x / 2, y / 2, 0, value);
            }
        }
        return reduced;
    }

    private class Expansion {
        public GrayscaleImage expand(double[][] gaussianKernel, GrayscaleImage reduced) {
            var width = reduced.getWidth();
            var height = reduced.getHeight();

            var widthNext = width * 2 - 1;
            var heightNext = height * 2 - 1;
            var expanded = ImageFactory.bytePrecision().gray(widthNext, heightNext);

            var conv = new ExpansionConvolution();
            for (var x = 0; x < widthNext; x++) {
                for (var y = 0; y < heightNext; y++) {
                    var value = 4 * conv.calculateConvolution(reduced, gaussianKernel, x, y);
                    expanded.setValue(x, y, 0, value);
                }
            }

            return expanded;
        }

        private class ExpansionConvolution extends Convolution {
            protected double g(GrayscaleImage input, int x, int y) {
                if (x % 2 != 0 || y % 2 != 0) return 0;

                return super.g(input, x / 2, y / 2);
            }
        }
    }

    private class Convolution {

        private static double f(double[][] kernel, int u, int v) {
            var offset = (kernel.length - 1) / 2;

            return kernel[offset + u][offset + v];
        }

        public double calculateConvolution(GrayscaleImage input, double[][] kernel, int x, int y) {
            var m = kernel.length;
            double value = 0;

            var max = (m - 1) / 2;
            var min = -max;
            for (var u = min; u <= max; u++) {
                for (var v = min; v <= max; v++) {
                    var f = f(kernel, u, v);
                    var g = g(input, x - u, y - v);
                    value += f * g;
                }
            }
            return value;
        }

        protected double g(GrayscaleImage input, int x, int y) {
            if (x < 0 || x >= input.getWidth()) return 0;
            if (y < 0 || y >= input.getHeight() - 1) return 0;

            return input.getValue(x, y, 0);
        }
    }
}