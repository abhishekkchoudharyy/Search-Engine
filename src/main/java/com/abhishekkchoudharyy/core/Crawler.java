package com.abhishekkchoudharyy.core;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.abhishekkchoudharyy.config.CrawlingConfig;
import io.dropwizard.lifecycle.Managed;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.gargoylesoftware.htmlunit.util.XmlUtils.buildDocument;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class Crawler implements Managed {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    // url -> content
    private final Set<String> crawledUrls = new HashSet<>();
    private final Indexer indexer;
    private final CrawlingConfig crawlingConfig;

    public void crawl(String url,boolean CrawlChildren,long depth){
        if(depth > crawlingConfig.getMaxDepth()){
            return;
        }
        if (crawledUrls.contains(url)){
            log.error("Already crawled, skipping....");
            return;
        }
        log.info("Crawling "+url+"....");
        //scrape the data from website
        HtmlPage htmlPage = getHtmlPage(url);
        if (Objects.isNull(htmlPage)){
            return;
        }

        //submit the page to indexer (Thread)
        executorService.submit(() -> indexer.index(buildDocument(htmlPage)));

        List<String> urlsFound = htmlPage.getPage().getAnchors().stream().map(HtmlAnchor::getHrefAttribute).toList();
        log.info("total urls found in: {} is: {}", url, urlsFound.size());

        List<String> validUrls = filterMalformedUrls(urlsFound);
        log.info("total valid urls found in: {} is: {} / {}", url, validUrls.size(), urlsFound.size());

        if (CrawlChildren) {
            validUrls.forEach(validUrl -> {
                try {
                    crawl(validUrl, true, depth + 1L);
                } catch (Exception e) {
                    log.error("unable to crawl: e", e);
                }
            });
        }
        crawledUrls.add(url);
    }


    private static HtmlPage getHtmlPage(String url) {
        HtmlPage htmlPage = null;
        // fetch the website content
        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            htmlPage = webClient.getPage(url);
        } catch (IOException e) {
            log.info("Unable to fetch the page at {}", url);
        }
        return htmlPage;
    }

    private PageDoc buildDocument(HtmlPage htmlPage) {
        return PageDoc.builder()
                .id(UUID.randomUUID().toString())
                .title(htmlPage.getTitleText())
                .url(htmlPage.getBaseURI())
                .body(htmlPage.getPage()
                        .getBody()
                        .asNormalizedText())
                .build();
    }

    private List<String> filterMalformedUrls(List<String> urls) {
        return urls.stream().filter(this::isValidUrl).toList();
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void stop() throws Exception {
        executorService.shutdown();
    }
}
