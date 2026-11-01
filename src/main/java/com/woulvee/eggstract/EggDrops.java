package com.woulvee.eggstract;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

@EventBusSubscriber(modid = Eggstract.MODID)
public final class EggDrops {
    private EggDrops() {
    }

    @SubscribeEvent
    static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target.level() instanceof ServerLevel level) || !(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        int enchantmentLevel = Eggstracting.holder(level.registryAccess())
                .map(enchantment -> player.getMainHandItem().getEnchantmentLevel(enchantment))
                .orElse(0);
        if (enchantmentLevel <= 0
                || Config.isBlacklisted(target.getType())
                || !Config.roll(target.getRandom(), Config.dropChance(enchantmentLevel))) {
            return;
        }

        SpawnEggItem.byId(target.getType()).ifPresent(egg -> event.getDrops().add(
                new ItemEntity(level, target.getX(), target.getY(), target.getZ(), new ItemStack(egg))));
    }
}
