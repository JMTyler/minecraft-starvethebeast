package io.github.jmtyler.minecraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import io.github.jmtyler.minecraft.item.Saltpeter;
import io.github.jmtyler.minecraft.item.Sulphur;
import net.minecraft.server.v1_6_R3.Block;
import net.minecraft.server.v1_6_R3.Item;
import net.minecraft.server.v1_6_R3.MaterialMapColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spout.inventory.SimpleSpoutShapelessRecipe;
import org.getspout.spoutapi.Spout;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapelessRecipe;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.material.item.Coal;

public class StarveTheBeast extends JavaPlugin implements Listener
{
	public static CustomItem sulphur;
	public static CustomItem saltpeter;

	@Override
	public void onEnable()
	{
		sulphur = new Sulphur(this);
		saltpeter = new Saltpeter(this);

		getLogger().info("sulphur id: " + sulphur.getCustomId());

		this.removeUnwantedItems();
		this.addSimpleTntRecipe();

		this.addSulphur();
		this.addSaltpeter();
		this.addNewGunpowder();

		//Spout.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("enabling StarveTheBeast 3");
	}

	@Override
	public void onDisable()
	{

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		// TODO: What is Material.LEAVES_2 ?
		if (event.getBlock().getType().equals(Material.LEAVES)) {
			// Remove saplings.  And also apples, I guess?
			Collection<ItemStack> drops = event.getBlock().getDrops();
			boolean isDroppingSapling = drops.contains(new ItemStack(Material.SAPLING));
			getLogger().info("LEAVES drop. " + isDroppingSapling);
			//event.getBlock().getDrops().clear();
		} else if (event.getBlock().getType().equals(Material.GRAVEL)) {
			Collection<ItemStack> drops = event.getBlock().getDrops();
			boolean isDroppingGravel = drops.contains(new ItemStack(Material.GRAVEL));
			boolean isDroppingFlint  = drops.contains(new ItemStack(Material.FLINT));
			getLogger().info("GRAVEL drop: " + isDroppingGravel + " " + isDroppingFlint);
			event.getBlock().getDrops().add(new ItemStack(Material.BAKED_POTATO));
		} else {
			getLogger().info("dispensing a thing: " + event.getBlock().getType().getClass());
		}
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event)
	{
		getLogger().info("LEAVES DECAY : " + event.getBlock().getDrops().toString());
	}

	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event)
	{
		if (event.getItem().getItemStack().getType().equals(Material.SAPLING)) {
			getLogger().info("got a sapling");
		} else if (event.getItem().getItemStack().getType().equals(Material.GRAVEL)) {
			getLogger().info("got a gravel");
		} else if (event.getItem().getItemStack().getType().equals(Material.FLINT)) {
			getLogger().info("got a flint");
		} else {
			getLogger().info("got something else: " + event.getItem().getItemStack().getType());
		}
	}

	protected void removeUnwantedItems()
	{
		Collection<ItemStack> unwantedItems = new ArrayList<ItemStack>();

		// TODO: Also, either remove buckets from loot chests or stop player from picking them up.
		unwantedItems.add(new ItemStack(Material.BUCKET));

		unwantedItems.add(new ItemStack(Material.WOOD_PICKAXE));
		unwantedItems.add(new ItemStack(Material.STONE_PICKAXE));
		unwantedItems.add(new ItemStack(Material.IRON_PICKAXE));
		unwantedItems.add(new ItemStack(Material.GOLD_PICKAXE));
		unwantedItems.add(new ItemStack(Material.DIAMOND_PICKAXE));

		unwantedItems.add(new ItemStack(Material.WOOD_HOE));
		unwantedItems.add(new ItemStack(Material.STONE_HOE));

		// Removing old TNT recipe so we can add a simpler one later.
		unwantedItems.add(new ItemStack(Material.TNT));

		Iterator<Recipe> recipes = this.getServer().recipeIterator();
		while (recipes.hasNext()) {
			Recipe recipe = recipes.next();
			if (unwantedItems.contains(recipe.getResult())) {
				recipes.remove();
			}
		}
	}

	protected void addSulphur()
	{
		// TODO: remove this once you get drops working
		ShapelessRecipe recipe = new ShapelessRecipe(new SpoutItemStack(sulphur));
		recipe.addIngredient(Material.GRAVEL);
		this.getServer().addRecipe(recipe);

		// TODO: change texture to dull yellow dust
		// TODO: drop from gravel in listener above
	}

	protected void addSaltpeter()
	{
		// TODO: remove this once you get drops working
		ShapelessRecipe recipe = new ShapelessRecipe(new SpoutItemStack(saltpeter));
		recipe.addIngredient(Material.SAND);
		this.getServer().addRecipe(recipe);

		// TODO: change texture but don't make it look like sugar?
		// TODO: drop from sand in listener above
	}

	protected void addNewGunpowder()
	{
		SpoutShapelessRecipe recipe = new SpoutShapelessRecipe(new ItemStack(Material.SULPHUR));
		// TODO: This CANNOT be the right way to achieve this.  And it doesn't even work for charcoal.
		recipe.addIngredient(new Coal(Material.COAL.name(), Material.COAL.getId(), 0));
		recipe.addIngredient(sulphur);
		recipe.addIngredient(saltpeter);
		SpoutManager.getMaterialManager().registerSpoutRecipe(recipe);
	}

	protected void addSimpleTntRecipe()
	{
		ShapedRecipe tnt1 = new ShapedRecipe(new ItemStack(Material.TNT));
		tnt1.shape(
			"SG",
			"GS"
		);
		tnt1.setIngredient('S', Material.SAND);
		tnt1.setIngredient('G', Material.SULPHUR);
		this.getServer().addRecipe(tnt1);

		ShapedRecipe tnt2 = new ShapedRecipe(new ItemStack(Material.TNT));
		tnt2.shape(
			"GS",
			"SG"
		);
		tnt2.setIngredient('S', Material.SAND);
		tnt2.setIngredient('G', Material.SULPHUR);
		this.getServer().addRecipe(tnt2);
	}
}
