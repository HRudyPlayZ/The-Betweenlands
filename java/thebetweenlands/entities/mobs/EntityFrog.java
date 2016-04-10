package thebetweenlands.entities.mobs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;

public class EntityFrog extends EntityCreature {
	private int ticksOnGround = 0;
	public int jumpAnimationTicks;
	public int prevJumpAnimationTicks;
	private int strokeTicks = 0;

	public static final int DW_SWIM_STROKE = 20;

	public EntityFrog(World world) {
		super(world);
		this.getNavigator().setCanSwim(true);
		this.getNavigator().setAvoidsWater(false);
		this.tasks.addTask(0, new EntityAIPanic(this, 0.1D));
		this.tasks.addTask(1, new EntityAIWander(this, 0));
		this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(3, new EntityAILookIdle(this));
		setSize(0.7F, 0.7F);
		this.stepHeight = 1.0F;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(18, (byte) 0);
		dataWatcher.addObject(DW_SWIM_STROKE, (byte) 0);
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onUpdate() {
		this.prevJumpAnimationTicks = this.jumpAnimationTicks;
		super.onUpdate();
		if(this.onGround || (this.strokeTicks == 0 && this.isInWater())) {
			this.ticksOnGround++;
			if(this.jumpAnimationTicks > 0)
				this.jumpAnimationTicks = 0;
		} else {
			this.ticksOnGround = 0;
			this.jumpAnimationTicks++;
		}
		if(this.strokeTicks > 0)
			this.strokeTicks--;
		if(!this.worldObj.isRemote) {
			if(this.strokeTicks > 0) {
				this.strokeTicks--;
				this.dataWatcher.updateObject(DW_SWIM_STROKE, (byte) 1);
			} else {
				this.dataWatcher.updateObject(DW_SWIM_STROKE, (byte) 0);
			}
		} else {
			if(this.dataWatcher.getWatchableObjectByte(DW_SWIM_STROKE) == 1) {
				if(this.strokeTicks < 20)
					this.strokeTicks++;
			} else {
				this.strokeTicks = 0;
			}
		}
		if (!this.worldObj.isRemote) {
			this.setAir(20);

			PathEntity path = getNavigator().getPath();
			if (path != null && !path.isFinished() && (onGround || this.isInWater()) && !this.isMovementBlocked()) {
				int index = path.getCurrentPathIndex();
				if (index < path.getCurrentPathLength()) {
					PathPoint nextHopSpot = path.getPathPointFromIndex(index);
					float x = (float) (nextHopSpot.xCoord - posX);
					float z = (float) (nextHopSpot.zCoord - posZ);
					float angle = (float) (Math.atan2(z, x));
					float distance = (float) Math.sqrt(x * x + z * z);
					if(distance > 1) {
						if(!this.isInWater()) {
							if(this.ticksOnGround > 5) {
								this.motionY += 0.5;
								this.motionX += 0.3 * MathHelper.cos(angle);
								this.motionZ += 0.3 * MathHelper.sin(angle);
								this.velocityChanged = true;
							}
						} else {
							if(this.strokeTicks == 0) {
								this.motionX += 0.3 * MathHelper.cos(angle);
								this.motionZ += 0.3 * MathHelper.sin(angle);
								this.velocityChanged = true;
								this.strokeTicks = 40;
								this.worldObj.setEntityState(this, (byte)8);
							} else if(this.isCollidedHorizontally) {
								motionX += 0.01 * MathHelper.cos(angle);
								motionZ += 0.01 * MathHelper.sin(angle);
							}
						}
					}
				}
			}

			if(!this.worldObj.isRemote) {
				if(this.motionY < 0.0F && this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.4D), MathHelper.floor_double(this.posZ)).getMaterial().isLiquid()) {
					this.motionY *= 0.1F;
					this.velocityChanged = true;
				}
				if(this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.5D), MathHelper.floor_double(this.posZ)).getMaterial().isLiquid()) {
					this.motionY += 0.04F;
					this.velocityChanged = true;
				}
			}
		}
	}

	@Override
	public void moveEntityWithHeading(float strafing, float forward) {
		super.moveEntityWithHeading(0, 0);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(3.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		// TODO Texture types
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		// TODO Texture types
	}
	/*
	protected String getJumpingSound() {
		return null;

	}

	@Override
	protected String getLivingSound() {
		return null;

	}

	@Override
	protected String getHurtSound() {
		return null;

	}

	@Override
	protected String getDeathSound() {
		return null;
	}
	 */
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		if (isBurning())
			entityDropItem(ItemGeneric.createStack(BLItemRegistry.frogLegsCooked, 1, 0), 0.0F);
		else
			entityDropItem(ItemGeneric.createStack(BLItemRegistry.frogLegsRaw, 1, 0), 0.0F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleHealthUpdate(byte type) {
		if (type == 8) {
			this.strokeTicks = 0;
		}
	}
}
