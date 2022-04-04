package uk.debb.togglesprint.mixin;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import uk.debb.togglesprint.ToggleSprint;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {
    /**
     * @author DragonEggBedrockBreaking
     * @param sprintKey the sprint key that the user has bound
     * @return whether or not the user should try to sprint
     */
    @Redirect(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/options/KeyBinding;isPressed()Z"
        )
    )
    private boolean alwaysPressed(KeyBinding sprintKey) {
        return ToggleSprint.toggleSprintEnabled ? true : sprintKey.isPressed();
    }
}
