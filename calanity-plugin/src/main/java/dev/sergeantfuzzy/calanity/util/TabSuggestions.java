package dev.sergeantfuzzy.calanity.util;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.api.clans.ClanMember;
import dev.sergeantfuzzy.calanity.domain.classes.AbilityManager;
import dev.sergeantfuzzy.calanity.domain.classes.ClassManager;
import dev.sergeantfuzzy.calanity.domain.clans.ClanManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/** Shared helpers for command tab-completion. */
public final class TabSuggestions {

    private TabSuggestions() {
    }

    public static List<String> onlinePlayers(String prefix) {
        String search = normalize(prefix);
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(Objects::nonNull)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(search))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(toList());
    }

    public static List<String> clanNames(ClanManager clanManager, String prefix) {
        String search = normalize(prefix);
        return clanManager.all().stream()
                .flatMap(clan -> Stream.of(clan.displayName(), clan.id()))
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(search))
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(toList());
    }

    public static List<String> classIds(ClassManager classManager, String prefix) {
        String search = normalize(prefix);
        return classManager.all().stream()
                .map(clazz -> clazz.id())
                .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(search))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(toList());
    }

    public static List<String> classDisplayNames(ClassManager classManager, String prefix) {
        String search = normalize(prefix);
        return classManager.all().stream()
                .map(clazz -> clazz.displayName())
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(search))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(toList());
    }

    public static List<String> passiveIds(ClassManager classManager, String prefix) {
        String search = normalize(prefix);
        return classManager.all().stream()
                .flatMap(clazz -> clazz.passives().stream())
                .map(passive -> passive.id())
                .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(search))
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(toList());
    }

    public static List<String> abilityIds(AbilityManager abilityManager, String prefix) {
        String search = normalize(prefix);
        return abilityManager.all().stream()
                .map(ability -> ability.id())
                .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(search))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(toList());
    }

    public static List<String> clanMemberNames(Clan clan, String prefix) {
        if (clan == null) {
            return List.of();
        }
        String search = normalize(prefix);
        return clan.members().stream()
                .map(ClanMember::name)
                .filter(Objects::nonNull)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(search))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(toList());
    }

    public static List<String> integers(int minInclusive, int maxInclusive, String prefix) {
        String search = normalize(prefix);
        return Stream.iterate(minInclusive, i -> i + 1)
                .limit(maxInclusive - minInclusive + 1L)
                .map(String::valueOf)
                .filter(value -> value.startsWith(search))
                .collect(toList());
    }

    private static String normalize(String input) {
        return input == null ? "" : input.toLowerCase(Locale.ROOT);
    }
}
