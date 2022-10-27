package org.cqframework.cql.elm.tags;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TagSet extends ForwardingSet<TagInfo> {

    private Set<TagInfo> set;

    public TagSet() {
        this.set = new HashSet<>();
    }

    public TagSet(Set<TagInfo> set) {
        this.set = set;
    }

    @Override
    public Set<TagInfo> delegate() {
        return this.set;
    }

    public List<TagInfo> select(Predicate<TagInfo> predicate) {
        return this.delegate().stream().filter(predicate).collect(Collectors.toList());
    }
}
