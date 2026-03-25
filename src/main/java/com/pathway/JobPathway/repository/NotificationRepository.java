package com.pathway.JobPathway.repository;

import com.pathway.JobPathway.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientIdOrderByIsReadAscCreatedAtDesc(Long userId, Pageable pageable);

    long countByRecipientIdAndIsReadFalse(Long userId);

    List<Notification> findByRecipientIdAndIsReadFalse(Long userId);
}
