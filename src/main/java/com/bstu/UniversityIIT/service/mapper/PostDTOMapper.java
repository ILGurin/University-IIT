package com.bstu.UniversityIIT.service.mapper;

import com.bstu.UniversityIIT.entity.Post;
import com.bstu.UniversityIIT.entity.DTO.PostDTO;
import com.bstu.UniversityIIT.entity.DTO.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PostDTOMapper implements Function<Post, PostDTO> {
    private final UserDTOMapper userDTOMapper;

    @Override
    public PostDTO apply(Post post) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd:MM:yyyy");
        UserDTO userDTO = userDTOMapper.apply(post.getUser());

        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getText(),
                userDTO,
                post.getCreationDate().format(formatter)
        );
    }
}
