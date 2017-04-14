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
    boolean collisionHandled = false;

    Model(double width, double height) {
        areaWidth = width;
        areaHeight = height;

        // Initialize the model with a few balls
        balls = new Ball[2];
        balls[0] = new Ball(width / 3, height * 0.6, 0.4, 0, 0.2,3);
        balls[1] = new Ball(3 * width / 3, height * 0.9, -0.6, 0, 0.3,5);
    }

    void step(double deltaT) {
        // TODO this method implements one step of simulation with a step deltaT
        for (Ball b : balls) {

            move(b, deltaT);
            for (Ball b2 : balls) {

                if(!b.equals(b2)) {
                    if (checkCollision(b, b2)) {
                        handleCollision(b, b2, b.vx, b2.vx);
                        move(b, deltaT);
                        move(b2, deltaT);
                        //System.out.println(rectToPolar(-1,-1));
                        //System.exit(0);
                    }
                }
            }

            //Detect collision with the border
            if (b.x < b.radius || b.x > areaWidth - b.radius) {

                if (b.x < b.radius) {
                    b.x = b.radius;
                } else if (b.x > areaWidth - b.radius) {
                    b.x = areaWidth - b.radius;
                }
                b.vx *= -1; // change direction of ball
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
     * @param deltaT simulation step
     */
    void move(Ball b, double deltaT) {
        time += deltaT;

        //x'' = 0
        b.x += deltaT * b.vx;

        //F = my''
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
        return Math.sqrt((b2.x - b1.x)*(b2.x - b1.x)+(b2.y - b1.y)*(b2.y - b1.y)) < b1.radius + b2.radius;
    }

    /**
     * Handles a collision between two balls
     * @param b1 the first ball
     * @param b2 the second ball
     */
    void handleCollision(Ball b1, Ball b2, double u1, double u2) {

        if (!collisionHandled) {
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

            //Calculate new velocities (elastic collision)
            v1.x = (u1 * (b1.m - b2.m) + 2 * b2.m * u2) / (b1.m + b2.m);
            v2.x = (u2 * (b2.m - b1.m) + 2 * b1.m * u1) / (b1.m + b2.m);

            //System.out.println(v1.x + " " + v2.x + " " + v1.y + " " + v2.y);
            //Convert to polar coordinates
            v1 = rectToPolar(v1.x, v1.y);
            v2 = rectToPolar(v2.x, v2.y);

            //Set back to old angle
            v1.y += beta;
            v2.y += beta;

            //Convert back to x/y coordinates
            v1 = polarToRect(v1.x, v1.y);
            v2 = polarToRect(v2.x, v2.y);
            //System.out.println(v1.x + " " + v2.x + " " + v1.y + " " + v2.y);
            b1.vx = v1.x;
            b1.vy = v1.y;
            b2.vx = v2.x;
            b2.vy = v2.y;

            System.out.println("b1 " + "vx: " + b1.vx + " r: " + b1.radius + "\nb2 " + "vx: " + b2.vx + " r: " + b2.radius);

            solveOverlap(b1, b2);
            collisionHandled = true;
        } else {
            collisionHandled = false;
        }
    }

    /**
     * Prevent overlapping of two balls
     * @param b1 the first ball
     * @param b2 the second ball
     */
    void solveOverlap(Ball b1, Ball b2) {
        double dx = (b1.x - b2.x);
        double dy = (b1.y - b2.y);
        double radiusSum = b1.radius + b2.radius;

        if (dx <  0) {
            double diffX = dx + radiusSum;
            if (diffX > 0) {
                b1.x -= diffX / 2;
                b2.x += diffX / 2;
            }
        } else if (dx > 0) {
            double diffX = dx - radiusSum;
            if (diffX < 0) {
                b1.x -= diffX / 2;
                b2.x += diffX / 2;
            }
        }
        if (dy < 0) {
            double diffY = dy + radiusSum;
            if (diffY > 0) {
                b1.y -= diffY / 2;
                b2.y += diffY / 2;
            }
        } else if (dy > 0) {
            double diffY = dy - radiusSum;
            if (diffY < 0) {
                b1.y -= diffY / 2;
                b2.y += diffY / 2;
            }
        }
    }

    /**
     * Converts rectangular coordinates to polar
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    Vec2d rectToPolar(double x, double y) {
        double r = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        double q;

        if (x >= 0 && y >= 0) {
            //First quadrant
            q = Math.atan(y/x);
        } else if(x < 0 && y > 0) {
            //Second quadrant
            r *= -1;
            q = Math.atan(y/x);
        } else if(x < 0 && y < 0) {
            //Third quadrant
            r *= -1;
            q = Math.atan(y/x);
        } else {
            //Fourth quadrant
            r *= -1;
            q = Math.atan(y/x);
        }
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