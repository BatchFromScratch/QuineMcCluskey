package com.bene.logic;

import com.bene.ui.Controller;

import java.util.*;
import java.util.stream.Collectors;

public class QuineMcCluskey {
    private final ArrayList<MinTerm> unique = new ArrayList<>();
    private int numberOfInputs = 0;

    private Controller controller;

    /**
     * Wandelt einen Array von Dezimalzahlen in einen ArrayList von MinTermen um.
     * @param array Der Dezimalarray
     * @return Die ArrayList aus MinTermen
     */
    private ArrayList<MinTerm> decToBin(int[] array) {
        ArrayList<MinTerm> bin = new ArrayList<>();
        for (int zahl : array) {
            HashSet<Integer> decimals = new HashSet<>();
            decimals.add(zahl);


            MinTerm term = new MinTerm(Utils.leftPad("" + Integer.toBinaryString(zahl), numberOfInputs, '0'), decimals, false);
            if (!bin.contains(term)) {
                bin.add(term);
            }
        }
        return bin;
    }

    /**
     * Sortierung der MinTerme nach Nullen.
     * @param binArray Der zu sortierende Array aus MinTermen
     * @return Eine ArrayList, die nach Einsen getrennt weitere ArrayListen enthält. In der Arraylist bei Index 0 sind alle Terme enthalten,
     *  die keine 1 enthalten. Bei Index 1 alle die eine 1 enthalten usw.
     */
    private ArrayList<ArrayList<MinTerm>> sortByOnes(ArrayList<MinTerm> binArray) {
        ArrayList<ArrayList<MinTerm>> arrayListBin = new ArrayList<>();
        for (int i = 0; i <= numberOfInputs; i++) {
            ArrayList<MinTerm> arrayList = new ArrayList<>();
            arrayListBin.add(arrayList);
        }

        for (MinTerm term : binArray) {
            int ones = 0;
            for (int i = 0; i < term.getBin().length(); i++) {
                if (term.getBin().charAt(i) == '1') {
                    ones++;
                }
            }
            arrayListBin.get(ones).add(new MinTerm(term.getBin(), term.getDecimals(), false));
        }

        return arrayListBin;
    }

    /**
     * Funktion zum Vergleich und der Kombination von Mintermen. Stellt einen Durchlauf dar
     * @param sortedArray Nimmt die nach Einsen sortierte ArrayList auf.
     * @return Einen Sortierungsdurchlauf
     */
    private ArrayList<ArrayList<MinTerm>> compare(ArrayList<ArrayList<MinTerm>> sortedArray) {

        ArrayList<ArrayList<MinTerm>> ret = new ArrayList<>();

        for (int i = 1; i < sortedArray.size(); i++) {
            ArrayList<MinTerm> arrayList = new ArrayList<>();
            ret.add(arrayList);

            for (MinTerm bin : sortedArray.get(i - 1)) {
                f:
                for (MinTerm bin2 : sortedArray.get(i)) {
                    int pos = -1;
                    for (int x = 0; x < bin2.getBin().length(); x++) {

                        if (bin2.getBin().charAt(x) != bin.getBin().charAt(x)) {

                            if (bin2.getBin().charAt(x) == '1' && bin.getBin().charAt(x) == '0') {
                                pos = x;
                            } else {
                                continue f;
                            }
                        }
                    }

                    if (pos != -1) {
                        StringBuilder builder = new StringBuilder(bin2.getBin());
                        builder.setCharAt(pos, '_');

                        HashSet<Integer> decimals = new HashSet<>(bin2.getDecimals());
                        decimals.addAll(bin.getDecimals());

                        MinTerm term = new MinTerm(builder.toString(), decimals, false);

                        bin.setUsed(true);
                        bin2.setUsed(true);

                        if (!arrayList.contains(term)) {
                            arrayList.add(term);
                        }
                    }
                }
            }
        }

        ArrayList<MinTerm> arrayList = new ArrayList<>();
        ret.add(arrayList);

        for (ArrayList<MinTerm> minTerms : sortedArray) {
            List<MinTerm> arr = minTerms.stream().filter(b -> !b.isUsed()).collect(Collectors.toList());
            arr.forEach(b -> b.setUsed(false));
            unique.addAll(arr);
        }


        return ret;
    }

