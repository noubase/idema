package com.noubase.idema.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.noubase.idema.model.CollectionRequest.DEFAULT_PAGE;
import static com.noubase.idema.model.CollectionRequest.PARAM_PAGE;
import static com.noubase.idema.util.HttpUtil.buildQueryFromMap;
import static com.noubase.idema.util.HttpUtil.replaceParameter;

/**
 * Created by rshuper on 27.07.15.
 */
public class Pager<T> {

    private final int page;

    private final int pages;

    private final int size;

    private final long total;

    private final Set<T> items;

    @Nullable
    private final String next;

    @Nullable
    private final String prev;

    @NotNull
    private String generateURL(int value, @NotNull HttpServletRequest request) {
        Map<String, String[]> clone = new HashMap<>();
        clone.putAll(request.getParameterMap());
        return buildQueryFromMap(request.getRequestURL().toString(),
                replaceParameter(clone, PARAM_PAGE, String.valueOf(value)));
    }

    public Pager(@NotNull CollectionRequest r, long total, Set<T> items) {
        this.total = total;
        this.items = items;
        this.size = r.getPageSize();
        int pgs = new Double(Math.ceil(getTotal() / getSize())).intValue();
        if (total > 0 && pgs <= 0) {
            pgs = 1;
        }
        this.pages = pgs;
        this.page = getSize() > getTotal() ? DEFAULT_PAGE : Math.min(getPages(), r.getPageNumber());
        int nex = getPage() + 1;
        int pre = getPage() - 1;
        this.next = nex < getPages() ? generateURL(nex, r.getRequest()) : null;
        this.prev = pre >= DEFAULT_PAGE ? generateURL(pre, r.getRequest()) : null;
    }

    public int getPage() {
        return page;
    }

    public int getPages() {
        return pages;
    }

    public long getTotal() {
        return total;
    }


    public Set<T> getItems() {
        return items;
    }

    @Nullable
    public String getNext() {
        return next;
    }

    @Nullable
    public String getPrev() {
        return prev;
    }


    public int getSize() {
        return size;
    }
}
