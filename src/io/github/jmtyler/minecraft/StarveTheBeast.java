package io.github.jmtyler.minecraft;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class StarveTheBeast extends JavaPlugin implements Listener
{
	@Override
	public void onEnable()
	{
		this.removeBucket();

		this.removePickaxe(Material.WOOD);
		this.removePickaxe(Material.COBBLESTONE);
		this.removePickaxe(Material.IRON_INGOT);
		this.removePickaxe(Material.GOLD_INGOT);
		this.removePickaxe(Material.DIAMOND);

		this.removeHoe(Material.WOOD);
		this.removeHoe(Material.COBBLESTONE);

		this.removeOldTnt();
		this.addNewTnt();

		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable()
	{

	}

	@EventHandler
	public void onBlockDispense(BlockDispenseEvent event)
	{
		// TODO: What is Material.LEAVES_2 ?
		if (event.getBlock().getType().equals(Material.LEAVES)) {
			// Remove saplings.  And also apples, I guess?
			event.setCancelled(true);
		}
	}

	protected void removeBucket()
	{
		ShapedRecipe bucket = new ShapedRecipe(new ItemStack(Material.AIR));
		bucket.shape(
			"I I",
			" I "
		);
		bucket.setIngredient('I', Material.IRON_INGOT);
		this.getServer().addRecipe(bucket);
		// TODO: Also, either remove buckets from loot chests or stop player from picking them up.
	}

	protected void removeCharcoal()
	{
		// TODO: What is Material.LOG_2?
		FurnaceRecipe charcoal = new FurnaceRecipe(new ItemStack(Material.AIR), Material.LOG);
		this.getServer().addRecipe(charcoal);
	}

	protected void removePickaxe(Material headMaterial)
	{
		ShapedRecipe pickaxe = new ShapedRecipe(new ItemStack(Material.AIR));
		pickaxe.shape(
			"HHH",
			" S ",
			" S "
		);
		pickaxe.setIngredient('H', headMaterial);
		pickaxe.setIngredient('S', Material.STICK);
		this.getServer().addRecipe(pickaxe);
	}

	protected void removeHoe(Material headMaterial)
	{
		ShapedRecipe hoe = new ShapedRecipe(new ItemStack(Material.AIR));
		hoe.shape(
			"HH",
			" S ",
			" S "
		);
		hoe.setIngredient('H', headMaterial);
		hoe.setIngredient('S', Material.STICK);
		this.getServer().addRecipe(hoe);
	}

	protected void removeOldTnt()
	{
		ShapedRecipe tnt = new ShapedRecipe(new ItemStack(Material.AIR));
		tnt.shape(
			"SGS",
			"GSG",
			"SGS"
		);
		tnt.setIngredient('S', Material.SAND);
		tnt.setIngredient('G', Material.SULPHUR);
		this.getServer().addRecipe(tnt);
	}

	protected void addNewTnt()
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
