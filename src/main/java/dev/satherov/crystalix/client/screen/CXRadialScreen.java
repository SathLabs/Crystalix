package dev.satherov.crystalix.client.screen;

import dev.satherov.crystalix.client.CXKeybinds;
import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.crystalix.network.CyclePropertyPayload;
import dev.satherov.sathlib.client.lang.InputLang;
import dev.satherov.sathlib.client.screen.RadialScreen;
import dev.satherov.sathlib.common.properties.BlockItemProperty;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NothingNull
public class CXRadialScreen extends RadialScreen<CXRadialScreen, CXRadialScreen.PropertySlice> {
    
    private static final float START_ANGLE_DEGREES = -90.0F;
    private static final float SLICE_INNER_RADIUS = 35.0F;
    private static final float SLICE_OUTER_RADIUS = 145.0F;
    private static final float HOVER_EXPAND_DISTANCE = 12.0F;
    private static final float HOVER_OUTWARD_OFFSET = 8.0F;
    private static final float SLICE_SPACING_DEGREES = 0.5F;
    private static final float DISPLAY_POSITION = 0.65F;
    private static final int TITLE_COLOR = 0xFFEAF0F7;
    private static final int HOVER_TITLE_COLOR = 0xFFFFFFFF;
    private static final int VALUE_COLOR = 0xFFC6D2DE;
    private static final int HOVER_VALUE_COLOR = 0xFFE5EEF7;
    private static final int DISABLED_TEXT_COLOR = 0xFF6E6E6E;
    private static final int DISABLED_SLICE_COLOR = 0xC0101010;
    
    private final ItemStack stack;
    
    public CXRadialScreen(ItemStack stack) {
        this.stack = stack;
        List<BlockItemProperty<?>> properties = CXProperties.CONTAINER.getProperties();
        
        this.setStartAngleDegrees(CXRadialScreen.START_ANGLE_DEGREES);
        this.setSliceInnerRadius(CXRadialScreen.SLICE_INNER_RADIUS);
        this.setSliceOuterRadius(CXRadialScreen.SLICE_OUTER_RADIUS);
        this.setHoverExpandDistance(CXRadialScreen.HOVER_EXPAND_DISTANCE);
        this.setHoverOutwardOffset(CXRadialScreen.HOVER_OUTWARD_OFFSET);
        this.setSliceSpacingDegrees(CXRadialScreen.SLICE_SPACING_DEGREES);
        
        for (BlockItemProperty<?> property : properties) {
            this.addSlice(new PropertySlice(property));
        }
        this.addSlice(new PropertySlice(CXProperties.APPLY_MODE));
    }
    
    @Override
    public boolean keyReleased(KeyEvent event) {
        if (CXKeybinds.OPEN_WAND_EDITOR.matches(event)) {
            this.onClose();
            return true;
        }
        return super.keyReleased(event);
    }
    
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        
        PropertySlice hovered = this.getHovered();
        if (hovered == null) return;
        
