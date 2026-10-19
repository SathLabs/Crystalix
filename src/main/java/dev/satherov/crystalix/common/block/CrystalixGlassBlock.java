package dev.satherov.crystalix.common.block;

import lombok.experimental.Accessors;

import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.common.properties.GhostState;
import dev.satherov.crystalix.common.properties.LightState;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.common.block.SLBlock;
import dev.satherov.sathlib.common.block.SLBlockProperties;
import dev.satherov.sathlib.common.block.SLEntityBlock;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@NothingNull
@Accessors(fluent = true)
public class CrystalixGlassBlock extends SLBlock implements SLEntityBlock<CrystalixGlassBlockEntity>, SimpleWaterloggedBlock {
    
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
    public static final EnumProperty<LightState> LIGHT = EnumProperty.create("light", LightState.class);
    public static final EnumProperty<GhostState> GHOST = EnumProperty.create("ghost", GhostState.class);
    
    public CrystalixGlassBlock(SLBlockProperties properties) {
        super(properties
                .noOcclusion()
                .mapColor(DyeColor.WHITE)
                .instrument(NoteBlockInstrument.HAT)
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .isValidSpawn(SLBlockProperties::never)
                .isSuffocating(SLBlockProperties::never)
                .isViewBlocking(SLBlockProperties::never)
                .isRedstoneConductor(CrystalixGlassBlockEntity.isRedstoneConductor())
                .lightLevel(state -> state.getValue(CrystalixGlassBlock.LIGHT) == LightState.LIGHT ? 15 : 0)
        );
    }
    
    @Override
    protected void registerState(StateBuilder builder) {
        builder.addValue(CrystalixGlassBlock.WATERLOGGED, false);
        builder.addValue(CrystalixGlassBlock.INVISIBLE, false);
        builder.addValue(CrystalixGlassBlock.LIGHT, LightState.NONE);
        builder.addValue(CrystalixGlassBlock.GHOST, GhostState.BLOCK_ALL);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(CXLanguage.TOOLTIP_USE_WAND.translate(ChatFormatting.GRAY));
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        BlockState state = this.defaultBlockState();
        if (player == null) return state;
        
        ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (stack.getItem() instanceof CrystalixWandItem) {
            state = CXProperties.applicable(player).applyToBlock(stack, state).state();
        }
        return state;
    }
    
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack held) {
        if (!(world instanceof ServerLevel level)) return;
        if (!(placer instanceof ServerPlayer player)) return;
        ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
        CrystalixGlassBlockEntity entity = CXRegistry.GLASS_BLOCK_ENTITY.get().getBlockEntity(level, pos);
        if (stack.getItem() instanceof CrystalixWandItem && entity != null) state = CXProperties.applicable(player).applyToBlock(stack, state, entity).state();
        
        FluidState fluid = level.getFluidState(pos);
        BlockState updated = state.setValue(CrystalixGlassBlock.WATERLOGGED, CrystalixGlassBlockEntity.isWaterloggable(level, pos) && fluid.getType() == Fluids.WATER);
        if (updated != state) level.setBlockAndUpdate(pos, updated);
    }
    
    @Override
    public CrystalixGlassBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrystalixGlassBlockEntity(pos, state);
    }
    
    @Override
    public Integer getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return level.getBlockEntity(pos) instanceof CrystalixGlassBlockEntity tile ? tile.getColor() : 0xFFFFFF;
    }
    
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(CrystalixGlassBlock.WATERLOGGED) ? Fluids.WATER.getSource(true) : super.getFluidState(state);
    }
    
    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity user, BlockGetter level, BlockPos pos, BlockState state, Fluid type) {
        return CrystalixGlassBlockEntity.isWaterloggable(level, pos) && type == Fluids.WATER;
    }
    
    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!CrystalixGlassBlockEntity.isWaterloggable(level, pos) || state.getValue(CrystalixGlassBlock.WATERLOGGED) || fluidState.getType() != Fluids.WATER) return false;
        
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(CrystalixGlassBlock.WATERLOGGED, true), 3);
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        
        return true;
    }
    
    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
    
    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
        return 1.0F;
    }
    
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(CrystalixGlassBlock.INVISIBLE) ? RenderShape.INVISIBLE : super.getRenderShape(state);
    }
    
    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        return adjacent.is(this) && !adjacent.getValue(CrystalixGlassBlock.INVISIBLE);
    }
    
    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return CrystalixGlassBlockEntity.isReinforced(level, pos) && super.canDropFromExplosion(state, level, pos, explosion);
    }
    
    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return CrystalixGlassBlockEntity.isReinforced(context.getLevel(), context.getClickedPos()) && super.canBeReplaced(state, context);
    }
    
    @Override
    public void onBlockExploded(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion) {
        if (!CrystalixGlassBlockEntity.isReinforced(level, pos)) super.onBlockExploded(state, level, pos, explosion);
    }
    
    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter getter, BlockPos pos, Entity entity) {
        return !CrystalixGlassBlockEntity.isReinforced(getter, pos);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public float getExplosionResistance(BlockState state, BlockGetter getter, BlockPos pos, Explosion explosion) {
        return CrystalixGlassBlockEntity.isReinforced(getter, pos) ? Float.MAX_VALUE : super.getExplosionResistance();
    }
    
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos) {
        float progress = super.getDestroyProgress(state, player, getter, pos);
        if (CrystalixGlassBlockEntity.isReinforced(getter, pos)) progress *= 0.1f;
        return progress;
    }
    
    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return state.getValue(CrystalixGlassBlock.LIGHT) != LightState.DARK;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (level instanceof ServerLevel) return;
        var light = state.getValue(CrystalixGlassBlock.LIGHT);
        if (!(light == LightState.FAKE_LIGHT || light == LightState.FAKE_DARK)) return;
        level.getLightEngine().checkBlock(pos);
    }
    
    @Override
    protected int getLightDampening(BlockState state) {
        return state.getValue(CrystalixGlassBlock.LIGHT) == LightState.DARK ? 15 : 0;
    }
    
    @Override
    public int getLightEmission(BlockState state, BlockGetter getter, BlockPos pos) {
        var light = state.getValue(CrystalixGlassBlock.LIGHT);
        boolean isServer = switch (getter) {
            case ChunkAccess chunk -> chunk.getLevel() instanceof ServerLevel;
            case Level level -> level instanceof ServerLevel;
            default -> false;
        };
        
        if (light == LightState.FAKE_LIGHT) return isServer ? 0 : 15;
        if (light == LightState.FAKE_DARK) return isServer ? 15 : 0;
        return super.getLightEmission(state, getter, pos);
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext ctx && ctx != CollisionContext.empty()) {
            if (state.getValue(CrystalixGlassBlock.GHOST).test(ctx)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, level, pos, context);
    }
    
    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        if (pathComputationType == PathComputationType.LAND) {
            return switch (state.getValue(CrystalixGlassBlock.GHOST)) {
                case ALLOW_ALL, ALLOW_ANIMAL, ALLOW_MONSTER -> true;
                default -> false;
            };
        }
        return false;
    }
    
    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }
    
    @Override
    protected int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction side) {
        return CrystalixGlassBlockEntity.getRedstone(getter, pos);
    }
}
