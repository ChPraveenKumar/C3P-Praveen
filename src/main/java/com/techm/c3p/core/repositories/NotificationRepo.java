package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.Notification;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Integer> {

	Notification findById(int notifId);

	@Query(value = "SELECT notif_id, notif_type, notif_reference, notif_message, notif_readby, notif_label FROM c3p_notification where  "
			+ " (FIND_IN_SET(:notif_to_user, notif_to_user) or notif_to_workgroup=:notif_to_workgroup) and notif_status='Pending'"
			+ " and notif_expiry_date>= CURDATE()", nativeQuery = true)
	List<String> getNotification(@Param("notif_to_user") String notif_to_user,
			@Param("notif_to_workgroup") String notif_to_workgroup);

	@Query(value = "SELECT notif_id, notif_type, notif_reference, notif_message, notif_readby, notif_label, notif_from_user, notif_to_user,"
			+ " notif_to_workgroup, notif_priority, notif_status, notif_completedby, notif_created_date FROM c3p_notification where "
			+ " (FIND_IN_SET(:notif_to_user, notif_to_user) or notif_to_workgroup=:notif_to_workgroup) and notif_status='Pending' "
			+ " and notif_expiry_date>= CURDATE() order by notif_created_date desc", nativeQuery = true)
	List<String> getNotificationToUser(@Param("notif_to_user") String notif_to_user,
			@Param("notif_to_workgroup") String notif_to_workgroup);

}