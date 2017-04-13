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
        balls[0] = new Ball(width / 3, height * 0.9, 0.8, 0, 0.2,0.2);
        balls[1] = new Ball(2 * width / 3, height * 0.9, -0.4, 0, 0.3,0.5);
    }

    void step(double deltaT) {
        // TODO this method implements one step of simulation with a step deltaT
        for (Ball b : balls) {

            for (Ball b2 : balls) {

                if(!b.equals(b2)) {
                    if (checkCollision(b, b2)) {
                        handleCollision(b, b2);
                    }
                }
            }

            // detect collision with the border
            if (b.x < b.radius || b.x > areaWidth - b.radius) {

                if (b.x < b.radius) {
                    b.x = b.radius;
                } else if (b.x > areaWidth - b.radius) {
                    b.x = areaWidth - b.radius;
                }
                b.vx *= -1; // change direction of ball
                reduceVelocityX(b);
            }

            if (b.y < b.radius || b.y > areaHeight - b.radius) {

                if (b.y < b.radius) {
                    b.y = b.radius;
                } else if (b.y > areaHeight - b.radius) {
                    b.y = areaHeight - b.radius;
                }
                b.vy *= -1;
                reduceVelocityY(b);
                reduceVelocityX(b);
            }

            //Compute new position during free fall according to Euler's method
            time += deltaT;

            //x'' = 0
            b.x += deltaT * b.vx;

            //F = my''
            b.vy += deltaT * b.g;
            b.y += deltaT * (b.vy);

        }
    }

    /**
     * Simulate a loss of energy in vx
     * @param b the affected ball
     */
    void reduceVelocityX(Ball b) {
        b.vx *= 0.95;
    }

    /**
     * Simulate a loss of energy in vy
     * @param b the affected ball
     */
    void reduceVelocityY(Ball b) {
        b.vy *= 0.95;
    }

    /**
     * Check if we have a collision between two balls
     * @param b1 the first ball
     * @param b2 the second ball
     * @return if we a collision
     */
    boolean checkCollision(Ball b1, Ball b2) {
        Vec2d v = new Vec2d(b1.x,b1.y);
        Vec2d v2 = new Vec2d(b2.x,b2.y);
        return v.distance(v2) < b1.radius + b2.radius;
    }

    /**
     * Handles a collision between two balls
     * @param b1 the first ball
     * @param b2 the second ball
     */
    void handleCollision(Ball b1, Ball b2) {

        Vec2d v1;
        Vec2d v2;

        //Calculate dx and dy
        double dx = Math.abs(b1.x - b2.x);
        double dy = Math.abs(b1.y - b2.y);

        //Calculate angle between b1 and b2
        double beta = rectToPolar(dx, dy).y;

        //Convert to polar coordinates
        v1 = rectToPolar(b1.vx, b1.vy);
        v2 = rectToPolar(b2.vx, b2.vy);

        //Calculate angle in new coordinate system
        v1.y -= beta;
        v2.y -= beta;

        //Convert to rectangular coordinates
        v1 = polarToRect(v1.x, v1.y);
        v2 = polarToRect(v2.x, v2.y);

        //Velocities before collision
        double u1 = v1.x;
        double u2 = v2.x;

        //Calculate new velocities (elastic collision)
        v1.x = (u1 * (b1.m - b2.m) + 2 * b2.m * u2) / (b1.m + b2.m);
        v2.x = (u2 * (b2.m - b1.m) + 2 * b1.m * u1) / (b1.m + b2.m);

        //Convert to polar coordinates
        v1 = rectToPolar(v1.x, v1.y);
        v2 = rectToPolar(v1.x, v2.y);

        //Set back to old angle
        v1.y += beta;
        v2.y += beta;

        //Convert back to x/y coordinates
        v1 = polarToRect(v1.x, v1.y);
        v2 = polarToRect(v2.x, v2.y);
        b1.vx = v1.x;
        b1.vy = v1.y;
        b2.vx = v2.x;
        b2.vy = v2.y;
    }
    
    /**
     * Converts rectangular coordinates to polar
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    Vec2d rectToPolar(double x, double y) {
        double r = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        double q = Math.atan(y/x);

        return new Vec2d(r, q);
    }

    /**
     * Converts polar coordinates to rectangular
     * @param r the distance from the origin
     * @param q the angle in radians measured from the positive x-axis to the point
     */
    Vec2d polarToRect(double r, double q) {
        double x = r * Math.cos(q);
        double y = r * Math.sin(q);

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
            g = -9.82;
        }

        /**
         * Position, speed, and radius of the ball. You may wish to add other attributes.
         */
        double x, y, vx, vy, radius, m, g;
    }
}