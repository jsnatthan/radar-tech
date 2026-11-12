package com.radartech.controller;

import com.radartech.domain.ContractType;
import com.radartech.domain.JobLocationType;
import com.radartech.domain.Seniority;
import com.radartech.dto.JobPostForm;
import com.radartech.service.JobService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UnitTwoController {
    private static final String SAVED = "unitTwoSavedJobs";
    private final JobService jobs;

    public UnitTwoController(JobService jobs) {
        this.jobs = jobs;
    }

    @GetMapping("/vagas/salvas")
    String saved(HttpSession session, Model model) {
        model.addAttribute("jobs", jobs.findSavedPublished(savedIds(session)));
        return "jobs/saved";
    }

    @PostMapping("/vagas/{id}/salvar")
    String save(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        jobs.findPublished(id);
        synchronized (session) {
            var ids = new LinkedHashSet<>(savedIds(session));
            if (ids.size() >= 100 && !ids.contains(id)) {
                redirect.addFlashAttribute("savedMessage", "Sua lista tem 100 vagas. Remova uma para guardar outra.");
                return "redirect:/vagas/salvas";
            }
            ids.add(id);
            session.setAttribute(SAVED, List.copyOf(ids));
        }
        redirect.addFlashAttribute("savedMessage", "Vaga salva. Você pode retomá-la nesta lista.");
        return "redirect:/vagas/salvas";
    }

    @PostMapping("/vagas/{id}/remover-das-salvas")
    String remove(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        synchronized (session) {
            var ids = new LinkedHashSet<>(savedIds(session));
            if (ids.remove(id)) {
                session.setAttribute(SAVED, List.copyOf(ids));
                redirect.addFlashAttribute("removedId", id);
                redirect.addFlashAttribute("savedMessage", "Vaga removida da lista.");
            }
        }
        return "redirect:/vagas/salvas";
    }

    @GetMapping("/vagas/{id}/{action:fonte|compartilhar}")
    String source(@PathVariable Long id, @PathVariable String action, Model model) {
        var job = jobs.findPublished(id);
        URI source = publicSource(job.getApplyUrl());
        model.addAttribute("job", job);
        model.addAttribute("sourceUrl", source == null ? null : source.toASCIIString());
        model.addAttribute("sourceHost", source == null ? null : source.getHost());
        model.addAttribute("sharing", "compartilhar".equals(action));
        return "jobs/source";
    }

    @GetMapping("/unidade-2/publicacao")
    String demo(Model model) {
        model.addAttribute("form", new JobPostForm("", "Empresa de exemplo", "demo@example.com",
                null, "", null, null, "", "", "", ""));
        return demoForm(model);
    }

    @PostMapping("/unidade-2/publicacao")
    String completeDemo(@Valid @ModelAttribute("form") JobPostForm form, BindingResult errors, Model model,
                        RedirectAttributes redirect) {
        if (form.locationType() != null && form.locationType() != JobLocationType.REMOTE
                && (form.city() == null || form.city().isBlank())) {
            errors.reject("demo.city", "Informe a cidade para vagas híbridas ou presenciais.");
        }
        if (errors.hasErrors()) {
            errors.reject("demo.fields", "Confira cargo, modelo, senioridade, contrato e descrição antes de confirmar.");
            return demoForm(model);
        }
        redirect.addFlashAttribute("success", true);
        return "redirect:/unidade-2/publicacao";
    }

    private String demoForm(Model model) {
        model.addAttribute("publicationDemo", true);
        model.addAttribute("locationTypes", JobLocationType.values());
        model.addAttribute("seniorities", Seniority.values());
        model.addAttribute("contractTypes", ContractType.values());
        return "jobs/post";
    }

    private List<Long> savedIds(HttpSession session) {
        Object value = session.getAttribute(SAVED);
        if (!(value instanceof List<?> ids)) return List.of();
        return ids.stream().filter(Long.class::isInstance).map(Long.class::cast).toList();
    }

    private URI publicSource(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            var url = URI.create(value.strip());
            if (url.getHost() != null && url.getUserInfo() == null
                    && ("https".equalsIgnoreCase(url.getScheme()) || "http".equalsIgnoreCase(url.getScheme()))) {
                return url;
            }
        } catch (IllegalArgumentException ignored) {
            // Alguns anúncios antigos podem ter um endereço inválido.
        }
        return null;
    }
}
