import com.jogamp.opengl.GLEventListener;

public class RendererFactory {

    public enum RendererType {
        SIMPLE_SQUARE,
        ROTATING_SQUARE,
        ROTATING_SQUARE_WITH_LIGHTING
    }

    public static GLEventListener createRenderer(RendererType type) {
        return switch (type) {
            case SIMPLE_SQUARE -> new SimpleSquareSceneRenderer();
            case ROTATING_SQUARE -> new RotatingSquareSceneRenderer();
            case ROTATING_SQUARE_WITH_LIGHTING -> new RotatingSquareSceneWithLightingRenderer();
        };
    }
}
