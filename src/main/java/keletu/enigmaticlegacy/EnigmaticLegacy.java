package keletu.enigmaticlegacy;

import keletu.enigmaticlegacy.api.cap.EnigmaticCapabilities;
import keletu.enigmaticlegacy.api.cap.IPlaytimeCounter;
import keletu.enigmaticlegacy.api.cap.PlayerPlaytimeCounter;
import keletu.enigmaticlegacy.block.EnigmaticBaseBlock;
import keletu.enigmaticlegacy.client.LayerScroll;
import keletu.enigmaticlegacy.effect.BlazingStrengthEffect;
import keletu.enigmaticlegacy.effect.GrowingHungerEffect;
import keletu.enigmaticlegacy.entity.EntityItemImportant;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import keletu.enigmaticlegacy.entity.EntityItemSoulCrystal;
import keletu.enigmaticlegacy.entity.RenderEntitySoulCrystal;
import keletu.enigmaticlegacy.event.ELEvents;
import keletu.enigmaticlegacy.event.EventHandlerEntity;
import keletu.enigmaticlegacy.item.*;
import keletu.enigmaticlegacy.item.etherium.*;
import keletu.enigmaticlegacy.key.EnderChestRingHandler;
import keletu.enigmaticlegacy.packet.*;
import keletu.enigmaticlegacy.proxy.ClientProxy;
import keletu.enigmaticlegacy.proxy.CommonProxy;
import keletu.enigmaticlegacy.recipe.EnchantmentTransposingRecipe;
import keletu.enigmaticlegacy.recipe.RecipeOblivionStone;
import keletu.enigmaticlegacy.util.Quote;
import keletu.enigmaticlegacy.util.QuoteHandler;
import keletu.enigmaticlegacy.util.compat.CompatTrinketEvent;
import keletu.enigmaticlegacy.util.compat.ModCompat;
import static keletu.enigmaticlegacy.util.compat.ModCompat.COMPAT_FORGOTTEN_RELICS;
import keletu.enigmaticlegacy.util.loot.LoggerWrapper;
import keletu.enigmaticlegacy.util.loot.LootHandler;
import keletu.enigmaticlegacy.util.loot.LootHandlerOptional;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(
        modid = EnigmaticLegacy.MODID,
        name = EnigmaticLegacy.MOD_NAME,
        version = EnigmaticLegacy.VERSION,
        dependencies = "required-after:baubles;after:fermiumbooter;after:mixinbooter;after:patchouli"
)
public class EnigmaticLegacy {

    public static final String MODID = "enigmaticlegacy";
    public static final String MOD_NAME = "Enigmatic Legacy²";
    public static final String VERSION = "0.10.3";

    @SidedProxy(clientSide = "keletu.enigmaticlegacy.proxy.ClientProxy", serverSide = "keletu.enigmaticlegacy.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static final LoggerWrapper logger = new LoggerWrapper("Enigmatic Legacy");
    public static ItemArmor.ArmorMaterial ARMOR_ETHERIUM = EnumHelper.addArmorMaterial("etherium", EnigmaticLegacy.MODID + ":etherium", 132, new int[]{4, 7, 9, 4}, 24, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 4F);
    public static Item.ToolMaterial ETHERIUM = EnumHelper.addToolMaterial("etherium", 5, 3000, 8.0F, 5.0F, 32);
    public static Item.ToolMaterial ELDRITCH_PAN = EnumHelper.addToolMaterial("eldritch_pan", 0, 4000, 6.0F, 3.0F, 24);

    public static CreativeTabs tabEnigmaticLegacy = new CreativeTabs("tabEnigmaticLegacy") {
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(theAcknowledgment);
        }
    };
    public static Block astralBlock = new EnigmaticBaseBlock("astral_block", Material.SAND, "shovel", 0, SoundType.SAND);
    public static Block etheriumBlock = new EnigmaticBaseBlock("etherium_block", Material.IRON, "pickaxe", 3, SoundType.METAL);

