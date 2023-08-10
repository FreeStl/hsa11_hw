Task:

Create ES index that will serve autocomplete needs with leveraging typos and errors (max 3 typos if word length is bigger than 7).
Please use english voc. Ang look at google as a ref.

1) Create index with correct analyser and mapping. For data insertion, I use Edge n-gram analyzer to break down words
into terms. If word length <= 7, then simply use it as a term. If word length > 7, break it down into terms: continuous -> c, co, con, cont enc..
```
PUT words
{
  "settings": {
    "analysis": {
      "analyzer": {
        "7_gram": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "7more_filter"
          ]
        }
      },
      "filter": {
        "7more_filter": {
          "type": "condition",
          "filter": ["edge_gram_filter"],
          "script": {
            "source": "token.getTerm().length() > 7"
          }
        },
        "edge_gram_filter": {
          "type": "edge_ngram",
          "min_gram": 1,
          "max_gram": 25
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "word": {
        "type": "text",
        "analyzer": "7_gram"
      }
    }
  }
}
```

2) Import English dictionary to elastic. I wrote a service for it (in src folder). When it run, POST http://localhost/api request 
is used to import words from src/main/resources/words.txt

3) Searching: it was a challengr for me. It is obvious, that we need to compare input query with indexed n-gram terms. 

This query didn't work:
```
GET words/_search
{
  "query": {
      "match": {
        "word": {
         "query": "continuous" ,
         "analyzer": "standard",
         "minimum_should_match": "70%"
        }
      }
  }
}
```
It found only words that contain word 'continuous'. Looks like for input query we need to generate 4 terms: continuous, continuou, continuo, continu.
I can use multiple analyzers to match input string length. But maybe simplier is to send these 4 input variations from application level:
```
GET words/_search
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "word": {
             "query": "continuous" ,
             "analyzer": "standard"
            }
          }
        },
        {
          "match": {
            "word": {
             "query": "continuou" ,
             "analyzer": "standard"
            }
          }
        },
        {
          "match": {
            "word": {
             "query": "continuo" ,
             "analyzer": "standard"
            }
          }
        },
        {
          "match": {
            "word": {
             "query": "continu" ,
             "analyzer": "standard"
            }
          }
        }
      ],
      "minimum_should_match": "70%"
    }
  }
}
```
result:
```
"hits": [
  {
    "_source": {
      "word": "continuous"
    }
  },
  {
    "_source": {
      "word": "continuously"
    }
  },
  {
    "_source": {
      "word": "continuousness"
    }
  },
  {
    "_source": {
      "word": "continuo"
    }
  },
  {
    "_source": {
      "word": "continuos"
    }
  }
]
```
Here I search by input, input.length - 1, input.length - 2, input.length - 3, input.length - 4. minimum_should_match is calculated by this formula: ((input.length() - 3) / 3)

This approach gives result that is close to google search: if input string is correct word of word prefix - it first suggests next words, that have common prefix.
If input string has mistmatch, it shows suggestions to fix that mismatch.

All search parameners precalculations are done on application level. You can use endpoint GET http://localhost/api?input={word}, to get a list of suggested words.

