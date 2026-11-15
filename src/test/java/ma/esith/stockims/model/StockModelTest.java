package ma.esith.stockims.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Tests du modèle métier : aucune base de données nécessaire. */
class StockModelTest {

    private final Article article = new Article("TIS-001", "Rouleau de coton");

    @Test
    void stockEgalEntreesMoinsSorties() {
        LocalDate jour = LocalDate.now();
        int stock = 0;
        for (MouvementStock m : new MouvementStock[]{
                new Entree(article, 20, jour), new Sortie(article, 7, jour)}) {
            stock += m.quantiteSignee();
        }
        assertEquals(13, stock);
    }

    @Test
    void quantiteNulleOuNegativeRefusee() {
        LocalDate jour = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () -> new Entree(article, 0, jour));
        assertThrows(IllegalArgumentException.class, () -> new Sortie(article, -3, jour));
    }

    @Test
    void champsObligatoiresVidesRefuses() {
        assertThrows(IllegalArgumentException.class, () -> new Article("  ", "Coton"));
        assertThrows(IllegalArgumentException.class, () -> new Article("TIS-002", ""));
        assertThrows(IllegalArgumentException.class, () -> new Entree(null, 5, LocalDate.now()));
    }
}
