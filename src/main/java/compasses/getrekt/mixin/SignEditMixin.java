/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package compasses.getrekt.mixin;

import compasses.getrekt.callbacks.SignEditCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SignBlockEntity.class)
public abstract class SignEditMixin extends BlockEntity {
	private SignEditMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Shadow
	public abstract SignText getText(boolean bl);

	@Inject(
			method = "updateSignText(Lnet/minecraft/world/entity/player/Player;ZLjava/util/List;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/entity/SignBlockEntity;updateText(Ljava/util/function/UnaryOperator;Z)Z"
			),
			cancellable = true
	)
	private void getrekt$preSignTextUpdated(Player updater, boolean isFront, List<FilteredText> lines, CallbackInfo ci) {
		SignEditCallback.INSTANCE.invoke(
				level,

				getBlockPos(),
				getBlockState(),

				updater,
				getText(isFront).getMessages(false),
				lines,

				ci
		);
	}
}