    /**
     * Wiederholt die compare() Funktion, bis sich die Rückgabe nicht mehr ändert.
     * @param binArray Eine nach Einsen sortierte ArrayList
     * @return Die Primimplikanten
     */
    private ArrayList<MinTerm> findAllPrimTerms(ArrayList<ArrayList<MinTerm>> binArray) {

        boolean isEmpty;

        int i = 1;

        do {
            isEmpty = true;
            binArray = compare(binArray);


            for (ArrayList<MinTerm> arr : binArray) {
                if (!arr.isEmpty()) {
                    isEmpty = false;
                    controller.addTab(binArray, i + ". Durchlauf");
                    i++;
                    break;
                }
            }


        } while (!isEmpty);

        return unique;
    }

    /**
     * Aus den Primimplikanten werden die Essentiellen herausgesucht
     * @param minTerms Alle Primimplikanten
     * @return Alle essentiellen Primimplikanten
     */
    private ArrayList<MinTerm> findEssentialPrimeImplicants(ArrayList<MinTerm> minTerms) {
        ArrayList<MinTerm> essentialMinTerms = new ArrayList<>();

        for (MinTerm minTerm : minTerms) {
            for (int decimal : minTerm.getDecimals()) {
                List<MinTerm> decCount = minTerms.stream().filter(term -> term.getDecimals().contains(decimal)).collect(Collectors.toList());
                if (decCount.size() == 1) {
                    essentialMinTerms.add(minTerm);
                    break;
                }
            }
        }
        return essentialMinTerms;
    }

    /**
     * Setzt Patricks Method um. Dafür werden alle nicht essentiellen Primimplikanten zu Instanzen der Klasse LogicalVariable umgewandelt.
     * Danach werden die Variablen über Terme miteinander multipliziert, danach ausmultipliziert und zum Schluss mit dem Absorbtionsverfahren reduziert.
     * @param minTerms Die Primimplikanten
     * @return Gibt alle zu verwendenden Primimplikanten zurück
     */
    private ArrayList<MinTerm> reducePrimeImplicantsToMinimum(ArrayList<MinTerm> minTerms) {
        ArrayList<MinTerm> essentialMinTerms = findEssentialPrimeImplicants(minTerms);

        Set<Integer> coveredDecimals = essentialMinTerms.stream().distinct().flatMap(term -> term.getDecimals().stream()).collect(Collectors.toSet());

        List<MinTerm> remainingMinTerms = new ArrayList<>(minTerms);
        remainingMinTerms.removeAll(essentialMinTerms);
        remainingMinTerms = remainingMinTerms.stream().filter(term -> !coveredDecimals.containsAll(term.getDecimals())).collect(Collectors.toList());

        HashSet<LogicalTerm> singleTerms = new HashSet<>();

        for (MinTerm term1 : remainingMinTerms) {
            HashSet<Integer> decimals1 = term1.getDecimals();
            for (MinTerm term2 : remainingMinTerms) {

                if (term1.equals(term2)) {
                    continue;
                }

                // Primimplianten werden zu logischen Variablen umgewandelt
                HashSet<Integer> decimals2 = term2.getDecimals();
                for (int decimal1 : decimals1) {
                    if (decimals2.contains(decimal1)) {
                        HashSet<HashSet<Integer>> h1 = new HashSet<>();
                        h1.add(decimals1);
                        LogicalVariable v1 = new LogicalVariable(h1);

                        HashSet<HashSet<Integer>> h2 = new HashSet<>();
                        h2.add(decimals2);
                        LogicalVariable v2 = new LogicalVariable(h2);

                        HashSet<LogicalVariable> vSet = new HashSet<>();
                        vSet.add(v1);
                        vSet.add(v2);

                        singleTerms.add(new LogicalTerm(vSet));

                    }
                }
            }
        }


        ArrayList<MinTerm> retTerm = new ArrayList<>(essentialMinTerms);

        if (!remainingMinTerms.isEmpty()) {
            // Alle Terme werden hier über simplifyTerm zu einem einzigen kombiniert
            LogicalTerm simplifiedTerm = simplifyTerm(singleTerms);


            Iterator<LogicalVariable> iterator = simplifiedTerm.getVars().iterator();

            LogicalVariable smallestTerm = iterator.next();


            // Der kleinste Term wird ausgesucht
            while (iterator.hasNext()) {
                LogicalVariable temp = iterator.next();


                if (smallestTerm.getVars().size() > temp.getVars().size()) {
                    smallestTerm = temp;
                }
            }

            // Der kleinste Term wird wieder zu einem Minterm umgewandelt und zurückgegeben
            for (HashSet<Integer> decimal : smallestTerm.getVars()) {
                Set<MinTerm> simplifiedTerms = remainingMinTerms.stream()
                        .filter(b -> b.getDecimals().equals(decimal)).collect(Collectors.toSet());
                retTerm.addAll(simplifiedTerms);
            }

        }

        return retTerm;
    }

