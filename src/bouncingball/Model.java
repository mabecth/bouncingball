package bouncingball;

import com.sun.javafx.geom.Vec2d;

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
    double time = 0;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new Ball[2];
        balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2,0.2);
        balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3,0.5);
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

            // compute new position during free fall according to euler's method
            time += deltaT;

            //x'' = 0
            b.x += deltaT * b.vx;

            //F = my''
            b.y += deltaT * (b.vy + deltaT * b.g / 2);
            b.vy += deltaT * b.g;
        }
    }

    //http://www.teacherschoice.com.au/maths_library/coordinates/polar_-_rectangular_conversion.htm
    //Convert rectangular coordinates to polar
    Vec2d rectToPolar(double x, double y) {
        double r = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        double q = Math.atan(y/x);

        return new Vec2d(r,q);
    }

    //Convert polar coordinates to rectangular
    Vec2d polarToRect(double r, double q) {
        double x =r*Math.cos(q);
        double y =r*Math.sin(q);

        return new Vec2d(x,y);
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
            kineticX = (m * Math.pow(vx,2)) / 2;
            kineticY = (m * Math.pow(vy,2)) / 2;
            momentumX = m * vx;
            momentumY = m * vy;
        }

        /**
         * Position, speed, and radius of the ball. You may wish to add other attributes.
         */
        double x, y, vx, vy, radius, m, kineticX, kineticY, momentumX, momentumY;
        double g = -9.82;
    }
}