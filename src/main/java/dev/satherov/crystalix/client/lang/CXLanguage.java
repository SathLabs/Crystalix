package dev.satherov.crystalix.client.lang;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.sathlib.client.lang.SLTranslatable;

import net.minecraft.util.Util;

import java.util.function.BiConsumer;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum CXLanguage implements SLTranslatable {
    // @formatter:off
    CREATIVE_TAB               ("creative_tab", "default",               "Crystalix"),
                                                                         
    KEY_CATEGORY               ("key.category", "default",               "Crystalix"),
    KEY_OPEN_WAND_EDITOR       ("key",          "open_wand_editor",      "Open Wand Editor"),
    KEY_TOGGLE_COLORLESS       ("key",          "toggle_colorless",      "Toggle Apply Colorless"),
    KEY_PICK_BLOCK             ("key",          "pick_block",            "Pick properties from block"),
    KEY_HIGHLIGHT_BLOCKS       ("key",          "highlight_blocks",      "Highlight Blocks"),
                                                
    MESSAGE_PROPERTY_PICK      ("message",      "property_pick",         "Picked properties from block"),
    MESSAGE_PROPERTY_MATCH     ("message",      "property_match",        "Properties already match"),
                                                                         
    TOOLTIP_BULK               ("tooltip",      "bulk",                  "Crouch to bulk edit"),
    TOOLTIP_EDITOR             ("tooltip",      "editor",                "%s to open property editor"),
    TOOLTIP_PICK_PROPERTY      ("tooltip",      "property_pick",         "%s to pick properties from a block"),
    TOOLTIP_COLORLESS          ("tooltip",      "colorless",             "%s to toggle applying properties with or without color"),
    TOOLTIP_HIGHLIGHT          ("tooltip",      "highlight",             "%s to highlight all crystalix blocks in your surroundings"),
    TOOLTIP_USE_WAND           ("tooltip",      "use_wand",              "To modify block properties and access different texture or colors use the Crystalix Wand"),
    TOOLTIP_SHOW_PROPERTIES    ("tooltip",      "show_properties",       "To show currently selected properties in the tooltip toggle the config"),
    TOOLTIP_LMB                ("tooltip",      "lmb",                   "%s or %s to cycle forward"),
    TOOLTIP_RMB                ("tooltip",      "rmb",                   "%s or %s to cycle backward"),
    TOOLTIP_OPEN_COLORS        ("tooltip",      "open_colors",           "%s to open color picker"),
    TOOLTIP_OPEN_MATERIALS     ("tooltip",      "open_materials",        "%s to open material picker"),
    TOOLTIP_DISABLED           ("tooltip",      "disabled",              "Disabled by the server config"),
                                                                         
    TOOLTIP_INVISIBLE_ON       ("tooltip",      "invisible.on",          "Will not be rendered"),
    TOOLTIP_INVISIBLE_OFF      ("tooltip",      "invisible.off",         "Renders as normal"),
    
    TOOLTIP_LIGHT_NONE         ("tooltip",      "light.none",            "Does not affect lighting"),
    TOOLTIP_LIGHT_LIGHT        ("tooltip",      "light.light",           "Emits a light level of 15"),
    TOOLTIP_LIGHT_DARK         ("tooltip",      "light.dark",            "Blocks surrounding light from passing through"),
    TOOLTIP_LIGHT_FAKE_LIGHT   ("tooltip",      "light.fake_light",      "Renders light without any real impact, such as blocking mob spawns"),
    TOOLTIP_LIGHT_FAKE_DARK    ("tooltip",      "light.fake_dark",       "Does not render light while maintaining a real impact, such as blocking mob spawns"),
    
    TOOLTIP_GHOST_ALL          ("tooltip",      "ghost.all",             "Allows any entities to pass through"),
    TOOLTIP_GHOST_NONE         ("tooltip",      "ghost.none",            "Allows no entities to pass through"), 
    TOOLTIP_GHOST_ALLOW_PLAYER ("tooltip",      "ghost.allow_player",    "Allows players to pass through"),
    TOOLTIP_GHOST_BLOCK_PLAYER ("tooltip",      "ghost.block_player",    "Allows any entities but players to pass through"),
    TOOLTIP_GHOST_ALLOW_ANIMAL ("tooltip",      "ghost.allow_animal",    "Allows animals to pass through"),
    TOOLTIP_GHOST_BLOCK_ANIMAL ("tooltip",      "ghost.block_animal",    "Allows any entities but animals to pass through"),
    TOOLTIP_GHOST_ALLOW_MONSTER("tooltip",      "ghost.allow_monster",   "Allows monsters to pass through"),
    TOOLTIP_GHOST_BLOCK_MONSTER("tooltip",      "ghost.block_monster",   "Allows any entities but monsters to pass through"),
    TOOLTIP_GHOST_ALLOW_ADULT  ("tooltip",      "ghost.allow_adult",     "Allows adult entities to pass through"),
    TOOLTIP_GHOST_BLOCK_ADULT  ("tooltip",      "ghost.block_adult",     "Allows any entities which aren't adults to pass through"),
    
    TOOLTIP_COLOR              ("tooltip",      "color",                 "Changes the color of the glass"),
    
    TOOLTIP_MATERIAL_NORMAL    ("tooltip",      "material.normal",       "Default glass texture"),
    TOOLTIP_MATERIAL_CLEAR     ("tooltip",      "material.clear",        "Completely clear texture"),
    TOOLTIP_MATERIAL_BORDERED  ("tooltip",      "material.bordered",     "Normal glass texture but without the specs on the glass"),
    
    TOOLTIP_SHADELESS_ON       ("tooltip",      "shadeless.on",          "Will not render any shadows on the block"),
    TOOLTIP_SHADELESS_OFF      ("tooltip",      "shadeless.off",         "Renders shadows as normal"),
    
    TOOLTIP_TINTED_ON          ("tooltip",      "tinted.on",             "Uses the fully tinted texture variant"),
    TOOLTIP_TINTED_OFF         ("tooltip",      "tinted.off",            "Uses the non-tinted texture variant while still applying the selected tint"),
    
    TOOLTIP_REINFORCED_ON      ("tooltip",      "reinforced.on",         "Makes the glass indestructible to anything but players and increases breaking duration"),
    TOOLTIP_REINFORCED_OFF     ("tooltip",      "reinforced.off",        "Makes the glass to be broken as normal"),
    
    TOOLTIP_WATERLOGGABLE_ON   ("tooltip",      "waterloggable.on",      "Allows the glass to change its waterlogged state"),
    TOOLTIP_WATERLOGGABLE_OFF  ("tooltip",      "waterloggable.off",     "Prevents the glass from changing its waterlogged state"),
    
    TOOLTIP_CONDUCTOR_ON       ("tooltip",      "conductor.on",          "Allows redstones signals to pass through this block"),
    TOOLTIP_CONDUCTOR_OFF      ("tooltip",      "conductor.off",         "Prevents redstone signals from passing through this block"),
    
    TOOLTIP_REDSTONE           ("tooltip",      "redstone",              "Emits a redstone signal at the given level"),
    
    TOOLTIP_APPLY_DEFAULT      ("tooltip",      "apply_mode.default",    "Applies all properties as expected"),
    TOOLTIP_APPLY_COLORLESS    ("tooltip",      "apply_mode.colorless",  "Applies all properties except for the color"),
    TOOLTIP_APPLY_EXACT        ("tooltip",      "apply_mode.exact",      "When bulk applying properties, only exact matches to the initial block will be edited"),
    
    PROPERTY_INVISIBLE         ("property",     "invisible",             "Invisible"),
    PROPERTY_LIGHT             ("property",     "light",                 "Light"),
    PROPERTY_GHOST             ("property",     "ghost",                 "Ghost"),
    PROPERTY_COLOR             ("property",     "color",                 "Color"),
    PROPERTY_SHADELESS         ("property",     "shadeless",             "Shadeless"),
    PROPERTY_TINTED            ("property",     "tinted",                "Tinted"),
    PROPERTY_REINFORCED        ("property",     "reinforced",            "Reinforced"),
    PROPERTY_WATERLOGGABLE     ("property",     "waterloggable",         "Waterloggable"),
    PROPERTY_CONDUCTOR         ("property",     "conductor",             "Conductor"),
    PROPERTY_REDSTONE          ("property",     "redstone",              "Redstone"),
    PROPERTY_MATERIAL          ("property",     "material",              "Material"),
    PROPERTY_APPLY_MODE        ("property",     "apply_mode",            "Apply Mode"),
                                                                         
    PROPERTY_LIGHT_LIGHT       ("property",     "light.light",           "Light"),
    PROPERTY_LIGHT_DARK        ("property",     "light.dark",            "Dark"),
    PROPERTY_LIGHT_FAKE_LIGHT  ("property",     "light.fake_light",      "Fake Light"),
    PROPERTY_LIGHT_FAKE_DARK   ("property",     "light.fake_dark",       "Fake Dark"),
                                                                         
    PROPERTY_GHOST_PLAYER      ("property",     "ghost.player",          "Player"),
    PROPERTY_GHOST_ANIMAL      ("property",     "ghost.animal",          "Animal"),
    PROPERTY_GHOST_MONSTER     ("property",     "ghost.monster",         "Monster"),
    PROPERTY_GHOST_ADULT       ("property",     "ghost.adult",           "Adult"),
    
    PROPERTY_APPLY_DEFAULT     ("apply_mode",   "default",               "Default"),
    PROPERTY_APPLY_COLORLESS   ("apply_mode",   "colorless",             "Colorless"),
    PROPERTY_APPLY_EXACT       ("apply_mode",   "exact",                 "Exact"),
                                                
    MATERIAL_NORMAL            ("material",     "normal",                "Normal"),
    MATERIAL_CLEAR             ("material",     "clear",                 "Clear"),
    MATERIAL_BORDERED          ("material",     "bordered",              "Bordered"),
    
    JADE_CONFIG                ("config.jade.plugin_crystalix.crystalix",      "Crystalix"),
    // @formatter:on
    ;
    
    private final String key;
    private final String translation;
    
    CXLanguage(String group, String key, String translation) {
        this(Util.makeDescriptionId(group, Crystalix.id(key)), translation);
    }
    
    public static void translate(BiConsumer<String, String> consumer) {
        for (SLTranslatable lang : CXLanguage.values()) {
            consumer.accept(lang.key(), lang.translation());
        }
    }
}