    /**
     * Funktion zur Ausmultiplizierung eines großen Terms
     * @param terms Alle Terme
     * @return Ein einziger kombinierter Term
     */
    private LogicalTerm simplifyTerm(HashSet<LogicalTerm> terms) {

        Iterator<LogicalTerm> iterator = terms.iterator();

        LogicalTerm firstTerm = iterator.next();

        while (iterator.hasNext()) {
            firstTerm = firstTerm.multiply(iterator.next());
        }

        return firstTerm.absorb();
    }

    /**
     * Wandelt die notwendigen Primimplikanten in eine finale Aussage um.
     * @param minTerms Die Primimplikanten
     * @return Die finale Aussage
     */
    private String findFinalEquation(ArrayList<MinTerm> minTerms) {

        StringBuilder builder = new StringBuilder();

        for (String bin : minTerms.stream().map(MinTerm::getBin).collect(Collectors.toList())) {

            builder.append(" ").append(Utils.logicalOr).append(" ");

            for (int i = 0; i < bin.length(); i++) {

                int y = bin.length() - 1 - i;

                String var = "X";

                switch (bin.charAt(i)) {
                    case '1':
                        var += y;
                        break;
                    case '0':
                        var = "\\overline " + var + "\\overline " + y + " ";
                        break;
                    case '_':
                        continue;
                }
                builder.append(var);
            }

        }

        builder.delete(0, 3);

        String ret = builder.toString();

        if(ret.isEmpty()){
            return "Y = 1";
        }
        return Utils.subscript("Y = " + ret);
    }

    /**
     * Startet das QuineMcCluskey-Verfahren
     * @param array Array aus Dezimalzahlen
     * @param controller Controller für das JavaFX-Panel
     * @return Eine logische Aussage
     */
    public String start(int[] array, Controller controller) {
        this.controller = controller;

        Arrays.sort(array);
        numberOfInputs = log(array[array.length - 1], 2) + 1;
        ArrayList<MinTerm> binArray = decToBin(array);

        ArrayList<ArrayList<MinTerm>> sortedByOnes = sortByOnes(binArray);

        controller.setInputTab(sortedByOnes);
        ArrayList<MinTerm> uniques = findAllPrimTerms(sortedByOnes);

        controller.addSingleTab(uniques, "alle Primimplikanten");

        ArrayList<MinTerm> essentialPrimeImplicants = findEssentialPrimeImplicants(uniques);
        controller.addSingleTab(essentialPrimeImplicants, "essentielle Primimplikanten");

        ArrayList<MinTerm> finalTerms = reducePrimeImplicantsToMinimum(uniques);

        controller.addSingleTab(finalTerms, "Lösung");

        return findFinalEquation(finalTerms);
    }

    @SuppressWarnings("SameParameterValue")
    private int log(int x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }
}
