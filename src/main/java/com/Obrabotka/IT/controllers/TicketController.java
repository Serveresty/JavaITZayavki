package com.Obrabotka.IT.controllers;

import com.Obrabotka.IT.models.*;
import com.Obrabotka.IT.repository.RoleRepository;
import com.Obrabotka.IT.repository.StatusRepository;
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
    private StatusRepository statusRepository;

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
            return "claim_request";
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

        Ticket activeTicket = ticketService.getTicketById(id);
        Status newStatus = new Status();

        List<Status> updates = activeTicket.getUpdates();

        if (customUser.getRoles().size() < 2) {
            if (!ticketService.isUsersTicket(customUser, id)) {
                return "user_tickets";
            }
        }

        model.addAttribute("statuses", activeTicket.getUpdates());
        model.addAttribute("status", newStatus);
        model.addAttribute("ticket", ticketService.getTicketById(id));
        model.addAttribute("currStatus", activeTicket.getStage().toString());
        model.addAttribute("user",user);
        return "current_ticket";
    }

    @PostMapping("/addComment")
    public String addComment (@AuthenticationPrincipal User user,
                              @RequestParam(required = true, defaultValue = "" ) Long ticketId,
                              @RequestParam(required = true, defaultValue = "") String newComment,
                              Model model, @Valid Status newStatus) {
        Ticket activeTicket = ticketService.getTicketById(ticketId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUser = (User)authentication.getPrincipal();

        newStatus.setAuthor(customUser.getUsername());
        activeTicket.addUpdate(newStatus);
        activeTicket.setLastUpdated(new Date());
        statusRepository.save(newStatus);

        return "redirect:/current_ticket/" + ticketId;
    }

    @PostMapping("/closeTicket")
    public String closeTicket (Model model, @RequestParam(required = true, defaultValue = "" ) Long ticketId, @Valid Ticket ticket, Errors errors) {
        Ticket activeTicket = ticketService.getTicketById(ticketId);
        activeTicket.setStage(Stage.CLOSED);
        activeTicket.setDateClosed(new Date());
        ticketRepository.save(activeTicket);
        return "redirect:/user_tickets";
    }
    @PostMapping("/waitingUserAnswer")
    public String waitingUserAnswer (Model model, @RequestParam(required = true, defaultValue = "" ) Long ticketId, @Valid Ticket ticket, Errors errors) {
        Ticket activeTicket = ticketService.getTicketById(ticketId);
        activeTicket.setStage(Stage.WAITING_INPUT);
        activeTicket.setDateClosed(new Date());
        ticketRepository.save(activeTicket);
        return "redirect:/current_ticket/" + ticketId;
    }

    @PostMapping("/take_ticket")
    public String takeTicketByOperator (@RequestParam(required = true, defaultValue = "" ) Long ticketId, @AuthenticationPrincipal User user, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User customUser = (User)authentication.getPrincipal();

        Ticket thisTicket = ticketService.getTicketById(ticketId);
        thisTicket.setAssignedTo(customUser);
        thisTicket.setStage(Stage.WORKING);
        thisTicket.setDateClosed(new Date());
        ticketRepository.save(thisTicket);
        return "redirect:/current_ticket/" + ticketId;
    }
}