    public static ItemEnigmaticAmulet enigmaticAmulet = new ItemEnigmaticAmulet();
    public static Item cursedRing = new ItemCursedRing();
    //public static Item cursedStone = new ItemCursedStone();
    public static ItemSoulCrystal soulCrystal = new ItemSoulCrystal();
    public static Item ironRing = new ItemIronRing();
    public static Item enderRing = new ItemEnderRing();
    public static Item gemRing = new ItemGemRing();
    public static Item magnetRing = new ItemMagnetRing();
    public static Item superMagnetRing = new ItemSuperMagnetRing();
    public static ItemMiningCharm miningCharm = new ItemMiningCharm();
    public static Item monsterCharm = new ItemMonsterCharm();
    public static Item berserkEmblem = new ItemBerserkEmblem();
    public static Item megaSponge = new ItemMegasponge();
    public static Item enchanterPearl = new ItemEnchanterPearl();
    public static Item xpScroll = new ItemExperienceScroll();
    public static Item cursedScroll = new ItemCursedScroll();
    public static Item animalGuide = new ItemAnimalGuide();
    public static Item ascensionAmulet = new ItemAscensionAmulet();
    public static Item golemHeart = new ItemGolemHeart();
    public static Item oceanStone = new ItemOceanStone();
    public static Item enigmaticEye = new ItemEnigmaticEye();
    public static ItemStorageCrystal storageCrystal = new ItemStorageCrystal();
    public static Item angelBlessing = new ItemAngelBlessing();
    public static ItemHeavenScroll heavenScroll = new ItemHeavenScroll("heaven_scroll", EnumRarity.EPIC);
    public static Item fabulousScroll = new ItemFabulousScroll();

    //Material
    public static Item earthHeart = new ItemEarthHeart();
    public static Item thiccScroll = new ItemBase("thicc_scroll", EnumRarity.COMMON);
    public static Item etheriumOre = new ItemBaseFireProof("etherium_ore", EnumRarity.RARE);
    public static Item etheriumIngot = new ItemBaseFireProof("etherium_ingot", EnumRarity.RARE);
    public static Item enderRod = new ItemBase("ender_rod", EnumRarity.EPIC);
    public static Item astralDust = new ItemBaseFireProof("astral_dust", EnumRarity.EPIC);
    public static Item evilEssence = new ItemBaseFireProof("evil_essence", EnumRarity.EPIC);
    public static Item ingotWitherite = new ItemIngotWitherite();
    public static Item twistedCore = new ItemBase("twisted_core", EnumRarity.EPIC).setMaxStackSize(1);
    public static Item abyssalHeart = new ItemBaseFireProof("abyssal_heart", EnumRarity.EPIC).setMaxStackSize(1);
    public static Item evilIngot = new ItemBaseFireProof("evil_ingot", EnumRarity.EPIC);
    public static Item cosmicHeart = new ItemBaseFireProof("cosmic_heart", EnumRarity.EPIC);

    //Armor
    public static Item etheriumHelm = new EtheriumArmor(EntityEquipmentSlot.HEAD, 1, "etherium_helm");
    public static Item etheriumChest = new EtheriumArmor(EntityEquipmentSlot.CHEST, 1, "etherium_chest");
    public static Item etheriumLegs = new EtheriumArmor(EntityEquipmentSlot.LEGS, 2, "etherium_legs");
    public static Item etheriumBoots = new EtheriumArmor(EntityEquipmentSlot.FEET, 1, "etherium_boots");

