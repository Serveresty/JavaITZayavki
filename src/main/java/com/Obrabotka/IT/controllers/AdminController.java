package com.Obrabotka.IT.controllers;

import com.Obrabotka.IT.models.Role;
import com.Obrabotka.IT.models.Theme;
import com.Obrabotka.IT.models.Ticket;
import com.Obrabotka.IT.models.User;
import com.Obrabotka.IT.repository.RoleRepository;
import com.Obrabotka.IT.repository.ThemeRepository;
import com.Obrabotka.IT.service.TicketService;
import com.Obrabotka.IT.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TicketService ticketService;

    @GetMapping("/admin")
    public String userList(@AuthenticationPrincipal User user,
                           Model model) {
        model.addAttribute("allUsers", userService.allUsers());
        model.addAttribute("themes", ticketService.allThemes());
        model.addAttribute("user",user);
        return "admin";
    }

    @PostMapping("/admin")
    public String  deleteUser(@AuthenticationPrincipal User user,
                              @RequestParam(required = true, defaultValue = "" ) Long userId,
                              @RequestParam(required = true, defaultValue = "" ) String action,
                              Model model) {
        if (action.equals("delete")){
            userService.deleteUser(userId);
        }
        if (action.equals("give_operator")) {
            User userr = userService.get(userId);
            userr.getRoles().add(new Role(2L, "ROLE_OPERATOR"));
            userService.saveWith(userr);
        }
        if (action.equals("delete_operator")) {
            User userr = userService.get(userId);
            userr.getRoles().clear();
            userr.getRoles().add(new Role(1L, "ROLE_USER"));
            userService.saveWith(userr);
        }
        model.addAttribute("user",user);
        return "redirect:/admin";
    }

    @PostMapping("/admin_registration")
    public String addUserByAdmin(@ModelAttribute("userForm")@Valid User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "admin";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "admin";
        }
        if (!userService.saveUser(userForm)){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "admin";
        }

        return "redirect:/admin";
    }

    @GetMapping("/admin/gt/{userId}")
    public String  gtUser(@AuthenticationPrincipal User user,
                          @PathVariable("userId") Long userId, Model model) {
        model.addAttribute("allUsers", userService.usergtList(userId));
        model.addAttribute("user",user);
        return "admin";
    }

    @PostMapping("/delete_theme")
    public String deleteTheme(@AuthenticationPrincipal User user,
                              @RequestParam(required = true, defaultValue = "" ) Long themeId,
                              @RequestParam(required = true, defaultValue = "" ) String action,
                              Model model) {
        if (action.equals("delete")){
            ticketService.deleteTheme(themeId);
        }
        return "redirect:/admin";
    }

    @PostMapping("/add_theme")
    public String addTheme(@AuthenticationPrincipal User user,
                              @RequestParam(required = true, defaultValue = "" ) String text,
                              Model model) {
            ticketService.saveTheme(text);
        return "redirect:/admin";
    }
}
