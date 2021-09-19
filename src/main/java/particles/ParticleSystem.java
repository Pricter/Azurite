/**
 * <h1>This class, creates the number of Particle Class', specified by the variable PARTICLE_COUNT, in the update method, it calls the update function in Particle class for all the particles</h1>
 *
 * @author CGO Clan (Mr. Hecker)
 * @version 19.09.2021
 * @see particles
 * @since 19.09.2021
 */


package particles;

import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import graphics.Color;
import graphics.Window;
import input.Mouse;
import org.joml.Vector2f;

public class ParticleSystem {
    /**
     * Particle Count (Number of particles to be generated)
     */
    private static final int PARTICLE_COUNT = 25000;

    /**
     * Array that stores the all the particles
     */
    private Particle[] particles = new Particle[PARTICLE_COUNT];

    /**
     * Random
     */
    private static Random random = new Random();

    /**
     * Color 1
     */
    private static Color colorA = new Color(1, 1, 1, 1);

    /**
     * Color 2
     */
    private static Color colorB = new Color(1, 1, 1, 1);

    /**
     * This variable tracks the time that has been passed from the generation of the particle, and lerps the color to the following percentage of its total lifespan
     */
    private float percentage;

    public ParticleSystem() {
        for (int i = 0; i < particles.length; i++)
            particles[i] = new Particle(new Vector2f(
                    random.nextInt(Window.getWidth()),
                    random.nextInt(Window.getHeight())), 0, 0, 1);
    }

    public void update(float delta) {
        // Update color
        percentage += delta;
        if (percentage > 1f) {
            colorB.setColor(random.nextFloat(), random.nextFloat(),
                    random.nextFloat(), random.nextFloat() * (1 - 0.6f) + 0.6f);
            percentage = 0;
        }

        colorA.lerp(colorB, percentage);

        int pLength = particles.length;
        for (int i = 0; i < pLength; i++) {
            if (Mouse.mouseButtonDown(0))
                particles[i].applyForce(Mouse.getMouseX(), Mouse.getMouseY(), 1, true);
            else if (Mouse.mouseButtonDown(1))
                particles[i].applyForce(Mouse.getMouseX(), Mouse.getMouseY(), 1, false);
            else
                particles[i].magnitude *= 0.985f;

            particles[i].update(delta);
        }
    }

    public void render() {
        int pLength = particles.length;
        for (int i = 0; i < pLength; i++) {
            glColor4f(colorA.r, colorA.g, colorA.b, colorA.a);
            particles[i].render();
        }
    }
}