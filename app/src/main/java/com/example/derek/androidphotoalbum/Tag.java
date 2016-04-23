package com.example.derek.androidphotoalbum;

import java.io.Serializable;

/**
 * Created by Derek on 4/11/16.
 */
public class Tag implements Serializable {

    static final long serialVersionUID = 19L;

    public enum TagType {
        PERSON, LOCATION
    }

    private TagType tagType;
    private String value;

    public Tag(TagType tagType, String value) {
        this.tagType = tagType;
        this.value = value;
    }

    public TagType getTagType() {
        return this.tagType;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Tag) {
            Tag o = (Tag)obj;
            return this.value.equals(o.value) && this.tagType == o.tagType;
        }
        return false;
    }

    public String toString() {
        return this.tagType.toString() + ":" + this.value;
    }

}
