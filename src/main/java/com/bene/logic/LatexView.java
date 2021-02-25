package com.bene.logic;

import javafx.beans.DefaultProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.css.*;
import javafx.geometry.NodeOrientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import org.jfree.fx.FXGraphics2D;
import org.scilab.forge.jlatexmath.ParseException;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

@DefaultProperty("formula")
public class LatexView extends Canvas {
    private static final float DEFAULT_SIZE = (float) Font.getDefault().getSize();
    private static final String DEFAULT_FORMULA = "";
    private TeXIcon texIcon;
    private StringProperty formula;
    private StyleableFloatProperty size;
    private static final String DEFAULT_STYLE_CLASS = "latex-view";

    public LatexView() {
        this.getStyleClass().add("latex-view");
        this.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
    }

    public LatexView(String formula) {
        this.getStyleClass().add("latex-view");
        this.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        this.formulaProperty().set(formula);
    }

    private void update() throws ParseException {
        TeXFormula teXFormula;
        try {
            teXFormula = new TeXFormula(this.getFormula());
        } catch (ParseException var8) {
            System.err.println(var8.getMessage());
            teXFormula = new TeXFormula("\\textcolor{red}{\\text{Error}}");
        }

        this.texIcon = teXFormula.createTeXIcon(0, this.getSize());
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0.0D, 0.0D, this.getWidth(), this.getHeight());
        double width = (double) this.texIcon.getIconWidth();
        double height = (double) this.texIcon.getIconHeight();
        this.setWidth(width);
        this.setHeight(height);
        gc.clearRect(0.0D, 0.0D, width, height);
        FXGraphics2D graphics = new FXGraphics2D(gc);
        this.texIcon.paintIcon((Component) null, graphics, 0, 0);
    }

    public boolean isResizable() {
        return false;
    }

    public double prefWidth(double height) {
        return (double) this.texIcon.getIconWidth();
    }

    public double prefHeight(double width) {
        return (double) this.texIcon.getIconHeight();
    }

    public final StringProperty formulaProperty() {
        if (this.formula == null) {
            this.formula = new StringPropertyBase("") {
                public void invalidated() {
                    LatexView.this.update();
                }

                public Object getBean() {
                    return LatexView.this;
                }

                public String getName() {
                    return "formula";
                }
            };
        }

        return this.formula;
    }

    public final void setFormula(String value) {
        this.formulaProperty().set(value == null ? "" : value);
    }

    public final String getFormula() {
        String value = (String) this.formulaProperty().get();
        return value == null ? "" : value;
    }

    public final StyleableFloatProperty sizeProperty() {
        if (this.size == null) {
            this.size = new StyleableFloatProperty(DEFAULT_SIZE) {
                public void invalidated() {
                    LatexView.this.update();
                }

                public Object getBean() {
                    return LatexView.this;
                }

                public String getName() {
                    return "size";
                }

                public CssMetaData<LatexView, Number> getCssMetaData() {
                    return LatexView.StyleableProperties.SIZE;
                }
            };
        }

        return this.size;
    }

    public final void setSize(float value) {
        this.sizeProperty().set(value);
    }

    public final float getSize() {
        return this.sizeProperty().get();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return LatexView.StyleableProperties.STYLEABLES;
    }

    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    static {
        BiPredicate<Path, BasicFileAttributes> isFont = (pathx, attr) -> pathx.toString().endsWith(".ttf");
        String[] packages = new String[]{"/org/scilab/forge/jlatexmath/fonts/", "/org/scilab/forge/jlatexmath/cyrillic/fonts/", "/org/scilab/forge/jlatexmath/greek/fonts/"};
        int var3 = packages.length;


        for (String pkg : packages) {
            try {
                URI uri = LatexView.class.getResource(pkg).toURI();
                Path path;
                if (uri.getScheme().equals("jar")) {
                    try {
                        path = FileSystems.newFileSystem(uri, Collections.emptyMap()).getPath(pkg);
                    } catch (FileSystemAlreadyExistsException e) {
                        path = FileSystems.getFileSystem(uri).getPath(pkg);
                    }
                } else {
                    path = Paths.get(uri);
                }

                Stream var10000 = Files.find(path, 5, isFont, new FileVisitOption[0]).map(Path::toString);
                LatexView.class.getClass();
                var10000.map((c) -> c.getClass().getResourceAsStream(c.toString())).forEach((font) -> {
                    Font.loadFont((InputStream) font, -1.0D);
                });
            } catch (IOException | URISyntaxException var8) {
                System.err.println("Failed to load fonts from package " + pkg);
            }
        }

    }

    private static class StyleableProperties {
        private static final CssMetaData<LatexView, Number> SIZE;
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        private StyleableProperties() {
        }

        static {
            SIZE = new CssMetaData<LatexView, Number>("-fx-font-size", new StyleConverter<>(), LatexView.DEFAULT_SIZE) {
                public boolean isSettable(LatexView node) {
                    return node.size == null || !node.size.isBound();
                }

                public StyleableProperty<Number> getStyleableProperty(LatexView node) {
                    return node.sizeProperty();
                }
            };
            List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList();
            styleables.add(SIZE);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
}
