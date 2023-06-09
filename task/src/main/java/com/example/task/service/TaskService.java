package com.example.task.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.io.FileOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.task.model.Task;
import com.example.task.repository.TaskRepository;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskrepository;

    
    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void saveTaskWithAttachment(Task task, MultipartFile attachment) throws IOException {
 
        taskRepository.save(task);

        String originalFilename = attachment.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;

 
        String destinationPath =  "C:\\Users\\asus\\Documents\\workspace-spring-tool-suite-4-4.18.0.RELEASE\\task\\src\\main\\resources\\files"+ File.separator + uniqueFileName;
        File destinationFile = new File(destinationPath);

        
        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            fileOutputStream.write(attachment.getBytes());
        }

        
        task.setTask_attachment(destinationPath);
        taskRepository.save(task);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //    @Transactional
//    public void saveTaskWithAttachment(Task task, MultipartFile attachment) throws IOException {
//      byte[] attachmentData = attachment.getBytes();
//      task.setTask_attachment(attachmentData);
//      taskrepository.save(task);
//    }

//    public void addTask(Task task, byte[] fileBytes) {
//        task.setTask_attachment(fileBytes);
//        taskrepository.save(task);
//    }	
//
//    public void addTask(Task task)	{
//    	taskrepository.save(task);
//    }
    
public List<Task>getAllStatus(){
	return taskrepository.findAll();
}

public Optional<Task> getStatusById(int taskId) {
    Optional<Task> optionalTask = taskrepository.findById(taskId);
    return optionalTask;
}
	
public Task updateTask(int Id,Task task) {
	Optional<Task>existingTask = taskrepository.findById(Id);
	if(!existingTask.isPresent()) {
		return null;
	}
	Task updateTask = taskrepository.save(task);
	return updateTask;
}
	
public boolean deleteTask(int Id) {
    Optional<Task> existingTask = taskrepository.findById(Id);
    if (!existingTask.isPresent()) {
        return false;
    } else {
        taskrepository.deleteById(Id);
        return true;
    }
}

}