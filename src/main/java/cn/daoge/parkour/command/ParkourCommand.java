package cn.daoge.parkour.command;

import cn.daoge.parkour.Parkour;
import cn.daoge.parkour.instance.IParkourInstance;
import cn.daoge.parkour.instance.ParkourInstance;
import cn.daoge.parkour.storage.JSONParkourStorage;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

import java.nio.file.Path;

public class ParkourCommand extends Command {
    public ParkourCommand(String name) {
        super(name, "Parkour Plugin Main Command", "", new String[]{"pk"});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.isPlayer()) {
            return false;
        }
        if (args.length == 0) {
            return false;
        }

        Parkour plugin = Parkour.getInstance();
        String name;
        IParkourInstance instance;
        switch (args[0].toLowerCase()) {
            case "see":
                if (args.length < 2 || !args[1].equals("info")) {
                    return false;
                }
                if (args.length < 3) {
                    return false;
                }
                name = args[2];
                instance = plugin.getParkourInstanceMap().get(name);
                if (instance == null) {
                    sender.sendMessage("[§bParkour§r] §cNo Parkour instance called §f" + name);
                    return false;
                }
                Parkour.getInstance().sendParkourInfo(sender.asPlayer(), instance);
                break;
            case "send":
                if (args.length < 2 || !args[1].equals("list")) {
                    return false;
                }
                Parkour.getInstance().sendParkourListForm(sender.asPlayer());
                break;
            case "create":
                if (!sender.isOp()) {
                    return false;
                }
                if (args.length < 2) {
                    return false;
                }
                name = args[1];
                Path dataPath = plugin.getDataPath().resolve(name + ".json");
                instance = new ParkourInstance(new JSONParkourStorage(dataPath));
                instance.getData().name = name;
                instance.getData().levelName = sender.getPosition().level.getName();
                plugin.addParkourInstance(instance);
                instance.save();
                sender.sendMessage("[§bParkour§r] Successfully add parkour §a" + name);
                break;
            case "set":
            case "add":
                if (!sender.isOp()) {
                    sender.sendMessage("[§bParkour§r] You must be an operator to perform this command.");
                    return false;
                }

                if (args.length < 3) {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                    return false;
                }

                name = args[2];
                instance = plugin.getParkourInstanceMap().get(name);
                if (instance == null) {
                    sender.sendMessage("[§bParkour§r] §cNo Parkour instance called §f" + name);
                    return false;
                }

                Vector3 pos;
                if (args.length > 4) {
                    pos = new Vector3(Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5]));
                } else {
                    pos = sender.getPosition().floor().add(0.5, 0, 0.5);
                }

                switch (args[1].toLowerCase()) {
                    case "start":
                        instance.getData().start = pos;
                        instance.save();
                        sender.sendMessage("[§bParkour§r] Successfully set start of parkour §a" + name);
                        break;
                    case "end":
                        instance.getData().end = pos;
                        instance.save();
                        sender.sendMessage("[§bParkour§r] Successfully set end of parkour §a" + name);
                        break;
                    case "point":
                        instance.getData().routePoints.add(pos);
                        instance.save();
                        sender.sendMessage("[§bParkour§r] Successfully add point to parkour §a" + name);
                        break;
                    case "rank":
                        // Assuming you have a method to add ranking text at a position
                        instance.addRankingText(Position.fromObject(pos, sender.getPosition().level));
                        sender.sendMessage("[§bParkour§r] Successfully add ranking text to parkour §a" + name);
                        break;
                    case "tppos":
                        instance.getData().tpPos = pos;
                        instance.save();
                        sender.sendMessage("[§bParkour§r] Successfully set tp pos of parkour §a" + name);
                        break;
                    default:
                        sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                        break;
                }
                break;
            default:
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                break;
        }
        return true;
    }
}