    //Tool
    public static Item etheriumSword = new ItemEtheriumSword();
    public static Item etheriumAxe = new ItemEtheriumAxe();
    public static Item etheriumPickaxe = new ItemEtheriumPick();
    public static Item etheriumSpade = new ItemEtheriumSpade();
    public static Item astralBreaker = new ItemAstralBreaker();
    public static Item theAcknowledgment = new ItemTheAcknowledgment();
    public static Item theTwist = new ItemTheTwist();
    public static Item theInfinitum = new ItemTheInfinitum();
    public static Item eldritchPan = new ItemEldritchPan();
    public static Item infinimeal = new ItemInfinimeal();
    public static Item infernalShield = new ItemInfernalShield();
    public static Item enchantmentTransposer = new ItemEnchantmentTransposer();
    public static ItemOblivionStone oblivionStone = new ItemOblivionStone();
    //public static ItemSoulCompass soulCompass = new ItemSoulCompass();
    public static ItemEldritchAmulet eldritchAmulet = new ItemEldritchAmulet();
    public static Item voidPearl = new ItemVoidPearl();
    public static Item desolationRing = new ItemDesolationRing();
    public static Item astralFruit = new ItemAstralFruit();
    public static Item ichorBottle = new ItemIchorBottle();


    public static SimpleNetworkWrapper packetInstance;
    public static Potion blazingStrengthEffect = new BlazingStrengthEffect();
    public static Potion growingHungerEffect = new GrowingHungerEffect();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ELConfigs.onConfig(event);

        CapabilityManager.INSTANCE.register(IPlaytimeCounter.class, new EnigmaticCapabilities.CapabilityPlayerPlayTime(), () -> new PlayerPlaytimeCounter(null));

        packetInstance = NetworkRegistry.INSTANCE.newSimpleChannel("EnigmaticChannel");
        packetInstance.registerMessage(PacketRecallParticles.Handler.class, PacketRecallParticles.class, 0, Side.CLIENT);
        packetInstance.registerMessage(PacketEnderRingKey.Handler.class, PacketEnderRingKey.class, 1, Side.SERVER);
        packetInstance.registerMessage(PacketPortalParticles.Handler.class, PacketPortalParticles.class, 2, Side.CLIENT);
        packetInstance.registerMessage(PacketEnchantedWithPearl.Handler.class, PacketEnchantedWithPearl.class, 3, Side.SERVER);
        packetInstance.registerMessage(PacketSyncPlayTime.Handler.class, PacketSyncPlayTime.class, 4, Side.CLIENT);
        packetInstance.registerMessage(PacketSyncPlayTime.Handler.class, PacketSyncPlayTime.class, 5, Side.SERVER);
        packetInstance.registerMessage(PacketCustom.Handler.class, PacketCustom.class, 6, Side.CLIENT);
        packetInstance.registerMessage(PacketPlayQuote.Handler.class, PacketPlayQuote.class, 29, Side.CLIENT);

        MinecraftForge.EVENT_BUS.register(new EventHandlerEntity());

