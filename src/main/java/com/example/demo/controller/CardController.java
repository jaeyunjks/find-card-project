package com.example.demo.controller;

import com.example.demo.model.Card;
import com.example.demo.repository.CardRepository;
import com.example.demo.util.AESEncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AESEncryptionUtil aes;

    @GetMapping("/")
    public String home() {
        return "add_card";
    }

    @GetMapping("/add-card")
    public String showAddForm(@RequestParam(required = false) String clear, Model model) {
        if (!model.containsAttribute("card") || clear != null) {
            model.addAttribute("card", new Card());
        }
        return "add_card";
    }

    @PostMapping("/add-card")
    public String addCard(@ModelAttribute Card card, RedirectAttributes redirectAttributes) {
        try {
            String encrypted = aes.encrypt(card.getEncryptedPan());

            boolean exists = cardRepository.findAll().stream().anyMatch(c -> {
                try {
                    return aes.decrypt(c.getEncryptedPan()).equals(card.getEncryptedPan());
                } catch (Exception e) {
                    return false;
                }
            });

            if (exists) {
                redirectAttributes.addFlashAttribute("message", "Card with that PAN already exists!");
            } else {
                card.setEncryptedPan(encrypted);
                cardRepository.save(card);
                redirectAttributes.addFlashAttribute("message", "Card added successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error adding card: " + e.getMessage());
        }
        return "redirect:/add-card?clear";
    }

    @GetMapping("/search-card")
    public String showSearchForm() {
        return "search_card";
    }

    @PostMapping("/search-card")
    public String searchCard(@RequestParam String last4, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");

        List<CardResult> results = cardRepository.findAll().stream()
                .filter(card -> {
                    try {
                        String pan = aes.decrypt(card.getEncryptedPan());
                        return pan.endsWith(last4);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(card -> {
                    try {
                        String pan = aes.decrypt(card.getEncryptedPan());
                        String masked = "**** **** " + pan.substring(pan.length() - 4);
                        String formattedTime = card.getCreatedAt().format(formatter);
                        return new CardResult(card.getCardholderName(), masked, formattedTime);
                    } catch (Exception e) {
                        return new CardResult(card.getCardholderName(), "ERROR", "N/A");
                    }
                })
                .collect(Collectors.toList());

        model.addAttribute("results", results);
        model.addAttribute("last4", last4);

        return "search_card";
    }

    // Static class for results
    public static class CardResult {
        public String name;
        public String maskedPan;
        public String createdAt; // already formatted nicely

        public CardResult(String name, String maskedPan, String createdAt) {
            this.name = name;
            this.maskedPan = maskedPan;
            this.createdAt = createdAt;
        }

        public String getName() {
            return name;
        }

        public String getMaskedPan() {
            return maskedPan;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
