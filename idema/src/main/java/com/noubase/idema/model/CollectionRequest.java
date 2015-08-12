package com.noubase.idema.model;

import com.google.common.base.Splitter;
import com.noubase.idema.model.search.SearchRequest;
import com.noubase.idema.model.search.SearchType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

/**
 * Created by rshuper on 27.07.15.
 */
public class CollectionRequest extends ResourceRequest {

    public static final String PARAM_PAGE = "page";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_ORDER = "order";
    public static final String PARAM_SEARCH = "q";

    public static final Integer DEFAULT_SIZE = 10;
    public static final Integer DEFAULT_PAGE = 0;
    public static final String DEFAULT_ORDER = "modified";

    private final SearchRequest search;

    private static Integer getParameter(@NotNull HttpServletRequest request, String param, Integer def) {
        String val = request.getParameter(param);
        if (hasText(val)) {
            try {
                return Integer.valueOf(val);
            } catch (Exception e) {
                return def;
            }
        }
        return def;
    }

    public CollectionRequest(@NotNull HttpServletRequest request, int maxCollectionSize) {
        super(request, getParameter(request, PARAM_PAGE, DEFAULT_PAGE),
                Math.min(maxCollectionSize, getParameter(request, PARAM_SIZE, DEFAULT_SIZE)),
                getSort(request));
        String s = request.getParameter(PARAM_SEARCH);
        this.search = hasText(s) ? new SearchRequest(s) : new SearchRequest(SearchType.EXACT, "*", "");
    }

    @NotNull
    public static Sort getSort(@NotNull HttpServletRequest request) {
        String orderParam = request.getParameter(PARAM_ORDER);
        List<String> list = hasText(orderParam) ? Splitter.on(",").splitToList(orderParam) : Arrays.asList("", "");
        String order = hasText(list.get(0)) ? list.get(0) : DEFAULT_ORDER;
        Sort.Direction direction = hasText(list.get(1)) && Sort.Direction.ASC.toString().equalsIgnoreCase(list.get(1))
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return new Sort(new Sort.Order(direction, order));
    }

    @NotNull
    public SearchRequest getSearch() {
        return search;
    }
}
