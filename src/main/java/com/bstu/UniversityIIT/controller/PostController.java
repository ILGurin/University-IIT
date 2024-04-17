package com.bstu.UniversityIIT.controller;

import com.bstu.UniversityIIT.entity.DTO.PostDTO;
import com.bstu.UniversityIIT.entity.Post;
import com.bstu.UniversityIIT.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class PostController {
    private final PostService postService;

    @GetMapping
    public List<PostDTO> getAllPosts(){
        return postService.getAllPosts();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPost(@PathVariable("id") Integer id){
        return postService.getPostById(id);
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @RequestBody Post post
    ){
        return new ResponseEntity<>(postService.createPost(post), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable("id") Integer id,
            @RequestBody Post post
    ){
        return postService.updatePost(post, id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Integer id){
        return postService.deletePostById(id);
    }
}
