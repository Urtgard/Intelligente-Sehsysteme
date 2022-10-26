import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.RgbImage;

/**
 * Skaliert die Intensität eines Kanals.
 *
 * @author Jan Konrad (2533619)
 */
@RequireImageType(RgbImage.class)
public class AdjustColorsFilter extends AbstractFilter {
	/** Property names */
	private static final String CHANNEL = "RGB", FACTOR = "Faktor";

	public AdjustColorsFilter() {
		properties.addOptionProperty(CHANNEL, "Kanal", "R", "G", "B");
		properties.addRangeProperty(FACTOR, 0, 0, 1, 10);
	}

	@Override
	public Image filter(Image input) {
		int channel = getChannelNumber();
		int factor = properties.getRangeProperty(FACTOR);

		// Ausgabebild erstellen
		RgbImage output = ImageFactory.bytePrecision().rgb(input.getSize());

		// Über Pixel iterieren
		for (int col = 0; col < input.getWidth(); col++) {
			for (int row = 0; row < input.getHeight(); row++) {
				for (int chan = 0; chan < input.getChannelCount(); chan++) {
					double value = input.getValue(col, row, chan);
					if (chan == channel)
						value = Math.min(factor * value, 255);

					output.setValue(col, row, chan, value);
				}
			}
		}

		return output;
	}

	private int getChannelNumber() {
		return "RGB".indexOf(properties.getOptionProperty(CHANNEL));
	}
}
