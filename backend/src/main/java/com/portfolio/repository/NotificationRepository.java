package com.portfolio.repository;

import com.portfolio.model.Notification;
import com.portfolio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
     
    // Find all unread notifications for a user
      
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    

      //Find all notifications for a user

    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    

      //Find all unread notifications for a user by userId

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);
    

    //Find all notifications for a user by userId

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findAllByUserId(@Param("userId") Long userId);
}
