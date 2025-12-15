# Getting Started
<warning>
AbyssalLib is in Development, not all features are fleshed out, there may be bugs and inconsistencies aswell as major changes in some parts.
</warning>

AbyssalLib eases creation of content in minecraft plugins and is developed to provide as many useful features as possible (as well as provide easy ways to integrate with other plugins using AbyssalLib)

<procedure title="Features">
<step>
Item API: an easy to use Item API for making items with custom behaviours (e.g when hitting entity, breaking block etc), aswell as an easy to use Component system which allows custom components aswell! (layer for Data Component API)
</step>
<step>
Gui API: AbyssalLib comes with a Builder-Style GUI Api (also an `extend`able AbstractGui class) which allows easy creation of GUIs, the basis of the GUI API are of "Elements" and "Layers" (These are VERY simple)
</step>
<step>
Block API: AbyssalLib also comes with a Block API for creation of custom blocks which can be ticked and store data (using a custom data object which is entirely optional)
<warning>Block API is Highly Experimental and can be entirely rewritten ANY time, use with care!</warning>
</step>
<step>
Particle API: an easy to use Builder-Style Particle API which also supports ItemStacks as particles!
</step>
<step>
And MUCH more!
</step>
</procedure>

### Adding as a Dependency
Follow these steps to add AbyssalLib as a dependency for your project:

- Adding the repository:
<tabs>
<tab title="Gradle">
<code-block lang="Gradle">
repositories {
    maven { url 'https://jitpack.io' }
}
</code-block>
</tab>
<tab title="Maven">
<code-block lang="XML">
&lt;repositories&gt;
    &lt;repository&gt;
        &lt;id&gt;jitpack.io&lt;/id&gt;
        &lt;url&gt;https://jitpack.io&lt;/url&gt;
    &lt;/repository&gt;
&lt;/repositories&gt;
</code-block>
</tab>
</tabs>

- Adding the dependency:
<tabs>
<tab title="Gradle">
<code-block lang="Gradle">
dependencies {
    implementation('com.github.darksoulq:AbyssalLib:&lt;version&gt;')
}
</code-block>
</tab>
<tab title="Maven">
<code-block lang="XML">
&lt;dependency&gt;
    &lt;groupId&gt;com.github.darksoulq&lt;/groupId&gt;
    &lt;artifactId&gt;AbyssalLib&lt;/artifactId&gt;
    &lt;version&gt;version&lt;/version&gt;
&lt;/dependency&gt;
</code-block>
</tab>
</tabs>

- Adding into paper-plugin.yml:
<code-block lang="YAML">
dependencies:
  server:
    AbyssalLib:
      required: true
      load: BEFORE
  bootstrap:
    AbyssalLib:
      required: true
      load: BEFORE
</code-block>

> It is recommended you use the latest version of AbyssalLib as i do not provide much support for older versions!
> AbyssalLib should be added as BootStrap dependency if you want to load your own datapacks and register DamageTypes.