package app.utils.tools;

import processing.core.PApplet;
import processing.core.PVector;

public class MeasureTool {

    public static final float PI = 3.1415f;
    public static final float DELTA = 0.001f;

    static public float calculateAngleAcos(PVector coordOrigin, PVector first, PVector second) {
        float angle = 0.0f;
        if (!first.equals(second)) {
            PVector originToFirst = PVector.sub(first, coordOrigin);
            PVector originToSecond = PVector.sub(second, coordOrigin);
            angle = PVector.angleBetween(originToFirst, originToSecond);
        }
        return angle;
    }

    static public float calculateAngleAtan2(PVector coordOrigin, PVector target) {
        PVector originToTarget = PVector.sub(target,coordOrigin);
        return (PApplet.atan2(originToTarget.y,originToTarget.x));
    }

    static public float calculateAngleAtan2(PVector coordOrigin, PVector base, PVector target) {
        float angle = 0.0f;
        if (!target.equals(base)) {
            angle = calculateAngleAtan2(coordOrigin,target) - calculateAngleAtan2(coordOrigin,base);
            angle += (angle < -PI) ? (2 * PApplet.PI) : ((angle > PI) ? (-2 * PApplet.PI) : 0);
        }
        return angle;
    }

    static public float calculateCircleLength(int radius) {
        return (2 * PApplet.PI * radius);
    }

    static public float calculateArcLength(PVector coordOrigin, PVector first, PVector second, int radius)  {
        return (radius * calculateAngleAcos(coordOrigin, first, second));
    }

    static public float calculateReverseArcLength(PVector coordOrigin, PVector first, PVector second, int radius) {
        return (calculateCircleLength(radius) - calculateArcLength(coordOrigin,first,second,radius));
    }

    static public int calculateLongestArcDirection(PVector coordOrigin, PVector base, PVector target1,
                                                   PVector target2) {
        int directionSign = 0;
        float angleBaseToTarget1 = calculateAngleAtan2(coordOrigin,base,target1),
              angleBaseToTarget2 = calculateAngleAtan2(coordOrigin,base,target2),
              absAngle1 = PApplet.abs(angleBaseToTarget1),
              absAngle2 = PApplet.abs(angleBaseToTarget2);
        float deg1 = PApplet.degrees(angleBaseToTarget1), deg2 = PApplet.degrees(angleBaseToTarget2);
        System.out.println("RADIANS: " + absAngle1 + " " + absAngle2);
        System.out.println("DEGREES: " + deg1 + " " + deg2);
        System.out.println("RAZNITSA WITH PI: " + PApplet.abs(absAngle1 - PI) + " LESS THAN PI: " + (PApplet.abs(absAngle1 - PI) > DELTA));
        System.out.println("RAZNITSA WITH EACH OTHER: " + PApplet.abs(absAngle1 - absAngle2) + " LESS THAN OTHER: " + ((PApplet.abs(absAngle1 - absAngle2) > DELTA)));
        if ((PApplet.abs(absAngle1 - PI) > DELTA || !target1.equals(target2)) && ((target1.equals(target2)) || (PApplet.abs(absAngle1 - absAngle2) > DELTA))) {
            directionSign = (int) ((absAngle1 < absAngle2) ? Math.signum(angleBaseToTarget2) : Math.signum(angleBaseToTarget1));
            directionSign *= (Math.signum(angleBaseToTarget1)==Math.signum(angleBaseToTarget2)) ? -1 : 1;
        }
        return directionSign;
    }

}
