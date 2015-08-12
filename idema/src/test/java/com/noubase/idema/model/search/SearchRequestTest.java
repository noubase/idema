package com.noubase.idema.model.search;

import org.junit.Test;

import static com.noubase.idema.model.search.SearchRequest.ALL_FIELDS;
import static com.noubase.idema.model.search.SearchRequest.DELIMITER;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by rshuper on 12.08.15.
 */
public class SearchRequestTest {

    @Test
    public void testInit() {
        String param = "";
        SearchRequest r = new SearchRequest(param);
        assertThat(r.getType(), is(SearchType.EXACT));
        assertEquals(ALL_FIELDS, r.getField());
        assertEquals("", r.getQuery());

        param = "" + DELIMITER + "";
        r = new SearchRequest(param);
        assertThat(r.getType(), is(SearchType.EXACT));
        assertEquals(ALL_FIELDS, r.getField());
        assertEquals("", r.getQuery());

        param = "" + DELIMITER + "" + DELIMITER + "";
        r = new SearchRequest(param);
        assertThat(r.getType(), is(SearchType.EXACT));
        assertEquals(ALL_FIELDS, r.getField());
        assertEquals("", r.getQuery());

        param = "" + DELIMITER + " hello " + DELIMITER + "";
        r = new SearchRequest(param);
        assertThat(r.getType(), is(SearchType.EXACT));
        assertEquals("hello", r.getField());
        assertEquals("", r.getQuery());

        param = "" + DELIMITER + ALL_FIELDS + "    " + DELIMITER + "";
        r = new SearchRequest(param);
        assertThat(r.getType(), is(SearchType.EXACT));
        assertEquals(ALL_FIELDS, r.getField());
        assertEquals("", r.getQuery());

        param = SearchType.PREFIX + DELIMITER + ALL_FIELDS + "    " + DELIMITER + "";
        r = new SearchRequest(param);
        assertThat(r.getType(), is(SearchType.PREFIX));
        assertEquals(ALL_FIELDS, r.getField());
        assertEquals("", r.getQuery());

        param = SearchType.PREFIX + DELIMITER + ALL_FIELDS + DELIMITER + "   hello";
        r = new SearchRequest(param);
        assertThat(r.getType(), is(SearchType.PREFIX));
        assertEquals(ALL_FIELDS, r.getField());
        assertEquals("hello", r.getQuery());


        param = SearchType.PREFIX + DELIMITER + ALL_FIELDS + DELIMITER + "   hello" + DELIMITER + " world";
        r = new SearchRequest(param);
        assertThat(r.getType(), is(SearchType.PREFIX));
        assertEquals(ALL_FIELDS, r.getField());
        assertEquals("hello" + DELIMITER + " world", r.getQuery());
    }
}