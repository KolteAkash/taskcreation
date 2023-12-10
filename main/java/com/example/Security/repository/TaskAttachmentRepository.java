package com.example.Security.repository;

import com.example.Security.model.Task;
import com.example.Security.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment,Integer> {
    TaskAttachment findByTask(Task task);
}
