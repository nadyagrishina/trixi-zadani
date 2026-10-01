package com.nadyagrishina.trixizadani.repository;

import com.nadyagrishina.trixizadani.model.Obec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObecRepository extends JpaRepository<Obec, Long> {
}
