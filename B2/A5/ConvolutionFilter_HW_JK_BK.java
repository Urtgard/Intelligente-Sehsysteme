import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * Wendet eine Konvolutionsmaske auf ein Grauwertbild an.
 */
@RequireImageType(GrayscaleImage.class)
public abstract class ConvolutionFilter_HW_JK_BK extends AbstractFilter {

    public abstract double[][] getKernel();

    @Override
    public Image filter(Image input) {
        //output soll gleiche genauigkeit und größe wie input haben
        //grayscale image da input auch grayscale image
        GrayscaleImage output = ImageFactory.getPrecision(input).gray(input.getSize());

        var kernel = getKernel();
        var m = kernel.length;

        //über alle pixel iterieren
        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                double ergebnispixel = 0;

                //neuer wert vom aktuellen pixel wird berechnet
                int max = (m - 1) / 2;
                int min = -max;
                for (int u = min; u <= max; u++) {
                    for (int v = min; v <= max; v++) {
                        double f = f(kernel, u, v);
                        double g = g(input, x - u, y - v);
                        ergebnispixel += f * g;
                    }
                }
                //neuer wert wird in ausgabebild übernommen
                output.setValue(x, y, ergebnispixel);
            }
        }

        return output;
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
