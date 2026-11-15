package ma.esith.stockims.web;

import ma.esith.stockims.model.Article;
import ma.esith.stockims.service.ArticleService;
import ma.esith.stockims.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller @RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService service;
    private final StockService stockService;
    @GetMapping
    public String liste(Model model) {
        List<Article> articles = service.lister();
        // Le stock de chaque article reste calculé à partir des mouvements
        Map<Long, Long> stocks = new HashMap<>();
        for (Article a : articles) {
            stocks.put(a.getId(), stockService.calculerStock(a.getId()));
        }
        model.addAttribute("articles", articles);
        model.addAttribute("stocks", stocks);
        return "articles/liste";
    }
    @GetMapping("/nouveau")
    public String formulaire(Model model) {
        model.addAttribute("article", new ArticleForm());
        return "articles/formulaire";
    }
    @PostMapping
    public String ajouter(@Valid @ModelAttribute("article") ArticleForm form,
                          BindingResult erreurs,
                          RedirectAttributes redirection) {
        if (erreurs.hasErrors()) return "articles/formulaire";
        if (service.referenceExiste(form.getReference())) {
            erreurs.rejectValue("reference", "doublon",
                    "Cette référence existe déjà.");
            return "articles/formulaire";
        }
        try {
            service.creer(form.getReference(), form.getDesignation());
        } catch (DataIntegrityViolationException e) {
            erreurs.rejectValue("reference", "doublon",
                    "Cette référence existe déjà.");
            return "articles/formulaire";
        }
        redirection.addFlashAttribute("succes",
                "L'article « " + form.getReference().strip() + " » a été ajouté.");
        return "redirect:/articles";
    }
}
