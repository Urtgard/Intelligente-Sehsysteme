import itb2.filter.AbstractFilter;
import itb2.image.BinaryImage;
import itb2.image.Image;
import itb2.image.ImageConverter;
import itb2.image.ImageFactory;
import itb2.image.RgbImage;

/**
 * Wendet Bild 2 als binäre Maske auf Bild 1 an.
 *
 * @author Jan Konrad (2533619)
 */
public class Mask extends AbstractFilter {
	public Mask() {
	}

	@Override
	public Image[] filter(Image[] input) {
		Image image = input[0];
		BinaryImage mask = getMask(input);

		// Ausgabebild erstellen
		RgbImage output = ImageFactory.bytePrecision().rgb(image.getSize());

		// Über Pixel iterieren
		for (int col = 0; col < image.getWidth(); col++) {
			for (int row = 0; row < image.getHeight(); row++) {
				for (int chan = 0; chan < image.getChannelCount(); chan++) {
					double intensityImage = image.getValue(col, row, chan);
					double intensityMask = mask.getValue(col, row, 0);

					double value = intensityImage * intensityMask;

					output.setValue(col, row, chan, value);
				}
			}
		}

		return new Image[] { output };
	}

	private BinaryImage getMask(Image[] input) {
		return ImageConverter.convert(input[1], BinaryImage.class);
	}

}
