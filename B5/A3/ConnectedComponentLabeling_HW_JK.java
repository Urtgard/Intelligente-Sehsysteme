import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.RgbImage;

import java.util.*;

@RequireImageType(GrayscaleImage.class)
public class ConnectedComponentLabeling_HW_JK extends AbstractFilter {
    private int width, height;

    private static Map<Integer, double[]> getRgbMap(ArrayList<ArrayList<Integer>> equivalences) {
        Set<Integer> finalLabels = new HashSet<>();
        for (var equivalence : equivalences) {
            finalLabels.add(equivalence.get(0));
        }
        var finalLabelsArray = finalLabels.toArray();

        var colors = finalLabels.size();
        var rgbSteps = (int) Math.pow(colors, 1.0 / 3.0) + 1;

        Map<Integer, double[]> rgbMap = new HashMap<>();
        for (var i = 0; i < colors; i++) {
            var finalLabel = (int) finalLabelsArray[i];
            var r = 255 - 255.0 / (rgbSteps - 1) * (i / (rgbSteps * rgbSteps) % rgbSteps);
            var g = 255 - 255.0 / (rgbSteps - 1) * (i / rgbSteps % rgbSteps);
            var b = 255 - 255.0 / (rgbSteps - 1) * (i % rgbSteps);
            var rgb = new double[]{r, g, b};
            rgbMap.put(finalLabel, rgb);
        }
        return rgbMap;
    }

    private static void updateEquivalences(ArrayList<ArrayList<Integer>> equivalences, List<Integer> neighborhood) {
        for (var labelA : neighborhood) {
            var equivalencesA = equivalences.get(labelA);
            for (var labelB : neighborhood) {
                var equivalencesB = equivalences.get(labelB);

                if (equivalencesA == equivalencesB) continue;

                for (var equivalentLabel : equivalencesB) {
                    if (!equivalencesA.contains(equivalentLabel)) {
                        equivalencesA.add(equivalentLabel);
                        equivalences.set(equivalentLabel, equivalencesA);
                    }
                }
            }

            equivalencesA.sort(Comparator.naturalOrder());
        }
    }

    @Override
    public Image filter(Image input) {
        //ausgabebild hat genauigkeit und größe des eingabebildes
        RgbImage output = ImageFactory.getPrecision(input).rgb(input.getSize());
        var label = 0;
        var equivalences = new ArrayList<ArrayList<Integer>>();
        equivalences.add(new ArrayList<>(List.of(label)));

        width = input.getWidth();
        height = input.getHeight();
        var labels = new int[width][height];

        //über alle pixel iterieren
        for (var y = 0; y < input.getHeight(); y++) {
            for (var x = 0; x < input.getWidth(); x++) {
                if (input.getValue(x, y, 0) == 0) {
                    var neighborhood = getNeighborhood(labels, y, x);

                    if (!neighborhood.isEmpty()) {
                        //min label der nachbarschaft als label des aktuellen pixels setzen
                        var l_min = Collections.min(neighborhood);
                        labels[x][y] = l_min;

                        //äquivalenzen vereinigen
                        updateEquivalences(equivalences, neighborhood);
                    } else {
                        label++;
                        labels[x][y] = label;
                        equivalences.add(label, new ArrayList<>(List.of(label)));
                    }
                }
            }
        }

        var rgbMap = getRgbMap(equivalences);
        //über alle pixel iterieren
        for (var x = 0; x < output.getWidth(); x++) {
            for (var y = 0; y < output.getHeight(); y++) {
                var rgb = getRgb(equivalences, labels, rgbMap, x, y);
                output.setValue(x, y, rgb);
            }
        }

        return output;
    }

    private List<Integer> getNeighborhood(int[][] labels, int y, int x) {
        List<Integer> labelNachbarschaft = new ArrayList<>();

        //8-nachbarschaft bestimmen
        for (var i = -1; i <= 1; i++) {
            for (var j = -1; j <= 1; j++) {
                var aktPixel = getLabel(labels, x + i, y + j);
                if (aktPixel != 0) {
                    labelNachbarschaft.add(aktPixel);
                }
            }
        }
        return labelNachbarschaft;
    }

    private double[] getRgb(ArrayList<ArrayList<Integer>> equivalences, int[][] labels, Map<Integer, double[]> rgbMap, int x, int y) {
        var label = getLabel(labels, x, y);
        var l_min = equivalences.get(label).get(0);
        return rgbMap.get(l_min);
    }

    //aktuelles label bekommen
    public int getLabel(int[][] labels, int x, int y) {
        if (x < 0) return 0;
        if (x >= width) return 0;
        if (y < 0) return 0;
        if (y >= height) return 0;

        return labels[x][y];
    }
}
