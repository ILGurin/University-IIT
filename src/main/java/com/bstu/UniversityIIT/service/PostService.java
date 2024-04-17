package com.bstu.UniversityIIT.service;

import com.bstu.UniversityIIT.entity.Post;
import com.bstu.UniversityIIT.entity.DTO.PostDTO;
import com.bstu.UniversityIIT.entity.User;
import com.bstu.UniversityIIT.repository.PostRepository;
import com.bstu.UniversityIIT.repository.UserRepository;
import com.bstu.UniversityIIT.service.mapper.PostDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostDTOMapper postDTOMapper;


    public List<PostDTO> getAllPosts(){
        return postRepository.findAll()
                .stream()
                .map(postDTOMapper)
                .toList();
    }

    public ResponseEntity<?> getPostById(Integer id){
        PostDTO postDTO = postRepository.findById(id)
                .map(postDTOMapper)
                .orElseThrow();
        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }

    public PostDTO createPost(Post post){
        User user = null;
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            post.setUser(user);
            post.setCreationDate(LocalDateTime.now());
        }catch (Exception e){
            System.out.println("PostService: createPost() - user not found");
            return null;
        }
        postRepository.save(post);

        return postDTOMapper.apply(post);
    }

    public ResponseEntity<?> deletePostById(Integer id){
        if (postRepository.findById(id).isPresent()){
            postRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>("Пост не найден", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> updatePost(Post post, Integer id){
        Post postFromDb = null;
        try{
            postFromDb = postRepository.findById(id).orElseThrow();
        }catch (Exception e){
            return new ResponseEntity<>("Пост не найден", HttpStatus.NOT_FOUND);
        }
        postFromDb.setTitle(post.getTitle());
        postFromDb.setText(post.getText());
        postRepository.save(postFromDb);

        ResponseEntity<?> response = getPostById(id);
        if(response.getStatusCode() == HttpStatus.OK){
            return response;
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
