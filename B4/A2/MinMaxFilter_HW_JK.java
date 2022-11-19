import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequireImageType(GrayscaleImage.class)
public class MinMaxFilter_HW_JK extends AbstractFilter {

    private static final String filterart = "Min / Max";

    //koordinaten vom kreuz
    private int[][] kreuzKoordinaten = {{-1,0},{0,-1},{0,0},{0,1},{1,0}};

    public MinMaxFilter_HW_JK(){
        //property zum einstellen der
        properties.addOptionProperty(filterart, "Erosion", "Erosion", "Dilatation");
    }

    @Override
    public Image filter(Image input) {

        //output soll gleiche genauigkeit und größe wie input haben
        //grayscale image da input auch grayscale image
        GrayscaleImage output = ImageFactory.getPrecision(input).gray(input.getSize());

        double[][] kreuzWerte = {{1,1,1},{1,1,1},{1,1,1}};

        //über alle pixel iterieren
        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {

                //prüfe ob erosion oder dilatation verwendet werden soll
                if (properties.getOptionProperty(filterart).equals("Erosion")){
                    output.setValue(x, y, erosion(input, x, y, kreuzWerte));
                }else if (properties.getOptionProperty(filterart).equals("Dilatation")){
                    output.setValue(x, y, dilatation(input, x, y, kreuzWerte));
                }
            }
        }

        return output;
    }

    public double erosion(Image input, int x, int y, double[][] kreuzwerte){

        List<Double> erosionWerte = new ArrayList<>();

        //berechnung der werte
        for (int i = 0; i < kreuzKoordinaten.length; i++){
            erosionWerte.add(getPixel(input, x + kreuzKoordinaten[i][0], y + kreuzKoordinaten[i][1]) - kreuzwerte[kreuzKoordinaten[i][0] + 1][kreuzKoordinaten[i][1] + 1]);
        }

        //gebe minimalen wert zurück
        return Collections.min(erosionWerte);
    }

    public double dilatation(Image input, int x, int y, double[][] kreuzwerte){

        List<Double> dilatationsWerte = new ArrayList<>();

        //berechnung der werte
        for (int i = 0; i < kreuzKoordinaten.length; i++){
            dilatationsWerte.add(getPixel(input, x + kreuzKoordinaten[i][0], y + kreuzKoordinaten[i][1]) + kreuzwerte[kreuzKoordinaten[i][0] + 1][kreuzKoordinaten[i][1] + 1]);
        }

        //gebe maximalen wert zurück
        return Collections.max(dilatationsWerte);
    }

    //pixelwert aus input bekommen
    public double getPixel(Image input, int x, int y){
        if (x < 0) x = 0;
        if (x >= input.getWidth()) x = input.getWidth() - 1;
        if (y < 0) y = 0;
        if (y >= input.getHeight() - 1) y = input.getHeight() - 1;

        return input.getValue(x, y, 0);

    }
}
