/* StockIMS - interactions côté navigateur.
   Tout fonctionne aussi sans JavaScript : les contrôles serveur restent la référence. */
(function () {
    "use strict";

    /* 1. Filtre de recherche dans la liste des articles */
    var zone = document.getElementById("zone-recherche");
    var champ = document.getElementById("recherche");
    var tableau = document.getElementById("tableau-articles");

    if (zone && champ && tableau) {
        zone.hidden = false; // la zone n'apparaît que si le script tourne

        var lignes = Array.prototype.slice.call(tableau.tBodies[0].rows);
        var compteur = document.getElementById("compteur-resultats");
        var aucun = document.getElementById("aucun-resultat");

        var normaliser = function (texte) {
            return texte.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
        };

        var filtrer = function () {
            var recherche = normaliser(champ.value.trim());
            var visibles = 0;
            lignes.forEach(function (ligne) {
                var correspond = normaliser(ligne.textContent).indexOf(recherche) !== -1;
                ligne.hidden = !correspond;
                if (correspond) { visibles++; }
            });
            compteur.textContent = visibles + " article(s) sur " + lignes.length;
            aucun.hidden = visibles !== 0;
            tableau.parentNode.hidden = visibles === 0;
        };

        champ.addEventListener("input", filtrer);
        filtrer();
    }

    /* 2. Confirmation avant d'enregistrer une sortie */
    var boutons = document.querySelectorAll("[data-confirmation]");
    Array.prototype.forEach.call(boutons, function (bouton) {
        bouton.addEventListener("click", function (evenement) {
            var saisie = bouton.form.elements["quantite"];
            var quantite = saisie ? saisie.value.trim() : "";
            // Quantité vide ou invalide : on laisse le serveur afficher son message.
            if (!/^[1-9][0-9]*$/.test(quantite)) { return; }
            var article = bouton.getAttribute("data-article") || "cet article";
            var question = "Enregistrer une sortie de " + quantite +
                " unité(s) pour « " + article + " » ?";
            if (!window.confirm(question)) { evenement.preventDefault(); }
        });
    });

    /* 3. Le message de réussite disparaît au clic sur sa croix */
    var fermer = document.querySelectorAll("[data-fermer]");
    Array.prototype.forEach.call(fermer, function (b) {
        b.hidden = false;
        b.addEventListener("click", function () { b.closest(".message").hidden = true; });
    });
})();
