package dev.normanpendzich.todorestapp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


//Bezieht sich auf Anfragen
@RestController
public class TodoController {
    @Autowired //Dependency Injection wird zur Verfügung gestellt
    private TodoRepository todoRepository;

    @Autowired //Dependency Injection wird zur Verfügung gestellt
    private UserRepository userRepository;

    @GetMapping("/todo")
    public ResponseEntity<Todo> get(@RequestParam(value = "id") int id) {
        // get todo from db ny id
        Optional<Todo> todoInDb = todoRepository.findById(id);
        if (todoInDb.isPresent()) {
            return new ResponseEntity<Todo>(todoInDb.get(), HttpStatus.OK);
        }

        return new ResponseEntity("no todo found with id " + id, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/todo/all")
    public ResponseEntity<Iterable<Todo>> getAll(@RequestHeader("api-secret") String secret) {
        System.out.println(secret);

        var userBySecret = userRepository.findBySecret(secret);

        if(userBySecret.isPresent()){
            Iterable<Todo> allTodosInDb = todoRepository.findAllByUserId(userBySecret.get().getId());
            return new ResponseEntity<Iterable<Todo>>(allTodosInDb, HttpStatus.OK);
        }
        return new ResponseEntity("Invalid secret", HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/todo")
    public  ResponseEntity<Todo> create(@RequestBody Todo newTodo) {

        todoRepository.save(newTodo);
        return new ResponseEntity<Todo>(newTodo, HttpStatus.CREATED);
    }

    @DeleteMapping("/todo")
    public ResponseEntity delete (@RequestParam(value = "id")int id) {
        Optional<Todo> todoInDB = todoRepository.findById(id);
        if (todoInDB.isPresent()) {
            todoRepository.deleteById(id);
            return new ResponseEntity("Todo deleted", HttpStatus.OK);
        }
        return new ResponseEntity("No todo to delte found with id "+ id,HttpStatus.NOT_FOUND);
    }

    @PutMapping("/todo")
    public ResponseEntity<Todo> edit(@RequestBody Todo editedTodo) {
        Optional<Todo> todoInDB = todoRepository.findById(editedTodo.getId());
        if (todoInDB.isPresent()) {
           //update
            Todo savedTodo = todoRepository.save(editedTodo);
            return new ResponseEntity<Todo>(savedTodo,HttpStatus.OK);
        }
        return new ResponseEntity("No todo to update found with id "+ editedTodo.getId(), HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/todo/setDone")
    public ResponseEntity<Todo> setDone(@RequestParam(value = "isDone") boolean isDone,
                                        @RequestParam(value = "id") int id) {

        Optional<Todo> todoInDB = todoRepository.findById(id);
        if (todoInDB.isPresent()) {
            //update isDone
            todoInDB.get().setIsDone(isDone);
            Todo savedTodo = todoRepository.save(todoInDB.get());
            return new ResponseEntity<Todo>(savedTodo,HttpStatus.OK);
        }
        return new ResponseEntity("No todo to update found with id "+ id, HttpStatus.NOT_FOUND);

    }
}
