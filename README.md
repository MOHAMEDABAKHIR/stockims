# StockIMS

Application de gestion de stock réalisée avec Spring Boot 4, Spring Data JPA, MySQL et Thymeleaf.
Elle permet de gérer des articles et d'enregistrer leurs entrées et leurs sorties, le stock étant
toujours calculé à partir des mouvements.

## Lancer le projet

1. Démarrer MySQL (port 3306).
2. Vérifier l'utilisateur et le mot de passe dans `src/main/resources/application.properties`.
   La base `stockims_db` est créée automatiquement si elle n'existe pas.
3. Lancer `StockimsApplication` (IntelliJ) ou `./mvnw spring-boot:run`.
4. Ouvrir <http://localhost:8080>.

Hibernate crée les trois tables au démarrage (`spring.jpa.hibernate.ddl-auto=update`).

## Architecture (conforme au guide)

| Couche | Classes |
|---|---|
| Modèle métier | `Article`, `MouvementStock` (classe abstraite), `Entree`, `Sortie` |
| Repositories (interfaces Spring Data) | `ArticleRepository`, `EntreeRepository`, `SortieRepository` |
| Services | `ArticleService`, `StockService` |
| Contrôleurs | `AccueilController`, `ArticleController`, `StockController` |
| Formulaires | `ArticleForm`, `MouvementForm` |

- **Encapsulation** : attributs privés, constructeurs protégés pour JPA, règles métier vérifiées dans les constructeurs.
- **Classe abstraite** : `MouvementStock` porte l'article, la quantité et la date ; `Entree` et `Sortie` définissent `quantiteSignee()` (+ ou −).
- **Interfaces** : les repositories héritent de `JpaRepository`.

## Base de données

| Table | Colonnes |
|---|---|
| `article` | `id`, `reference` (unique, 20 car. max.), `designation` (80 car. max.) |
| `entree` | `id`, `article_id` (clé étrangère vers `article`), `quantite`, `date_mouvement` |
| `sortie` | `id`, `article_id` (clé étrangère vers `article`), `quantite`, `date_mouvement` |

Le stock n'est jamais stocké : `stock = somme des entrées − somme des sorties`
(0 pour un article sans mouvement).

## Fonctionnalités et validations (côté serveur)

- Ajouter un article et consulter la liste (avec le stock de chaque article).
- Enregistrer une entrée ou une sortie, voir le stock disponible et l'historique des mouvements.
- Refus avec un message clair :
  - référence ou désignation vide ;
  - référence déjà utilisée (vérifiée avant l'enregistrement, puis protégée par la contrainte `unique` de la base) ;
  - quantité vide, nulle, négative, décimale ou non numérique (`@NotNull`, `@Min(1)`, type `Integer`, messages dans `messages.properties`) ;
  - sortie supérieure au stock disponible : « Stock insuffisant : N disponible(s). » (article verrouillé pendant le calcul).
- Les formulaires portent `novalidate` : le navigateur ne bloque pas l'envoi, c'est toujours le serveur qui valide.
  Tout fonctionne donc avec JavaScript désactivé.

## Personnalisation de l'interface

**Identité** : l'application gère du tissu, donc l'interface s'inspire de l'atelier de couture :
bleu indigo du denim, jaune du ruban de couturière, fond gris clair. Le ruban gradué sous l'en-tête
est l'élément signature.

**HTML**
- Un fragment Thymeleaf (`fragments/layout.html`) fournit l'en-tête, la navigation (la page courante est marquée) et le pied de page à toutes les pages.
- Titres hiérarchisés, libellés liés aux champs (`label for`), tableaux avec `scope`, messages d'erreur annoncés (`role="alert"`).
- Page 404 personnalisée (`templates/error/404.html`).
- Les liaisons Thymeleaf d'origine (`th:object`, `th:field`, `th:errors`, `th:action`, `th:formaction`) sont conservées.

**CSS** (`static/css/style.css`)
- Variables de couleur et de police en tête de fichier ; titres en police à empattement carré (Rockwell, sinon Georgia), texte en police système.
- Boutons : indigo (action principale), vert (entrée), rouge brique (sortie).
- Tableaux lisibles, quantités alignées à droite ; pastille de stock verte, rouge quand le stock est nul.
- Messages de réussite (vert) et d'erreur (rouge), champs invalides surlignés.
- Responsive : sous 640 px, chaque ligne du tableau des articles devient une fiche ; l'historique passe sur une colonne.
- Focus clavier visible, animations réduites si le système le demande.

**JavaScript** (`static/js/app.js`, sans bibliothèque)
1. Filtre de recherche instantané dans la liste des articles (insensible à la casse et aux accents, avec compteur de résultats). Il n'apparaît que si JavaScript est actif.
2. Confirmation avant d'enregistrer une sortie, qui rappelle la quantité et l'article.
3. Croix pour fermer le message de réussite.

## Tests

`StockModelTest` vérifie le calcul du stock et les refus du modèle sans base de données.
`StockimsApplicationTests` (chargement du contexte) nécessite que MySQL soit démarré.
