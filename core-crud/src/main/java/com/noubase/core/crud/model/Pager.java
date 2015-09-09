package com.noubase.core.crud.model;

import com.noubase.core.crud.util.HttpUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by rshuper on 27.07.15.
 */
@SuppressWarnings("unused")
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
        return HttpUtil.buildQueryFromMap(request.getRequestURL().toString(),
                HttpUtil.replaceParameter(clone, CollectionRequest.PARAM_PAGE, String.valueOf(value)));
    }

    public Pager(@NotNull CollectionRequest r, long total, Set<T> items) {
        this.total = total;
        this.items = items;
        this.size = r.getPageSize();
        int pgs = new Double(Math.ceil(getTotal() / (double)getSize())).intValue();
        if (total > 0 && pgs <= 0) {
            pgs = 1;
        }
        this.pages = pgs;
        this.page = getSize() > getTotal() ? CollectionRequest.DEFAULT_PAGE : Math.min(getPages(), r.getPageNumber());
        int nex = getPage() + 1;
        int pre = getPage() - 1;
        this.next = nex < getPages() ? generateURL(nex, r.getRequest()) : null;
        this.prev = pre >= CollectionRequest.DEFAULT_PAGE ? generateURL(pre, r.getRequest()) : null;
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
