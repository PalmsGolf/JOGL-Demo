import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);

        JFrame frame = new JFrame("JOGL Demo");
        frame.setSize(600, 600);
        frame.add(canvas);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();

        RendererFactory.RendererType selectedRenderer = RendererFactory.RendererType.ROTATING_SQUARE_WITH_LIGHTING;
        GLEventListener renderer = RendererFactory.createRenderer(selectedRenderer);

        canvas.addGLEventListener(renderer);
    }
}
