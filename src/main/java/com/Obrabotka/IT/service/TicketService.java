package com.Obrabotka.IT.service;


import com.Obrabotka.IT.models.Role;
import com.Obrabotka.IT.models.Theme;
import com.Obrabotka.IT.models.Ticket;
import com.Obrabotka.IT.models.User;
import com.Obrabotka.IT.repository.RoleRepository;
import com.Obrabotka.IT.repository.ThemeRepository;
import com.Obrabotka.IT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Service
public class TicketService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ThemeRepository themeRepository;

    public List<Theme> allThemes() {
        return themeRepository.findAll();
    }

    public boolean deleteTheme(Long themeId) {
        themeRepository.findById(themeId);
        themeRepository.deleteById(themeId);
        return true;
    }

    public boolean saveTheme(String theme) {

        Theme them = themeRepository.findByTitle(theme);

        if (them != null) {
            return false;
        }

        them = new Theme(theme);

        themeRepository.save(them);
        return true;
    }

    public List<Ticket> usergtTicketList(User user) {
        return em.createQuery("SELECT u FROM Ticket u WHERE u.createdBy LIKE :paramId", Ticket.class)
                .setParameter("paramId", user).getResultList();
    }
}
