import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@RequireImageType(GrayscaleImage.class)
public class Binarisierung_HW_JK extends AbstractFilter {

    private static final String[] THRESHOLD = new String[]{
            "Schwellwert 1",
            "Schwellwert 2",
            "Schwellwert 3",
            "Schwellwert 4",
            "Schwellwert 5",
            "Schwellwert 6",
            "Schwellwert 7",
            "Schwellwert 8",
            "Schwellwert 9"};

    private static final String THRESHOLDS_NUM = "Anzahl Schwellwerte";

    public Binarisierung_HW_JK() {
        properties.addRangeProperty(THRESHOLDS_NUM, 1, 1, 1, 9);

        properties.addIntegerProperty(THRESHOLD[0], 0);
        properties.addIntegerProperty(THRESHOLD[1], 0);
        properties.addIntegerProperty(THRESHOLD[2], 0);
        properties.addIntegerProperty(THRESHOLD[3], 0);
        properties.addIntegerProperty(THRESHOLD[4], 0);
        properties.addIntegerProperty(THRESHOLD[5], 0);
        properties.addIntegerProperty(THRESHOLD[6], 0);
        properties.addIntegerProperty(THRESHOLD[7], 0);
        properties.addIntegerProperty(THRESHOLD[8], 0);
    }

    private static int[] getMaxima(ArrayList<Integer> thresholds, int[] histogram) {
        var thresholdIndex = 0;
        var threshold = thresholds.get(thresholdIndex);
        var max = 0;
        var i_max = 0;
        var maxima = new int[thresholds.size() + 1];
        for (var i = 0; i < 256; i++) {
            if (histogram[i] > max) {
                max = histogram[i];
                i_max = i;
            }

            if (i == threshold) {
                maxima[thresholdIndex] = i_max;
                max = 0;
                thresholdIndex++;
                if (thresholdIndex < thresholds.size()) threshold = thresholds.get(thresholdIndex);
            }
        }

        maxima[0] = 0;
        maxima[thresholdIndex] = 255;

        return maxima;
    }

    private static int[] getHistogram(Image input) {
        var histogram = new int[256];
        for (var x = 0; x < input.getWidth(); x++) {
            for (var y = 0; y < input.getHeight(); y++) {
                var value = (int) input.getValue(x, y, 0);
                histogram[value]++;
            }
        }
        return histogram;
    }

    private static int getThresholdIndex(ArrayList<Integer> thresholds, double value) {
        for (var i = 0; i < thresholds.size(); i++) {
            var threshold = thresholds.get(i);

            if (value <= threshold) return i;
        }

        return thresholds.size();
    }

    @Override
    public Image filter(Image input) {
        //output soll gleiche genauigkeit und größe wie input haben
        //grayscale image da input auch grayscale image
        var output = ImageFactory.getPrecision(input).gray(input.getSize());

        var thresholds = getThresholds();
        var histogram = getHistogram(input);
        var maxima = getMaxima(thresholds, histogram);

        //über alle pixel iterieren
        for (var x = 0; x < input.getWidth(); x++) {
            for (var y = 0; y < input.getHeight(); y++) {
                var value = input.getValue(x, y, 0);
                var thresholdIndex = getThresholdIndex(thresholds, value);
                output.setValue(x, y, 0, maxima[thresholdIndex]);
            }
        }

        return output;
    }

    private ArrayList<Integer> getThresholds() {
        var thresholdsNum = properties.getRangeProperty(THRESHOLDS_NUM);

        var thresholds = new ArrayList<Integer>();
        for (var i = 0; i < thresholdsNum; i++) {
            var propertyName = THRESHOLD[i];
            var threshold = properties.getIntegerProperty(propertyName);
            thresholds.add(threshold);
        }
        Collections.sort(thresholds, Comparator.naturalOrder());
        return thresholds;
    }
}
