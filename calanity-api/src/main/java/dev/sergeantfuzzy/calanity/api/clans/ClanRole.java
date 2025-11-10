package dev.sergeantfuzzy.calanity.api.clans;

/** Clan hierarchy used for permissions. */
public enum ClanRole {
    LEADER,
    CAPTAIN,
    MEMBER;

    public boolean atLeast(ClanRole other) {
        return this.ordinal() <= other.ordinal();
    }
}
