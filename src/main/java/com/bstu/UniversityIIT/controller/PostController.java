package com.bstu.UniversityIIT.controller;

import com.bstu.UniversityIIT.entity.Post;
import com.bstu.UniversityIIT.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class PostController {
    private final PostService postService;

    @GetMapping
    public List<Post> getAllPosts(){
        return postService.getAllPosts();
    }

    @GetMapping("{id}")
    public Post getPost(@PathVariable("id") Post post){
        return post; //Метод getPostById() из PostService ??????
    }

    @PostMapping
    public Post createPost(@RequestBody Post post){
        return postService.createPost(post);
    }

    @PutMapping("{id}")
    public Post updatePost(
            @PathVariable("id") Post postFromDb,
            @RequestBody Post post
    ){
        return postService.updatePost(post, postFromDb);
    }

    @DeleteMapping("{id}")
    public void deletePost(@PathVariable("id") Post post){
        postService.deletePostById(post.getId());
    }
}