        graphics.setTooltipForNextFrame(
                this.font,
                this.tooltipLines(hovered.property),
                Optional.empty(),
                mouseX,
                mouseY
        );
    }
    
    private boolean openEditor(BlockItemProperty<?> property) {
        if (property == CXProperties.COLOR) {
            Minecraft.getInstance().setScreen(new CXColorScreen(this, this.stack));
            return true;
        }
        return false;
    }
    
    private boolean cycle(BlockItemProperty<?> property, boolean dir) {
        property.cycleItem(dir, this.stack, CXRegistry.CRYSTALIX_BLOCK.get().defaultBlockState());
        ClientPacketDistributor.sendToServer(new CyclePropertyPayload(property.getIdentifier(), dir));
        return true;
    }
    
    private List<Component> tooltipLines(BlockItemProperty<?> property) {
        List<Component> lines = new ArrayList<>();
        lines.add(property.getName().translate(ChatFormatting.DARK_GRAY));
        lines.add(property.displayItemTooltip(this.stack, CXRegistry.CRYSTALIX_BLOCK.get().defaultBlockState()).style(ChatFormatting.DARK_GRAY));
        
        if (!CXProperties.isEditable(property)) {
            lines.add(CXLanguage.TOOLTIP_DISABLED.translate(ChatFormatting.RED));
        } else if (property == CXProperties.COLOR) {
            lines.add(CXLanguage.TOOLTIP_OPEN_COLORS.translate(
                    ChatFormatting.DARK_GRAY,
                    CXRadialScreen.mouseKey(GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
            ));
        } else {
            lines.add(CXLanguage.TOOLTIP_LMB.translate(
                    ChatFormatting.DARK_GRAY,
                    CXRadialScreen.mouseKey(GLFW.GLFW_MOUSE_BUTTON_LEFT),
                    CXRadialScreen.wheelKey(InputLang.WHEEL_UP)
            ));
            lines.add(CXLanguage.TOOLTIP_RMB.translate(
                    ChatFormatting.DARK_GRAY,
                    CXRadialScreen.mouseKey(GLFW.GLFW_MOUSE_BUTTON_RIGHT),
                    CXRadialScreen.wheelKey(InputLang.WHEEL_DOWN)
            ));
        }
        
        return lines;
    }
    
    private static Component mouseKey(int button) {
        return SLComponent.squareBrackets(SLComponent.key(InputConstants.Type.MOUSE.getOrCreate(button)).style(ChatFormatting.GOLD));
    }
    
    private static Component wheelKey(InputLang direction) {
        return SLComponent.squareBrackets(direction.translate(ChatFormatting.GOLD));
    }
    
    final class PropertySlice extends RadialScreen.RadialSlice<CXRadialScreen, PropertySlice> {
        
        private final BlockItemProperty<?> property;
        private final boolean editable;
        
        private PropertySlice(BlockItemProperty<?> property) {
            this.property = property;
            this.editable = CXProperties.isEditable(property);
            this.setLabelColor(this.editable ? CXRadialScreen.TITLE_COLOR : CXRadialScreen.DISABLED_TEXT_COLOR);
            
            if (!this.editable) {
                this.setBaseColor(CXRadialScreen.DISABLED_SLICE_COLOR);
                this.setHoverColor(CXRadialScreen.DISABLED_SLICE_COLOR);
            }
        }
        
        @Override
        protected void renderContents(GuiGraphicsExtractor graphics, RadialScreen.SliceRenderContext<CXRadialScreen, PropertySlice> context) {
            Component title = this.property.getName().translate();
            Component display = this.property.displayItemValue(CXRadialScreen.this.stack, CXRegistry.CRYSTALIX_BLOCK.get().defaultBlockState());
            Component value = this.editable ? display : Component.literal(display.getString());
            
            int halfLineHeight = CXRadialScreen.this.font.lineHeight / 2;
            int displayX = Math.round(context.xAlongMiddle(CXRadialScreen.DISPLAY_POSITION));
            int displayY = Math.round(context.yAlongMiddle(CXRadialScreen.DISPLAY_POSITION)) - halfLineHeight;
            
            graphics.centeredText(
                    CXRadialScreen.this.font,
                    title,
                    displayX,
                    displayY - CXRadialScreen.this.font.lineHeight,
                    context.hovered() && this.editable ? CXRadialScreen.HOVER_TITLE_COLOR : this.getLabelColor()
            );
            graphics.centeredText(
                    CXRadialScreen.this.font,
                    value,
                    displayX,
                    displayY + CXRadialScreen.this.font.lineHeight,
                    this.editable ? (context.hovered() ? CXRadialScreen.HOVER_VALUE_COLOR : CXRadialScreen.VALUE_COLOR) : CXRadialScreen.DISABLED_TEXT_COLOR
            );
        }
        
        @Override
        public boolean mousePressed(CXRadialScreen screen, MouseButtonEvent event, boolean doubleClick) {
            if (!this.editable) {
                return false;
            }
            
            return switch (event.button()) {
                case GLFW.GLFW_MOUSE_BUTTON_LEFT -> screen.cycle(this.property, true);
                case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> screen.cycle(this.property, false);
                case GLFW.GLFW_MOUSE_BUTTON_MIDDLE -> screen.openEditor(this.property);
                default -> false;
            };
        }
        
        @Override
        public boolean mouseReleased(CXRadialScreen screen, MouseButtonEvent event) {
            return switch (event.button()) {
                case GLFW.GLFW_MOUSE_BUTTON_LEFT, GLFW.GLFW_MOUSE_BUTTON_RIGHT, GLFW.GLFW_MOUSE_BUTTON_MIDDLE -> true;
                default -> false;
            };
        }
        
        @Override
        public boolean mouseScrolled(CXRadialScreen screen, double mouseX, double mouseY, double scrollX, double scrollY) {
            if (scrollY == 0.0D || !this.editable) return false;
            return screen.cycle(this.property, scrollY > 0.0D);
        }
    }
}
