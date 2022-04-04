package uk.debb.togglesprint.mixin;

import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.debb.togglesprint.ToggleSprint;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding {
    /**
     * @author DragonEggBedrockBreaking
     * @reason toggles the mod's feature when the i key is pressed 
     * @param keyCode the code of the key that was pressed
     * @param ci the callback info
     */
    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onIPressed(int keyCode, CallbackInfo ci) {
        if (keyCode == 23) {
            ToggleSprint.toggleSprintEnabled = !ToggleSprint.toggleSprintEnabled;
        }
    }
}
