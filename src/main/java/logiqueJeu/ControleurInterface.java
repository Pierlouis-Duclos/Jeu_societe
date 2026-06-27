package logiqueJeu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;


import java.awt.*;
import java.util.ArrayList;

public class ControleurInterface {
    private Pane paneHovered;

    private LogiqueJeu logiqueJeu = new LogiqueJeu();

    private boolean partieEnCours = false;

    @FXML
    private AnchorPane coteBleu;
    @FXML
    private AnchorPane coteRouge;

    @FXML
    private GridPane grilleJeu;

    @FXML
    private Pane pane0;
    @FXML
    private Pane pane1;
    @FXML
    private Pane pane2;
    @FXML
    private Pane pane3;
    @FXML
    private Pane pane4;
    @FXML
    private Pane pane5;
    @FXML
    private Pane pane6;
    @FXML
    private Pane pane7;
    @FXML
    private Pane pane8;
    @FXML
    private Pane pane9;
    @FXML
    private Pane pane10;
    @FXML
    private Pane pane11;
    @FXML
    private Pane pane12;
    @FXML
    private Pane pane13;
    @FXML
    private Pane pane14;
    @FXML
    private Pane pane15;

    @FXML
    void debuterPartie(ActionEvent event) {
        if (!partieEnCours) {
            resetGrille();
            Circle n1 = new Circle(35, Color.GRAY);
            Circle n2 = new Circle(35, Color.GRAY);
            Circle n3 = new Circle(35, Color.GRAY);
            Circle n4 = new Circle(35, Color.GRAY);
            /*n1.setId("none");
            n2.setId("none");
            n3.setId("none");
            n4.setId("none");*/
            double wid = pane0.getWidth() / 2;
            double hei = pane0.getHeight() - 70;
            n1.setLayoutX(wid);
            n1.setLayoutY(hei);
            n2.setLayoutX(wid);
            n2.setLayoutY(hei);
            n3.setLayoutX(wid);
            n3.setLayoutY(hei);
            n4.setLayoutX(wid);
            n4.setLayoutY(hei);
            logiqueJeu = new LogiqueJeu();
            partieEnCours = true;
            coteBleu.setStyle("-fx-background-color: lightblue");
            pane0.getChildren().add(n1);
            pane6.getChildren().add(n2);
            pane9.getChildren().add(n3);
            pane15.getChildren().add(n4);
        }
    }

    @FXML
    void mouseHoverGrid(MouseEvent event) {
        Pane paneHovered = ((Pane) event.getTarget());
        Integer x = GridPane.getColumnIndex(paneHovered);
        Integer y = GridPane.getRowIndex(paneHovered);
        x = (x == null ? 0 : x);
        y = (y == null ? 0 : y);
        if (logiqueJeu.coupPermis(new Point(x, y))) {
            paneHovered.setStyle("-fx-background-color: #94CDD4");
        }
        this.paneHovered = paneHovered;
    }

    @FXML
    void mouseUnhoveredGrid(MouseEvent event) {
        Pane paneUnhovered = ((Pane) event.getTarget());
        Integer x = GridPane.getColumnIndex(paneUnhovered);
        Integer y = GridPane.getRowIndex(paneUnhovered);
        x = (x == null ? 0 : x);
        y = (y == null ? 0 : y);
        if (logiqueJeu.coupPermis(new Point(x, y))) {
            paneUnhovered.setStyle("-fx-background-color: white");
        }
    }

