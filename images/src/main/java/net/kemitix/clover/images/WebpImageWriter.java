package net.kemitix.clover.images;

import javax.enterprise.context.Dependent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Dependent
public class WebpImageWriter implements ImageWriter {

    public static final String FORMAT_NAME = "webp";
    private static final Logger LOGGER =
            Logger.getLogger(
                    WebpImageWriter.class.getName());

    @Override
    public boolean accepts(final String format) {
        return FORMAT_NAME.equals(format);
    }

    @Override
    public void write(
            final BufferedImage image,
            final File file
    ) {
        try {
            ImageIO.write(image, FORMAT_NAME, file);
            LOGGER.info(String.format("Wrote %s", file));
        } catch (final IOException e) {
            LOGGER.severe("Failed to write " + file);
        }
    }

}
