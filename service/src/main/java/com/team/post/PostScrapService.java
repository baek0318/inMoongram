package com.team.post;

import com.team.exception.IdNotFoundException;
import com.team.post.dto.input.PostScrapDeleteInput;
import com.team.post.dto.input.PostScrapGetInput;
import com.team.post.dto.input.PostScrapSaveInput;
import com.team.post.dto.output.PostScrapGetOutput;
import com.team.post.dto.output.PostScrapSaveOutput;
import com.team.user.User;
import com.team.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostScrapService {
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository; //나중에 PostService로 교체해도 된다
    private final UserService userService;

    @Transactional
    public PostScrapSaveOutput postScrap(PostScrapSaveInput postScrapSaveInput) {
        Post post = postRepository.findById(postScrapSaveInput.getPostId())
                .orElseThrow(IdNotFoundException::new);
        User user = userService.findUserById(postScrapSaveInput.getUserId());
        PostScrap saved = postScrapRepository.save(new PostScrap(user, post));
        return new PostScrapSaveOutput(saved.getId());
    }

    @Transactional(readOnly = true)
    public PostScrapGetOutput getScrap(PostScrapGetInput input) {
        return new PostScrapGetOutput(
                postScrapRepository.findAllByUserId(input.getUserId()).stream()
                .map(PostScrapGetOutput.PostScrapInfo::new)
                .collect(Collectors.toList())
        );
    }

    @Transactional
    public void unScrap(PostScrapDeleteInput input) {
        PostScrap postScrap = postScrapRepository.findByUserIdAndPostId(input.getUserId(), input.getPostId())
                .orElseThrow(IdNotFoundException::new);
        postScrapRepository.deleteById(postScrap.getId());
    }
}
