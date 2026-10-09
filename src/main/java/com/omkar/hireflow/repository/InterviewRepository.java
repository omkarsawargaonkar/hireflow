package com.omkar.hireflow.repository;

import com.omkar.hireflow.entity.Interview;
import com.omkar.hireflow.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    Optional<Interview> findByApplication(Application application);

    boolean existsByApplication(Application application);
}