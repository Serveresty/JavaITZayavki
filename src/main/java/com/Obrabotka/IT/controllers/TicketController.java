package com.Obrabotka.IT.controllers;

import com.Obrabotka.IT.models.Stage;
import com.Obrabotka.IT.models.Status;
import com.Obrabotka.IT.models.Ticket;
import com.Obrabotka.IT.models.User;
import com.Obrabotka.IT.repository.RoleRepository;
import com.Obrabotka.IT.repository.TicketRepository;
import com.Obrabotka.IT.service.TicketService;
import com.Obrabotka.IT.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class TicketController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/send_request")
    public String reqSend(Model model) {
        model.addAttribute("themes", ticketService.allThemes());
        Ticket newTicket = new Ticket();
        model.addAttribute(newTicket);
        return "send_request";
    }

    @GetMapping("/claim_request")
    public String reqClaim(Model model) {
        return "claim_request";
    }

    @PostMapping("/create_ticket")
    public String createTicket (Model model, @Valid Ticket newTicket, Errors errors) {
        if(errors.hasErrors()){
            return "send_request";
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Date currentDate = new Date();
        newTicket.setDateOpened(currentDate);
        newTicket.setCreatedBy(user);
        ticketRepository.save(newTicket);
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
