package com.abhishekkchoudharyy.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchHit {
    private String id;
    private String title;
    private String url;
}
