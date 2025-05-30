package com.group_finity.mascot.action;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.environment.MascotEnvironment;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.hotspot.Hotspot;
import com.group_finity.mascot.script.Variable;
import com.group_finity.mascot.script.VariableMap;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ResourceBundle;

/**
 * Abstract class that implements common functionality of actions.
 *
 * @author Yuki Yamada of <a href="http://www.group-finity.com/Shimeji/">Group Finity</a>
 * @author Shimeji-ee Group
 */
public abstract class ActionBase implements Action {
    public static final String PARAMETER_DURATION = "Duration";

    private static final int DEFAULT_DURATION = Integer.MAX_VALUE;

    public static final String PARAMETER_CONDITION = "Condition";

    private static final boolean DEFAULT_CONDITION = true;

    public static final String PARAMETER_DRAGGABLE = "Draggable";

    private static final boolean DEFAULT_DRAGGABLE = true;

    public static final String PARAMETER_AFFORDANCE = "Affordance";

    private static final String DEFAULT_AFFORDANCE = "";

    @Getter @Setter private Mascot mascot;

    private int startTime;

    @Getter private final List<Animation> animations;

    @Getter private final VariableMap variables;

    @Getter private final ResourceBundle schema;

    public ActionBase(ResourceBundle schema, final List<Animation> animations, final VariableMap context) {
        this.schema = schema;
        this.animations = animations;
        variables = context;
    }

    @Override
    public String toString() {
        try {
            return "Action(" + getClass().getSimpleName() + "," + getName() + ")";
        } catch (final VariableException e) {
            return "Action(" + getClass().getSimpleName() + "," + null + ")";
        }
    }

    @Override
    public void init(final Mascot mascot) throws VariableException {
        if (mascot == null) {
            return;
        }

        setMascot(mascot);
        setTime(0);

        // Add mascot and action to the variable map, so they can be used in the script
        getVariables().put("mascot", mascot);
        getVariables().put("action", this);

        // Initialize variable values
        getVariables().init();

        // Initialize the animations
        for (final Animation animation : animations) {
            animation.init();
        }
    }

    @Override
    public void next() throws LostGroundException, VariableException {
        if (getMascot() == null) {
            return;
        }

        initFrame();

        // Clear affordances
        if (!getMascot().getAffordances().isEmpty()) {
            getMascot().getAffordances().clear();
        }
        if (!getAffordance().trim().isEmpty()) {
            getMascot().getAffordances().add(getAffordance());
        }

        // Refresh hotspots
        refreshHotspots();

        tick();
    }

    private void initFrame() {
        // Initialize variable values (each frame)
        getVariables().initFrame();

        // Initialize animation frames
        for (final Animation animation : getAnimations()) {
            animation.initFrame();
        }
    }

    protected abstract void tick() throws LostGroundException, VariableException;

    @Override
    public boolean hasNext() throws VariableException {
        if (getMascot() == null) {
            return false;
        }

        final boolean effective = isEffective();
        final boolean inTime = getTime() < getDuration();

        return effective && inTime;
    }

    protected void refreshHotspots() {
        synchronized (getMascot().getHotspots()) {
            getMascot().getHotspots().clear();
            try {
                if (getAnimation() != null) {
                    for (final Hotspot hotspot : getAnimation().getHotspots())
                        getMascot().getHotspots().add(hotspot);
                }
            } catch (VariableException ex) {
                getMascot().getHotspots().clear();
            }
        }
    }

    public Boolean isDraggable() throws VariableException {
        return eval(schema.getString(PARAMETER_DRAGGABLE), Boolean.class, DEFAULT_DRAGGABLE);
    }

    private Boolean isEffective() throws VariableException {
        return eval(schema.getString(PARAMETER_CONDITION), Boolean.class, DEFAULT_CONDITION);
    }

    private int getDuration() throws VariableException {
        return eval(schema.getString(PARAMETER_DURATION), Number.class, DEFAULT_DURATION).intValue();
    }

    protected String getAffordance() throws VariableException {
        return eval(schema.getString(PARAMETER_AFFORDANCE), String.class, DEFAULT_AFFORDANCE);
    }

    protected int getTime() {
        return getMascot().getTime() - startTime;
    }

    protected void setTime(final int time) {
        startTime = getMascot().getTime() - time;
    }

    private String getName() throws VariableException {
        return eval(schema.getString("Name"), String.class, null);
    }

    protected Animation getAnimation() throws VariableException {
        for (final Animation animation : getAnimations()) {
            if (animation.isEffective(getVariables())) {
                return animation;
            }
        }

        return null;
    }

    protected void putVariable(final String key, final Object value) {
        synchronized (getVariables()) {
            getVariables().put(key, value);
        }
    }

    protected <T> T eval(final String name, final Class<T> type, final T defaultValue) throws VariableException {
        synchronized (getVariables()) {
            final Variable variable = getVariables().getRawMap().get(name);
            if (variable != null) {
                return type.cast(variable.get(getVariables()));
            }
        }

        return defaultValue;
    }

    protected MascotEnvironment getEnvironment() {
        return getMascot().getEnvironment();
    }
}
