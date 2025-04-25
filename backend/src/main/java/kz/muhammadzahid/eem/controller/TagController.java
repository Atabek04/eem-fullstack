package kz.muhammadzahid.eem.controller;

import kz.muhammadzahid.eem.dto.TagDto;
import kz.muhammadzahid.eem.dto.TagRequest;
import kz.muhammadzahid.eem.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<TagDto> getAllTags() {
        return tagService.getAllTags();
    }

    @PostMapping
    public TagDto createTag(TagRequest tag) {
        return tagService.createTag(tag);
    }
}
