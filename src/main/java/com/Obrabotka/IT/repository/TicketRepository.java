package com.Obrabotka.IT.repository;

import com.Obrabotka.IT.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
