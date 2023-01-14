package com.Obrabotka.IT.repository;

import com.Obrabotka.IT.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface StatusRepository extends JpaRepository<Status, Long> {

}
