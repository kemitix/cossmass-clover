package net.kemitix.clover.service;

import net.kemitix.clover.spi.CloverProperties;
import net.kemitix.clover.spi.images.Area;
import net.kemitix.clover.spi.images.Image;
import net.kemitix.clover.spi.images.ImageService;
import net.kemitix.clover.spi.images.XY;
import net.kemitix.properties.typed.TypedProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.logging.Logger;

public abstract class CloverFormat {

    private static final Logger LOGGER =
            Logger.getLogger(
                    CloverFormat.class.getName());

    protected abstract CloverProperties getCloverConfig();
    protected abstract Issue getIssue();
    protected abstract ImageService getImageService();
    private Image cover;

    @PostConstruct
    public void create() throws IOException {
        String issueDir = getCloverConfig().getIssueDir();
        String coverArt = getIssue().getCoverArt();
        LOGGER.info(String.format("coverArt: %s/%s", issueDir, coverArt));
        final File coverArtFile =
                Paths.get(issueDir, coverArt)
                        .toFile();
        LOGGER.info(String.format("Cover Art: %s", coverArtFile));
        final Area area = Area.of(getWidth(), getHeight());
        cover = getImageService().load(coverArtFile)
                .scaleToCover(area)
                .crop(XY.at(getCropXOffset(), getCropYOffset()), area)
                .apply(frontCover())
                .apply(spine())
                .apply(backCover());
    }

    protected abstract int getHeight();

    protected abstract int getWidth();

    protected Function<Image, Image> backCover() {
        return image -> {
            LOGGER.info("Drawing the Back Cover");
            // Do nothing - subclasses should override if they want a back cover
            return image;
        };
    }

    protected Function<Image, Image> spine() {
        return image -> {
            LOGGER.info("Drawing the Spine");
            // Do nothing - subclasses should override if they want a spine
            return image;
        };
    }

    protected Function<Image, Image> frontCover() {
        LOGGER.info("Drawing the Front Cover");
        return image -> {
            // Do nothing - subclasses should override if they want a spine
            return image;
        };
    }

    protected abstract int getCropYOffset();

    protected abstract int getCropXOffset();

    public void write() {
        final TypedProperties properties = getImageProperties();
        cover.rescale(writeScale())
                .write(Paths.get(getCloverConfig().getIssueDir()),
                        getName(), properties);
    }

    protected TypedProperties getImageProperties() {
        return TypedProperties.create();
    }

    protected abstract String getName();

    protected abstract float writeScale();
}
