# ResourcePack

AbyssalLib comes with two ways of hosting its Resource Packs (Builtin and [ResourcePackManager](https://www.spigotmc.org/resources/resource-pack-manager.118574/) Integration)

### Built in Hosting
To enable builtin hosting open <code>/config/abyssallib/config.yml</code> and set the values:
<code-block lang="YAML">
resource_pack:
  enabled: true
  host: "your-numerical-ip"
  port: your-port
</code-block>

### RSPM Integration
In case [ResourcePackManager](https://www.spigotmc.org/resources/resource-pack-manager.118574/) is installed AbyssalLib will automatically utilize it for ResourcePack hosting.