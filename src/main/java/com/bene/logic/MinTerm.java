package com.bene.logic;

import java.util.HashSet;

/**
 * Stellt einen Minterm da. Eine Instanz der Klasse speichert die Dezimalzahlen, ob sie bereits genutzt wurde und den Binärstring.
 */
public class MinTerm {
    private final String bin;
    private boolean used;
    private final HashSet<Integer> decimals;

    public String getBin() {
        return bin;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public MinTerm(String bin, HashSet<Integer> decimals, boolean used) {
        this.bin = bin;
        this.used = used;
        this.decimals = decimals;
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(MinTerm.class)) {
            return false;
        }

        MinTerm term = (MinTerm) obj;
        return this.bin.equals(term.getBin());
    }

    public HashSet<Integer> getDecimals() {
        return decimals;
    }

    @Override
    public String toString() {
        return "com.bene.logic.MinTerm{" +
                "bin='" + bin + '\'' +
                ", used=" + used +
                ", decimals=" + decimals +
                '}';
    }
}
