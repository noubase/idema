package com.noubase.idema.model.search;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;

/**
 * Created by rshuper on 12.08.15.
 */
public class SearchRequest {

    public static final String DELIMITER = "!";
    public static final String ALL_FIELDS = "*";

    private final SearchType type;

    private final String field;

    private final String query;

    public SearchRequest(SearchType type, String field, String query) {
        this.type = type;
        this.field = field;
        this.query = query;
    }

    protected SearchRequest(Iterable<String> param) {
        List<String> list = Lists.newArrayList(param);
        if (list.size() >= 3) {
            this.type = hasText(list.get(0)) ? SearchType.valueOf(list.get(0)) : SearchType.EXACT;
            this.field = hasText(list.get(1)) ? list.get(1).trim() : ALL_FIELDS;
            this.query = Joiner.on(DELIMITER).join(list.subList(2, list.size())).trim();
        } else {
            this.type = SearchType.EXACT;
            this.field = ALL_FIELDS;
            this.query = "";
        }
    }

    public SearchRequest(@NotNull String param) {
        this(Splitter.on(DELIMITER).split(param));
    }

    public SearchType getType() {
        return type;
    }

    public String getField() {
        return field;
    }

    public String getQuery() {
        return query;
    }
}
