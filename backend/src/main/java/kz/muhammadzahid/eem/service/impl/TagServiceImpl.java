package kz.muhammadzahid.eem.service.impl;

import kz.muhammadzahid.eem.dto.TagDto;
import kz.muhammadzahid.eem.dto.TagRequest;
import kz.muhammadzahid.eem.repo.TagRepository;
import kz.muhammadzahid.eem.service.TagService;
import kz.muhammadzahid.eem.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(Mapper::mapToTagDto)
                .toList();
    }

    @Override
    public TagDto createTag(TagRequest tag) {
        var tagEntity = Mapper.mapToTag(tag);
        return Mapper.mapToTagDto(tagRepository.save(tagEntity));
    }
}
