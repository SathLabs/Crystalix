package dev.satherov.crystalix.network;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.handling.SLPayload;
import dev.satherov.sathlib.network.handling.ServerPayloadProvider;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@NothingNull
public record SetColorPayload(int color) implements SLPayload<SetColorPayload> {
    
    private static final Type<SetColorPayload> TYPE = SLPayload.type(Crystalix.id("set_color"));
    
    private static final StreamCodec<RegistryFriendlyByteBuf, SetColorPayload> STREAM_CODEC = SLPayload.codec(SetColorPayload::encode, SetColorPayload::new);
    
    @Override
    public CustomPacketPayload.Type<SetColorPayload> type() {
        return SetColorPayload.TYPE;
    }
    
    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.color);
    }
    
    private SetColorPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readInt());
    }
    
    public static final class Provider implements ServerPayloadProvider<SetColorPayload> {
        
        @Override
        public void handle(SetColorPayload payload, IPayloadContext context, ServerPlayer player) {
            final ItemStack stack = CrystalixWandItem.find(player);
            if (stack.isEmpty() || !CXProperties.isEditable(CXProperties.COLOR)) return;
            
            CXProperties.COLOR.applyValueItem(payload.color(), stack, CXRegistry.CRYSTALIX_BLOCK.get().defaultBlockState());
            player.getInventory().setChanged();
        }
        
        @Override
        public CustomPacketPayload.Type<SetColorPayload> type() {
            return SetColorPayload.TYPE;
        }
        
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SetColorPayload> codec() {
            return SetColorPayload.STREAM_CODEC;
        }
    }
}
