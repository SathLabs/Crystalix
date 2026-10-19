package dev.satherov.crystalix.core.registry;

import lombok.experimental.UtilityClass;

import dev.satherov.crystalix.CXConfig;
import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.crystalix.common.block.CrystalixGlassBlock;
import dev.satherov.crystalix.common.block.CrystalixGlassBlockEntity;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.common.properties.ApplyMode;
import dev.satherov.crystalix.common.properties.GhostState;
import dev.satherov.crystalix.common.properties.GlassMaterial;
import dev.satherov.crystalix.common.properties.LightState;
import dev.satherov.crystalix.core.mixin.RedStoneWireBlockAccessor;
import dev.satherov.sathlib.common.properties.BlockItemProperty;
import dev.satherov.sathlib.common.properties.BlockItemPropertyContainer;
import dev.satherov.sathlib.common.properties.PropertyApplicator;
import dev.satherov.sathlib.common.properties.PropertyCycler;
import dev.satherov.sathlib.common.properties.PropertyDisplayer;
import dev.satherov.sathlib.common.properties.PropertyExtractor;
import dev.satherov.sathlib.network.chat.SLComponent;

import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

@UtilityClass
public class CXProperties {
    
    // DO NOT ADD TO THE CONTAINER!
    public static final BlockItemProperty<ApplyMode> APPLY_MODE = BlockItemProperty.builder(Crystalix.id("apply_mode"), ApplyMode.class, CXLanguage.PROPERTY_APPLY_MODE)
            .tooltipDisplayer(ApplyMode::tooltip)
            .valueDisplayer(ApplyMode::display)
            .cycler(PropertyCycler.enumCycler(ApplyMode.class))
            .item(PropertyExtractor.item(CXRegistry.APPLY_MODE, ApplyMode.DEFAULT), PropertyApplicator.item(CXRegistry.APPLY_MODE))
            .build();
    
    public static final BlockItemProperty<Boolean> INVISIBLE = BlockItemProperty.builder(Crystalix.id("invisible"), Boolean.class, CXLanguage.PROPERTY_INVISIBLE)
            .tooltipDisplayer(PropertyDisplayer.boolDisplayer(CXLanguage.TOOLTIP_INVISIBLE_ON, CXLanguage.TOOLTIP_INVISIBLE_OFF))
            .cycler(PropertyCycler.BOOLEAN)
            .item(PropertyExtractor.item(CXRegistry.INVISIBLE, false), PropertyApplicator.item(CXRegistry.INVISIBLE))
            .block(PropertyExtractor.block(CrystalixGlassBlock.INVISIBLE, false), PropertyApplicator.block(CrystalixGlassBlock.INVISIBLE))
            .build();
    
    public static final BlockItemProperty<LightState> LIGHT = BlockItemProperty.builder(Crystalix.id("light"), LightState.class, CXLanguage.PROPERTY_LIGHT)
            .tooltipDisplayer(LightState::tooltip)
            .cycler(PropertyCycler.enumCycler(LightState.class))
            .item(PropertyExtractor.item(CXRegistry.LIGHT, LightState.NONE), PropertyApplicator.item(CXRegistry.LIGHT))
            .block(PropertyExtractor.block(CrystalixGlassBlock.LIGHT, LightState.NONE), PropertyApplicator.block(CrystalixGlassBlock.LIGHT))
            .build();
    
    public static final BlockItemProperty<GhostState> GHOST = BlockItemProperty.builder(Crystalix.id("ghost"), GhostState.class, CXLanguage.PROPERTY_GHOST)
            .tooltipDisplayer(GhostState::tooltip)
            .cycler(PropertyCycler.enumCycler(GhostState.class))
            .item(PropertyExtractor.item(CXRegistry.GHOST, GhostState.ALLOW_ALL), PropertyApplicator.item(CXRegistry.GHOST))
            .block(PropertyExtractor.block(CrystalixGlassBlock.GHOST, GhostState.BLOCK_ALL), PropertyApplicator.block(CrystalixGlassBlock.GHOST))
            .build();
    
