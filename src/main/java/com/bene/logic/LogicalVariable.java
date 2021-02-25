package com.bene.logic;

import java.util.HashSet;
import java.util.Objects;

/**
 * Stellt eine logische Variable dar. Dabei handelt es sich um einen Primimplikanten. Notwendig für Patricks Method.
 */
public class LogicalVariable {
    private final HashSet<HashSet<Integer>> vars = new HashSet<>();

    public LogicalVariable(HashSet<HashSet<Integer>> vars) {
        this.vars.addAll(vars);
    }

    public LogicalVariable(LogicalVariable vars, LogicalVariable vars2) {
        this.vars.addAll(vars.getVars());
        this.vars.addAll(vars2.getVars());
    }

    public HashSet<HashSet<Integer>> getVars() {
        return vars;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (HashSet<Integer> var : vars) {
            builder.append(var);
        }

        return builder.toString();
    }

    /**
     * Überprüft, ob die gegebene Instanz eine Teilmenge von den Variablen dieser Instanz enthält.
     * @param logicalVariable Die möglich Teilinstanz
     * @return Ob es sich um eine Teilmenge handelt
     */
    public boolean isSubVariable(LogicalVariable logicalVariable) {


        return !logicalVariable.equals(this) && this.getVars().containsAll(logicalVariable.getVars());
    }

    @Override
    public int hashCode() {
        return Objects.hash(vars);
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(LogicalVariable.class)) {
            return false;
        }

        LogicalVariable var2 = (LogicalVariable) obj;

        return this.getVars().size() == var2.getVars().size() && var2.getVars().containsAll(this.vars);
    }
}
