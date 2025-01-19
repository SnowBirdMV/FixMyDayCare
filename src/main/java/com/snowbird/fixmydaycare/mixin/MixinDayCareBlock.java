package com.snowbird.fixmydaycare.mixin;

import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.comm.packetHandlers.daycare.SendEntireDayCarePacket;
import com.pixelmonmod.pixelmon.api.util.helpers.NetworkHelper;
import com.pixelmonmod.pixelmon.blocks.daycare.DayCareBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A Mixin that injects into DayCareBlock#use(...) to send the entire
 * DayCare data to the client whenever the block is successfully used.
 */
@Mixin(value = DayCareBlock.class, remap = false) // "remap=false" because DayCareBlock is a mod class, not vanilla.
public abstract class MixinDayCareBlock {

	/**
	 * Inject after the DayCareBlock#use(...) method returns. If it
	 * returned SUCCESS on the server side, we send the entire day-care info.
	 */
	@Inject(
		method = "use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
		at = @At("TAIL"),
		cancellable = true,
		remap = true
	)
	private void afterUse(
		BlockState state,
		Level world,
		BlockPos pos,
		Player player,
		InteractionHand hand,
		BlockHitResult hit,
		CallbackInfoReturnable<InteractionResult> cir
	) {
		// Check the return value of "use". If it's SUCCESS on the server, we proceed.
		InteractionResult result = cir.getReturnValue();
		if (result == InteractionResult.SUCCESS && !world.isClientSide && player instanceof ServerPlayer serverPlayer) {

			// Double-check Pixelmon conditions so we only send if the real code allowed the GUI to open:
			if (!PixelmonConfigProxy.getBreeding().isAllowBreeding()) {
				return;
			}
			if (BattleRegistry.getBattle(player) != null) {
				return;
			}

			// At this point, the container has been opened. Let's send the data:
			PlayerPartyStorage party = StorageProxy.getPartyNow(serverPlayer);
			SendEntireDayCarePacket packet = new SendEntireDayCarePacket(party.getDayCare());

			// Finally, send that packet to the player:
			NetworkHelper.sendPacket(packet, serverPlayer);
		}
	}
}
