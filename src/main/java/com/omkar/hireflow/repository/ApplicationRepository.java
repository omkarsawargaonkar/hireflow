package com.omkar.hireflow.repository;

import com.omkar.hireflow.entity.Application;
import com.omkar.hireflow.entity.User;
import com.omkar.hireflow.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByCandidateAndJob(User candidate, Job job);

    List<Application> findByCandidate(User candidate);

    List<Application> findByJob(Job job);
}