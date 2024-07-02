package toaster.zone.toastedrails;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public final class ToastedRails extends JavaPlugin implements Listener, CommandExecutor {
    public final List<Material> rail_types = Arrays.asList(Material.POWERED_RAIL, Material.RAIL, Material.DETECTOR_RAIL);
    private final double VANILLA_SPEED = 0.4d;
    private double maxSpeed = 1.0d;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getCommand("setminecartspeed").setExecutor(this);

        ItemStack poweredRail = new ItemStack(Material.POWERED_RAIL, 6);
        for (Recipe r : getServer().getRecipesFor(poweredRail))
            if (r instanceof Keyed)
                getServer().removeRecipe(((Keyed) r).getKey());

        NamespacedKey key = new NamespacedKey(this, "powered_rail");
        ItemStack copperRail = new ItemStack(Material.POWERED_RAIL, 12);
        ShapedRecipe copperRailRecipe = new ShapedRecipe(key, copperRail);
        copperRailRecipe.shape("# #","#|#","#*#");
        copperRailRecipe.setIngredient('#', Material.COPPER_INGOT);
        copperRailRecipe.setIngredient('|', Material.STICK);
        copperRailRecipe.setIngredient('*', Material.REDSTONE);
        getServer().addRecipe(copperRailRecipe);

        getLogger().info("ToastedRails has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("ToastedRails has been enabled!");
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Minecart)) return;
        Minecart cart = (Minecart) event.getVehicle();
        Block currentBlock = cart.getLocation().getBlock();
        if (!rail_types.contains(currentBlock.getType())) {
            if (currentBlock.getType() == Material.ACTIVATOR_RAIL) {
                Vector speed = cart.getVelocity();
                if (speed.getX() > VANILLA_SPEED / 2)
                    speed.setX(VANILLA_SPEED / 2);
                else if (speed.getX() < -0.5d * VANILLA_SPEED)
                    speed.setX(-0.5d * VANILLA_SPEED / 2);
                if (speed.getY() > VANILLA_SPEED / 2)
                    speed.setY(VANILLA_SPEED / 2);
                else if (speed.getY() < -0.5 * VANILLA_SPEED)
                    speed.setY(-0.5d * VANILLA_SPEED);
                cart.setMaxSpeed(VANILLA_SPEED / 2);
            }
            return;
        }
//        Rail currentRail = (Rail) currentBlock.getBlockData();
//        Block nextBlock = currentBlock.getRelative(cart.getFacing());

        cart.setMaxSpeed(maxSpeed);
    }

//    private boolean isTurn(Rail currentRail, Rail nextRail) {
//        // Determine if a turn is present by comparing directions
//        Rail.Shape currentShape = currentRail.getShape();
//        Rail.Shape nextShape = nextRail.getShape();
//        return (currentShape != nextShape &&
//                (currentShape == Rail.Shape.NORTH_EAST || currentShape == Rail.Shape.NORTH_WEST ||
//                        currentShape == Rail.Shape.SOUTH_EAST || currentShape == Rail.Shape.SOUTH_WEST));
//    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setminecartspeed")) {
            if (!sender.hasPermission("toastedrails.setspeed")) {
                sender.sendMessage("You do not have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                sender.sendMessage("Usage: /setminecartspeed <multiplier>");
                return true;
            }
            try {
                double speedMultiplier = Double.parseDouble(args[0]);
                if (speedMultiplier <= 0) {
                    sender.sendMessage("The speed multiplier must be greater than 0.");
                    return true;
                }
                maxSpeed = VANILLA_SPEED * speedMultiplier;
                sender.sendMessage("Minecart speed multiplier set to " + speedMultiplier);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number format. Please enter a valid number.");
            }
            return true;
        }
        return false;
    }
}
