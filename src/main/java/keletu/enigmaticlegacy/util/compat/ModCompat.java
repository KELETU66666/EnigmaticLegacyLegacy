package keletu.enigmaticlegacy.util.compat;

import net.minecraftforge.fml.common.Loader;

import java.util.Objects;

public class ModCompat {
    public static final boolean COMPAT_TRINKETS = Loader.isModLoaded("xat");
    public static final boolean COMPAT_FORGOTTEN_RELICS = !Loader.isModLoaded("forgotten_relics");
    public static final boolean COMPAT_SIMPLED_DIFFICULTY = Loader.isModLoaded("simpledifficulty");
    public static final boolean COMPAT_FIRSTAID = Loader.isModLoaded("firstaid");
    public static final boolean COMPAT_BUBBLES = Objects.equals(Loader.instance().getIndexedModList().get("baubles").getName(), "Bubbles");
}
