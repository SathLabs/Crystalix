package dev.satherov.crystalix.compat.framedblocks;

import dev.satherov.crystalix.common.block.CrystalixGlassBlock;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.common.properties.ApplyMode;
import dev.satherov.crystalix.common.properties.CrystalixModelState;
import dev.satherov.crystalix.common.properties.GhostState;
import dev.satherov.crystalix.common.properties.GlassMaterial;
import dev.satherov.crystalix.common.properties.LightState;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.neoforged.neoforge.transfer.access.ItemAccess;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jspecify.annotations.Nullable;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.TriggerRegistrar;
import io.github.xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.CamoMessageVerbosity;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;

@NothingNull
public class CrystalixGlassCamoContainerFactory extends AbstractBlockCamoContainerFactory<CrystalixGlassCamoContainer> {
    
    private static final MapCodec<CrystalixGlassCamoContainer> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockState.CODEC.fieldOf("state").forGetter(CrystalixGlassCamoContainer::getState),
            Codec.INT.fieldOf("color").forGetter(CrystalixGlassCamoContainer::getTintColor),
            Codec.BOOL.fieldOf("light").forGetter(CrystalixGlassCamoContainer::isLight),
            CrystalixModelState.CODEC.fieldOf("model_state").forGetter(CrystalixGlassCamoContainer::getModelState)
    ).apply(instance, CrystalixGlassCamoContainer::new));
    
    private static final StreamCodec<RegistryFriendlyByteBuf, CrystalixGlassCamoContainer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), CrystalixGlassCamoContainer::getState,
            ByteBufCodecs.INT, CrystalixGlassCamoContainer::getTintColor,
            ByteBufCodecs.BOOL, CrystalixGlassCamoContainer::isLight,
            CrystalixModelState.STREAM_CODEC, CrystalixGlassCamoContainer::getModelState,
            CrystalixGlassCamoContainer::new
    );
    
    private static final int DEFAULT_TINT = 0xFFFFFF;
    
    @Override
    protected CrystalixGlassCamoContainer createContainer(BlockState camoState, Level level, BlockPos blockPos, Player player, ItemAccess itemAccess) {
        final CrystalixGlassCamoContainer empty = new CrystalixGlassCamoContainer(camoState, CrystalixGlassCamoContainerFactory.DEFAULT_TINT, false, CrystalixModelState.empty());
        return CrystalixGlassCamoContainerFactory.createContainer(camoState, CrystalixWandItem.find(player), player, empty);
    }
    
    private static CrystalixGlassCamoContainer createContainer(BlockState camoState, ItemStack wandStack, Player player, CrystalixGlassCamoContainer previous) {
        int tintColor = previous.getTintColor();
        if (CXProperties.canApply(player, CXProperties.COLOR) && !wandStack.getOrDefault(CXRegistry.APPLY_MODE, ApplyMode.DEFAULT).equals(ApplyMode.COLORLESS)) {
            tintColor = wandStack.getOrDefault(CXRegistry.COLOR, CrystalixGlassCamoContainerFactory.DEFAULT_TINT);
        }
        
        final CrystalixModelState model = previous.getModelState();
        boolean light = CXProperties.canApply(player, CXProperties.LIGHT) ? wandStack.getOrDefault(CXRegistry.LIGHT, false) == LightState.LIGHT : previous.isLight();
        CrystalixModelState modelState = new CrystalixModelState(
                CXProperties.canApply(player, CXProperties.SHADELESS) ? wandStack.getOrDefault(CXRegistry.SHADELESS, false) : model.isShadeless(),
                CXProperties.canApply(player, CXProperties.TINTED) ? wandStack.getOrDefault(CXRegistry.TINTED, false) : model.isTinted(),
                CXProperties.canApply(player, CXProperties.MATERIAL) ? wandStack.getOrDefault(CXRegistry.MATERIAL, GlassMaterial.defaultMaterial()) : model.getMaterial()
        );
        return new CrystalixGlassCamoContainer(camoState, tintColor, light, modelState);
    }
    
    @Override
    protected @Nullable BlockState getStateFromItemStack(Level level, BlockPos pos, Player player, ItemAccess itemAccess) {
        if (itemAccess.getResource().getItem() instanceof BlockItem item) {
            return CrystalixGlassCamoContainerFactory.applyWandModifiers(item.getBlock().defaultBlockState(), CrystalixWandItem.find(player), player);
        }
        return null;
    }
    
    private static BlockState applyWandModifiers(BlockState state, ItemStack wand, Player player) {
        if (!(state.getBlock() instanceof CrystalixGlassBlock)) return state;
        
        BlockState newState = CXProperties.applicable(player).applyToBlock(wand, state).state()
                .setValue(CrystalixGlassBlock.INVISIBLE, false)
                .setValue(CrystalixGlassBlock.GHOST, GhostState.BLOCK_ALL);
        
        LightState light = newState.getValue(CrystalixGlassBlock.LIGHT);
        if (light != LightState.NONE && light != LightState.LIGHT) {
            newState = newState.setValue(CrystalixGlassBlock.LIGHT, LightState.NONE);
        }
        
        return newState;
    }
    
    @Override
    protected CrystalixGlassCamoContainer copyContainerWithState(CrystalixGlassCamoContainer container, BlockState newCamoState) {
        return new CrystalixGlassCamoContainer(newCamoState, container.getTintColor(), container.isLight(), container.getModelState());
    }
    
    @Override
    protected ItemStack createItemStack(Level level, BlockPos blockPos, Player player, ItemAccess itemAccess, CrystalixGlassCamoContainer crystalixGlassCamoContainer) {
        return this.dropCamo(crystalixGlassCamoContainer);
    }
    
    @Override
    public ItemStack dropCamo(CrystalixGlassCamoContainer crystalixGlassCamoContainer) {
        return new ItemStack(crystalixGlassCamoContainer.getState().getBlock());
    }
    
    @Override
    public CrystalixGlassCamoContainer handleInteraction(Level level, BlockPos pos, Player player, CrystalixGlassCamoContainer camo, ItemStack stack, InteractionHand hand) {
        if (stack.is(CXRegistry.CRYSTALIX_WAND)) {
            BlockState state = CrystalixGlassCamoContainerFactory.applyWandModifiers(camo.getState(), stack, player);
            return CrystalixGlassCamoContainerFactory.createContainer(state, stack, player, camo);
        }
        return camo;
    }
    
    @Override
    protected boolean isValidBlock(BlockState camoState, BlockGetter level, BlockPos pos, @Nullable Player player) {
        if (!(camoState.getBlock() instanceof CrystalixGlassBlock)) {
            return false;
        }
        if (camoState.is(FramedConstants.Tags.BLOCK_BLACKLIST)) {
            CamoContainerFactory.displayValidationMessage(player, CamoContainerFactory.MSG_BLACKLISTED, CamoMessageVerbosity.DEFAULT);
            return false;
        }
        return true;
    }
    
    @Override
    protected void writeToNetwork(ValueOutput output, CrystalixGlassCamoContainer container) {
        output.putInt("state", Block.getId(container.getState()));
        output.putInt("tint", container.getTintColor());
        output.putBoolean("light", container.isLight());
        output.store("model_state", CrystalixModelState.CODEC, container.getModelState());
    }
    
    @Override
    protected CrystalixGlassCamoContainer readFromNetwork(ValueInput input) {
        final BlockState state = Block.stateById(input.getInt("state").orElseGet(() -> Block.getId(Blocks.AIR.defaultBlockState())));
        final int tint = input.getInt("tint").orElse(CrystalixGlassCamoContainerFactory.DEFAULT_TINT);
        final boolean light = input.getBooleanOr("light", false);
        final CrystalixModelState modelState = input.read("model_state", CrystalixModelState.CODEC).orElse(CrystalixModelState.empty());
        return new CrystalixGlassCamoContainer(state, tint, light, modelState);
    }
    
    @Override
    public boolean canTriviallyConvertToItemStack() {
        return true;
    }
    
    @Override
    public MapCodec<CrystalixGlassCamoContainer> codec() {
        return CrystalixGlassCamoContainerFactory.CODEC;
    }
    
    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, CrystalixGlassCamoContainer> streamCodec() {
        return CrystalixGlassCamoContainerFactory.STREAM_CODEC;
    }
    
    @Override
    public void registerTriggerItems(TriggerRegistrar registrar) {
        registrar.registerApplicationItem(CXRegistry.CRYSTALIX_ITEM.get());
        registrar.registerRemovalItem(FramedConstants.Objects.FRAMED_HAMMER.value());
    }
}
