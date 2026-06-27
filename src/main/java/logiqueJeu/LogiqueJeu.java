package logiqueJeu;


import java.awt.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;

public class LogiqueJeu {
    /**
     * Collection stockant l'ensemble du jeu (l'ensemble des piles de galets sur la grille 4x4)
     */
    private LinkedBlockingDeque<Integer>[][] listeJeu = new LinkedBlockingDeque[4][4]; //-1 pour galet neutre, 0 pour soi (bleu) et 1 pour aversaire (rouge)
    /**
     * La liste des déplacements effectuées ce tour ci par le joueur courant
     * (afin de s'assurer qu'il ne repasse pas par le même point plusieures fois)
     */
    private ArrayList<Point> listeDeplacements = new ArrayList<>();
    /**
     * Indique si la partie est terminée (s'il y a un gagnant ou que c'est une partie nulle)
     */
    private boolean partieTerminee = false;
    /**
     * La pile de galet sur laquelle le joueur a placé le sien au début de son tour et qu'il
     * doit maintenant "vider" petit à petit
     */
    private LinkedBlockingDeque<Integer> pileADeplacer = new LinkedBlockingDeque<>();

    /**
     * garde en mémoire quel est le joueur courant, {@code 0} pour le joueur
     * <span style:color=blue>bleu</span>, {@code 1} pour le joueur
     * <span style:color=red>rouge</span> <br><br> La valeur de cette variable est modifiée par
     * les méthodes {@code debuterPartie()} (qui la remet à 0) et {@code jouerCoup(.)} (qui
     * la fait changer lors de la fin du tour d'un joueur (lorsque {@code pileADeplacer} est vide))
     */
    private Integer joueurCourant = 0;

    /**
     * permet de déterminer qui a remporté la partie
     */
    private int gagnant = -1;

