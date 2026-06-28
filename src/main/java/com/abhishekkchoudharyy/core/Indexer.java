package com.abhishekkchoudharyy.core;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static edu.stanford.nlp.ie.machinereading.common.SimpleTokenize.tokenize;


@Slf4j
public class Indexer {
    private  final static Map<String, Set<String>> IN_MEMORY_INDEX = new HashMap<>();
    private final static Map<String,SearchHit> HIT_MAP = new HashMap<>();

    private StanfordCoreNLP pipeline;

    public Indexer(){
        setup();
    }

    private void setup(){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public static Set<String> STOP_WORDS = Set.of("a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "s", "such",
            "t", "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with");

    public void index(PageDoc pageDoc){
        //Entery Point
        long start = System.currentTimeMillis();
        List<String> tokens = tokenize(pageDoc.getBody());
        generateInvertedIndex(pageDoc,tokens);
        long end = System.currentTimeMillis();
        log.info("Successfully indexed document with id: {} in {}ms", pageDoc.getId(), (end - start));
    }

    private void generateInvertedIndex(PageDoc pageDoc, List<String> tokens) {
    final String documentID =pageDoc.getId();
        tokens.forEach(token -> {
            if (!IN_MEMORY_INDEX.containsKey(token)) {
                IN_MEMORY_INDEX.put(token, new HashSet<>());
            }
            IN_MEMORY_INDEX.get(token).add(documentID);
        });
        HIT_MAP.put(documentID, SearchHit.builder()
                .id(documentID)
                .title(pageDoc.getTitle())
                .url(pageDoc.getUrl())
                .build());
    }

    private List<String> tokenize(String corpus){
        // convert corpus to lowercase
        Annotation annotation = new Annotation(corpus.toLowerCase());
        pipeline.annotate(annotation);

        // Extract token and lemmata from the annotation
        List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);

        //Build a list to Store Result
        List<String> tokenized = new ArrayList<>();

        //Append and lemmatized tokens from the list
        for (CoreLabel token : tokens){
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            tokenized.add(lemma);
        }
        return tokenized.stream().filter(token -> !invalidToken(token)).collect(Collectors.toList());

    }

    private boolean invalidToken(String token){
        //if the token is stop words or punctuation numbers or special characters don't consider this as a valid token
        return STOP_WORDS.contains(token) || StringUtils.matches(token, "[\\p{Punct}\\d]+");
    }

    public List<SearchHit> query(String query){
        // split the word into terms and check in the index if they are present
        List<String> terms = Arrays.stream(query.toLowerCase().split(" ")).toList();
        List<SearchHit> hits = new ArrayList<>();
        if (terms.isEmpty()) {
            return hits;
        }

        terms.forEach(term -> {
            if (IN_MEMORY_INDEX.containsKey(term)) {
                Set<String> documentIds = IN_MEMORY_INDEX.get(term);
                hits.addAll(documentIds.stream().map(HIT_MAP::get).toList());
            }
        });

        return hits;
    }






}


