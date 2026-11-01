package com.woulvee.eggstract.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.woulvee.eggstract.Config;
import com.woulvee.eggstract.Eggstracting;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootContext;

@Mixin(VillagerTrade.class)
public abstract class VillagerTradeMixin {
    @Inject(method = "getOffer", at = @At("RETURN"), cancellable = true)
    private void eggstract$sellEggstracting(LootContext context, CallbackInfoReturnable<MerchantOffer> cir) {
        MerchantOffer offer = cir.getReturnValue();
        RandomSource random = context.getRandom();
        if (offer == null || !offer.getResult().is(Items.ENCHANTED_BOOK) || !Config.TRADING.roll(random)) {
            return;
        }

        Eggstracting.holder(context.getLevel().registryAccess()).ifPresent(enchantment -> {
            int level = Eggstracting.randomLevel(enchantment, random);
            // Same formula as vanilla book trades, doubled like treasure enchantments
            int price = Math.min((2 + random.nextInt(5 + level * 10) + 3 * level) * 2, 64);
            ItemCost cost = offer.getItemCostA();
            cir.setReturnValue(new MerchantOffer(
                    new ItemCost(cost.item(), price, cost.components()),
                    offer.getItemCostB(),
                    Eggstracting.book(enchantment, level),
                    offer.getMaxUses(),
                    offer.getXp(),
                    offer.getPriceMultiplier()));
        });
    }
}