        if (event.getSide().isClient()) {
            EnderChestRingHandler.registerKeybinds();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "soul_crystal"), EntityItemSoulCrystal.class, "soul_crystal", 0, MODID, 80, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "permanent_item"), EntityItemIndestructible.class, "permanent_item", 1, MODID, 80, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":" + "important_item"), EntityItemImportant.class, "important_item", 2, MODID, 80, 3, true);
        MinecraftForge.EVENT_BUS.register(new LootHandler());
        for(Quote quote : Quote.getAllQuotes())
        {
            ForgeRegistries.SOUND_EVENTS.register(quote.getSound());
        }
        //MinecraftForge.EVENT_BUS.register(new LootHandlerSpecial());
        if (COMPAT_FORGOTTEN_RELICS)
            MinecraftForge.EVENT_BUS.register(new LootHandlerOptional());

        if (event.getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new LayerScroll());
            MinecraftForge.EVENT_BUS.register(QuoteHandler.INSTANCE);
            ClientProxy.addRenderLayers();
        }
        GameRegistry.addSmelting(etheriumOre, new ItemStack(etheriumIngot, 1), 8.0f);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (ModCompat.COMPAT_TRINKETS)
            MinecraftForge.EVENT_BUS.register(new CompatTrinketEvent());
    }

    @Mod.EventHandler
    private void onServerStart(FMLServerAboutToStartEvent event) {
        performCleanup();
    }

    public void performCleanup() {
        // TODO Figure something out with those multimaps
        // I'd really like there to be a weak multimap or something

        //proxy.clearTransientData();
        //ELEvents.angeredGuardians.clear();
        //ELEvents.postmortalPossession.clear();
        ELEvents.knockbackThatBastard.clear();
        //ELEvents.deferredToast.clear();
        soulCrystal.attributeDispatcher.clear();
        //enigmaticItem.flightMap.clear();
        heavenScroll.flyMap.clear();
        //RegisteredMeleeAttack.clearRegistry();
    }


    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {

        @SubscribeEvent
        public static void addBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().register(astralBlock);
            event.getRegistry().register(etheriumBlock);
        }

        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().register(theAcknowledgment);
            event.getRegistry().register(enigmaticAmulet);
            event.getRegistry().register(ironRing);
            event.getRegistry().register(gemRing);
            event.getRegistry().register(enderRing);
            event.getRegistry().register(magnetRing);
            event.getRegistry().register(superMagnetRing);
            event.getRegistry().register(cursedRing);
            //event.getRegistry().register(cursedStone);
            event.getRegistry().register(soulCrystal);
            event.getRegistry().register(storageCrystal);
            event.getRegistry().register(miningCharm);
            event.getRegistry().register(monsterCharm);
            event.getRegistry().register(berserkEmblem);
            event.getRegistry().register(megaSponge);
            event.getRegistry().register(golemHeart);
            event.getRegistry().register(oceanStone);
            event.getRegistry().register(angelBlessing);
            event.getRegistry().register(voidPearl);
            event.getRegistry().register(earthHeart);
            event.getRegistry().register(infinimeal);
            event.getRegistry().register(twistedCore);
            event.getRegistry().register(theTwist);
            event.getRegistry().register(evilEssence);
            event.getRegistry().register(ingotWitherite);
            event.getRegistry().register(enchanterPearl);
            event.getRegistry().register(infernalShield);
            //event.getRegistry().register(soulCompass);
            event.getRegistry().register(evilIngot);
            event.getRegistry().register(etheriumOre);
            event.getRegistry().register(etheriumIngot);
            event.getRegistry().register(enchantmentTransposer);
            event.getRegistry().register(enderRod);
            event.getRegistry().register(astralDust);
            event.getRegistry().register(thiccScroll);
            event.getRegistry().register(xpScroll);
            event.getRegistry().register(cursedScroll);
            event.getRegistry().register(heavenScroll);
            event.getRegistry().register(fabulousScroll);
            event.getRegistry().register(animalGuide);
            event.getRegistry().register(etheriumHelm);
            event.getRegistry().register(etheriumChest);
            event.getRegistry().register(etheriumLegs);
            event.getRegistry().register(etheriumBoots);
            event.getRegistry().register(etheriumSword);
            event.getRegistry().register(etheriumAxe);
            event.getRegistry().register(etheriumPickaxe);
            event.getRegistry().register(etheriumSpade);
            event.getRegistry().register(astralBreaker);
            event.getRegistry().register(enigmaticEye);
            event.getRegistry().register(cosmicHeart);
            event.getRegistry().register(ascensionAmulet);
            event.getRegistry().register(abyssalHeart);
            event.getRegistry().register(theInfinitum);
            event.getRegistry().register(eldritchPan);
            event.getRegistry().register(desolationRing);
            event.getRegistry().register(eldritchAmulet);
            event.getRegistry().register(astralFruit);
            event.getRegistry().register(ichorBottle);

            event.getRegistry().register(new ItemBlock(astralBlock).setRegistryName("astral_block"));
            event.getRegistry().register(new ItemBlock(etheriumBlock).setRegistryName("etherium_block"));

            if (COMPAT_FORGOTTEN_RELICS) {
                event.getRegistry().register(oblivionStone);
            }
        }

        @SubscribeEvent
        public static void registerEffects(final RegistryEvent.Register<Potion> event) {
            event.getRegistry().register(blazingStrengthEffect);
            event.getRegistry().register(growingHungerEffect);
        }

        @SubscribeEvent
        public static void OreRegister(RegistryEvent.Register<Enchantment> event) {
            OreDictionary.registerOre("coreEarth", new ItemStack(earthHeart, 1, 0));
            OreDictionary.registerOre("coreEarth", new ItemStack(earthHeart, 1, 1));
            OreDictionary.registerOre("ingotNetherite", new ItemStack(ingotWitherite, 1, 0));


            OreDictionary.registerOre("amuletEnigmatic", new ItemStack(enigmaticAmulet, 1, 1));
            OreDictionary.registerOre("amuletEnigmatic", new ItemStack(enigmaticAmulet, 1, 2));
            OreDictionary.registerOre("amuletEnigmatic", new ItemStack(enigmaticAmulet, 1, 3));
            OreDictionary.registerOre("amuletEnigmatic", new ItemStack(enigmaticAmulet, 1, 4));
            OreDictionary.registerOre("amuletEnigmatic", new ItemStack(enigmaticAmulet, 1, 5));
            OreDictionary.registerOre("amuletEnigmatic", new ItemStack(enigmaticAmulet, 1, 6));
            OreDictionary.registerOre("amuletEnigmatic", new ItemStack(enigmaticAmulet, 1, 7));
        }

        @SubscribeEvent
        public static void registerRecipes(RegistryEvent.Register<IRecipe> evt) {
            IForgeRegistry<IRecipe> r = evt.getRegistry();

            r.register(new EnchantmentTransposingRecipe().setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "enchantment_transport")));

            if (COMPAT_FORGOTTEN_RELICS) {
                r.register(new RecipeOblivionStone().setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "oblivion_stone")));
            }
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void modelRegistryEvent(ModelRegistryEvent event) {
            ModelLoader.setCustomModelResourceLocation(cursedRing, 0, new ModelResourceLocation(cursedRing.getRegistryName(), "inventory"));
            //ModelLoader.setCustomModelResourceLocation(cursedStone, 0, new ModelResourceLocation(cursedStone.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(soulCrystal, 0, new ModelResourceLocation(soulCrystal.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(ironRing, 0, new ModelResourceLocation(ironRing.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(gemRing, 0, new ModelResourceLocation(gemRing.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(enderRing, 0, new ModelResourceLocation(enderRing.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(magnetRing, 0, new ModelResourceLocation(magnetRing.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(superMagnetRing, 0, new ModelResourceLocation(superMagnetRing.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(miningCharm, 0, new ModelResourceLocation(miningCharm.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(monsterCharm, 0, new ModelResourceLocation(monsterCharm.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(berserkEmblem, 0, new ModelResourceLocation(berserkEmblem.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(earthHeart, 0, new ModelResourceLocation(earthHeart.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(earthHeart, 1, new ModelResourceLocation(earthHeart.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(etheriumOre, 0, new ModelResourceLocation(etheriumOre.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(etheriumIngot, 0, new ModelResourceLocation(etheriumIngot.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(enderRod, 0, new ModelResourceLocation(enderRod.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(astralDust, 0, new ModelResourceLocation(astralDust.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(etheriumHelm, 0, new ModelResourceLocation(etheriumHelm.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(etheriumChest, 0, new ModelResourceLocation(etheriumChest.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(etheriumLegs, 0, new ModelResourceLocation(etheriumLegs.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(etheriumBoots, 0, new ModelResourceLocation(etheriumBoots.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(etheriumSword, 0, new ModelResourceLocation(etheriumSword.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(etheriumAxe, 0, new ModelResourceLocation(etheriumAxe.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(etheriumPickaxe, 0, new ModelResourceLocation(etheriumPickaxe.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(etheriumSpade, 0, new ModelResourceLocation(etheriumSpade.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(astralBreaker, 0, new ModelResourceLocation(astralBreaker.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(megaSponge, 0, new ModelResourceLocation(megaSponge.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(twistedCore, 0, new ModelResourceLocation(twistedCore.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(theAcknowledgment, 0, new ModelResourceLocation(theAcknowledgment.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(theTwist, 0, new ModelResourceLocation(theTwist.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(evilEssence, 0, new ModelResourceLocation(evilEssence.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(ingotWitherite, 0, new ModelResourceLocation(ingotWitherite.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(enchanterPearl, 0, new ModelResourceLocation(enchanterPearl.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(infinimeal, 0, new ModelResourceLocation(infinimeal.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(xpScroll, 0, new ModelResourceLocation(xpScroll.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(cursedScroll, 0, new ModelResourceLocation(cursedScroll.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(animalGuide, 0, new ModelResourceLocation(animalGuide.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(thiccScroll, 0, new ModelResourceLocation(thiccScroll.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(theInfinitum, 0, new ModelResourceLocation(theInfinitum.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(abyssalHeart, 0, new ModelResourceLocation(abyssalHeart.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(storageCrystal, 0, new ModelResourceLocation(storageCrystal.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(infernalShield, 0, new ModelResourceLocation(infernalShield.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(enchantmentTransposer, 0, new ModelResourceLocation(enchantmentTransposer.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(ascensionAmulet, 0, new ModelResourceLocation(ascensionAmulet.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(golemHeart, 0, new ModelResourceLocation(golemHeart.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(oceanStone, 0, new ModelResourceLocation(oceanStone.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(eldritchPan, 0, new ModelResourceLocation(eldritchPan.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(enigmaticEye, 0, new ModelResourceLocation(enigmaticEye.getRegistryName() + "_dormant", "inventory"));
            ModelLoader.setCustomModelResourceLocation(enigmaticEye, 1, new ModelResourceLocation(enigmaticEye.getRegistryName(), "inventory"));
            //ModelLoader.setCustomModelResourceLocation(soulCompass, 0, new ModelResourceLocation(soulCompass.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(evilIngot, 0, new ModelResourceLocation(evilIngot.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(angelBlessing, 0, new ModelResourceLocation(angelBlessing.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(heavenScroll, 0, new ModelResourceLocation(heavenScroll.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(cosmicHeart, 0, new ModelResourceLocation(cosmicHeart.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(eldritchAmulet, 0, new ModelResourceLocation(eldritchAmulet.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(voidPearl, 0, new ModelResourceLocation(voidPearl.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(fabulousScroll, 0, new ModelResourceLocation(fabulousScroll.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(desolationRing, 0, new ModelResourceLocation(desolationRing.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(astralFruit, 0, new ModelResourceLocation(astralFruit.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(ichorBottle, 0, new ModelResourceLocation(ichorBottle.getRegistryName(), "inventory"));

            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(astralBlock), 0, new ModelResourceLocation(astralBlock.getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(etheriumBlock), 0, new ModelResourceLocation(etheriumBlock.getRegistryName(), "inventory"));

            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(astralBlock), 0, new ModelResourceLocation(Item.getItemFromBlock(astralBlock).getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(etheriumBlock), 0, new ModelResourceLocation(Item.getItemFromBlock(etheriumBlock).getRegistryName(), "inventory"));


            if (COMPAT_FORGOTTEN_RELICS) {
                oblivionStone.registerModels();
            }

            for (int i = 0; i < 8; i++)
                ModelLoader.setCustomModelResourceLocation(enigmaticAmulet, i, new ModelResourceLocation(enigmaticAmulet.getRegistryName(), "inventory"));

            RenderingRegistry.registerEntityRenderingHandler(EntityItemSoulCrystal.class, manager -> new RenderEntitySoulCrystal(manager, Minecraft.getMinecraft().getRenderItem()));
        }

    }

}
