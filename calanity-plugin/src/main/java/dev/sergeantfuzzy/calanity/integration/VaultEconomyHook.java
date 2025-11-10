package dev.sergeantfuzzy.calanity.integration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Optional;

/** Wraps Vault's Economy provider. */
public final class VaultEconomyHook {

    private Economy economy;

    public void hook() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            this.economy = rsp.getProvider();
        }
    }

    public Optional<Economy> economy() {
        return Optional.ofNullable(economy);
    }
}
