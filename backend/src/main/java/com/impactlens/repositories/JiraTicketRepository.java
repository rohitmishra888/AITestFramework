package com.impactlens.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.impactlens.entities.JiraTicket;

@Repository
public interface JiraTicketRepository extends JpaRepository<JiraTicket, UUID> {
    
    /**
     * Find ticket by ticket key
     */
    Optional<JiraTicket> findByTicketKey(String ticketKey);
    
    /**
     * Find tickets by status
     */
    List<JiraTicket> findByStatus(String status);
    
    /**
     * Find tickets by assignee
     */
    List<JiraTicket> findByAssignee(String assignee);
    
    /**
     * Find tickets by priority
     */
    List<JiraTicket> findByPriority(String priority);
    
    /**
     * Find tickets that have expired TTL
     */
    @Query("SELECT jt FROM JiraTicket jt WHERE jt.ttlExpiresAt < :now")
    List<JiraTicket> findExpiredTickets(@Param("now") String now);
    
    /**
     * Find tickets that need to be synced (older than specified time)
     */
    @Query("SELECT jt FROM JiraTicket jt WHERE jt.lastSyncedAt < :syncTime")
    List<JiraTicket> findTicketsNeedingSync(@Param("syncTime") String syncTime);
    
    /**
     * Search tickets by summary or description containing text
     */
    @Query("SELECT jt FROM JiraTicket jt WHERE " +
           "LOWER(jt.summary) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(jt.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<JiraTicket> searchTickets(@Param("searchTerm") String searchTerm);
    
    /**
     * Find tickets created by specific user
     */
    @Query("SELECT jt FROM JiraTicket jt WHERE jt.createdBy.id = :userId")
    List<JiraTicket> findByCreatedBy(@Param("userId") UUID userId);
    
    /**
     * Count tickets by status
     */
    @Query("SELECT jt.status, COUNT(jt) FROM JiraTicket jt GROUP BY jt.status")
    List<Object[]> countTicketsByStatus();
    
    /**
     * Find tickets updated after specific date
     */
    @Query("SELECT jt FROM JiraTicket jt WHERE jt.updatedAt > :since")
    List<JiraTicket> findTicketsUpdatedSince(@Param("since") String since);
    
    /**
     * Check if ticket exists by key
     */
    boolean existsByTicketKey(String ticketKey);
    
    /**
     * Delete expired tickets
     */
    @Query("DELETE FROM JiraTicket jt WHERE jt.ttlExpiresAt < :now")
    void deleteExpiredTickets(@Param("now") String now);
} 