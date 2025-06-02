package com.group_finity.mascot.script;

import com.group_finity.mascot.exception.VariableException;

/**
 * @author Yuki Yamada of <a href="http://www.group-finity.com/Shimeji/">Group Finity</a>
 * @author Shimeji-ee Group
 * @author Valkryst
 */
public abstract class Variable {
    public abstract void init();

    public abstract void initFrame();

    public abstract Object get(VariableMap variables) throws VariableException;

    public static Variable parse(final String source) throws VariableException {
        if (source == null) {
            return null;
        }

        if (source.startsWith("${") && source.endsWith("}")) {
            return new Script(source.substring(2, source.length() - 1), false);
        } else if (source.startsWith("#{") && source.endsWith("}")) {
            return new Script(source.substring(2, source.length() - 1), true);
        } else {
            return new Constant(parseConstant(source));
        }
    }

    private static Object parseConstant(final String source) {
        if (source.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        } else if (source.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        } else {
            try {
                return Double.parseDouble(source);
            } catch (final NumberFormatException e) {
                return source;
            }
        }
    }
}
