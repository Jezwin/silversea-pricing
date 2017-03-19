package com.silversea.aem.helper;

import com.day.cq.tagging.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aurelienolivier on 19/03/2017.
 */
public class TagHelper {

    /**
     * Get a list of tag ids, including all the ancestors of the current <code>tag</code>
     * @param tag
     * @return tag ids, including current <code>tag</code> and all ancestors tag ids
     */
    public static List<String> getTagIdsWithParents(final Tag tag) {
        List<String> tagIds = new ArrayList<>();

        tagIds.add(tag.getTagID());

        Tag parent = tag;

        while (parent.getParent() != null) {
            parent = parent.getParent();
            tagIds.add(parent.getTagID());
        }

        return tagIds;
    }
}
