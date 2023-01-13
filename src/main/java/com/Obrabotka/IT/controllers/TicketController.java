package com.Obrabotka.IT.controllers;

import com.Obrabotka.IT.models.*;
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
    public String reqClaim(@AuthenticationPrincipal User user, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUser = (User)authentication.getPrincipal();

        if (ticketService.getAllTickets().size() < 1) {
            model.addAttribute("noTickets", true);
            model.addAttribute("user",user);
            return "user_tickets";
        }

        model.addAttribute("noTickets", false);
        model.addAttribute("allTickets", ticketService.getAllTickets());
        model.addAttribute("user",user);

        return "claim_request";
    }

    @PostMapping("/create_ticket")
    public String createTicket (Model model, @Valid Ticket newTicket, Errors errors) {
        if(errors.hasErrors()){
            model.addAttribute("themes", ticketService.allThemes());
            Ticket newTickett = new Ticket();
            model.addAttribute(newTickett);
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

        model.addAttribute("noTickets", false);
        model.addAttribute("notTickets", false);

        if (customUser.getRoles().size() < 2) {
            model.addAttribute("notOperator", true);
        }else{
            model.addAttribute("notOperator", false);
        }

        if (ticketService.usergtTicketList(customUser).size() < 1) {
            model.addAttribute("noTickets", true);
        }

        if (ticketService.opergtTicketList(customUser).size() < 1){
            model.addAttribute("notTickets", true);
        }else{
            model.addAttribute("notTickets", false);
        }

        model.addAttribute("allTickets", ticketService.usergtTicketList(customUser));
        model.addAttribute("allOperatorTickets", ticketService.opergtTicketList(customUser));
        model.addAttribute("user",user);
        return "user_tickets";
    }

    @PostMapping("/current_ticket")
    public String getTicketByIdd(@AuthenticationPrincipal User user,
                              @RequestParam(required = true, defaultValue = "" ) Long ticketId,
                              Model model) {



        return "redirect:/current_ticket/" + ticketId;
    }

    @GetMapping("/current_ticket/{id}")
    public String ticketByIdd (@PathVariable("id") Long id, @AuthenticationPrincipal User user, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUser = (User)authentication.getPrincipal();

        if (customUser.getRoles().size() < 2) {
            if (!ticketService.isUsersTicket(customUser, id)) {
                return "user_tickets";
            }
        }

        model.addAttribute("ticket", ticketService.getTicketById(id));
        model.addAttribute("user",user);
        return "current_ticket";
    }

    @PostMapping("/take_ticket")
    public String takeTicketByOperator (@RequestParam(required = true, defaultValue = "" ) Long ticketId, @AuthenticationPrincipal User user, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUser = (User)authentication.getPrincipal();

        Ticket thisTicket = ticketService.getTicketById(ticketId);
        thisTicket.setAssignedTo(customUser);
        ticketRepository.save(thisTicket);
        return "redirect:/current_ticket/" + ticketId;
    }
}
