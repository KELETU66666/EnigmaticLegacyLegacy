package keletu.enigmaticlegacy;

import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;

@Grimmix(id = "enigmaticMixins", name = "Enigmatic Mixins")
public class ELLoader extends GrimmixController {

    @Override
    public void buildMixinConfigs(IConfigBuildingEvent event) {
        event.createBuilder("mixins.enigmaticlegacy.json")
                .mixinPackage("keletu.enigmaticlegacy.mixins")
                .commonMixins("common.*")
                .refmap("@MIXIN_REFMAP@")
                .configurationType(ConfigurationType.MOD)
                .verbose(true)
                .required(true)
                .build();
    }
}