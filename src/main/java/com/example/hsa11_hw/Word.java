package com.example.hsa11_hw;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Word {
    private String word;
}
