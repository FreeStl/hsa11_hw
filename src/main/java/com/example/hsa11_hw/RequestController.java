package com.example.hsa11_hw;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.elasticsearch.client.elc.Queries.matchQuery;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class RequestController {
    final ElasticsearchClient esClient;
    final WordRepository wordRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void insert() throws URISyntaxException, IOException {

        var path = Paths.get(getClass().getClassLoader().getResource("words.txt").toURI());
        BulkRequest.Builder br = new BulkRequest.Builder();
        Files.lines(path)
            .forEach(w ->
                br.operations(op -> op
                    .index(idx -> idx
                        .index("words")
                        .document(new Word(w))
                    )
                )
            );

        BulkResponse result = esClient.bulk(br.build());

        // Log errors, if any
        if (result.errors()) {
            log.error("Bulk had errors");
            for (BulkResponseItem item: result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
        }
    }

    @GetMapping
    public List<String> get(@RequestParam String input) {
        var result = wordRepository.getMatch(input,
                input.substring(0, input.length() - 1),
                input.substring(0, input.length() - 2),
                input.substring(0, input.length() - 3),
                "" + ((input.length() - 3) / 3) + "%");
        return result.stream()
                .map(WordResult::getWord)
                .toList();
    }


}
