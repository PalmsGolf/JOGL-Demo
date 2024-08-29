import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.jogamp.opengl.GL3.GL_COMPILE_STATUS;
import static com.jogamp.opengl.GL3.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL3.GL_INFO_LOG_LENGTH;
import static com.jogamp.opengl.GL3.GL_LINK_STATUS;
import static com.jogamp.opengl.GL3.GL_TRUE;
import static com.jogamp.opengl.GL3.GL_VERTEX_SHADER;

public class JoglUtils {

    public static Texture loadTexture(String path) {
        try (InputStream stream = JoglUtils.class.getResourceAsStream(path)) {
            return TextureIO.newTexture(stream, false, TextureIO.PNG);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load texture: " + path, e);
        }
    }

    public static int createProgram(GL3 gl, String vertexPath, String fragmentPath) {
        int vertexShader = createShader(gl, GL_VERTEX_SHADER, vertexPath);
        int fragmentShader = createShader(gl, GL_FRAGMENT_SHADER, fragmentPath);

        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);
        gl.glLinkProgram(program);

        int[] linkStatus = new int[1];
        gl.glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GL_TRUE) {
            //System.out.println("Error linking program");
            //System.out.println(getProgramInfoLog(gl, program));

            gl.glDeleteProgram(program);

            return 0;
        }

        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);

        return program;
    }

    private static int createShader(GL3 gl, int type, String path) {
        int shader = gl.glCreateShader(type);

        String[] source = {readResource(path)};
        gl.glShaderSource(shader, 1, source, null);
        gl.glCompileShader(shader);

        int[] compileStatus = new int[1];
        gl.glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] != GL_TRUE) {
            System.err.println("Error compiling shader");
            gl.glDeleteShader(shader);

            return 0;
        }

        return shader;
    }

    private static String readResource(String path) {
        try (InputStream in = JoglUtils.class.getResourceAsStream(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource: " + path, e);
        }
    }

    private static String getShaderInfoLog(GL3 gl, int shader) {
        int[] infoLogLength = new int[1];
        gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, infoLogLength, 0);

        byte[] infoLog = new byte[infoLogLength[0]];
        gl.glGetShaderInfoLog(shader, infoLogLength[0], (int[]) null, 0, infoLog, 0);

        return new String(infoLog, StandardCharsets.UTF_8);
    }

    private static String getProgramInfoLog(GL3 gl, int program) {
        int[] infoLogLength = new int[1];
        gl.glGetProgramiv(program, GL_INFO_LOG_LENGTH, infoLogLength, 0);

        byte[] infoLog = new byte[infoLogLength[0]];
        gl.glGetProgramInfoLog(program, infoLogLength[0], null, 0, infoLog, 0);

        return new String(infoLog, StandardCharsets.UTF_8);
    }
}
