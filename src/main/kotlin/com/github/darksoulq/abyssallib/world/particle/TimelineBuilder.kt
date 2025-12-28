package com.github.darksoulq.abyssallib.world.particle

import com.github.darksoulq.abyssallib.world.particle.timeline.Timeline
import org.bukkit.util.Vector

fun timeline(init: TimelineBuilder.() -> Unit): Timeline {
    val dsl = TimelineBuilder()
    dsl.init()
    return dsl.build()
}

class TimelineBuilder {
    private val builder = Timeline.builder()
    var duration: Long
        get() = 0L
        set(value) {
            builder.duration(value)
        }
    var loop: Boolean
        get() = true
        set(value) {
            builder.loop(value)
        }

    fun keyframe(start: Long, duration: Long, block: (Vector, Long) -> Vector) {
        builder.add(start, duration) { input, relativeTick ->
            block(input, relativeTick)
        }
    }

    fun keyframe(start: Long, duration: Long, transformer: Transformer) {
        builder.add(start, duration, transformer)
    }

    fun build(): Timeline = builder.build()
}