package kz.muhammadzahid.eem.service;

import kz.muhammadzahid.eem.dto.TagDto;
import kz.muhammadzahid.eem.dto.TagRequest;

import java.util.List;

public interface TagService {

    List<TagDto> getAllTags();

    TagDto createTag(TagRequest tag);
}
