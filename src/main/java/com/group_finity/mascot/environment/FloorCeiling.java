package com.group_finity.mascot.environment;

import lombok.AllArgsConstructor;

import java.awt.*;

/**
 * @author Yuki Yamada of <a href="http://www.group-finity.com/Shimeji/">Group Finity</a>
 * @author Shimeji-ee Group
 */
@AllArgsConstructor
public class FloorCeiling implements Border {
    private Area area;

    private boolean isBottom;

    public int getY() {
        return isBottom ? area.getBottom() : area.getTop();
    }

    public int getLeft() {
        return area.getLeft();
    }

    public int getRight() {
        return area.getRight();
    }

    public int getDY() {
        return isBottom ? area.getDbottom() : area.getDtop();
    }

    public int getDLeft() {
        return area.getDleft();
    }

    public int getDRight() {
        return area.getDright();
    }

    public int getWidth() {
        return area.getWidth();
    }

    @Override
    public boolean isOn(final Point location) {
        return area.isVisible() && getY() == location.y && getLeft() <= location.x
                && location.x <= getRight();
    }

    @Override
    public Point move(final Point location) {
        if (!area.isVisible()) {
            return location;
        }

        final int d = getRight() - getDRight() - (getLeft() - getDLeft());
        if (d == 0) {
            return location;
        }

        final Point newLocation = new Point((location.x - (getLeft() - getDLeft())) * ((getRight() - getLeft()) / d)
                + getLeft(), location.y + getDY());

        if (Math.abs(newLocation.x - location.x) >= 80 || newLocation.y - location.y > 20
                || newLocation.y - location.y < -80) {
            return location;
        }

        return newLocation;
    }
}
