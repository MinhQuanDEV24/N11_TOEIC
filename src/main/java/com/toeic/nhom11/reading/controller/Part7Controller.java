package com.toeic.nhom11.reading.controller;

import com.toeic.nhom11.reading.service.Part7Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.*;

@Controller
public class Part7Controller {
    private final Part7Service service;
    public Part7Controller(Part7Service service) {this.service=service;}
    @ModelAttribute void page(Model model) { model.addAttribute("activePage","reading"); model.addAttribute("pageTitle","Luyện Reading · Part 7"); }
    @GetMapping({"/reading/part7"})
    public String index(Model model,Principal principal) {
        model.addAttribute("levels",service.levels(principal==null?null:principal.getName()));
        if(principal!=null) model.addAttribute("history",service.history(principal.getName()));
        return "reading/part7-index";
    }
    @GetMapping({"/reading/part7/{level}", "/reading/part7/{level}/ready"})
    public String intro(@PathVariable int level,Model model,Principal principal) {
        model.addAttribute("level",service.level(level,principal==null?null:principal.getName())); return "reading/part7-intro";
    }
    @PostMapping("/reading/part7/{level}/start")
    public String start(@PathVariable int level,Principal principal) {
        return "redirect:/reading/part7/attempts/"+service.start(level,principal.getName());
    }
    @GetMapping("/reading/part7/attempts/{id}")
    public String practice(@PathVariable long id,Model model,Principal principal) {
        var state=service.state(id,principal.getName()); model.addAttribute("attemptId",id);
        model.addAttribute("attemptTitle",state.get("title")); return "reading/part7-practice";
    }
    @GetMapping("/reading/api/part7/attempts/{id}") @ResponseBody
    public Map<String,Object> state(@PathVariable long id,Principal principal) { return service.state(id,principal.getName()); }
    public record AnswerRequest(long questionId,long optionId) {}
    @PostMapping("/reading/api/part7/attempts/{id}/answers") @ResponseBody
    public Map<String,Object> answer(@PathVariable long id,@RequestBody AnswerRequest body,Principal principal) { return service.answer(id,body.questionId(),body.optionId(),principal.getName()); }
    @PostMapping("/reading/api/part7/attempts/{id}/finish") @ResponseBody
    public Map<String,Object> finish(@PathVariable long id,Principal principal) {service.finish(id,principal.getName()); return service.state(id,principal.getName());}
    @ExceptionHandler(ResponseStatusException.class)
    public Object error(ResponseStatusException exception,jakarta.servlet.http.HttpServletRequest request,Model model) {
        if(request.getRequestURI().contains("/reading/api/part7/")) return ResponseEntity.status(exception.getStatusCode()).body(Map.of("message",Objects.toString(exception.getReason(),"Không thể xử lý yêu cầu.")));
        request.setAttribute("jakarta.servlet.error.status_code",exception.getStatusCode().value());
        model.addAttribute("message",exception.getReason());
        return new org.springframework.web.servlet.ModelAndView("reading/error",model.asMap(),org.springframework.http.HttpStatus.valueOf(exception.getStatusCode().value()));
    }
}
