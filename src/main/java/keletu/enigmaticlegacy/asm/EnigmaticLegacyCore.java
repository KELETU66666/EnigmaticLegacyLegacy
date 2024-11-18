package keletu.enigmaticlegacy.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.Name(value = "EnigmaticLegacyCore")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class EnigmaticLegacyCore implements IFMLLoadingPlugin {

    public EnigmaticLegacyCore() {
        try {
            Class.forName("zone.rong.mixinbooter.MixinBooterPlugin");
            System.out.println("mixinbooter detected, ignoring fermiumbooter.");
        } catch (ClassNotFoundException e) {
           fermiumbooter.FermiumRegistryAPI.enqueueMixin(true, "mixins.enigmaticlegacy.json");
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return "keletu.enigmaticlegacy.asm.ELCoreTransformer";
    }

}