package dev.sergeantfuzzy.calanity.storage.util;

import dev.sergeantfuzzy.calanity.api.clans.Clan;
import dev.sergeantfuzzy.calanity.api.clans.ClanMember;
import dev.sergeantfuzzy.calanity.api.clans.ClanRole;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** Serializes/deserializes clans for persistence. */
public final class ClanCodec {

    private ClanCodec() {
    }

    public static Map<String, Object> toMap(Clan clan) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", clan.id());
        data.put("displayName", clan.displayName());
        data.put("power", clan.power());
        List<Map<String, Object>> members = new ArrayList<>();
        for (ClanMember member : clan.members()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("uuid", member.uuid().toString());
            entry.put("name", member.name());
            entry.put("role", member.role().name());
            entry.put("joinedAt", member.joinedAt().toEpochMilli());
            members.add(entry);
        }
        data.put("members", members);
        return data;
    }

    @SuppressWarnings("unchecked")
    public static Clan fromMap(Map<String, Object> map) {
        String id = String.valueOf(map.get("id"));
        String displayName = String.valueOf(map.getOrDefault("displayName", id));
        int power = ((Number) map.getOrDefault("power", 0)).intValue();
        List<Map<String, Object>> memberList = (List<Map<String, Object>>) map.getOrDefault("members", List.of());
        List<ClanMember> members = new ArrayList<>();
        for (Map<String, Object> entry : memberList) {
            UUID uuid = UUID.fromString(String.valueOf(entry.get("uuid")));
            String name = String.valueOf(entry.getOrDefault("name", "Unknown"));
            String roleName = String.valueOf(entry.getOrDefault("role", "MEMBER"));
            ClanRole role = ClanRole.valueOf(roleName.toUpperCase(Locale.ROOT));
            long joined = ((Number) entry.getOrDefault("joinedAt", System.currentTimeMillis())).longValue();
            members.add(new ClanMember(uuid, name, role, Instant.ofEpochMilli(joined)));
        }
        return new Clan(id, displayName, power, members);
    }
}
