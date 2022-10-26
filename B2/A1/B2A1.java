import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * Wendet die Gamma-Korrektur auf ein Grauwertbild an.
 *
 * @author Jan Konrad (2533619)
 */
public class B2A1 extends AbstractFilter {
    private static final String GAMMA = "Gamma";

    public B2A1() {
        properties.addDoubleProperty(GAMMA, 1);
    }

    @Override
    public Image filter(Image input) {
        GrayscaleImage output = ImageFactory.getPrecision(input).gray(input.getSize());

        double gamma = properties.getDoubleProperty(GAMMA);

        final double iMin = 0;
        final double iMax = 255;
        final double n = iMax + 1;

        for (int col = 0; col < input.getWidth(); col++) {
            for (int row = 0; row < input.getHeight(); row++) {
                double value = input.getValue(col, row, 0);

                double base = (value - iMin) / (iMax - iMin);
                value = Math.round(n * Math.pow(base, gamma) + iMin);
                value = value == n ? iMax : value;

                output.setValue(col, row, 0, value);
            }
        }

        return output;
    }
}
