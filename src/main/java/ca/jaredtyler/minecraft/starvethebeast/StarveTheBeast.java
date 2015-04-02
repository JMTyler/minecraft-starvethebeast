package ca.jaredtyler.minecraft.starvethebeast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;

@Mod(modid = StarveTheBeast.MODID, version = StarveTheBeast.VERSION, name = StarveTheBeast.NAME)
public class StarveTheBeast {
	
	public static final String MODID = "starvethebeast";
	public static final String VERSION = "0.9.2";
	public static final String NAME = "Starve The Beast";
	
	@Instance(MODID)
	public static StarveTheBeast instance;
	
	public static Item sulphur;
	public static Item saltpeter;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		_registerItems();
		GameRegistry.addShapelessRecipe(new ItemStack(Items.gunpowder), new Object[] {
			sulphur, saltpeter, new ItemStack(Items.coal, 1, 1)
		});
		
		_removeUnwantedRecipes();
		
		_addTntRecipes();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		_removeUnwantedItemsFromChestGen();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onHarvestDrops(HarvestDropsEvent event) {
		if (Block.isEqualTo(event.block, Blocks.gravel)) {
			_onHarvestGravel(event);
		} else if (Block.isEqualTo(event.block, Blocks.sand)) {
			_onHarvestSand(event);
		} else if (Block.isEqualTo(event.block, Blocks.leaves)
			|| Block.isEqualTo(event.block, Blocks.leaves2))
		{
			_onHarvestLeaves(event);
		}
	}
	
	protected void _registerItems() {
		sulphur = new Item()
			.setUnlocalizedName("stb_sulphur")
			.setCreativeTab(CreativeTabs.tabMaterials)
			.setTextureName(StarveTheBeast.MODID + ":" + "sulphur");
		GameRegistry.registerItem(sulphur, sulphur.getUnlocalizedName().substring(5));
		
		saltpeter = new Item()
			.setUnlocalizedName("stb_saltpeter")
			.setCreativeTab(CreativeTabs.tabMaterials)
			.setTextureName(StarveTheBeast.MODID + ":" + "saltpeter");
		GameRegistry.registerItem(saltpeter, saltpeter.getUnlocalizedName().substring(5));
	}
	
	protected void _removeUnwantedRecipes() {
		// Removing recipes for items we don't want... this is pretty nasty.
		List<ItemStack> unwantedItems = new ArrayList<ItemStack>();
		
		unwantedItems.add(new ItemStack(Items.bucket));
		
		unwantedItems.add(new ItemStack(Items.wooden_pickaxe));
		unwantedItems.add(new ItemStack(Items.stone_pickaxe));  // TODO: failing
		unwantedItems.add(new ItemStack(Items.iron_pickaxe));
		unwantedItems.add(new ItemStack(Items.golden_pickaxe));
		unwantedItems.add(new ItemStack(Items.diamond_pickaxe));  // TODO: failing
		
		unwantedItems.add(new ItemStack(Items.wooden_hoe));
		unwantedItems.add(new ItemStack(Items.stone_hoe));
		
		// Removing original TNT recipe so we can add a simpler one later.
		unwantedItems.add(new ItemStack(Item.getItemFromBlock(Blocks.tnt)));
		
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		for (int i = 0; i < recipes.size(); i++) {
			ItemStack output = recipes.get(i).getRecipeOutput();
			for (int j = 0; j < unwantedItems.size(); j++) {
				if (ItemStack.areItemStacksEqual(output, unwantedItems.get(j))) {
					recipes.remove(i);
				}
			}
		}
	}
	
	protected void _addTntRecipes() {
		GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(Blocks.tnt)), new Object[] {
			"GS",
			"SG",
			'G', Items.gunpowder,
			'S', Item.getItemFromBlock(Blocks.sand)
		});
		
		GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(Blocks.tnt)), new Object[] {
			"SG",
			"GS",
			'G', Items.gunpowder,
			'S', Item.getItemFromBlock(Blocks.sand)
		});
	}
	
	protected void _removeUnwantedItemsFromChestGen() {
		String[] buildingTypes = new String[] {
			ChestGenHooks.DUNGEON_CHEST, ChestGenHooks.MINESHAFT_CORRIDOR,
			ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST,
			ChestGenHooks.PYRAMID_JUNGLE_DISPENSER, ChestGenHooks.STRONGHOLD_CORRIDOR,
			ChestGenHooks.STRONGHOLD_CROSSING, ChestGenHooks.STRONGHOLD_LIBRARY,
			ChestGenHooks.VILLAGE_BLACKSMITH
		};
		
		ItemStack[] unwantedItems = new ItemStack[] {
			new ItemStack(Items.bucket),
			new ItemStack(Items.iron_pickaxe),
			new ItemStack(Item.getItemFromBlock(Blocks.sapling))
		};
		
		for (String buildingType : buildingTypes) {
			ChestGenHooks building = ChestGenHooks.getInfo(buildingType);
			for (ItemStack item : unwantedItems) {
				building.removeItem(item);
			}
		}
	}
	
	protected void _onHarvestGravel(HarvestDropsEvent event) {
		//event.fortuneLevel;
		//event.isSilkTouching;
		if (new Random(/* TODO: Should use the world seed here. */).nextFloat() < 0.1F) {
			event.drops.clear();
			event.drops.add(new ItemStack(sulphur));
		}
	}
	
	protected void _onHarvestSand(HarvestDropsEvent event) {
		//event.fortuneLevel;
		//event.isSilkTouching;
		if (new Random(/* TODO: Should use the world seed here. */).nextFloat() < 0.1F) {
			event.drops.clear();
			event.drops.add(new ItemStack(saltpeter));
		}
	}
	
	protected void _onHarvestLeaves(HarvestDropsEvent event) {
		// Stop trees from dropping saplings.  They may still drop apples.
		for (int i = 0; i < event.drops.size(); i++) {
			if (ItemStack.areItemStacksEqual(event.drops.get(i), new ItemStack(Item.getItemFromBlock(Blocks.sapling)))) {
				event.drops.remove(i);
			}
		}
	}

}
