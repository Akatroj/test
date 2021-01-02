package agh.cs.lab.AnimalSimulator.GUI.FXMLControllers.Map.Statistics;

import agh.cs.lab.AnimalSimulator.Logic.Animal;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;

import java.util.Optional;

public class PinnedAnimalStatisticsController {

    private static final int MAX_DESCENDATS = 10000;

    @FXML
    private FlowPane root;
    @FXML
    private Label topText;
    @FXML
    private Label name;
    @FXML
    private Label genome;
    @FXML
    private Label childrenCount;
    @FXML
    private Label descendantsCount;
    @FXML
    private Label status;


    private Animal observedAnimal;
    private boolean isDead;
    private boolean isSet;
    private boolean showDescendants;

    public void initialize() {
        isSet = false;
        isDead = true;
        showDescendants = false;
        setVisibility(false);
        topText.setVisible(true);
    }

    public Animal getObservedAnimal() {
        return observedAnimal;
    }

    public void setObservedAnimal(Animal observedAnimal) {
        Optional<String> dialogResult = askForName();
        if (dialogResult.isPresent()) {
            if (!isSet) {
                setVisibility(true);
                root.getChildren().remove(topText);
                isSet = true;
            }
            this.observedAnimal = observedAnimal;
            name.setText(dialogResult.get());
            genome.setText(observedAnimal.getGenes().toString());
            isDead = false;
            showDescendants = true;
            status.setText("Żyje!");
            updateStats();
        }
    }

    public boolean isSet() {
        return isSet;
    }

    public void updateStats() {
        if (isSet) {
            if (showDescendants) {
                long descendants = observedAnimal.getDescendantCount();
                descendantsCount.setText(String.format("%d", descendants));
                if (descendants > MAX_DESCENDATS) {
                    showDescendants = false;
                    descendantsCount.setText("Bardzo dużo");
                }
            }
            if (!isDead) {
                childrenCount.setText(String.format("%d", observedAnimal.getChildCount()));
                if (observedAnimal.isDead()) {
                    status.setText("Zwierze zmarło dnia " + observedAnimal.getDeathDate());
                    isDead = true;
                }
            }
        }
    }

    private Optional<String> askForName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Kliknąłeś zwierzaka!");
        dialog.setHeaderText("");
        dialog.setContentText("Podaj nazwe dla wybranego zwierzaka:");
        return dialog.showAndWait();
    }


    private void setVisibility(boolean b) {
        for (Node node : root.getChildren()) {
            node.setVisible(b);
        }
    }
}
