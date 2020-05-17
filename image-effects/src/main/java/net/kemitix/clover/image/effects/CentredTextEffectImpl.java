package net.kemitix.clover.image.effects;

import lombok.*;
import net.kemitix.clover.spi.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.*;
import java.util.function.Function;
import java.util.stream.IntStream;

@ApplicationScoped
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CentredTextEffectImpl
        extends AbstractTextEffect
        implements CenteredTextEffect<Graphics2D>,
        CenteredTextEffect.TextNext<Graphics2D>,
        CenteredTextEffect.RegionNext<Graphics2D>,
        Function<Graphics2D, Graphics2D> {

    @Inject @Getter FontCache fontCache;
    @Inject @Getter FontMetrics fontMetrics;
    @Inject WordWrapper wordWrapper;
    @Inject FitToRegion fitToRegion;

    @Getter Region region;

    FontFace fontFace;
    String text;

    @Override
    public Graphics2D apply(Graphics2D graphics2D) {
        FontFace face = fitToRegion.fit(text, fontFace, graphics2D, region);
        String[] split =
                wordWrapper.wrap(text, face, graphics2D, region.getWidth());
        IntStream.range(0, split.length)
                .forEach(lineNumber -> {
                    String lineOfText = split[lineNumber];
                    if (lineOfText.length() > 0) {
                        drawText(graphics2D, lineNumber, lineOfText,
                                region.getArea(), face);
                    }
                });
        return graphics2D;
    }

    private void drawText(
            Graphics2D graphics2d,
            int lineCount,
            String line,
            Area imageArea,
            FontFace face
    ) {
        Area stringBounds = getStringBounds(graphics2d, line, face);
        int top = region.getTop() + ((int) stringBounds.getHeight() * lineCount);
        int left = region.getLeft() + ((region.getWidth() - (int) stringBounds.getWidth()) / 2);
        AbstractTextEffect.drawText(line, framing -> XY.at(left, top),
                face, graphics2d, fontCache, imageArea);
        graphics2d.drawRect(left, top, (int) stringBounds.getWidth(), (int) stringBounds.getHeight());
    }

    @Override
    public Function<Graphics2D, Graphics2D> text(String text) {
        return withText(text);
    }

    @Override
    public RegionNext<Graphics2D> fontFace(FontFace fontFace) {
        return withFontFace(fontFace);
    }

    @Override
    public TextNext<Graphics2D> region(Region region) {
        return withRegion(region);
    }

}
