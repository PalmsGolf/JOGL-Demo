import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.swing.JFrame;
import java.io.InputStream;

public class RotatingCubeGL2Demo implements GLEventListener {
    private float angle = 0.0f;
    private Texture texture;
    private int shaderProgram;
    private int alphaLocation;

    public static void main(String[] args) {
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(capabilities);

        JFrame frame = new JFrame("JOGL Transparent Textured Box Example");
        frame.setSize(800, 600);
        frame.add(canvas);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas.addGLEventListener(new RotatingCubeGL2Demo());

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClearColor(0.68f, 0.85f, 0.9f, 1.0f);

        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glEnable(GL.GL_TEXTURE_2D);


        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_BLEND);
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("texture.png")) {
            if (stream != null) {
                texture = TextureIO.newTexture(stream, true, TextureIO.JPG);
            } else {
                throw new RuntimeException("Texture file not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        shaderProgram = createShaderProgram(gl, "vertex_shader.glsl", "fragment_shader.glsl");

        alphaLocation = gl.glGetUniformLocation(shaderProgram, "alpha");
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        if (texture != null) {
            texture.destroy(drawable.getGL().getGL2());
        }

        GL2 gl = drawable.getGL().getGL2();
        gl.glDeleteProgram(shaderProgram);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glRotatef(angle, 1.0f, 1.0f, 0.0f);

        if (texture != null) {
            texture.bind(gl);
        }

      //  gl.glUseProgram(shaderProgram);


        gl.glUniform1f(alphaLocation, 0.9f);


        drawTexturedBox(gl);
        gl.glUseProgram(0);
        gl.glFlush();

        angle += 0.5f;
        if (angle > 360.0f) {
            angle -= 360.0f;
        }
    }

    private void drawTexturedBox(GL2 gl) {
        float[] vertices = {
                // Front face
                -1.0f, -1.0f, 1.0f,  0.0f, 0.0f,
                1.0f, -1.0f, 1.0f,  1.0f, 0.0f,
                1.0f,  1.0f, 1.0f,  1.0f, 1.0f,
                -1.0f,  1.0f, 1.0f,  0.0f, 1.0f,
                // Back face
                -1.0f, -1.0f, -1.0f, 0.0f, 0.0f,
                -1.0f,  1.0f, -1.0f, 0.0f, 1.0f,
                1.0f,  1.0f, -1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, -1.0f, 1.0f, 0.0f,
                // Left face
                -1.0f, -1.0f, -1.0f, 0.0f, 0.0f,
                -1.0f, -1.0f,  1.0f, 0.0f, 1.0f,
                -1.0f,  1.0f,  1.0f, 1.0f, 1.0f,
                -1.0f,  1.0f, -1.0f, 1.0f, 0.0f,
                // Right face
                1.0f, -1.0f, -1.0f, 0.0f, 0.0f,
                1.0f,  1.0f, -1.0f, 0.0f, 1.0f,
                1.0f,  1.0f,  1.0f, 1.0f, 1.0f,
                1.0f, -1.0f,  1.0f, 1.0f, 0.0f,
                // Top face
                -1.0f,  1.0f, -1.0f, 0.0f, 0.0f,
                -1.0f,  1.0f,  1.0f, 0.0f, 1.0f,
                1.0f,  1.0f,  1.0f, 1.0f, 1.0f,
                1.0f,  1.0f, -1.0f, 1.0f, 0.0f,
                // Bottom face
                -1.0f, -1.0f, -1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, -1.0f, 1.0f, 0.0f,
                1.0f, -1.0f,  1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f,  1.0f, 0.0f, 1.0f
        };

        gl.glBegin(GL2.GL_QUADS);
        for (int i = 0; i < vertices.length; i += 5) {
            gl.glTexCoord2f(vertices[i + 3], vertices[i + 4]);
            gl.glVertex3f(vertices[i], vertices[i + 1], vertices[i + 2]);
        }
        gl.glEnd();
    }

    private int createShaderProgram(GL2 gl, String vertexShaderFile, String fragmentShaderFile) {
        int vertexShader = loadShader(gl, vertexShaderFile, GL3.GL_VERTEX_SHADER);
        int fragmentShader = loadShader(gl, fragmentShaderFile, GL3.GL_FRAGMENT_SHADER);

        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);
        gl.glLinkProgram(program);

        int[] linkStatus = new int[1];
        gl.glGetProgramiv(program, GL3.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GL.GL_TRUE) {
            int[] logLength = new int[1];
            gl.glGetProgramiv(program, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
            byte[] log = new byte[logLength[0]];
            gl.glGetProgramInfoLog(program, logLength[0], (int[]) null, 0, log, 0);
            System.err.println("Error linking shader program: " + new String(log));
            gl.glDeleteProgram(program);
            return 0;
        }

        return program;
    }

    private int loadShader(GL2 gl, String fileName, int shaderType) {
        int shader = gl.glCreateShader(shaderType);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new RuntimeException("Shader file not found: " + fileName);
            }

            StringBuilder shaderCode = new StringBuilder();
            int ch;
            while ((ch = inputStream.read()) != -1) {
                shaderCode.append((char) ch);
            }

            String[] shaderSource = new String[]{shaderCode.toString()};
            int[] lengths = new int[]{shaderSource[0].length()};
            gl.glShaderSource(shader, 1, shaderSource, lengths, 0);
            gl.glCompileShader(shader);

            int[] compileStatus = new int[1];
            gl.glGetShaderiv(shader, GL2.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] != GL.GL_TRUE) {
                int[] logLength = new int[1];
                gl.glGetShaderiv(shader, GL2.GL_INFO_LOG_LENGTH, logLength, 0);
                byte[] log = new byte[logLength[0]];
                gl.glGetShaderInfoLog(shader, logLength[0], (int[]) null, 0, log, 0);
                System.err.println("Error compiling shader (" + fileName + "): " + new String(log));
                gl.glDeleteShader(shader);
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            gl.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        new GLU().gluPerspective(45.0, (double) width / height, 1.0, 100.0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
}
