package com.group_finity.mascot.environment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

/**
 * @author Yuki Yamada of <a href="http://www.group-finity.com/Shimeji/">Group Finity</a>
 * @author Shimeji-ee Group
 */
@AllArgsConstructor
@Getter
public class Wall implements Border {
    private Area area;

    private boolean right;

    public int getX() {
        return isRight() ? getArea().getRight() : getArea().getLeft();
    }

    public int getTop() {
        return getArea().getTop();
    }

    public int getBottom() {
        return getArea().getBottom();
    }

    public int getDX() {
        return isRight() ? getArea().getDright() : getArea().getDleft();
    }

    public int getDTop() {
        return getArea().getDtop();
    }

    public int getDBottom() {
        return getArea().getDbottom();
    }

    public int getHeight() {
        return getArea().getHeight();
    }

    @Override
    public boolean isOn(final Point location) {
        return getArea().isVisible() && getX() == location.x && getTop() <= location.y
                && location.y <= getBottom();
    }

    @Override
    public Point move(final Point location) {
        if (!getArea().isVisible()) {
            return location;
        }

        final int d = getBottom() - getDBottom() - (getTop() - getDTop());
        if (d == 0) {
            return location;
        }

        final Point newLocation = new Point(location.x + getDX(), (location.y - (getTop() - getDTop()))
                * (getBottom() - getTop()) / d + getTop());

        if (Math.abs(newLocation.x - location.x) >= 80 || Math.abs(newLocation.y - location.y) >= 80) {
            return location;
        }

        return newLocation;
    }
}
