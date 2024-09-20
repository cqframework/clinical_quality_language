package org.cqframework.cql.cql2elm.model;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.kotlinruntime.misc.Interval;
import org.hl7.elm.r1.Element;

/**
 * Created by Bryn on 6/14/2017.
 */
public class Chunk {
    private Interval interval;

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public Chunk withInterval(Interval interval) {
        setInterval(interval);
        return this;
    }

    /*
    If a chunk is a header chunk then the narrative construction can choose to "trim" the content
    to avoid the inclusion of whitespace between definitions in the narrative output.
    This does have the side-affect of needing to reconstitute whitespace when reconstructing the
    entire narrative from the source annotations, but that isn't a use case we're optimizing for,
    we're focusing on providing minimal required narrative per definition.
     */
    private boolean headerChunk = false;

    public boolean isHeaderChunk() {
        return this.headerChunk;
    }

    public void setHeaderChunk(boolean isHeaderChunk) {
        this.headerChunk = isHeaderChunk;
    }

    public Chunk withIsHeaderChunk(boolean isHeaderChunk) {
        setHeaderChunk(isHeaderChunk);
        return this;
    }

    private Element element;

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Chunk withElement(Element element) {
        setElement(element);
        return this;
    }

    private void ensureChunks() {
        if (chunks == null) {
            chunks = new ArrayList<Chunk>();
            chunks.add(new Chunk().withInterval(interval));
        }
    }

    private List<Chunk> chunks;

    public Iterable<Chunk> getChunks() {
        ensureChunks();
        return chunks;
    }

    public boolean hasChunks() {
        return chunks != null;
    }

    public void addChunk(Chunk chunk) {
        if (chunk.getInterval().getA()< interval.getA()|| chunk.getInterval().getB() > interval.getB()) {
            throw new IllegalArgumentException(
                    "Child chunk cannot be added because it is not contained within the parent chunk.");
        }

        ensureChunks();
        int chunkIndex = -1;
        Chunk targetChunk = null;
        for (int i = 0; i < chunks.size(); i++) {
            if (chunk.getInterval().getA()>= chunks.get(i).getInterval().getA()
                    && chunk.getInterval().getA()<= chunks.get(i).getInterval().getB()) {
                chunkIndex = i;
                targetChunk = chunks.get(chunkIndex);
                break;
            }
        }

        if (chunk.getInterval().getA()== targetChunk.getInterval().getA()) {
            // the chunk being added starts the targetChunk
            // insert the chunk at the targetChunk's index
            // update the targetChunk's interval start to be the chunk's interval end + 1
            chunks.add(chunkIndex, chunk);
            chunkIndex++;
            int newA = chunk.getInterval().getB() + 1;
            while (newA > chunks.get(chunkIndex).getInterval().getB()) {
                chunks.remove(chunkIndex);
                if (chunkIndex >= chunks.size()) {
                    break;
                }
            }
            if (chunkIndex < chunks.size()) {
                chunks.get(chunkIndex)
                        .setInterval(new Interval(newA, chunks.get(chunkIndex).getInterval().getB()));
            }
        } else {
            int newB = chunk.getInterval().getA()- 1;
            int newA = chunk.getInterval().getB() + 1;
            int oldA = chunks.get(chunkIndex).getInterval().getA();
            int oldB = chunks.get(chunkIndex).getInterval().getB();
            chunks.get(chunkIndex).setInterval(new Interval(oldA, newB));
            chunkIndex++;
            chunks.add(chunkIndex, chunk);
            chunkIndex++;
            if (newA <= oldB) {
                chunks.add(chunkIndex, new Chunk().withInterval(new Interval(newA, oldB)));
            }
        }
    }
}
