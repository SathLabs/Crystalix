package dev.satherov.crystalix.network;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.block.CrystalixGlassBlockEntity;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.handling.SLPayload;
import dev.satherov.sathlib.network.handling.ServerPayloadProvider;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

@NothingNull
public record SwapPropertiesPayload(BlockPos pos) implements SLPayload<SwapPropertiesPayload> {
    
    private static final CustomPacketPayload.Type<SwapPropertiesPayload> TYPE = SLPayload.type(Crystalix.id("swap_properties"));
    
    private static final StreamCodec<RegistryFriendlyByteBuf, SwapPropertiesPayload> STREAM_CODEC = SLPayload.codec(SwapPropertiesPayload::encode, SwapPropertiesPayload::new);
    
    @Override
    public CustomPacketPayload.Type<SwapPropertiesPayload> type() {
        return SwapPropertiesPayload.TYPE;
    }
    
    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
    
    private SwapPropertiesPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }
    
    public static final class Provider implements ServerPayloadProvider<SwapPropertiesPayload> {
        
        @Override
        public void handle(SwapPropertiesPayload payload, IPayloadContext context, ServerPlayer player) {
            final ItemStack stack = CrystalixWandItem.find(player);
            if (stack.isEmpty()) return;
            
            final ServerLevel level = player.level();
            final BlockPos pos = payload.pos();
            final BlockState state = level.getBlockState(pos);
            final CrystalixGlassBlockEntity entity = CXRegistry.GLASS_BLOCK_ENTITY.get().getBlockEntity(level, pos);
            if (!state.is(CXRegistry.CRYSTALIX_BLOCK_TAG) || entity == null) return;
            
            CXProperties.editable().applyToItem(stack, state, entity);
            player.getInventory().setChanged();
        }
        
        @Override
        public CustomPacketPayload.Type<SwapPropertiesPayload> type() {
            return SwapPropertiesPayload.TYPE;
        }
        
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SwapPropertiesPayload> codec() {
            return SwapPropertiesPayload.STREAM_CODEC;
        }
    }
}
