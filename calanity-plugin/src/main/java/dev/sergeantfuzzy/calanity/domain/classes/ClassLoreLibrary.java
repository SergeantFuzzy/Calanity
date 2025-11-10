package dev.sergeantfuzzy.calanity.domain.classes;

import java.util.List;
import java.util.Map;

/** Descriptive metadata for built-in classes used in menus/tooltips. */
public final class ClassLoreLibrary {

    private static final Map<String, ClassLore> LORES = Map.of(
            "elfin", new ClassLore(
                    "Healer",
                    "Guardian spirits mend wounds and revive allies mid-battle.",
                    List.of(
                            "Chain heal pulses scale with clan synergy.",
                            "Revive ability restores lost inventory items.",
                            "Pair with Bargainers for sustain-heavy pushes.")),
            "panzer", new ClassLore(
                    "Vanguard",
                    "Kinetic shields and fortification tactics lock down choke points.",
                    List.of(
                            "Shield ability pushes enemies away when you crouch.",
                            "Stack defense stats to maximize damage reduction.",
                            "Great frontline partner for Craftists supplying gear.")),
            "bargainer", new ClassLore(
                    "Damage Dealer",
                    "Trades life-force for explosive burst windows and retaliation strikes.",
                    List.of(
                            "Thunder Strike auto-zaps attackers every 25s.",
                            "Passive lifesteal lets you duel without potions.",
                            "Keep a bow ready—abilities amplify projectile hits.")),
            "craftist", new ClassLore(
                    "Support Crafter",
                    "Master artisans duplicating gear, reclaiming mats, and forging relics.",
                    List.of(
                            "Double Craft Passive refunds extra outputs randomly.",
                            "Item Return salvages failed recipes into shards.",
                            "Magical Artifacts grant situational buffs by tool type.")),
            "elementalist", new ClassLore(
                    "Battle Mage",
                    "Rotates elemental combos for control, sustain, and burst.",
                    List.of(
                            "Swap abilities mid-fight to counter enemy armor.",
                            "Water surge grants brief heals before unleashing fire.",
                            "Future add-ons can expand with new elements.")));

    private ClassLoreLibrary() {
    }

    public static ClassLore describe(String classId) {
        return LORES.getOrDefault(classId.toLowerCase(), ClassLore.empty());
    }

    public record ClassLore(String role, String description, List<String> tips) {
        private static ClassLore empty() {
            return new ClassLore("Adventurer", "A customizable path awaiting addon designers.", List.of(
                    "Use /class help <ability> to learn combos.",
                    "Bind abilities with /class bind <ability> <slot>."));
        }
    }
}
