package com.bstu.UniversityIIT.service;

import com.bstu.UniversityIIT.entity.Post;
import com.bstu.UniversityIIT.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;


    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public Post getPostById(){
        return null;
    }

    public Post createPost(Post post){
        post.setCreationDate(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void deletePostById(Integer id){
        if (postRepository.findById(id).isPresent()){
            postRepository.deleteById(id);
        }
    }

    public Post updatePost(Post post, Post postFromDb){
        BeanUtils.copyProperties(post, postFromDb, "id");
        return postRepository.save(postFromDb);
    }
}
