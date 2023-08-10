package com.example.hsa11_hw;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "words")
@Data
public class WordResult {
    @Id
    private String id;
    private String word;
}
