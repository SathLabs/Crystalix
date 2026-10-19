package dev.satherov.crystalix.network;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.common.properties.BlockItemProperty;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.handling.SLPayload;
import dev.satherov.sathlib.network.handling.ServerPayloadProvider;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

@NothingNull
public record CyclePropertyPayload(Identifier identifier, boolean dir) implements SLPayload<CyclePropertyPayload> {
    
    private static final Type<CyclePropertyPayload> TYPE = SLPayload.type(Crystalix.id("cycle_property"));
    
    private static final StreamCodec<RegistryFriendlyByteBuf, CyclePropertyPayload> STREAM_CODEC = SLPayload.codec(CyclePropertyPayload::encode, CyclePropertyPayload::new);
    
    @Override
    public CustomPacketPayload.Type<CyclePropertyPayload> type() {
        return CyclePropertyPayload.TYPE;
    }
    
    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeIdentifier(this.identifier);
        buf.writeBoolean(this.dir);
    }
    
    private CyclePropertyPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readIdentifier(), buf.readBoolean());
    }
    
    public static final class Provider implements ServerPayloadProvider<CyclePropertyPayload> {
        
        @Override
        public void handle(CyclePropertyPayload payload, IPayloadContext context, ServerPlayer player) {
            final ItemStack stack = CrystalixWandItem.find(player);
            if (stack.isEmpty()) return;
            
            BlockItemProperty<?> property = Provider.getProperty(payload.identifier());
            if (property == null || !CXProperties.isEditable(property)) return;
            
            property.cycleItem(payload.dir(), stack, CXRegistry.CRYSTALIX_BLOCK.get().defaultBlockState());
            player.getInventory().setChanged();
        }
        
        @Override
        public CustomPacketPayload.Type<CyclePropertyPayload> type() {
            return CyclePropertyPayload.TYPE;
        }
        
        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, CyclePropertyPayload> codec() {
            return CyclePropertyPayload.STREAM_CODEC;
        }
        
        private static @Nullable BlockItemProperty<?> getProperty(Identifier id) {
            if (CXProperties.APPLY_MODE.getIdentifier().equals(id)) return CXProperties.APPLY_MODE;
            return CXProperties.CONTAINER.getProperty(id);
        }
    }
}
