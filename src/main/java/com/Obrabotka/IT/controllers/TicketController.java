package com.Obrabotka.IT.controllers;

import com.Obrabotka.IT.models.User;
import com.Obrabotka.IT.repository.RoleRepository;
import com.Obrabotka.IT.service.TicketService;
import com.Obrabotka.IT.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TicketController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/send_request")
    public String reqSend( Model model) {
        return "send_request";
    }

    @GetMapping("/claim_request")
    public String reqClaim( Model model) {
        return "claim_request";
    }

    @PostMapping("/create_ticket")
    public String createTicket (@AuthenticationPrincipal User user, Model model) {

        return "redirect:/user_tickets";
    }

    @GetMapping("/user_tickets")
    public String userTickets (@AuthenticationPrincipal User user, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUser = (User)authentication.getPrincipal();
        model.addAttribute("allTickets", ticketService.usergtTicketList(customUser));
        model.addAttribute("user",user);
        return "user_tickets";
    }
}
