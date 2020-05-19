package net.kemitix.clover.image.effects;

import lombok.*;
import net.kemitix.clover.spi.*;
import net.kemitix.text.fit.WordWrapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@ApplicationScoped
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SimpleTextEffectImpl
        extends AbstractTextEffect
        implements
        SimpleTextEffect<Graphics2D>,
        TextEffect.TextNext<Graphics2D>,
        TextEffect.RegionNext<Graphics2D>,
        TextEffect.HAlignNext<Graphics2D>,
        TextEffect.VAlignNext<Graphics2D>,
        TextEffect<Graphics2D> {

    @Inject @Getter FontCache fontCache;
    @Inject @Getter FontMetrics fontMetrics;
    @Inject WordWrapper wordWrapper;
    @Inject FitToRegion fitToRegion;

    @Getter Region region;

    VAlignment vAlignment;
    HAlignment hAlignment;
    FontFace fontFace;
    String text;

    @Override
    public void accept(Graphics2D graphics2D) {
        FontFace face = fitToRegion.fit(text, fontFace, graphics2D, region);
        List<String> split =
                wordWrapper.wrap(text, fontCache.loadFont(face), graphics2D, region.getWidth());
        int top = topEdge(split.size() * face.getSize());
        IntStream.range(0, split.size())
                .forEach(lineNumber -> {
                    String lineOfText = split.get(lineNumber);
                    if (lineOfText.length() > 0) {
                        drawText(graphics2D, lineNumber, lineOfText,
                                region.getArea(), face, top);
                    }
                });
    }

    private void drawText(
            Graphics2D graphics2d,
            int lineCount,
            String line,
            Area imageArea,
            FontFace face,
            int topOffset
    ) {
        Area stringBounds = getStringBounds(graphics2d, line, face);
        int top = topOffset + ((int) stringBounds.getHeight() * lineCount);
        int left = lineLeftEdge((int) stringBounds.getWidth());
        AbstractTextEffect.drawText(line, framing -> XY.at(left, top),
                face, graphics2d, fontCache, imageArea);
        //graphics2d.drawRect(left, top, (int) stringBounds.getWidth(), (int) stringBounds.getHeight());
    }

    private int topEdge(int height) {
        switch (vAlignment) {
            case TOP:
                return region.getTop();
            case BOTTOM:
                return region.getTop() + (region.getHeight() - height);
            case CENTRE:
                return region.getTop() + ((region.getHeight() - height) / 2);
        }
        throw new UnsupportedOperationException(
                "Unknown Vertical Alignment: " + hAlignment);
    }

    private int lineLeftEdge(int width) {
        switch (hAlignment) {
            case LEFT:
                return region.getLeft();
            case RIGHT:
                return region.getWidth() - width;
            case CENTRE:
                return region.getLeft() + ((region.getWidth() - width) / 2);
        }
        throw new UnsupportedOperationException(
                "Unknown Horizontal Alignment: " + hAlignment);
    }

    @Override
    public VAlignNext<Graphics2D> text(String text) {
        return withText(text);
    }

    @Override
    public TextNext<Graphics2D> fontFace(FontFace fontFace) {
        return withFontFace(fontFace);
    }

    @Override
    public Consumer<Graphics2D> region(Region region) {
        return withRegion(region);
    }

    @Override
    public RegionNext<Graphics2D> hAlign(HAlignment hAlignment) {
        return withHAlignment(hAlignment);
    }

    @Override
    public HAlignNext<Graphics2D> vAlign(VAlignment vAlignment) {
        return withVAlignment(vAlignment);
    }
}
