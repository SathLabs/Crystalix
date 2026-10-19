package dev.satherov.crystalix.client;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.crystalix.client.screen.CXRadialScreen;
import dev.satherov.crystalix.common.block.CrystalixGlassBlock;
import dev.satherov.crystalix.common.block.CrystalixGlassBlockEntity;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.crystalix.network.SwapPropertiesPayload;
import dev.satherov.sathlib.client.input.SLKeybindManager;
import dev.satherov.sathlib.common.properties.BlockItemPropertyContainer;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.lwjgl.glfw.GLFW;

public class CXKeybinds {
    
    public static final SLKeybindManager MANAGER = SLKeybindManager.create(Crystalix.MOD_ID, Crystalix.id("default"));
    
    public static final KeyMapping OPEN_WAND_EDITOR = CXKeybinds.MANAGER.add(CXLanguage.KEY_OPEN_WAND_EDITOR, GLFW.GLFW_KEY_V, event -> {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        if (mc.level == null || player == null || mc.screen != null) return;
        
        final ItemStack stack = CrystalixWandItem.find(player);
        if (stack.isEmpty()) return;
        
        if (event.getAction() == GLFW.GLFW_PRESS) {
            if (mc.screen != null) return;
            mc.setScreen(new CXRadialScreen(stack));
        } else if (event.getAction() == GLFW.GLFW_RELEASE) {
            if (mc.screen instanceof CXRadialScreen) mc.setScreen(null);
        }
    });
    
    public static final KeyMapping PICK_BLOCK = CXKeybinds.MANAGER.add(CXLanguage.KEY_PICK_BLOCK, GLFW.GLFW_KEY_X, event -> {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        final Level level = mc.level;
        if (level == null || player == null || mc.screen != null) return;
        
        final ItemStack stack = CrystalixWandItem.find(player);
        if (stack.isEmpty()) return;
        
        if (event.getAction() == GLFW.GLFW_PRESS) {
            final BlockPos pos = player.pick(player.blockInteractionRange(), 1.0F, false) instanceof BlockHitResult hit ? hit.getBlockPos() : null;
            if (pos == null) return;
            
            final BlockState state = level.getBlockState(pos);
            final CrystalixGlassBlockEntity entity = CXRegistry.GLASS_BLOCK_ENTITY.get().getBlockEntity(level, pos);
            if (!(state.getBlock() instanceof CrystalixGlassBlock) || entity == null) return;
            
            final BlockItemPropertyContainer<CrystalixGlassBlock, CrystalixWandItem> editable = CXProperties.editable();
            if (editable.matches(stack, state, entity)) {
                player.sendOverlayMessage(CXLanguage.MESSAGE_PROPERTY_MATCH.translate(ChatFormatting.GRAY));
                return;
            }
            
            editable.applyToItem(stack, state, entity);
            ClientPacketDistributor.sendToServer(new SwapPropertiesPayload(pos));
            player.sendOverlayMessage(CXLanguage.MESSAGE_PROPERTY_PICK.translate(ChatFormatting.GRAY));
        }
    });
    
    public static final KeyMapping HIGHLIGHT_BLOCKS = CXKeybinds.MANAGER.add(CXLanguage.KEY_HIGHLIGHT_BLOCKS, GLFW.GLFW_KEY_R, event -> {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        final Level level = mc.level;
        if (level == null || player == null || mc.screen != null || CrystalixWandItem.find(player).isEmpty()) return;
        
        if (event.getAction() == GLFW.GLFW_PRESS) CXBlockHighlighter.highlight();
    });
}
