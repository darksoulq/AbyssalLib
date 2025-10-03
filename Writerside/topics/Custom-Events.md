# Custom Events

> AbyssalLib comes with a few custom events, some are cancellable.

<table>
<tr>
<td>Name</td>
<td>When it is fired</td>
</tr>
<tr>
<td>BlockBrokenEvent</td>
<td>Fired when a <code>CustomBlock</code> is broken.</td>
</tr>
<tr>
<td>BlockPlacedEvent</td>
<td>Fired when a <code>CustomBlock</code> is placed.</td>
</tr>
<tr>
<td>BlockInteractionEvent</td>
<td>Fired when a <code>CustomBlock</code> is interacted with (right or left click).</td>
</tr>
<tr>
<td>EntityDeathEvent</td>
<td>Fired when a custom <code>Entity</code> dies.</td>
</tr>
<tr>
<td>EntityLoadEvent</td>
<td>Fired when a custom <code>Entity</code> is loaded into the world.</td>
</tr>
<tr>
<td>EntitySpawnEvent</td>
<td>Fired when a custom <code>Entity</code> is spawned into the world.</td>
</tr>
<tr>
<td>PacketSendEvent</td>
<td>Fired when a packet is sent by the server.</td>
</tr>
<tr>
<td>PacketReceiveEvent</td>
<td>Fired when a packet is received by the server.</td>
</tr>
<tr>
<td>RegistryApplyEvent</td>
<td>Fired when a <code>DeferredRegistry</code> is applied.</td>
</tr>
<tr>
<td>ResourcePackGenerateEvent</td>
<td>Fired when a resource pack is generated (<code>ResourcePack#compile</code>, which is auto called by <code>ResourcePack#register</code>).</td>
</tr>
</table>