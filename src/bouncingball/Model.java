package bouncingball;

import com.sun.javafx.geom.Vec2d;

/**
 * The physics model.
 */
class Model {

    double areaWidth, areaHeight;

    Ball [] balls;
    private double deltaT;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new Ball[3];
        balls[0] = new Ball(width * 0.3, height * 0.8, 0.5, 0, 0.2,0.4);
        balls[1] = new Ball(width * 0.7, height * 0.5, -0.5, 0, 0.3,0.6);
        balls[2] = new Ball(width * 0.1, height * 0.6, -0.7, 0, 0.3,0.6);
    }

    void step(double deltaT) {
        this.deltaT = deltaT;

        for (int i = 0; i < balls.length; i++) {

            Ball b = balls[i];
            move(b);
            for (int j = i + 1; j < balls.length; j++) {

                Ball b2 = balls[j];
                //Check for collisions
                if (checkCollision(b, b2)) {
                    handleOverlap(b, b2);
                    handleCollision(b, b2);
                }
            }

            //Detect collision with the border
            if (b.x < b.radius || b.x > areaWidth - b.radius) {

                if (b.x < b.radius) {
                    b.x = b.radius;
                } else if (b.x > areaWidth - b.radius) {
                    b.x = areaWidth - b.radius;
                }
                b.vx *= -1;
                reduceVelocityX(b);
            }

            //Detect collision with the border
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
        }
    }

    /**
     * Move a ball
     * @param b the affected ball
     */
    void move(Ball b) {

        b.x += deltaT * b.vx;
        b.vy += deltaT * b.g;
        b.y += deltaT * (b.vy);
    }

    /**
     * Simulate a loss of energy in vx
     * @param b the affected ball
     */
    void reduceVelocityX(Ball b) {
        b.vx *= 0.98;
    }

    /**
     * Simulate a loss of energy in vy
     * @param b the affected ball
     */
    void reduceVelocityY(Ball b) {
        b.vy *= 0.98;
    }

    /**
     * Check if we have a collision between two balls
     * @param b1 the first ball
     * @param b2 the second ball
     * @return if we a collision
     */
    boolean checkCollision(Ball b1, Ball b2) {
        return Math.sqrt((b2.x - b1.x)*(b2.x - b1.x)+(b2.y - b1.y)*(b2.y - b1.y)) <= b1.radius + b2.radius;
    }

    /**
     * Handles a collision between two balls
     * @param b1 the first ball
     * @param b2 the second ball
     */
    void handleCollision(Ball b1, Ball b2) {

        double u1x = b1.vx;
        double u2x = b2.vx;
        double u1y = b1.vy;
        double u2y = b2.vy;
        Vec2d v1 = new Vec2d(u1x, u1y);
        Vec2d v2 = new Vec2d(u2x, u2y);

        //Calculate new velocities (elastic collision)
        v1.x = (u1x * (b1.m - b2.m) + 2 * b2.m * u2x) / (b1.m + b2.m);
        v2.x = (u2x * (b2.m - b1.m) + 2 * b1.m * u1x) / (b1.m + b2.m);
        v1.y = (u1y * (b1.m - b2.m) + 2 * b2.m * u2y) / (b1.m + b2.m);
        v2.y = (u2y * (b2.m - b1.m) + 2 * b1.m * u1y) / (b1.m + b2.m);

        //Set new velocities
        b1.vx = v1.x;
        b1.vy = v1.y;
        b2.vx = v2.x;
        b2.vy = v2.y;
    }

    /**
     * Prevent overlapping of two balls
     * @param b1 the first ball
     * @param b2 the second ball
     */
    void handleOverlap(Ball b1, Ball b2) {

        while(checkCollision(b1, b2)) {
            b1.x += deltaT / 100 * -1 * b1.vx;
            b1.y += deltaT / 100 * -1 * b1.vy;

            b2.x += deltaT / 100 * -1 * b2.vx;
            b2.y += deltaT / 100 * -1 * b2.vy;
        }
    }

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
        double x, y, vx, vy, radius, m, g;
    }
}