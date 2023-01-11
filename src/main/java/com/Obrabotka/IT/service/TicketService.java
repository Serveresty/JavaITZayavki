package com.Obrabotka.IT.service;


import com.Obrabotka.IT.models.Role;
import com.Obrabotka.IT.models.Theme;
import com.Obrabotka.IT.models.Ticket;
import com.Obrabotka.IT.models.User;
import com.Obrabotka.IT.repository.RoleRepository;
import com.Obrabotka.IT.repository.ThemeRepository;
import com.Obrabotka.IT.repository.UserRepository;
import org.hibernate.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
        return em.createQuery("select ticket from Ticket ticket where ticket.createdBy=:param", Ticket.class)
                .setParameter("param", user).getResultList();
    }

    public boolean isUsersTicket(User user, Long ticketId) {
        try {
            em.createQuery("select ticket from Ticket ticket where ticket.createdBy=:param1 and ticket.id=:param2", Ticket.class)
                    .setParameter("param1", user).setParameter("param2", ticketId).getSingleResult();
            return true;
        } catch (NoResultException e) {
            System.out.println(e.getMessage());
        }
            return false;
    }

    public Ticket getTicketById(Long ticketId) {
        return em.createQuery("select ticket from Ticket ticket where ticket.id=:param", Ticket.class)
                .setParameter("param", ticketId).getSingleResult();
    }
}
