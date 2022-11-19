import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.RgbImage;

import java.util.ArrayList;
import java.util.Collections;

public class Medianfilter_HW_JK extends AbstractFilter {

    private static final String OPTION = "Filtergröße";

    public Medianfilter_HW_JK() {
        properties.addOptionProperty(OPTION, "3", "3", "5");
    }

    @Override
    public Image filter(Image input) {
        //output soll gleiche genauigkeit und größe wie input haben
        RgbImage output = ImageFactory.getPrecision(input).rgb(input.getSize());

        int size = Integer.parseInt(properties.getOptionProperty(OPTION));
        int max = (size - 1) / 2;
        int min = -max;

        ArrayList<Double> werte = new ArrayList<>();

        //über alle pixel iterieren
        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                for (int chan = 0; chan < input.getChannelCount(); chan++) {
                    //umgebung der pixel in einer liste speichern
                    for (int i = min; i <= max; i++) {
                        for (int j = min; j <= max; j++) {
                            werte.add(getPixel(input, x + i, y + j, chan));
                        }
                    }

                    //list der werte sortieren
                    Collections.sort(werte, Collections.reverseOrder());

                    //median der liste als output pixel setzten
                    var value = werte.get(werte.size() / 2);
                    output.setValue(x, y, chan, value);
                    werte.clear();
                }
            }
        }
        return output;
    }

    //pixelwert aus input bekommen
    public double getPixel(Image input, int x, int y, int chan) {
        if (x < 0) x = 0;
        if (x >= input.getWidth()) x = input.getWidth() - 1;
        if (y < 0) y = 0;
        if (y >= input.getHeight() - 1) y = input.getHeight() - 1;

        return input.getValue(x, y, chan);
    }
}