    public LogiqueJeu() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                listeJeu[i][j] = new LinkedBlockingDeque<>();
            }
        }
        debuterPartie();
    }

    /**
     * met en place les 4 galets neutres sur le plateau.
     */
    public void debuterPartie() {
        listeJeu[0][0].push(-1);
        listeJeu[1][2].push(-1);
        listeJeu[2][1].push(-1);
        listeJeu[3][3].push(-1);
        gagnant = -1;
        joueurCourant = 0;
        partieTerminee = false;

    }

    /**
     * Sélectionne la pile sur laquelle sera placée un galet au début du tour du joueur
     * Place un galet du joueur courant sur le sommet de cette pile, défini la pile comme
     * étant la {@code pileADeplacer} puis clear la pile du jeu. Enfin, ajoute la position
     * de la pile qui vient d'être vidée à {@code listeDeplacements}
     *
     * @param coordonneesGrille les coordonnées de la cellule de la grille où se trouve la
     *                          pile sur laquelle sera placée un galet au début du tour du joueur
     */
    public void selectionnerPile(Point coordonneesGrille) {
        int x = (int) coordonneesGrille.getX();
        int y = (int) coordonneesGrille.getY();
        assert x >= 0 && x < 4;
        assert y >= 0 && y < 4;
        assert pileADeplacer.isEmpty();
        assert listeDeplacements.isEmpty();

        listeJeu[x][y].push(joueurCourant);

        pileADeplacer.addAll(listeJeu[x][y]);
        listeJeu[x][y].clear();

        listeDeplacements.add(coordonneesGrille);
    }

    /**
     * @param coordonneesGrille
     * @return {@code true} si {@code pileADeplacer} est vide (si
     * le tour du joueur courant est terminé), {@code false} sinon
     */
    public boolean jouerCoup(Point coordonneesGrille) {
        int x = (int) coordonneesGrille.getX();
        int y = (int) coordonneesGrille.getY();
        assert x >= 0 && x < 4;
        assert y >= 0 && y < 4;

        if (tourEnCours()) {
            try {
                listeJeu[x][y].push(pileADeplacer.takeLast());
                listeDeplacements.add(coordonneesGrille);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("La pile est vide, votre tour est terminé. Vous devez sélectionner uune pile avant de jouer un coup");
        }
        if (pileADeplacer.isEmpty()) {
            listeDeplacements.clear();
            joueurCourant = (joueurCourant + 1) % 2;
        }
        return pileADeplacer.isEmpty();
    }

    /**
     * Vérifie si le coup reçu en paramètre est permis (s'il ne se
     * trouve pas dans {@code listeDeplacements} et s'il se trouve
     * dans les 8 cases sur le pourtour de celle où a été joué le
     * dernier coup)
     *
     * @param coordonneesGrille les coordonnées du coup à vérifier
     * @return {@code true} si le coup est permis, {@code false} sinon
     */
    public boolean coupPermis(Point coordonneesGrille) {
        int x = (int) coordonneesGrille.getX();
        int y = (int) coordonneesGrille.getY();
        assert x >= 0 && x < 4;
        assert y >= 0 && y < 4;
        int ecartX = 0;
        int ecartY = 0;
        if (tourEnCours()) {
            Point dernierCoup = listeDeplacements.get(listeDeplacements.size() - 1);
            int dernierX = dernierCoup.x;
            int dernierY = dernierCoup.y;

            ecartX = Math.abs(x - dernierX);
            ecartY = Math.abs(y - dernierY);
        }
        return !listeDeplacements.contains(coordonneesGrille) && ecartX <= 1 && ecartY <= 1;
    }

    /**
     * Indique si un tour est en cours
     * (si la pile à déplacer n'est pas encore finie d'être déplacée)
     *
     * @return {@code true} si {@code pileADeplacer} n'est pas vide <br>
     * {@code false} sinon
     */
    public boolean tourEnCours() {
        return !pileADeplacer.isEmpty();
    }

    /**
     * Vérifie s'il y a un gagnant. <br>
     * S'il n'y en a pas, vérifie si c'est un match nul
     * (s'il ne reste aucun galet à chacun des joueurs)
     *
     * @return {@code true} si la partie est terminée (match nul ou s'il y a un gagnant)
     * <br>{@code false} sinon
     */
    public boolean estTerminee() {
        boolean a;
        boolean b;
        boolean c;
        for (int i = 0; i < 4 && !partieTerminee; i++) {
            //vérification de si la ième colonne est un tictactoe pour un des 2 joueurs
            a = Objects.equals(listeJeu[i][0].peek(), listeJeu[i][1].peek());
            b = Objects.equals(listeJeu[i][1].peek(), listeJeu[i][2].peek());
            c = Objects.equals(listeJeu[i][2].peek(), listeJeu[i][3].peek());

            if (!(a && b && c && !Objects.equals(listeJeu[i][0].peek(), -1) && !Objects.isNull(listeJeu[i][0].peek()))) {
                //vérification de si la ième ligne est un tictactoe pour un des 2 joueurs
                a = Objects.equals(listeJeu[0][i].peek(), listeJeu[1][i].peek());
                b = Objects.equals(listeJeu[1][i].peek(), listeJeu[2][i].peek());
                c = Objects.equals(listeJeu[2][i].peek(), listeJeu[3][i].peek());
            }
            if (a && b && c && !Objects.equals(listeJeu[i][i].peek(), -1) && !Objects.isNull(listeJeu[i][i].peek())) {
                partieTerminee = true;
                gagnant = listeJeu[i][i].peek();
            }
        }
        //vérification des diagonales
        if (!partieTerminee) {
            a = Objects.equals(listeJeu[0][0].peek(), listeJeu[1][1].peek());
            b = Objects.equals(listeJeu[1][1].peek(), listeJeu[2][2].peek());
            c = Objects.equals(listeJeu[2][2].peek(), listeJeu[3][3].peek());
            partieTerminee = a && b && c && !Objects.equals(listeJeu[0][0].peek(), -1) && !Objects.isNull(listeJeu[0][0].peek());
            gagnant = (partieTerminee ? listeJeu[0][0].peek() : -1);
            if (!partieTerminee) {
                a = Objects.equals(listeJeu[3][0].peek(), listeJeu[2][1].peek());
                b = Objects.equals(listeJeu[2][1].peek(), listeJeu[1][2].peek());
                c = Objects.equals(listeJeu[1][2].peek(), listeJeu[0][3].peek());
                partieTerminee = a && b && c && !Objects.equals(listeJeu[3][0].peek(), -1) && !Objects.isNull(listeJeu[3][0].peek());
                gagnant = (partieTerminee ? listeJeu[3][0].peek() : -1);
            }
        }
        return partieTerminee;
    }

    /**
     * Renvoie le joueur courant {@code -1} pour neutre
     * {@code 0} pour joueur<span style:color='blue'>bleu</span>
     * et {@code 1} pour joueur <span style:color='red'>rouge</span>
     *
     * @return le chiffre indiquant le joueur courant
     */
    public Integer getJoueurCourant() {
        return joueurCourant;
    }

    /**
     * Renvoie l'{@link Integer} représentant la couleur du galet
     * en dessous de la pile à déplacer
     *
     * @return {@code -1} pour neutre {@code 0} pour joueur
     * <span style:color='blue'>bleu</span> et {@code 1} pour
     * joueur <span style:color='red'>rouge</span>
     */
    public Integer getDessousPileADeplacer() {
        assert !pileADeplacer.isEmpty();
        return pileADeplacer.peekLast();
    }

    public int getGagnant() {
        return gagnant;
    }
}