    public static final BlockItemProperty<Integer> COLOR = BlockItemProperty.builder(Crystalix.id("color"), Integer.class, CXLanguage.PROPERTY_COLOR)
            .tooltipDisplayer(PropertyDisplayer.direct(CXLanguage.TOOLTIP_COLOR.translate()))
            .valueDisplayer(val -> {
                String hex = String.format("#%06X", (0xFFFFFF & val));
                return SLComponent.of(Component.literal(hex).withColor(val));
            })
            .cycler(PropertyCycler.numberCycler(0x00000, 0xFFFFFF))
            .item(PropertyExtractor.item(CXRegistry.COLOR, 0xFFFFFF), PropertyApplicator.item(CXRegistry.COLOR))
            .block(PropertyExtractor.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::getColor, 0xFFFFFF), (block, item, val) -> {
                if (item.stack().getOrDefault(CXRegistry.APPLY_MODE, ApplyMode.DEFAULT).equals(ApplyMode.COLORLESS)) return block;
                if (block.blockEntity() instanceof CrystalixGlassBlockEntity entity) entity.setColor(val);
                return block;
            })
            .build();
    
    public static final BlockItemProperty<GlassMaterial> MATERIAL = BlockItemProperty.builder(Crystalix.id("material"), GlassMaterial.class, CXLanguage.PROPERTY_MATERIAL)
            .tooltipDisplayer(GlassMaterial::tooltip)
            .valueDisplayer(GlassMaterial::display)
            .cycler(PropertyCycler.enumCycler(GlassMaterial.class))
            .item(PropertyExtractor.item(CXRegistry.MATERIAL, GlassMaterial.defaultMaterial()), PropertyApplicator.item(CXRegistry.MATERIAL))
            .block(PropertyExtractor.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::getMaterial, GlassMaterial.defaultMaterial()), PropertyApplicator.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::setMaterial))
            .build();
    
    public static final BlockItemProperty<Boolean> SHADELESS = BlockItemProperty.builder(Crystalix.id("shadeless"), Boolean.class, CXLanguage.PROPERTY_SHADELESS)
            .tooltipDisplayer(PropertyDisplayer.boolDisplayer(CXLanguage.TOOLTIP_SHADELESS_ON, CXLanguage.TOOLTIP_SHADELESS_OFF))
            .cycler(PropertyCycler.BOOLEAN)
            .item(PropertyExtractor.item(CXRegistry.SHADELESS, false), PropertyApplicator.item(CXRegistry.SHADELESS))
            .block(PropertyExtractor.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::isShadeless, false), PropertyApplicator.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::setShadeless))
            .build();
    
    public static final BlockItemProperty<Boolean> TINTED = BlockItemProperty.builder(Crystalix.id("tinted"), Boolean.class, CXLanguage.PROPERTY_TINTED)
            .tooltipDisplayer(PropertyDisplayer.boolDisplayer(CXLanguage.TOOLTIP_TINTED_ON, CXLanguage.TOOLTIP_TINTED_OFF))
            .cycler(PropertyCycler.BOOLEAN)
            .item(PropertyExtractor.item(CXRegistry.TINTED, false), PropertyApplicator.item(CXRegistry.TINTED))
            .block(PropertyExtractor.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::isTinted, false), PropertyApplicator.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::setTinted))
            .build();
    
    public static final BlockItemProperty<Boolean> REINFORCED = BlockItemProperty.builder(Crystalix.id("reinforced"), Boolean.class, CXLanguage.PROPERTY_REINFORCED)
            .tooltipDisplayer(PropertyDisplayer.boolDisplayer(CXLanguage.TOOLTIP_REINFORCED_ON, CXLanguage.TOOLTIP_REINFORCED_OFF))
            .cycler(PropertyCycler.BOOLEAN)
            .item(PropertyExtractor.item(CXRegistry.REINFORCED, false), PropertyApplicator.item(CXRegistry.REINFORCED))
            .block(PropertyExtractor.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::isReinforced, false), PropertyApplicator.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::setReinforced))
            .build();
    
    public static final BlockItemProperty<Boolean> WATERLOGGABLE = BlockItemProperty.builder(Crystalix.id("waterloggable"), Boolean.class, CXLanguage.PROPERTY_WATERLOGGABLE)
            .tooltipDisplayer(PropertyDisplayer.boolDisplayer(CXLanguage.TOOLTIP_WATERLOGGABLE_ON, CXLanguage.TOOLTIP_WATERLOGGABLE_OFF))
            .cycler(PropertyCycler.BOOLEAN)
            .item(PropertyExtractor.item(CXRegistry.WATERLOGGABLE, false), PropertyApplicator.item(CXRegistry.WATERLOGGABLE))
            .block(PropertyExtractor.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::isWaterloggable, false), PropertyApplicator.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::setWaterloggable))
            .build();
    
    public static final BlockItemProperty<Boolean> CONDUCTOR = BlockItemProperty.builder(Crystalix.id("conductor"), Boolean.class, CXLanguage.PROPERTY_CONDUCTOR)
            .tooltipDisplayer(PropertyDisplayer.boolDisplayer(CXLanguage.TOOLTIP_CONDUCTOR_ON, CXLanguage.TOOLTIP_CONDUCTOR_OFF))
            .cycler(PropertyCycler.BOOLEAN)
            .item(PropertyExtractor.item(CXRegistry.CONDUCTOR, false), PropertyApplicator.item(CXRegistry.CONDUCTOR))
            .block(PropertyExtractor.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::isConductor, false), PropertyApplicator.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::setConductor))
            .build();
    
    public static final BlockItemProperty<Integer> REDSTONE = BlockItemProperty.builder(Crystalix.id("redstone"), Integer.class, CXLanguage.PROPERTY_REDSTONE)
            .tooltipDisplayer(PropertyDisplayer.direct(CXLanguage.TOOLTIP_REDSTONE.translate()))
            .valueDisplayer(val -> SLComponent.of(Component.literal(String.valueOf(val)).withColor(RedStoneWireBlockAccessor.getColors()[val])))
            .cycler(PropertyCycler.numberCycler(0, 15))
            .item(PropertyExtractor.item(CXRegistry.REDSTONE, 0), PropertyApplicator.item(CXRegistry.REDSTONE))
            .block(PropertyExtractor.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::getRedstone, 0), PropertyApplicator.blockEntity(CrystalixGlassBlockEntity.class, CrystalixGlassBlockEntity::setRedstone))
            .build();
    
    public static final BlockItemPropertyContainer<CrystalixGlassBlock, CrystalixWandItem> CONTAINER = BlockItemPropertyContainer.builder(CrystalixGlassBlock.class, CrystalixWandItem.class)
            .property(CXProperties.INVISIBLE)
            .property(CXProperties.LIGHT)
            .property(CXProperties.GHOST)
            .property(CXProperties.COLOR)
            .property(CXProperties.MATERIAL)
            .property(CXProperties.SHADELESS)
            .property(CXProperties.TINTED)
            .property(CXProperties.REINFORCED)
            .property(CXProperties.WATERLOGGABLE)
            .property(CXProperties.CONDUCTOR)
            .property(CXProperties.REDSTONE)
            .build();

    public static boolean isEditable(BlockItemProperty<?> property) {
        return !CXConfig.Server.getDisabledProperties().contains(property.getIdentifier().getPath());
    }

    public static boolean canApply(Player player, BlockItemProperty<?> property) {
        return CXProperties.isEditable(property) || player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    public static BlockItemPropertyContainer<CrystalixGlassBlock, CrystalixWandItem> editable() {
        return CXProperties.filter(CXProperties::isEditable);
    }

    public static BlockItemPropertyContainer<CrystalixGlassBlock, CrystalixWandItem> applicable(Player player) {
        return CXProperties.filter(property -> CXProperties.canApply(player, property));
    }

    private static BlockItemPropertyContainer<CrystalixGlassBlock, CrystalixWandItem> filter(Predicate<BlockItemProperty<?>> filter) {
        final BlockItemPropertyContainer.Builder<CrystalixGlassBlock, CrystalixWandItem> builder = BlockItemPropertyContainer.builder(CrystalixGlassBlock.class, CrystalixWandItem.class);
        CXProperties.CONTAINER.getProperties().stream().filter(filter).forEach(builder::property);
        return builder.build();
    }
}
