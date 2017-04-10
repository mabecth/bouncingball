package bouncingball;

import com.sun.javafx.geom.Vec2d;

import java.util.Vector;

/**
 * The physics model.
 *
 * This class is where you should implement your bouncing balls model.
 *
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 *
 * @author Simon Robillard
 *
 */
class Model {

    double areaWidth, areaHeight;

    Ball [] balls;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new Ball[2];
        balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2,0.2);
        balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3,0.2);
    }

    void step(double deltaT) {
        // TODO this method implements one step of simulation with a step deltaT
        for (Ball b : balls) {
            // detect collision with the border
            if (b.x < b.radius || b.x > areaWidth - b.radius) {
                b.vx *= -1; // change direction of ball
            }
            if (b.y < b.radius || b.y > areaHeight - b.radius) {
                b.vy *= -1;
            }

            // compute new position according to the speed of the ball
            b.x += deltaT * b.vx;
            b.y += deltaT * b.vy;
        }
    }

    //http://www.teacherschoice.com.au/maths_library/coordinates/polar_-_rectangular_conversion.htm
    Vec2d rectToPolar(double x, double y) {
        double r = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        double q = Math.atan(y/x);


        return new Vec2d(r,q);

    }

    Vec2d polarToRect() {
        return new Vec2d();
    }

    /**
     * Simple inner class describing balls.
     */
    class Ball {

        Ball(double x, double y, double vx, double vy, double r, double m) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = r;
            this.m = m;
        }

        /**
         * Position, speed, and radius of the ball. You may wish to add other attributes.
         */
        double x, y, vx, vy, radius, m;
        double g = -9.82;
    }
}