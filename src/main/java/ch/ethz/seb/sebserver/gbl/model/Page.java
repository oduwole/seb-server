/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sebserver.gbl.model;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sebserver.gbl.util.Utils;

public final class Page<T> {

    public static final String ATTR_NAMES_ONLY = "names_only";
    public static final String ATTR_NUMBER_OF_PAGES = "number_of_pages";
    public static final String ATTR_PAGE_NUMBER = "page_number";
    public static final String ATTR_PAGE_SIZE = "page_size";
    public static final String ATTR_SORT = "sort";
    public static final String ATTR_CONTENT = "content";

    @JsonProperty(ATTR_NUMBER_OF_PAGES)
    public final Integer numberOfPages;
    @JsonProperty(ATTR_PAGE_NUMBER)
    public final Integer pageNumber;
    @JsonProperty(ATTR_PAGE_SIZE)
    public final Integer pageSize;
    @JsonProperty(ATTR_SORT)
    public final String sort;

    @JsonProperty(ATTR_CONTENT)
    public final List<T> content;

    @JsonCreator
    public Page(
            @JsonProperty(ATTR_NUMBER_OF_PAGES) final Integer numberOfPages,
            @JsonProperty(ATTR_PAGE_NUMBER) final Integer pageNumber,
            @JsonProperty(ATTR_SORT) final String sort,
            @JsonProperty(ATTR_CONTENT) final Collection<T> content) {

        this.numberOfPages = numberOfPages;
        this.pageNumber = pageNumber;
        this.content = Utils.immutableListOf(content);
        this.pageSize = content.size();
        this.sort = sort;
    }

    public Integer getNumberOfPages() {
        return this.numberOfPages;
    }

    public Integer getPageNumber() {
        return this.pageNumber;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public Collection<T> getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        return "Page [numberOfPages=" + this.numberOfPages + ", pageNumber=" + this.pageNumber + ", pageSize="
                + this.pageSize
                + ", sort=" + this.sort + ", content=" + this.content + "]";
    }

}