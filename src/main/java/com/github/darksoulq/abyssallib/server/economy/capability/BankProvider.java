package com.github.darksoulq.abyssallib.server.economy.capability;

import com.github.darksoulq.abyssallib.server.economy.Account;
import net.kyori.adventure.key.Key;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BankProvider {
    CompletableFuture<Account> createBank(Key id, String name, UUID owner);
    CompletableFuture<Account> bank(Key id);
    CompletableFuture<Boolean> isMember(Key bankId, UUID player);
    CompletableFuture<Collection<UUID>> members(Key bankId);
    CompletableFuture<Boolean> addMember(Key bankId, UUID player, Set<BankFlag> flags);
    CompletableFuture<Boolean> removeMember(Key bankId, UUID player);
    CompletableFuture<Set<BankFlag>> flags(Key bankId, UUID player);
    CompletableFuture<Boolean> setFlags(Key bankId, UUID player, Set<BankFlag> flags);
    CompletableFuture<Boolean> updateFlag(Key bankId, UUID player, BankFlag flag, boolean state);
    CompletableFuture<Boolean> transferOwnership(Key bankId, UUID newOwner);
    CompletableFuture<Boolean> rename(Key bankId, String newName);
    CompletableFuture<Integer> memberLimit(Key bankId);
    CompletableFuture<Void> setMemberLimit(Key bankId, int limit);

    enum BankFlag {
        DEPOSIT,
        WITHDRAW,
        MANAGE_MEMBERS,
        MANAGE_FLAGS,
        MANAGE_SETTINGS,
        VIEW_HISTORY,
        FREEZE_ACCOUNT,
        DELETE_BANK
    }
}