package com.bene.ui;

import com.bene.logic.LatexView;
import com.bene.logic.MinTerm;
import com.bene.logic.QuineMcCluskey;
import com.bene.logic.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Der Controller für das JavaFX Panel. Reagiert auf Ereignisse in der Benutzeroberfläche und führt Änderungen daran durch.
 */
public class Controller {

    public TextArea input;
    public TabPane tabs;
    public VBox vbox;
    public LatexView latex = new LatexView();

    /**
     * Wird ausgeführt, wenn der Berechnenknopf gedrückt wird. Startet das QuineMcCluskey-Verfahren mit den Werten
     * in der TextArea.
     */
    public void buttonClicked() {
        if (!input.getText().equals("0")) {
            QuineMcCluskey qmC = new QuineMcCluskey();
            int[] array;

            try {
                array = Stream.of(input.getText().split(",")).mapToInt(Integer::parseInt).toArray();
            } catch (NumberFormatException e) {
                return;
            }

            String solution = qmC.start(array, this);
            solution = solution.replace(Utils.logicalOr, "\\rotatebox[origin=c]{180}{\\land}");

            System.out.println(solution);
            latex.setFormula(solution);
            latex.setVisible(true);


        } else {
            latex.setFormula("Y = 0");
            latex.setSize(20);
            latex.setVisible(true);
        }

        vbox.getChildren().add(latex);
    }

    /**
     * Fügt die Eingabe-Wertetabelle hinzu.
     *
     * @param input Die nach Einsen sortierte Eingabe
     */
    public void setInputTab(ArrayList<ArrayList<MinTerm>> input) {
        Tab tab = getTab(input.stream().distinct().flatMap(Collection::stream).collect(Collectors.toList()), "Eingabe");
        tabs.getTabs().clear();
        tabs.getTabs().add(tab);
    }

    /**
     * Fügt eine Wertetabelle für einen Durchgang hinzu.
     *
     * @param values Die Werte
     * @param name   Name des Tabs
     */
    public void addTab(ArrayList<ArrayList<MinTerm>> values, String name) {
        Tab tab = getTab(values.stream().distinct().flatMap(Collection::stream).collect(Collectors.toList()), name);
        tabs.getTabs().add(tab);
    }

    /**
     * Stellt eine Wertetabelle für eine ArrayList dar.
     *
     * @param values Die Werte
     * @param name   Name des Tabs
     */
    public void addSingleTab(ArrayList<MinTerm> values, String name) {
        Tab tab = getTab(values, name);
        tabs.getTabs().add(tab);
    }

    /**
     * Erstellt eine Instanz der Klasse Tab.
     *
     * @param input Die Werte
     * @param name  Der Name des Tabs
     * @return Die daraus resultierende Instanz der Tabklasse
     */
    private Tab getTab(List<MinTerm> input, String name) {
        Tab tab = new Tab(name, new Label(name));

        TableView<ObservableList<String>> table = new TableView<>();

        for (int i = input.get(0).getBin().length() - 1; i >= 0; i--) {
            TableColumn<ObservableList<String>, String> c = new TableColumn<>("x" + Utils.subscript("" + i));
            final int j = i;
            c.setCellValueFactory(param ->
                    new ReadOnlyObjectWrapper<>(param.getValue().get(j))
            );
            table.getColumns().add(c);
        }

        ArrayList<MinTerm> minTerms = new ArrayList<>(input);

        for (MinTerm minTerm : minTerms) {
            List<String> string = Arrays.asList(minTerm.getBin().split(""));
            table.getItems().add(FXCollections.observableArrayList(string));
        }

        tab.setContent(table);
        return tab;
    }

}
