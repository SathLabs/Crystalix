package dev.satherov.crystalix.network;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.common.properties.GlassMaterial;
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
public record SetGlassMaterialPayload(GlassMaterial material) implements SLPayload<SetGlassMaterialPayload> {
    
    private static final Type<SetGlassMaterialPayload> TYPE = SLPayload.type(Crystalix.id("set_glass_material"));
    
    private static final StreamCodec<RegistryFriendlyByteBuf, SetGlassMaterialPayload> STREAM_CODEC = SLPayload.codec(SetGlassMaterialPayload::encode, SetGlassMaterialPayload::new);
    
    @Override
    public CustomPacketPayload.Type<SetGlassMaterialPayload> type() {
        return SetGlassMaterialPayload.TYPE;
    }
    
    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        GlassMaterial.STREAM_CODEC.encode(buf, this.material);
    }
    
    private SetGlassMaterialPayload(RegistryFriendlyByteBuf buf) {
        this(GlassMaterial.STREAM_CODEC.decode(buf));
    }
    
    public static final class Provider implements ServerPayloadProvider<SetGlassMaterialPayload> {
        
        @Override
        public void handle(SetGlassMaterialPayload payload, IPayloadContext context, ServerPlayer player) {
            ItemStack stack = CrystalixWandItem.find(player);
            if (stack.isEmpty() || !CXProperties.isEditable(CXProperties.MATERIAL)) return;
            
            stack.set(CXRegistry.MATERIAL, payload.material());
            player.getInventory().setChanged();
        }
        
        @Override
        public CustomPacketPayload.Type<SetGlassMaterialPayload> type() {
            return SetGlassMaterialPayload.TYPE;
        }
        
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SetGlassMaterialPayload> codec() {
            return SetGlassMaterialPayload.STREAM_CODEC;
        }
    }
}
