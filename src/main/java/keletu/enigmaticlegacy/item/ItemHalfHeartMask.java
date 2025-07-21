package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.util.compat.CompatFirstAid;
import keletu.enigmaticlegacy.util.compat.ModCompat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ItemHalfHeartMask extends ItemBaseFireProof implements IBauble {

    public ItemHalfHeartMask() {
        super("half_heart_mask", EnumRarity.UNCOMMON);
        this.maxStackSize = 1;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.HEAD;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayerMP) {
            if(ModCompat.COMPAT_FIRSTAID){
                CompatFirstAid.onFirstAidHlfHealth((EntityPlayerMP) player);
            }else{
                if(player.getHealth() >= player.getMaxHealth() * 0.5F)
                {
                    player.setHealth(player.getMaxHealth() * 0.5F);
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.halfHeartMask1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.halfHeartMask2"));
            list.add(I18n.format("tooltip.enigmaticlegacy.halfHeartMask3"));
            list.add(I18n.format("tooltip.enigmaticlegacy.halfHeartMask4"));
            list.add(I18n.format("tooltip.enigmaticlegacy.halfHeartMask5"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        list.add("");
    }

    public static class IllagerTrades implements IMerchant {
        static final String STOCKS_KEY = "IllagerTrades";
        private final EntityLiving illagerEntity;
        private EntityPlayer customer;
        private MerchantRecipeList currentList;

        public IllagerTrades(final EntityLiving illagerEntity) {
            this.currentList = null;
            this.illagerEntity = illagerEntity;
        }

        public static class PriceInfo extends Tuple<Integer, Integer> {
            public PriceInfo(int min, int max) {
                super(min, max);
            }

            public int getPrice(Random rand) {
                return this.getFirst() >= this.getSecond() ? this.getFirst() : this.getFirst() + rand.nextInt(this.getSecond() - this.getFirst() + 1);
            }
        }

        public static void tradesForEmerald(MerchantRecipeList list, Random random, ItemStack buyingItemStack, PriceInfo buyingPriceInfo, PriceInfo emeraldCount) {
            int k = emeraldCount.getPrice(random);
            int j = buyingPriceInfo.getPrice(random);
            list.add(new MerchantRecipe(new ItemStack(buyingItemStack.getItem(), j, buyingItemStack.getMetadata()), new ItemStack(Items.EMERALD, k)));
        }

        public static void tradesToGoods(MerchantRecipeList list, Random random, ItemStack sellingItemstack, PriceInfo emeraldCount) {
            int k = emeraldCount.getPrice(random);
            list.add(new MerchantRecipe(new ItemStack(Items.EMERALD, k), sellingItemstack));
        }

        public static void tradesToGoods(MerchantRecipeList list, Random random, PriceInfo emeraldCount, ItemStack sellingItemstack, PriceInfo sellingPriceInfo) {
            int k = emeraldCount.getPrice(random);
            int j = sellingPriceInfo.getPrice(random);
            list.add(new MerchantRecipe(new ItemStack(Items.EMERALD, k), new ItemStack(sellingItemstack.getItem(), j, sellingItemstack.getMetadata())));
        }

        public static void tradesToGoods(MerchantRecipeList list, Random random, ItemStack buyingItemStack, PriceInfo buyingPriceInfo, PriceInfo emeraldCount, ItemStack sellingItemstack, PriceInfo sellingPriceInfo) {
            int i = buyingPriceInfo.getPrice(random);
            int k = emeraldCount.getPrice(random);
            int j = sellingPriceInfo.getPrice(random);
            list.add(new MerchantRecipe(new ItemStack(buyingItemStack.getItem(), i, buyingItemStack.getMetadata()), new ItemStack(Items.EMERALD, k), new ItemStack(sellingItemstack.getItem(), j, sellingItemstack.getMetadata())));
        }

        public static void tradesToGoods(MerchantRecipeList list, Random random, ItemStack buyingItemStack, PriceInfo buyingPriceInfo, PriceInfo emeraldCount, ItemStack sellingItemstack) {
            int i = buyingPriceInfo.getPrice(random);
            int k = emeraldCount.getPrice(random);
            list.add(new MerchantRecipe(new ItemStack(buyingItemStack.getItem(), i, buyingItemStack.getMetadata()), new ItemStack(Items.EMERALD, k), sellingItemstack));
        }

        private static void populateList(final EntityLiving illager, final MerchantRecipeList finalList) {
            final Random r = illager.world.rand;
            final MerchantRecipeList list = new MerchantRecipeList();

            tradesForEmerald(list, itemRand, new ItemStack(Blocks.LOG2, 1, 1), new PriceInfo(24, 64), new PriceInfo(1, 3));
            tradesForEmerald(list, itemRand, new ItemStack(Blocks.WEB), new PriceInfo(16, 32), new PriceInfo(1, 2));
            tradesToGoods(list, itemRand, new PriceInfo(1, 3), new ItemStack(Items.MUSHROOM_STEW), new PriceInfo(1, 1));
            tradesToGoods(list, itemRand, new ItemStack(Items.BANNER, 1, 15), new PriceInfo(1, 1), new PriceInfo(2, 3), SuperpositionHandler.ominousBanner());
            tradesToGoods(list, itemRand, new PriceInfo(3, 5), new ItemStack(Items.NAME_TAG), new PriceInfo(1, 2));

            if (illager instanceof EntityVindicator) {
                ItemStack ironAxe = new ItemStack(Items.IRON_AXE);
                EnchantmentHelper.addRandomEnchantment(itemRand, ironAxe, 20, true);
                tradesToGoods(list, itemRand, new ItemStack(Items.IRON_AXE), new PriceInfo(1, 1), new PriceInfo(7, 12), ironAxe);
                tradesToGoods(list, itemRand, new PriceInfo(3, 10), new ItemStack(Items.IRON_INGOT), new PriceInfo(5, 20));

                ItemStack vindicatorCloth = new ItemStack(Items.LEATHER_CHESTPLATE);
                ItemArmor vindicatorClothArmor = (ItemArmor) vindicatorCloth.getItem();
                vindicatorClothArmor.setColor(vindicatorCloth, 3025704);
                tradesToGoods(list, itemRand, vindicatorCloth, new PriceInfo(3, 10));

                if (r.nextDouble() < 0.3) {
                    ItemStack diamondAxe = new ItemStack(Items.DIAMOND_AXE);
                    EnchantmentHelper.addRandomEnchantment(itemRand, diamondAxe, 40, true);
                    tradesToGoods(list, itemRand, new ItemStack(Items.DIAMOND_AXE), new PriceInfo(1, 1), new PriceInfo(10, 20), diamondAxe);
                }
            } else if (illager instanceof EntityIllusionIllager) {
                tradesForEmerald(list, itemRand, new ItemStack(Items.ARROW), new PriceInfo(16, 32), new PriceInfo(1, 3));

                ItemStack bow = new ItemStack(Items.BOW);
                EnchantmentHelper.addRandomEnchantment(itemRand, bow, 20, true);
                tradesToGoods(list, itemRand, new ItemStack(Items.BOW), new PriceInfo(1, 1), new PriceInfo(5, 10), bow);

                ItemStack arrow = new ItemStack(Items.TIPPED_ARROW);
                PotionUtils.appendEffects(arrow, Collections.singletonList(new PotionEffect(MobEffects.BLINDNESS, 200)));
                arrow.setCount(new PriceInfo(5, 10).getPrice(itemRand));
                tradesToGoods(list, itemRand, arrow, new PriceInfo(1, 3));

                ItemStack arrowPoi = new ItemStack(Items.TIPPED_ARROW);
                PotionUtils.appendEffects(arrowPoi, Collections.singletonList(new PotionEffect(MobEffects.POISON, 100, 1)));
                arrowPoi.setCount(new PriceInfo(3, 5).getPrice(itemRand));
                tradesToGoods(list, itemRand, arrowPoi, new PriceInfo(5, 10));

                ItemStack arrowInd = new ItemStack(Items.TIPPED_ARROW);
                PotionUtils.appendEffects(arrowInd, Collections.singletonList(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 2)));
                arrowInd.setCount(new PriceInfo(2, 3).getPrice(itemRand));
                tradesToGoods(list, itemRand, arrowInd, new PriceInfo(5, 10));

            } else if (illager instanceof EntityEvoker) {
                tradesToGoods(list, itemRand, new PriceInfo(16, 32), new ItemStack(Items.GOLDEN_APPLE), new PriceInfo(1, 3));

                if (r.nextDouble() < 0.5) {
                    tradesToGoods(list, itemRand, new PriceInfo(48, 64), new ItemStack(Items.GOLDEN_APPLE), new PriceInfo(1, 2));
                }

                if (r.nextDouble() < 0.2) {
                    tradesToGoods(list, itemRand, new PriceInfo(32, 64), new ItemStack(Items.TOTEM_OF_UNDYING), new PriceInfo(1, 1));
                }
            }

            Collections.shuffle(list);
            for (int MAX_ITEMS = r.nextInt(2) + 1, i = 0; i < MAX_ITEMS && i < list.size(); ++i) {
                finalList.add(list.get(i));
            }
        }

        public void playIntro() {
            this.playGreeting(this.illagerEntity);
        }

        public EntityPlayer getCustomer() {
            return this.customer;
        }

        public void setCustomer(final EntityPlayer player) {
            this.customer = player;
        }

        public MerchantRecipeList getRecipes(final EntityPlayer player) {
            final NBTTagCompound nbtTag = this.illagerEntity.getEntityData();
            if (this.currentList != null) {
                return this.currentList;
            }
            if (nbtTag.hasKey("IllagerTrades")) {
                final NBTTagCompound nbtTagStocks = nbtTag.getCompoundTag("IllagerTrades");
                if (nbtTagStocks.isEmpty()) {
                    this.currentList = new MerchantRecipeList();
                } else {
                    this.currentList = new MerchantRecipeList(nbtTagStocks);
                }
                return this.currentList;
            }
            this.currentList = new MerchantRecipeList();
            populateList(this.illagerEntity, this.currentList);
            nbtTag.setTag("IllagerTrades", this.currentList.getRecipiesAsTags());
            return this.currentList;
        }

        public void useRecipe(final MerchantRecipe recipe) {
            if (this.illagerEntity != null && this.illagerEntity.isEntityAlive() && !this.illagerEntity.world.isRemote) {
                recipe.incrementToolUses();
                if (this.currentList != null) {
                    final NBTTagCompound nbtTag = this.illagerEntity.getEntityData();
                    nbtTag.setTag("IllagerTrades", this.currentList.getRecipiesAsTags());
                }
            }
            this.illagerEntity.playLivingSound();
        }

        public void verifySellingItem(final ItemStack itemstack) {
            this.illagerEntity.playLivingSound();
        }

        public ITextComponent getDisplayName() {
            return this.illagerEntity.getDisplayName();
        }

        public World getWorld() {
            return this.illagerEntity.world;
        }

        public BlockPos getPos() {
            return this.illagerEntity.getPosition();
        }

        @SideOnly(Side.CLIENT)
        public void setRecipes(final MerchantRecipeList list) {
        }

        private void playGreeting(final EntityLiving animal) {
            animal.playLivingSound();
        }
    }
}
