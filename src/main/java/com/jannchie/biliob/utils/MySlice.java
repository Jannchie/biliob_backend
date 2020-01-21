package com.jannchie.biliob.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Slice;

import java.util.List;

/**
 * @author jannchie
 */
public class MySlice<T> {
    private List<T> content;
    private Integer number;
    private Integer size;
    private Boolean last;
    private Boolean first;
    private Integer numberOfElements;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    @JsonIgnoreProperties({"sort", "pageable"})
    public MySlice(
            @JsonProperty("content") List<T> content,
            @JsonProperty("number") Integer number,
            @JsonProperty("size") Integer size,
            @JsonProperty("first") Boolean first,
            @JsonProperty("numberOfElements") Integer numberOfElements,
            @JsonProperty("last") Boolean last) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.last = last;
        this.first = first;
        this.numberOfElements = numberOfElements;
    }

    public MySlice(Slice<T> slice) {
        this.content = slice.getContent();
        this.number = slice.getNumber();
        this.size = slice.getSize();
        this.last = slice.isLast();
        this.first = slice.isFirst();
        this.numberOfElements = slice.getNumberOfElements();
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}
