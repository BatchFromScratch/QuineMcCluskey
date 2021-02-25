package com.bene.logic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

/**
 * Beschreibt eine logische Aussage. Mit dieser Klasse können zwei Variablen miteinander multipliziert werden.
 */
public class LogicalTerm {
    // Enthält kombinierte Primimplikanten.
    private final HashSet<LogicalVariable> vars;

    public LogicalTerm(HashSet<LogicalVariable> vars) {
        this.vars = vars;
    }

    public HashSet<LogicalVariable> getVars() {
        return vars;
    }

    /**
     * Multipliziert einen gegebenen Term mit dem der Klasse.
     * @param term Der Faktor mit dem multipliziert wird.
     * @return Das Produkt der Terme
     */
    public LogicalTerm multiply(LogicalTerm term) {

        HashSet<LogicalVariable> vars2 = term.getVars();

        HashSet<LogicalVariable> retVars = new HashSet<>();

        for (LogicalVariable var1 : this.vars) {
            for (LogicalVariable var2 : vars2) {
                retVars.add(new LogicalVariable(var1, var2));
            }
        }

        return new LogicalTerm(retVars);
    }

    /**
     * Wendet das Absorptionsverfahren auf den Term der Klasse an.
     * @return Der verringerte Term
     */
    public LogicalTerm absorb() {
        LogicalTerm ret = new LogicalTerm(vars);

        Iterator<LogicalVariable> it = ret.vars.iterator();

        while (it.hasNext()) {
            LogicalVariable var = it.next();
            if (vars.stream().anyMatch(var::isSubVariable)) {
                it.remove();
            }
        }

        return ret;
    }

    /**
     * Überprüft ob zwei Terme gleich sind. Wird verwendet, um Duplikate zu eliminieren.
     * @param obj Das abzugleichende Objekt
     * @return Ob die Objekte gleich sind
     */
    @Override
    public boolean equals(Object obj) {

        if (!obj.getClass().equals(LogicalTerm.class)) {
            return false;
        }


        LogicalTerm term = (LogicalTerm) obj;

        for (LogicalVariable var : term.vars) {
            if (!this.vars.contains(var)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Vergleich wie bei equals() notwendig für HashSets.
     * @return Hashset der Variablen
     */
    @Override
    public int hashCode() {
        return Objects.hash(vars);
    }

    /**
     * Gibt die Instanz als String zurück
     * @return String der Instanz
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (LogicalVariable var : vars) {
            builder.append(" + ");
            builder.append(var);
        }
        builder.delete(0, 3);
        return builder.toString();
    }
}
