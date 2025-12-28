import com.github.darksoulq.abyssallib.world.particle.*
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.function.BooleanSupplier
import java.util.function.Supplier

fun particles(block: ParticlesBuilder.() -> Unit): Particles {
    val dsl = ParticlesBuilder()
    dsl.block()
    return dsl.build()
}

@DslMarker
annotation class ParticlesDsl

@ParticlesDsl
class ParticlesBuilder {

    private var origin: Supplier<Location>? = null
    private var generator: Generator? = null
    private var renderer: ParticleRenderer? = null
    private val transformers = mutableListOf<Transformer>()
    private var interval: Long = 1
    private var duration: Long = -1
    private var smoothen: Boolean = false
    private var viewers: Supplier<List<Player>>? = null
    private var cancelIf: BooleanSupplier? = null

    fun origin(location: Location) {
        origin = Supplier { location }
    }
    fun origin(block: () -> Location) {
        origin = Supplier { block() }
    }

    fun shape(generator: Generator) {
        this.generator = generator
    }

    fun render(renderer: ParticleRenderer) {
        this.renderer = renderer
    }

    fun transform(block: (Vector, Long) -> Vector) {
        transformers += Transformer { v, tick -> block(v, tick) }
    }
    fun rotate(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0) =
        transform { v, _ ->
            v.rotateAroundX(x)
                .rotateAroundY(y)
                .rotateAroundZ(z)
        }
    fun scale(scale: Double) =
        transform { v, _ -> v.multiply(scale) }
    fun offset(x: Double, y: Double, z: Double) =
        transform { v, _ -> v.add(Vector(x, y, z)) }

    fun interval(ticks: Long) {
        interval = ticks
    }
    fun duration(ticks: Long) {
        duration = ticks
    }
    fun smooth(smooth: Boolean) {
        smoothen = smooth
    }


    fun viewers(players: List<Player>) {
        viewers = Supplier { players }
    }
    fun viewers(block: () -> List<Player>) {
        viewers = Supplier { block() }
    }
    fun viewers(vararg players: Player) =
        viewers(players.toList())

    fun stopIf(block: () -> Boolean) {
        cancelIf = BooleanSupplier { block() }
    }

    fun build(): Particles {
        val b = Particles.builder()
        val o = origin ?: error("Particles origin not set")
        val g = generator ?: error("Particles generator not set")
        val r = renderer ?: error("Particles renderer not set")

        b.origin(o)
            .shape(g)
            .render(r)
            .interval(interval)
            .duration(duration)
            .smooth(smoothen)

        viewers?.let { b.viewers(it) }
        cancelIf?.let { b.stopIf(it) }
        transformers.forEach { b.transform(it) }

        return b.build()
    }
}