    @FXML
    void mouseReleasedPane(MouseEvent event) {
        if (partieEnCours) {
            paneHovered.setStyle("-fx-background-color: lightgreen");
            Integer x = GridPane.getColumnIndex(paneHovered);
            Integer y = GridPane.getRowIndex(paneHovered);
            x = (x == null ? 0 : x);
            y = (y == null ? 0 : y);

            Point coordonnees = new Point(x, y);

            if (!logiqueJeu.tourEnCours()) {
                logiqueJeu.selectionnerPile(coordonnees);
                paneHovered.getChildren().removeAll(paneHovered.getChildren());
            } else if (logiqueJeu.coupPermis(coordonnees)) {
                int dessousPile = logiqueJeu.getDessousPileADeplacer();
                boolean tourTermine = logiqueJeu.jouerCoup(coordonnees);
                Circle galet;
                switch (dessousPile) {
                    case -1:
                        galet = new Circle(35, Color.GRAY);
                        galet.setId("none");
                        break;
                    case 0:
                        galet = new Circle(35, Color.DODGERBLUE);
                        galet.setId("blue");
                        break;
                    case 1:
                        galet = new Circle(35, Color.RED);
                        galet.setId("red");
                        break;
                    default:
                        galet = new Circle(35, Color.MAGENTA);
                        galet.setId("erreur");
                        break;
                }
                double wid = paneHovered.getWidth() / 2;
                double hei = paneHovered.getHeight() - 70 + 5 * paneHovered.getChildren().size();
                galet.setLayoutX(wid);
                galet.setLayoutY(hei);
                galet.setStyle("-fx-stroke: black");
                paneHovered.getChildren().add(galet);

                if (tourTermine) { //si le tour est terminé
                    switch (logiqueJeu.getJoueurCourant()) {
                        case 0: //bleu
                            coteRouge.setStyle("-fx-background-color: white");
                            coteBleu.setStyle("-fx-background-color: lightblue");
                            break;
                        case 1: //rouge
                            coteBleu.setStyle("-fx-background-color: white");
                            coteRouge.setStyle("-fx-background-color: orange");
                            break;
                    }
                    resetPaneColors();
                    if (logiqueJeu.estTerminee()) {
                        partieEnCours = false;
                        String texte;
                        switch (logiqueJeu.getGagnant()) {
                            case 0: //bleu
                                texte = "Victoire du joueur bleu!";
                                break;
                            case 1: //rouge
                                texte = "Victoire du joueur rouge!";
                                break;
                            default:
                                texte = "Partie nulle";
                                break;
                        }
                        Alert partieTerminee = new Alert(Alert.AlertType.INFORMATION, texte, ButtonType.OK);
                        partieTerminee.setTitle("Partie terminée");
                        partieTerminee.showAndWait();
                    }
                }


            }
        }
    }

    void resetPaneColors() {
        pane0.setStyle("-fx-background-color: white");
        pane1.setStyle("-fx-background-color: white");
        pane2.setStyle("-fx-background-color: white");
        pane3.setStyle("-fx-background-color: white");
        pane4.setStyle("-fx-background-color: white");
        pane5.setStyle("-fx-background-color: white");
        pane6.setStyle("-fx-background-color: white");
        pane7.setStyle("-fx-background-color: white");
        pane8.setStyle("-fx-background-color: white");
        pane9.setStyle("-fx-background-color: white");
        pane10.setStyle("-fx-background-color: white");
        pane11.setStyle("-fx-background-color: white");
        pane12.setStyle("-fx-background-color: white");
        pane13.setStyle("-fx-background-color: white");
        pane14.setStyle("-fx-background-color: white");
        pane15.setStyle("-fx-background-color: white");
        paneHovered.setStyle("-fx-background-color: #94CDD4");
    }
    void resetGrille() {
        pane0.getChildren().removeAll(pane0.getChildren());
        pane1.getChildren().removeAll(pane1.getChildren());
        pane2.getChildren().removeAll(pane2.getChildren());
        pane3.getChildren().removeAll(pane3.getChildren());
        pane4.getChildren().removeAll(pane4.getChildren());
        pane5.getChildren().removeAll(pane5.getChildren());
        pane6.getChildren().removeAll(pane6.getChildren());
        pane7.getChildren().removeAll(pane7.getChildren());
        pane8.getChildren().removeAll(pane8.getChildren());
        pane9.getChildren().removeAll(pane9.getChildren());
        pane10.getChildren().removeAll(pane10.getChildren());
        pane11.getChildren().removeAll(pane11.getChildren());
        pane12.getChildren().removeAll(pane12.getChildren());
        pane13.getChildren().removeAll(pane13.getChildren());
        pane14.getChildren().removeAll(pane14.getChildren());
        pane15.getChildren().removeAll(pane15.getChildren());
    }
}
