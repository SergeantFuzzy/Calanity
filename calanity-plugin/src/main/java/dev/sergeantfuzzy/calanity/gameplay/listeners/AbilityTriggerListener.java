package dev.sergeantfuzzy.calanity.gameplay.listeners;

import dev.sergeantfuzzy.calanity.api.CalanityAPI;
import dev.sergeantfuzzy.calanity.api.classes.Ability;
import dev.sergeantfuzzy.calanity.api.events.AbilityTriggerEvent;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import dev.sergeantfuzzy.calanity.domain.classes.AbilityManager;
import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.domain.classes.DefaultAbilityContext;
import dev.sergeantfuzzy.calanity.domain.classes.binding.AbilityBindingService;
import dev.sergeantfuzzy.calanity.domain.classes.binding.CooldownService;
import dev.sergeantfuzzy.calanity.domain.stats.StatService;
import dev.sergeantfuzzy.calanity.integration.WorldGuardHook;
import dev.sergeantfuzzy.calanity.util.Messenger;
import dev.sergeantfuzzy.calanity.util.Sounds;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

/** Handles hotbar binding interaction events. */
public final class AbilityTriggerListener implements Listener {

    private final AbilityBindingService bindings;
    private final AbilityManager abilityManager;
    private final CooldownService cooldownService;
    private final ClanManager clanManager;
    private final ClassManager classManager;
    private final StatService statService;
    private final WorldGuardHook worldGuardHook;

    public AbilityTriggerListener(AbilityBindingService bindings,
                                  AbilityManager abilityManager,
                                  CooldownService cooldownService,
                                  ClanManager clanManager,
                                  ClassManager classManager,
                                  StatService statService,
                                  WorldGuardHook worldGuardHook) {
        this.bindings = bindings;
        this.abilityManager = abilityManager;
        this.cooldownService = cooldownService;
        this.clanManager = clanManager;
        this.classManager = classManager;
        this.statService = statService;
        this.worldGuardHook = worldGuardHook;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        var player = event.getPlayer();
        int slot = player.getInventory().getHeldItemSlot();
        Optional<String> abilityId = bindings.boundAbility(player.getUniqueId(), slot);
        if (abilityId.isEmpty()) {
            return;
        }
        abilityManager.find(abilityId.get()).ifPresent(ability -> {
            event.setCancelled(true);
            if (worldGuardHook.isProtected(player.getLocation())) {
                Messenger.warn(player, "Abilities are disabled in this region.");
                return;
            }
            if (cooldownService.isCooling(player.getUniqueId(), ability.id())) {
                Messenger.warn(player, "Ability cooling down...");
                return;
            }
            DefaultAbilityContext context = new DefaultAbilityContext(
                    player.getUniqueId(),
                    clanManager.findByMember(player.getUniqueId()),
                    classManager.get(player.getUniqueId()),
                    statService.get(player.getUniqueId()));
            AbilityTriggerEvent trigger = new AbilityTriggerEvent(CalanityAPI.provider(), ability, context, "hotbar");
            org.bukkit.Bukkit.getPluginManager().callEvent(trigger);
            if (trigger.isCancelled()) {
                return;
            }
            ability.execute(context);
            cooldownService.apply(player.getUniqueId(), ability.id(), ability.cooldown());
            Sounds.playSuccess(player);
        });
    }
}
