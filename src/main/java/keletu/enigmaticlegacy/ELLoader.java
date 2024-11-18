package keletu.enigmaticlegacy;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Optional;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.List;

@Optional.Interface(iface = "zone.rong.mixinbooter.ILateMixinLoader", modid = "mixinbooter")
public class ELLoader implements ILateMixinLoader {
    @Override
    @Optional.Method(modid = "mixinbooter")
    public List<String> getMixinConfigs() {
        return Lists.newArrayList("mixins.enigmaticlegacy.json");
    }
}