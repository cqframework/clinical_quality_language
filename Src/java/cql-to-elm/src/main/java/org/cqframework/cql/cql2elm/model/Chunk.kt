package org.cqframework.cql.cql2elm.model

import org.antlr.v4.runtime.misc.Interval
import org.hl7.elm.r1.Element

/*
If a chunk is a header chunk then the narrative construction can choose to "trim" the content
to avoid the inclusion of whitespace between definitions in the narrative output.
This does have the side effect of needing to reconstitute whitespace when reconstructing the
entire narrative from the source annotations, but that isn't a use case we're optimizing for,
we're focusing on providing minimal required narrative per definition.
 */
class Chunk(var interval: Interval, val isHeaderChunk: Boolean) {
    constructor(interval: Interval) : this(interval, false)

    var element: Element? = null

    private val chunks: MutableList<Chunk> = ArrayList()

    private fun ensureChunks() {
        if (chunks.isEmpty()) {
            chunks.add(Chunk(interval))
        }
    }

    fun getChunks(): List<Chunk> {
        ensureChunks()
        return chunks
    }

    fun hasChunks(): Boolean {
        return chunks.isNotEmpty()
    }

    fun addChunk(chunk: Chunk) {
        require(chunk.interval.a >= interval.a && chunk.interval.b <= interval.b) {
            "Child chunk cannot be added because it is not contained within the parent chunk."
        }

        ensureChunks()
        var chunkIndex = -1
        var targetChunk: Chunk? = null
        for (i in chunks.indices) {
            if (
                chunk.interval.a >= chunks[i].interval.a && chunk.interval.a <= chunks[i].interval.b
            ) {
                chunkIndex = i
                targetChunk = chunks[chunkIndex]
                break
            }
        }

        checkNotNull(targetChunk) { "Unable to find target chunk for insertion." }

        if (chunk.interval.a == targetChunk.interval.a) {
            // the chunk being added starts the targetChunk
            // insert the chunk at the targetChunk's index
            // update the targetChunk's interval start to be the chunk's interval end + 1
            chunks.add(chunkIndex, chunk)
            chunkIndex++
            val newA = chunk.interval.b + 1
            while (newA > chunks[chunkIndex].interval.b) {
                chunks.removeAt(chunkIndex)
                if (chunkIndex >= chunks.size) {
                    break
                }
            }
            if (chunkIndex < chunks.size) {
                chunks[chunkIndex].interval = Interval(newA, chunks[chunkIndex].interval.b)
            }
        } else {
            val newB = chunk.interval.a - 1
            val newA = chunk.interval.b + 1
            val oldA = chunks[chunkIndex].interval.a
            val oldB = chunks[chunkIndex].interval.b
            chunks[chunkIndex].interval = Interval(oldA, newB)
            chunkIndex++
            chunks.add(chunkIndex, chunk)
            chunkIndex++
            if (newA <= oldB) {
                chunks.add(chunkIndex, Chunk(Interval(newA, oldB)))
            }
        }
    }
}
