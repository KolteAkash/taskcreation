package com.example.task.controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.task.model.Task;
import com.example.task.repository.TaskRepository;
import com.example.task.service.TaskService;

import jakarta.validation.Valid;
@Validated
@RestController
@CrossOrigin(origins="http://localhost:3000")
public class TaskController {

    @Autowired
    TaskService taskservice;

    @Autowired
    TaskRepository taskrepository;
    
    
    @PostMapping("/task")
    public ResponseEntity<String> addTask(@RequestParam("file") MultipartFile file, @ModelAttribute @Valid Task task, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Validation errors occurred.");
        } else {
            try {
                String originalFilename = file.getOriginalFilename();
                String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                String destinationPath = "C:\\Users\\asus\\Documents\\workspace-spring-tool-suite-4-4.18.0.RELEASE\\task\\src\\main\\resources\\files" + File.separator + uniqueFilename;
                File destinationFile = new File(destinationPath);
                file.transferTo(destinationFile);

        
                task.setTask_attachment(destinationPath);
                taskrepository.save(task);
                
                return ResponseEntity.ok("Task saved successfully");
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save the file.");
            }
        }
    }    
    

    @GetMapping("/task/{taskId}/file")
    public ResponseEntity<byte[]> getTaskFile(@PathVariable int taskId) {
        Optional<Task> optionalTask = taskrepository.findById(taskId);

        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            String filePath = task.getTask_attachment();
            File file = new File(filePath);

            if (file.exists()) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    byte[] fileContent = IOUtils.toByteArray(fileInputStream);

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(file.length())
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(fileContent);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    

    
    
//	
//	    @PostMapping("/task")
//		public String addTask(@Valid @RequestBody Task task) {
//			taskservice.addTask(task);
//			return "success";
//		}


	@GetMapping("/task")
	public List<Task>getAllStatus(){
		return taskservice.getAllStatus();
	}
	

	@GetMapping("/task/{taskid}")
	public ResponseEntity<Task> getStatusById(@PathVariable int taskid) {
	Optional<Task> task = taskservice.getStatusById(taskid);
	if (task.isPresent()) {
	return new ResponseEntity<>(task.get(), HttpStatus.OK);
	} else {
	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	}
	
	
	@PutMapping("/task/{taskid}")
	public ResponseEntity<String> updateTask(@PathVariable int taskid, @RequestBody Task task) {
	  Task updateTask = taskservice.updateTask(taskid,task);
	  if(updateTask==null) {
		  return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  }else {
		  return new ResponseEntity<>(HttpStatus.OK);
	  }

	}
	
	@DeleteMapping("/task/{taskid}")
	public ResponseEntity<String> deleteTask(@PathVariable int taskid) {
	    boolean taskDeleted = taskservice.deleteTask(taskid);
	    if (taskDeleted) {
	        return new ResponseEntity<>(HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
}