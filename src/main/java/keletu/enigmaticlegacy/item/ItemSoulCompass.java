package keletu.enigmaticlegacy.item;

/*public class ItemSoulCompass extends ItemBaseFireProof {
    public ItemSoulCompass() {
        super("soul_compass", EnumRarity.EPIC);
        this.maxStackSize = 1;

        this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
            double rotation;
            double rota;
            long lastUpdateTick;

            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (entityIn == null && !stack.isOnItemFrame()) {
                    return 0.0F;
                } else {
                    boolean flag = entityIn != null;
                    Entity entity = flag ? entityIn : stack.getItemFrame();

                    double d0;
                    if (entity != null) {
                        if (worldIn == null) {
                            worldIn = entity.world;
                        }

                        d0 = this.getSpawnToAngle(flag, entity);

                        if (flag) {
                            d0 = this.wobble(worldIn, d0);
                        }
                    } else d0 = Math.random();

                    return MathHelper.positiveModulo((float) d0, 1.0F);
                }
            }

            private double wobble(World worldIn, double p_185093_2_) {
                if (worldIn.getTotalWorldTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = worldIn.getTotalWorldTime();
                    double d0 = p_185093_2_ - this.rotation;
                    d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                    this.rota += d0 * 0.1D;
                    this.rota *= 0.8D;
                    this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
                }

                return this.rotation;
            }

            private double getFrameRotation(EntityItemFrame p_185094_1_) {
                return MathHelper.wrapDegrees(180 + p_185094_1_.facingDirection.getHorizontalIndex() * 90);
            }

            private double getSpawnToAngle(boolean flag, Entity entity) {

                double d1 = flag ? (double) entity.rotationYaw : this.getFrameRotation((EntityItemFrame) entity);
                d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);

                Entity target = getNearestEntity(entity, ELEvents.soulCrystalPos.get(entity));

                if (target == null)
                    return Math.random();

                return 0.5D - (d1 - 0.25D - Math.atan2((double) target.getPosition().getZ() - entity.posZ, (double) target.getPosition().getX() - entity.posX) / (Math.PI * 2D));
            }
        });
    }

    public static Entity getNearestEntity(Entity playerPos, Entity target) {
        Entity nearestEntity = null;
        double nearestDistance = Double.MAX_VALUE;


        if (target == null)
            return null;

        if (playerPos.dimension == target.dimension) {

            double distance = playerPos.getPosition().distanceSq(target.getPosition());

            if (distance < nearestDistance) {
                nearestEntity = target;
            }
        }

        return nearestEntity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.soulCompass1"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }
}*/