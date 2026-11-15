package ma.esith.stockims;

import ma.esith.stockims.model.Article;
import ma.esith.stockims.model.Entree;
import ma.esith.stockims.model.MouvementStock;
import ma.esith.stockims.model.Sortie;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class StockimsApplication {

	public static void main(String[] args) {

		SpringApplication.run(StockimsApplication.class, args);
		Article article = new Article("TIS-001", "Rouleau de coton");
		LocalDate date = LocalDate.now();
		List<MouvementStock> mouvements = List.of(
				new Entree(article, 20, date),
				new Sortie(article, 7, date));
		int stock = 0;
		for (MouvementStock m : mouvements) {
			stock += m.quantiteSignee();
		}
		System.out.println(stock); // 13
	}

}
