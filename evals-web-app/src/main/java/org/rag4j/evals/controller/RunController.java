package org.rag4j.evals.controller;

import org.rag4j.evals.model.EvaluationRun;
import org.rag4j.evals.model.RunConfiguration;
import org.rag4j.evals.service.EvaluationDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/runs")
public class RunController {
    
    private static final Logger logger = LoggerFactory.getLogger(RunController.class);
    
    private final EvaluationDataService dataService;
    
    @Autowired
    public RunController(EvaluationDataService dataService) {
        this.dataService = dataService;
    }
    
    @GetMapping
    public String runsList(Model model) {
        List<EvaluationRun> runs = dataService.getAllRuns();
        model.addAttribute("runs", runs);
        logger.info("Displaying {} evaluation runs", runs.size());
        return "evals/runs-list";
    }
    
    @GetMapping("/new")
    public String newRunForm(Model model) {
        model.addAttribute("run", new EvaluationRun());
        model.addAttribute("configuration", new RunConfiguration());
        return "evals/new-run";
    }
    
    @PostMapping("/new")
    public String createRun(
            @Validated @ModelAttribute("run") EvaluationRun run,
            @ModelAttribute("configuration") RunConfiguration configuration,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Set basic properties
            run.setId(UUID.randomUUID().toString());
            run.setConfiguration(configuration);
            
            // Save the run
            EvaluationRun savedRun = dataService.saveRun(run);
            
            logger.info("Created new evaluation run: {} - {}", savedRun.getId(), savedRun.getName());
            redirectAttributes.addFlashAttribute("success", "Evaluation run created successfully");
            
            return "redirect:/evaluations?runId=" + savedRun.getId();
            
        } catch (Exception e) {
            logger.error("Failed to create evaluation run: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to create evaluation run: " + e.getMessage());
            return "redirect:/runs/new";
        }
    }
    
    @GetMapping("/{id}")
    public String runDetail(@PathVariable String id, Model model) {
        Optional<EvaluationRun> runOpt = dataService.getRunById(id);
        
        if (runOpt.isPresent()) {
            EvaluationRun run = runOpt.get();
            model.addAttribute("run", run);
            
            // Get associated records count
            int recordCount = dataService.getRecordsByRunId(id).size();
            model.addAttribute("recordCount", recordCount);
            
            return "evals/run-detail";
        } else {
            logger.warn("Run not found: {}", id);
            return "redirect:/runs?error=Run not found";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String editRunForm(@PathVariable String id, Model model) {
        Optional<EvaluationRun> runOpt = dataService.getRunById(id);
        
        if (runOpt.isPresent()) {
            EvaluationRun run = runOpt.get();
            model.addAttribute("run", run);
            model.addAttribute("configuration", run.getConfiguration() != null ? run.getConfiguration() : new RunConfiguration());
            return "evals/edit-run";
        } else {
            logger.warn("Run not found for edit: {}", id);
            return "redirect:/runs?error=Run not found";
        }
    }
    
    @PostMapping("/{id}/edit")
    public String updateRun(
            @PathVariable String id,
            @Validated @ModelAttribute("run") EvaluationRun run,
            @ModelAttribute("configuration") RunConfiguration configuration,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Ensure the ID is set correctly
            run.setId(id);
            run.setConfiguration(configuration);
            
            // Save the updated run
            EvaluationRun updatedRun = dataService.saveRun(run);
            
            logger.info("Updated evaluation run: {} - {}", updatedRun.getId(), updatedRun.getName());
            redirectAttributes.addFlashAttribute("success", "Evaluation run updated successfully");
            
            return "redirect:/runs/" + updatedRun.getId();
            
        } catch (Exception e) {
            logger.error("Failed to update evaluation run {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to update evaluation run: " + e.getMessage());
            return "redirect:/runs/" + id + "/edit";
        }
    }
    
    @DeleteMapping("/{id}")
    public String deleteRun(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            dataService.deleteRun(id);
            logger.info("Deleted evaluation run: {}", id);
            redirectAttributes.addFlashAttribute("success", "Evaluation run deleted successfully");
            return "redirect:/runs";
            
        } catch (Exception e) {
            logger.error("Failed to delete evaluation run {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to delete evaluation run: " + e.getMessage());
            return "redirect:/runs";
        }
    }
    
    @PostMapping("/{id}/duplicate")
    public String duplicateRun(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            Optional<EvaluationRun> runOpt = dataService.getRunById(id);
            
            if (runOpt.isPresent()) {
                EvaluationRun originalRun = runOpt.get();
                
                // Create a copy
                EvaluationRun duplicateRun = new EvaluationRun();
                duplicateRun.setId(UUID.randomUUID().toString());
                duplicateRun.setName(originalRun.getName() + " (Copy)");
                duplicateRun.setDescription(originalRun.getDescription());
                duplicateRun.setConfiguration(originalRun.getConfiguration());
                
                EvaluationRun savedRun = dataService.saveRun(duplicateRun);
                
                logger.info("Duplicated evaluation run {} to {}", id, savedRun.getId());
                redirectAttributes.addFlashAttribute("success", "Evaluation run duplicated successfully");
                
                return "redirect:/runs/" + savedRun.getId();
            } else {
                redirectAttributes.addFlashAttribute("error", "Run not found for duplication");
                return "redirect:/runs";
            }
            
        } catch (Exception e) {
            logger.error("Failed to duplicate evaluation run {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to duplicate evaluation run: " + e.getMessage());
            return "redirect:/runs";
        }
    }
}
