package keletu.enigmaticlegacy.util.compat;

import net.minecraftforge.fml.common.Loader;

public class ModCompat {
    public static final boolean COMPAT_TRINKETS = Loader.isModLoaded("xat");
    public static final boolean COMPAT_FORGOTTEN_RELICS = !Loader.isModLoaded("forgotten_relics");
    public static final boolean COMPAT_SIMPLED_DIFFICULTY = !Loader.isModLoaded("forgotten_relics");
}
