package keletu.enigmaticlegacy.asm;

import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.Name("EnigmaticLegacyCore")
public class EnigmaticLegacyCore implements IFMLLoadingPlugin {

    public EnigmaticLegacyCore() {
		FermiumRegistryAPI.enqueueMixin(false, "mixins.enigmaticlegacy.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.enigmaticlegacy_late.json");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
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