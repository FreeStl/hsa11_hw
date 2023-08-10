package com.example.hsa11_hw;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface WordRepository extends ElasticsearchRepository<WordResult, String> {
    @Query("{\"bool\":{\"should\":[{\"match\":{\"word\":{\"query\":\"?0\",\"analyzer\":\"standard\"}}},{\"match\":{\"word\":{\"query\":\"?1\",\"analyzer\":\"standard\"}}},{\"match\":{\"word\":{\"query\":\"?2\",\"analyzer\":\"standard\"}}},{\"match\":{\"word\":{\"query\":\"?3\",\"analyzer\":\"standard\"}}}],\"minimum_should_match\":\"?4\"}}")
    List<WordResult> getMatch(String term1, String term2, String term3, String term4, String minMatch);
}
