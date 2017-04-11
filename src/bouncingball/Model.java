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

            for (Ball b2 : balls) {

                if(!b.equals(b2)) {
                    if (checkCollision(b, b2)) {
                        handleCollision(b, b2);
                    }
                }
            }

            // detect collision with the border
            if (b.x < b.radius || b.x > areaWidth - b.radius) {
                b.vx *= -1; // change direction of ball
                reduceVelocityX(b);
            }
            if (b.y < b.radius || b.y > areaHeight - b.radius) {
                b.vy *= -1;
                reduceVelocityY(b);
                reduceVelocityX(b);
            }

            // compute new position during free fall according to euler's method
            time += deltaT;

            //x'' = 0
            b.x += deltaT * b.vx;

            //Prevent ball from falling off screen
            if (b.y - b.radius < 0) {
                b.y = b.radius;

            }

            //F = my''
            b.vy -= deltaT * b.g;
            b.y -= deltaT * (b.vy);
        }
    }

    //Simulate friction as loss of energy when hitting the ground or wall
    void reduceVelocityX(Ball b) {
        b.vx *= 0.95;
    }

    //Simulate a loss of energy when hitting the ground
    void reduceVelocityY(Ball b) {
        b.vy *= 0.95;
    }

    boolean checkCollision(Ball b1, Ball b2) {
        Vec2d v = new Vec2d(b1.x,b1.y);
        Vec2d v2 = new Vec2d(b2.x,b2.y);
        return v.distance(v2) <= b1.radius + b2.radius;
    }

    void handleCollision(Ball b1, Ball b2) {
        //Conservation of momentum
        double momentum = (b1.m * b1.vx) + (b2.m * b2.vx);
        //Conservation of kinetic energy
        double kinetic = ((b1.m * Math.pow(b1.vx,2)) + (b2.m * Math.pow(b2.vx,2))) / 2;
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